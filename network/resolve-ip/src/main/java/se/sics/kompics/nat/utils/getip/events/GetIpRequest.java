/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.sics.kompics.nat.utils.getip.events;

import java.util.EnumSet;
import se.sics.kompics.Request;

/**
 *
 * @author jdowling
 */
public class GetIpRequest extends Request {

    private final boolean periodicCheckNetworkInterfaces;
    private final int upnpMappedPort;

    public static enum NetworkInterfacesMask {
        IGNORE_PRIVATE /* 192.168.*.* IP addresses*/,
        IGNORE_TEN_DOT_PRIVATE /* 10.*.*.* IP addresses*/,
        IGNORE_LOCAL_ADDRESSES /*127.0.*.* IP addresses*/,
        IGNORE_PUBLIC /*all public IP addresses - non 192., 10.0, and 127. addresses*/,
        NO_MASK
    };

    public static enum Protocol {
        UDP, TCP, NONE_SPECIFIED
    };
    private final Protocol protocol;

    private final EnumSet<NetworkInterfacesMask> ignoreMask;

    /**
     * Ignores any loopback addresses
     * @param periodicCheckNetworkInterfaces
     */
    public GetIpRequest(boolean periodicCheckNetworkInterfaces) {
        this(periodicCheckNetworkInterfaces, EnumSet.of(NetworkInterfacesMask.IGNORE_LOCAL_ADDRESSES));
    }

    public GetIpRequest(boolean periodicCheckNetworkInterfaces,
            EnumSet<NetworkInterfacesMask> ignoreNetInterfaces) {
        this(periodicCheckNetworkInterfaces, 0, Protocol.NONE_SPECIFIED,
                ignoreNetInterfaces);
    }

    public GetIpRequest(boolean periodicCheckNetworkInterfaces, int upnpMapPort,
            Protocol protocol, EnumSet<NetworkInterfacesMask> ignoreNetInterfaces) {
        this.periodicCheckNetworkInterfaces = periodicCheckNetworkInterfaces;
        this.upnpMappedPort = upnpMapPort;
        this.protocol = protocol;
        this.ignoreMask = ignoreNetInterfaces;
    }

    public boolean isFilterTenDotIps() {
        return ignoreMask.contains(NetworkInterfacesMask.IGNORE_TEN_DOT_PRIVATE);
    }

    public boolean isFilterPrivateIps() {
        return ignoreMask.contains(NetworkInterfacesMask.IGNORE_PRIVATE);
    }

    public boolean isFilterPublicIps() {
        return ignoreMask.contains(NetworkInterfacesMask.IGNORE_PUBLIC);
    }

    public boolean isFilterLoopback() {
        return ignoreMask.contains(NetworkInterfacesMask.IGNORE_LOCAL_ADDRESSES);
    }

    public boolean isPeriodicCheckNetworkInterfaces() {
        return periodicCheckNetworkInterfaces;
    }

    public int getUpnpBindPort() {
        return upnpMappedPort;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public EnumSet<NetworkInterfacesMask> getIgnoreMask() {
        return ignoreMask;
    }

}
