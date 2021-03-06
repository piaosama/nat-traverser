package se.sics.gvod.croupier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import se.sics.gvod.common.Self;
import se.sics.gvod.common.VodDescriptor;
import se.sics.gvod.config.VodConfig;
import se.sics.gvod.net.VodAddress;

public class View {

    private final int size;
    private final Self self;
    private List<ViewEntry> entries;
    private HashMap<VodAddress, ViewEntry> d2e;
    private final Random random;
    
    private Comparator<ViewEntry> comparatorByAge = new Comparator<ViewEntry>() {
        @Override
        public int compare(ViewEntry o1, ViewEntry o2) {
            if (o1.getDescriptor().getAge() > o2.getDescriptor().getAge()) {
                return 1;
            } else if (o1.getDescriptor().getAge() < o2.getDescriptor().getAge()) {
                return -1;
            } else {
                return 0;
            }
        }
    };

    //-------------------------------------------------------------------	
    public View(Self self, int size, long seed) {
        super();
        this.self = self;
        this.size = size;
        this.entries = new ArrayList<ViewEntry>();
        this.d2e = new HashMap<VodAddress, ViewEntry>();
        this.random = new Random(seed);
    }

//-------------------------------------------------------------------	
    public void incrementDescriptorAges() {
        for (ViewEntry entry : entries) {
            entry.getDescriptor().incrementAndGetAge();
//            System.out.println("Croupier(" + self.getId() + "): " + entry);
        }
    }

    public VodAddress selectPeerToShuffleWith(VodConfig.CroupierSelectionPolicy policy,
            boolean softmax, double temperature) {
        if (entries.isEmpty()) {
            return null;
        }

        ViewEntry selectedEntry = null;

        if (!softmax || policy == VodConfig.CroupierSelectionPolicy.RANDOM) {
            if (policy == VodConfig.CroupierSelectionPolicy.TAIL) {
                selectedEntry = Collections.max(entries, comparatorByAge);
            } else if (policy == VodConfig.CroupierSelectionPolicy.HEALER) {
                selectedEntry = Collections.max(entries, comparatorByAge);
            } else if (policy == VodConfig.CroupierSelectionPolicy.RANDOM) {
                selectedEntry = entries.get(random.nextInt(entries.size()));
            } else {
                throw new IllegalArgumentException("Invalid Croupier policy selected:" +
                        policy);
            } 
        
        } else {

            List<ViewEntry> tempEntries =
                    new ArrayList<ViewEntry>(entries);
            if (policy == VodConfig.CroupierSelectionPolicy.TAIL) {
                Collections.sort(tempEntries, comparatorByAge);
            } else if (policy == VodConfig.CroupierSelectionPolicy.HEALER) {
                Collections.sort(tempEntries, comparatorByAge);
            } else {
                throw new IllegalArgumentException("Invalid Croupier policy selected:" +
                        policy);
            }            

            double rnd = random.nextDouble();
            double total = 0.0d;
            double[] values = new double[tempEntries.size()];
            int j = tempEntries.size() + 1;
            for (int i = 0; i < tempEntries.size(); i++) {
                // get inverse of values - lowest have highest value.
                double val = j;
                j--;
                values[i] = Math.exp(val / temperature);
                total += values[i];
            }

            boolean found = false;
            for (int i = 0; i < values.length; i++) {
                if (i != 0) {
                    values[i] += values[i - 1];
                }
                // normalise the probability
                double normalisedReward = values[i] / total;
                if (normalisedReward >= rnd) {
                    selectedEntry = tempEntries.get(i);
                    found = true;
                    break;
                }
            }
            if (!found) {
                selectedEntry = tempEntries.get(tempEntries.size() - 1);
            }
        }

        // TODO - by not removing a reference to the node I am shuffling with, we
        // break the 'batched random walk' (Cyclon) behaviour. But it's more important
        // to keep the graph connected.
        if (entries.size() >= size) {
            removeEntry(selectedEntry);
        }

        return selectedEntry.getDescriptor().getVodAddress();
    }

//-------------------------------------------------------------------	
    public List<VodDescriptor> selectToSendAtInitiator(int count, VodAddress destinationPeer) {
        List<ViewEntry> randomEntries = generateRandomSample(count);
        List<VodDescriptor> descriptors = new ArrayList<VodDescriptor>();
        for (ViewEntry cacheEntry : randomEntries) {
            cacheEntry.sentTo(destinationPeer);
            descriptors.add(cacheEntry.getDescriptor());
        }
        return descriptors;
    }

//-------------------------------------------------------------------	
    public List<VodDescriptor> selectToSendAtReceiver(int count, VodAddress destinationPeer) {
        List<ViewEntry> randomEntries = generateRandomSample(count);
        List<VodDescriptor> descriptors = new ArrayList<VodDescriptor>();
        for (ViewEntry cacheEntry : randomEntries) {
            cacheEntry.sentTo(destinationPeer);
            descriptors.add(cacheEntry.getDescriptor());
        }
        return descriptors;
    }

//-------------------------------------------------------------------	
    public void selectToKeep(VodAddress from, List<VodDescriptor> descriptors) {

        // TODO: Changing the check from the VodAddress equality to Address because original check resulted in retaining duplicate nodes in case of partitioning as only overlay id changed.
        if (from.getPeerAddress().equals(self.getAddress().getPeerAddress())) {
            return;
        }
        LinkedList<ViewEntry> entriesSentToThisPeer = new LinkedList<ViewEntry>();
        ViewEntry fromEntry = d2e.get(from);
        if (fromEntry != null) {
            entriesSentToThisPeer.add(fromEntry);
        }

        for (ViewEntry cacheEntry : entries) {
            if (cacheEntry.wasSentTo(from)) {
                entriesSentToThisPeer.add(cacheEntry);
            }
        }

        for (VodDescriptor descriptor : descriptors) {
            VodAddress id = descriptor.getVodAddress();
            if (self.getAddress().equals(id)) {
                // do not keep descriptor of self
                continue;
            }
            if (d2e.containsKey(id)) {
                // we already have an entry for this peer. keep the youngest one
                ViewEntry entry = d2e.get(id);
                if (entry.getDescriptor().getAge() > descriptor.getAge()) {
                    // we keep the lowest age descriptor
                    removeEntry(entry);
                    addEntry(new ViewEntry(descriptor));
                }
                continue;
            }
            if (entries.size() < size) {
                // fill an empty slot
                addEntry(new ViewEntry(descriptor));
                continue;
            }
            // replace one slot out of those sent to this peer
            ViewEntry sentEntry = entriesSentToThisPeer.poll();
            if (sentEntry != null) {
                removeEntry(sentEntry);
                addEntry(new ViewEntry(descriptor));
            }
        }
    }

//-------------------------------------------------------------------	
    public final List<VodDescriptor> getAll() {
        List<VodDescriptor> descriptors = new ArrayList<VodDescriptor>();
        for (ViewEntry cacheEntry : entries) {
            descriptors.add(cacheEntry.getDescriptor());
        }
        return descriptors;
    }

//-------------------------------------------------------------------	
    public final List<VodAddress> getAllAddress() {
        List<VodAddress> all = new ArrayList<VodAddress>();
        for (ViewEntry cacheEntry : entries) {
            all.add(cacheEntry.getDescriptor().getVodAddress());
        }
        return all;
    }

//-------------------------------------------------------------------	
    public final List<VodAddress> getRandomPeers(int count) {
        List<ViewEntry> randomEntries = generateRandomSample(count);
        List<VodAddress> randomPeers = new ArrayList<VodAddress>();

        for (ViewEntry cacheEntry : randomEntries) {
            randomPeers.add(cacheEntry.getDescriptor().getVodAddress());
        }

        return randomPeers;
    }

//-------------------------------------------------------------------	
    private List<ViewEntry> generateRandomSample(int n) {
        List<ViewEntry> randomEntries;
        if (n >= entries.size()) {
            // return all entries
            randomEntries = new ArrayList<ViewEntry>(entries);
        } else {
            // return count random entries
            randomEntries = new ArrayList<ViewEntry>();
            // Don Knuth, The Art of Computer Programming, Algorithm S(3.4.2)
            int t = 0, m = 0, N = entries.size();
            while (m < n) {
                int x = random.nextInt(N - t);
                if (x < n - m) {
                    randomEntries.add(entries.get(t));
                    m += 1;
                    t += 1;
                } else {
                    t += 1;
                }
            }
        }
        return randomEntries;
    }

//-------------------------------------------------------------------	
    private void addEntry(ViewEntry entry) {

        // if the entry refers to a stun port, change it to the default port.
        if (entry.getDescriptor().getVodAddress().getPort() == VodConfig.DEFAULT_STUN_PORT
                || entry.getDescriptor().getVodAddress().getPort() == VodConfig.DEFAULT_STUN_PORT_2) {
            entry.getDescriptor().getVodAddress().getPeerAddress().setPort(VodConfig.DEFAULT_PORT);
        }

        // don't add yourself
        if (entry.getDescriptor().getId() == self.getId()) {
            return;
        }
        
        if (!entries.contains(entry)) {
            entries.add(entry);
            d2e.put(entry.getDescriptor().getVodAddress(), entry);
            checkSize();
        } else {
            // replace the entry if it already exists
            removeEntry(entry);
            addEntry(entry);
        }
    }

//-------------------------------------------------------------------	
    private boolean removeEntry(ViewEntry entry) {
        boolean res = entries.remove(entry);
        if (d2e.remove(entry.getDescriptor().getVodAddress()) == null
                && res == true) {
            System.err.println("Croupier View corrupted.");
        }
        checkSize();
        return res;
    }

    public boolean timedOutForShuffle(VodAddress node) {
        ViewEntry entry = d2e.get(node);
        if (entry == null) {
            return false;
        }
        return removeEntry(entry);
    }

//-------------------------------------------------------------------	
    private void checkSize() {
        if (entries.size() != d2e.size()) {
            StringBuilder sb = new StringBuilder("Entries: \n");
            for (ViewEntry d : entries) {
                sb.append(d.toString()).append(", ");
            }
            sb.append(" \n IndexEntries: \n");
            for (VodAddress d : d2e.keySet()) {
                sb.append(d.toString()).append(", ");
            }
            System.err.println(sb.toString());
            throw new RuntimeException("WHD " + entries.size() + " <> " + d2e.size());
        }
    }

//-------------------------------------------------------------------
    public void initialize(Set<VodDescriptor> insiders) {
        for (VodDescriptor peer : insiders) {
            if (!peer.getVodAddress().equals(self.getAddress())) {
                addEntry(new ViewEntry(peer));
            }
        }
    }

    public boolean isEmpty() {
        return this.entries.isEmpty();
    }

    public int size() {
        return this.entries.size();
    }

    public void updateDescriptor(VodDescriptor d) {
    }
}
