/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.sics.kompics;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import se.sics.gvod.address.Address;
import se.sics.gvod.common.DescriptorBuffer;
import se.sics.gvod.common.UtilityVod;
import se.sics.gvod.common.VodDescriptor;
import se.sics.gvod.common.hp.HPMechanism;
import se.sics.gvod.common.hp.HPRole;
import se.sics.gvod.common.msgs.ConnectMsg;
import se.sics.gvod.common.msgs.ConnectMsgFactory;
import se.sics.gvod.common.msgs.DirectMsgNetty;
import se.sics.gvod.common.msgs.DirectMsgNettyFactory;
import se.sics.gvod.common.msgs.DisconnectMsg;
import se.sics.gvod.common.msgs.DisconnectMsgFactory;
import se.sics.gvod.common.msgs.Encodable;
import se.sics.gvod.common.msgs.MessageDecodingException;
import se.sics.gvod.common.msgs.MessageEncodingException;
import se.sics.gvod.common.msgs.NatReportMsg;
import se.sics.gvod.common.msgs.NatReportMsgFactory;
import se.sics.gvod.common.msgs.RelayMsgNetty;
import se.sics.gvod.config.BaseCommandLineConfig;
import se.sics.gvod.config.VodConfig;
import se.sics.gvod.croupier.msgs.ShuffleMsg;
import se.sics.gvod.croupier.msgs.ShuffleMsgFactory;
import se.sics.gvod.gradient.msgs.SetsExchangeMsg;
import se.sics.gvod.gradient.msgs.SetsExchangeMsgFactory;
import se.sics.gvod.hp.events.OpenConnectionResponseType;
import se.sics.gvod.hp.msgs.GoMsg;
import se.sics.gvod.hp.msgs.GoMsgFactory;
import se.sics.gvod.hp.msgs.HolePunchingMsg;
import se.sics.gvod.hp.msgs.HolePunchingMsgFactory;
import se.sics.gvod.hp.msgs.HpConnectMsg;
import se.sics.gvod.hp.msgs.HpConnectMsgFactory;
import se.sics.gvod.hp.msgs.HpFinishedMsg;
import se.sics.gvod.hp.msgs.HpFinishedMsgFactory;
import se.sics.gvod.hp.msgs.HpKeepAliveMsg;
import se.sics.gvod.hp.msgs.HpKeepAliveMsgFactory;
import se.sics.gvod.hp.msgs.HpRegisterMsg;
import se.sics.gvod.hp.msgs.HpRegisterMsgFactory;
import se.sics.gvod.hp.msgs.HpUnregisterMsg;
import se.sics.gvod.hp.msgs.HpUnregisterMsgFactory;
import se.sics.gvod.hp.msgs.Interleaved_PRC_OpenHoleMsg;
import se.sics.gvod.hp.msgs.Interleaved_PRC_OpenHoleMsgFactory;
import se.sics.gvod.hp.msgs.Interleaved_PRC_ServersRequestForPredictionMsg;
import se.sics.gvod.hp.msgs.Interleaved_PRC_ServersRequestForPredictionMsgFactory;
import se.sics.gvod.hp.msgs.Interleaved_PRP_ConnectMsg;
import se.sics.gvod.hp.msgs.Interleaved_PRP_ConnectMsgFactory;
import se.sics.gvod.hp.msgs.Interleaved_PRP_ServerRequestForAvailablePortsMsg;
import se.sics.gvod.hp.msgs.Interleaved_PRP_ServerRequestForAvailablePortsMsgFactory;
import se.sics.gvod.hp.msgs.PRC_OpenHoleMsg;
import se.sics.gvod.hp.msgs.PRC_OpenHoleMsgFactory;
import se.sics.gvod.hp.msgs.PRC_ServerRequestForConsecutiveMsg;
import se.sics.gvod.hp.msgs.PRC_ServerRequestForConsecutiveMsgFactory;
import se.sics.gvod.hp.msgs.PRP_ConnectMsg;
import se.sics.gvod.hp.msgs.PRP_ConnectMsgFactory;
import se.sics.gvod.hp.msgs.PRP_PreallocatedPortsMsg;
import se.sics.gvod.hp.msgs.PRP_PreallocatedPortsMsgFactory;
import se.sics.gvod.hp.msgs.PRP_ServerRequestForAvailablePortsMsg;
import se.sics.gvod.hp.msgs.PRP_ServerRequestForAvailablePortsMsgFactory;
import se.sics.gvod.hp.msgs.ParentKeepAliveMsg;
import se.sics.gvod.hp.msgs.ParentKeepAliveMsgFactory;
import se.sics.gvod.hp.msgs.RelayRequestMsg;
import se.sics.gvod.hp.msgs.RelayRequestMsgFactory;
import se.sics.gvod.hp.msgs.SHP_InitiateSimpleHolePunchingMsg;
import se.sics.gvod.hp.msgs.SHP_InitiateSimpleHolePunchingMsgFactory;
import se.sics.gvod.hp.msgs.SHP_OpenHoleMsg;
import se.sics.gvod.hp.msgs.SHP_OpenHoleMsgFactory;
import se.sics.gvod.hp.msgs.TConnectionMsg;
import se.sics.gvod.hp.msgs.TConnectionMsgFactory;
import se.sics.gvod.net.BaseMsgFrameDecoder;
import se.sics.gvod.net.msgs.NatMsg;
import se.sics.gvod.net.Nat;
import se.sics.gvod.net.VodAddress;
import se.sics.gvod.net.util.UserTypesDecoderFactory;
import se.sics.gvod.net.util.UserTypesEncoderFactory;
import se.sics.gvod.stun.msgs.EchoChangeIpAndPortMsg;
import se.sics.gvod.stun.msgs.EchoChangeIpAndPortMsgFactory;
import se.sics.gvod.stun.msgs.EchoChangePortMsg;
import se.sics.gvod.stun.msgs.EchoChangePortMsgFactory;
import se.sics.gvod.stun.msgs.EchoMsg;
import se.sics.gvod.stun.msgs.EchoMsgFactory;
import se.sics.gvod.stun.msgs.ReportMsg;
import se.sics.gvod.stun.msgs.ReportMsgFactory;
import se.sics.gvod.stun.msgs.ServerHostChangeMsg;
import se.sics.gvod.stun.msgs.ServerHostChangeMsgFactory;
import se.sics.gvod.timer.TimeoutId;
import se.sics.gvod.timer.UUID;

/**
 *
 * @author jdowling
 */
public class EncodingDecodingTest {

    private static Address src, dest;
    private static InetSocketAddress inetSrc, inetDest;
    private static VodAddress gSrc, gDest;
    private static int overlay = 120;
    private static UtilityVod utility = new UtilityVod(1, 12, 123);
    private static TimeoutId id = UUID.nextUUID();
    private static int age = 200;
    private static int freshness = 100;
    private static int remoteClientId = 12123454;
    private static HPMechanism hpMechanism = HPMechanism.PRP_PRC;
    private static HPRole hpRole = HPRole.PRC_RESPONDER;
    private static Nat nat;
    private static VodDescriptor nodeDescriptor;
    private static List<VodDescriptor> descriptors = new ArrayList<VodDescriptor>();
    private static byte[] availableChunks = new byte[2031];
    private static byte[][] availablePieces = new byte[52][19];

    public EncodingDecodingTest() {
        System.setProperty("java.net.preferIPv4Stack", "true");
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        InetAddress self = InetAddress.getByName("127.0.0.1");
        src = new Address(self, 58027, 123);
        dest = new Address(self, 65535, 13445);
        inetSrc = new InetSocketAddress(self, 58027);
        inetDest = new InetSocketAddress(self, 65535);
        gSrc = new VodAddress(src, VodConfig.SYSTEM_OVERLAY_ID);
        gDest = new VodAddress(dest, VodConfig.SYSTEM_OVERLAY_ID);
        nodeDescriptor = new VodDescriptor(gSrc, utility,
                age, BaseCommandLineConfig.DEFAULT_MTU);
        descriptors.add(nodeDescriptor);
        nat = new Nat(Nat.Type.NAT,
                Nat.MappingPolicy.HOST_DEPENDENT,
                Nat.AllocationPolicy.PORT_PRESERVATION,
                Nat.FilteringPolicy.PORT_DEPENDENT,
                1,
                100 * 1000l);
        DirectMsgNettyFactory.Base.setMsgFrameDecoder(BaseMsgFrameDecoder.class);
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    @Test
    public void unsignedIntTwoBytesNetty() throws MessageEncodingException, MessageDecodingException {

        ByteBuf buffer = Unpooled.buffer(2);
        int t1 = 32231;
        UserTypesEncoderFactory.writeUnsignedintAsTwoBytes(buffer, t1);
        int t2 = UserTypesDecoderFactory.readUnsignedIntAsTwoBytes(buffer);
        assert (t1 == t2);
        t1 = 65535;
        UserTypesEncoderFactory.writeUnsignedintAsTwoBytes(buffer, t1);
        t2 = UserTypesDecoderFactory.readUnsignedIntAsTwoBytes(buffer);
        assert (t1 == t2);
    }

    @Test
    public void unsignedIntOneByteNetty() throws MessageEncodingException, MessageDecodingException {

        ByteBuf buffer = Unpooled.buffer(1);
        int t1 = 255;
        UserTypesEncoderFactory.writeUnsignedintAsOneByte(buffer, t1);
        int t2 = UserTypesDecoderFactory.readUnsignedIntAsOneByte(buffer);
        assert (t1 == t2);
        t1 = 0;
        UserTypesEncoderFactory.writeUnsignedintAsOneByte(buffer, t1);
        t2 = UserTypesDecoderFactory.readUnsignedIntAsOneByte(buffer);
        assert (t1 == t2);
        t1 = 1;
        UserTypesEncoderFactory.writeUnsignedintAsOneByte(buffer, t1);
        t2 = UserTypesDecoderFactory.readUnsignedIntAsOneByte(buffer);
        assert (t1 == t2);
    }

    @Test
    public void booleanNetty() throws MessageEncodingException, MessageDecodingException {
        boolean yes = true;
        ByteBuf buffer = Unpooled.buffer(1);
        UserTypesEncoderFactory.writeBoolean(buffer, yes);
        boolean id2 = UserTypesDecoderFactory.readBoolean(buffer);
        assert (yes == id2);
    }

    @Test
    public void stringNetty() throws MessageEncodingException, MessageDecodingException {
        String str = "Jim Dowling";
        ByteBuf buffer = Unpooled.buffer(str.length());
        UserTypesEncoderFactory.writeStringLength256(buffer, str);
        String str2 = UserTypesDecoderFactory.readStringLength256(buffer);
        assert (str.equals(str2));

        str = "Jim Dowling Jim Dowling Jim Dowling Jim Dowling Jim Dowling ";
        UserTypesEncoderFactory.writeStringLength256(buffer, str);
        str2 = UserTypesDecoderFactory.readStringLength256(buffer);
        assert (str.equals(str2));
    }

    private void opCodeCorrect(ByteBuf buffer, Encodable msg) {
        byte type = buffer.readByte();
        assert (type == msg.getOpcode());
    }

    @Test
    public void connectMsgRequest() {
        ConnectMsg.Request msg = new ConnectMsg.Request(gSrc, gSrc,
                utility, true, BaseCommandLineConfig.DEFAULT_MTU);
        msg.setTimeoutId(UUID.nextUUID());
        try {
            ByteBuf buffer = msg.toByteArray();
            opCodeCorrect(buffer, msg);
            ConnectMsg.Request res = ConnectMsgFactory.Request.fromBuffer(buffer);
            compareNatMsgs(msg, res);
        } catch (MessageDecodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        } catch (MessageEncodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        }
    }

    @Test
    public void connectMsgResponse1() {
        TimeoutId id = UUID.nextUUID();
        ConnectMsg.Response msg = new ConnectMsg.Response(gSrc, gSrc,
                id, ConnectMsg.ResponseType.OK,
                utility, availableChunks, availablePieces, true,
                BaseCommandLineConfig.DEFAULT_MTU);
        try {
            ByteBuf buffer = msg.toByteArray();
            opCodeCorrect(buffer, msg);
            ConnectMsg.Response res = ConnectMsgFactory.Response.fromBuffer(buffer);
            compareNatMsgs(msg, res);
        } catch (MessageDecodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        } catch (MessageEncodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        }
    }

    @Test
    public void connectMsgResponse2() {
        ConnectMsg.Response msg = new ConnectMsg.Response(gSrc, gSrc,
                UUID.nextUUID(), ConnectMsg.ResponseType.OK,
                utility, null, null, true, BaseCommandLineConfig.DEFAULT_MTU);
        try {
            ByteBuf buffer = msg.toByteArray();
            opCodeCorrect(buffer, msg);
            ConnectMsg.Response res = ConnectMsgFactory.Response.fromBuffer(buffer);
            compareNatMsgs(msg, res);
        } catch (MessageDecodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        } catch (MessageEncodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        }
    }

    @Test
    public void disconnectMsgRequest() {
        DisconnectMsg.Request msg = new DisconnectMsg.Request(gSrc, gSrc);
        msg.setTimeoutId(UUID.nextUUID());
        try {
            ByteBuf buffer = msg.toByteArray();
            opCodeCorrect(buffer, msg);
            DisconnectMsg.Request res = DisconnectMsgFactory.Request.fromBuffer(buffer);
            compareNatMsgs(msg, res);
        } catch (MessageDecodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        } catch (MessageEncodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        }
    }

    @Test
    public void disconnectMsgResponse() {
        TimeoutId id = UUID.nextUUID();
        DisconnectMsg.Response msg = new DisconnectMsg.Response(gSrc, gSrc, id, 4);
        try {
            ByteBuf buffer = msg.toByteArray();
            opCodeCorrect(buffer, msg);
            DisconnectMsg.Response res = DisconnectMsgFactory.Response.fromBuffer(buffer);
            compareNatMsgs(msg, res);
        } catch (MessageDecodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        } catch (MessageEncodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        }
    }

    @Test
    public void shuffleRequest() {
        try {

            InetAddress address1 = InetAddress.getByName("192.168.0.1");
            InetAddress address2 = InetAddress.getByName("192.168.0.2");

            VodAddress vodAddress1 = new VodAddress(new Address(address1, 8081, 1),
                    VodConfig.SYSTEM_OVERLAY_ID, nat);
            VodAddress vodAddress2 = new VodAddress(new Address(address2, 8082, 2), VodConfig.SYSTEM_OVERLAY_ID);
            VodAddress vodAddress5 = new VodAddress(new Address(address1, 8081, 5), VodConfig.SYSTEM_OVERLAY_ID);

            VodDescriptor desc1 = new VodDescriptor(vodAddress1,
                    new UtilityVod(100, 0), 0, 0);
            VodDescriptor desc2 = new VodDescriptor(vodAddress2,
                    new UtilityVod(100, 0), 0, 0);
            VodDescriptor desc5 = new VodDescriptor(vodAddress5, new UtilityVod(100, 0), 0, 0);

            List<VodDescriptor> publicView = new ArrayList<VodDescriptor>();
            List<VodDescriptor> privateView = new ArrayList<VodDescriptor>();

            DescriptorBuffer descBuffer = new DescriptorBuffer(gSrc, publicView, privateView);
            ShuffleMsg.Request request = new ShuffleMsg.Request(vodAddress1, vodAddress2,
                    descBuffer, nodeDescriptor);
            request.setTimeoutId(UUID.nextUUID());
            ByteBuf channelBuffer = request.toByteArray();
            opCodeCorrect(channelBuffer, request);
            ShuffleMsg.Request fromBuffer = ShuffleMsgFactory.Request.fromBuffer(channelBuffer);
            compareNatMsgs(request, fromBuffer);

            assert (fromBuffer.getBuffer().getPublicDescriptors().isEmpty());
            assert (fromBuffer.getBuffer().getPrivateDescriptors().isEmpty());

            publicView.add(desc1);
            publicView.add(desc2);

            privateView.add(desc5);

            ShuffleMsg.Request request2 = new ShuffleMsg.Request(vodAddress1, vodAddress2,
                    descBuffer, nodeDescriptor);
            request2.setTimeoutId(UUID.nextUUID());
            ByteBuf channelBuffer2 = request2.toByteArray();
            opCodeCorrect(channelBuffer2, request2);
            ShuffleMsg.Request fromBuffer2 = ShuffleMsgFactory.Request.fromBuffer(channelBuffer2);

            assert (fromBuffer2.getBuffer().getPublicDescriptors().size() == 2);
            assert (fromBuffer2.getBuffer().getPrivateDescriptors().size() == 1);
            assert (fromBuffer2.getBuffer().getPublicDescriptors().get(0).equals(desc1));
            assert (fromBuffer2.getBuffer().getPublicDescriptors().get(1).equals(desc2));
            assert (fromBuffer2.getBuffer().getPrivateDescriptors().get(0).equals(desc5));
            compareNatMsgs(request2, fromBuffer2);

        } catch (MessageDecodingException ex) {
            assert (false);
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MessageEncodingException ex) {
            assert (false);
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnknownHostException ex) {
            assert (false);
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    public void shuffleResponse() {
        try {
            InetAddress address1 = InetAddress.getByName("192.168.0.1");
            InetAddress address2 = InetAddress.getByName("192.168.0.2");

            VodAddress vodAddress1 = new VodAddress(new Address(address1, 8081, 1), VodConfig.SYSTEM_OVERLAY_ID);
            VodAddress vodAddress2 = new VodAddress(new Address(address2, 8082, 2), VodConfig.SYSTEM_OVERLAY_ID);
            VodAddress vodAddress3 = new VodAddress(new Address(address1, 8081, 3), VodConfig.SYSTEM_OVERLAY_ID);
            VodAddress vodAddress4 = new VodAddress(new Address(address2, 8082, 4), VodConfig.SYSTEM_OVERLAY_ID);
            VodAddress vodAddress5 = new VodAddress(new Address(address1, 8081, 5), VodConfig.SYSTEM_OVERLAY_ID);

            VodDescriptor desc3 = new VodDescriptor(vodAddress3,
                    new UtilityVod(100, 0), 0, 0);
            VodDescriptor desc4 = new VodDescriptor(vodAddress4,
                    new UtilityVod(100, 0), 0, 0);
            VodDescriptor desc5 = new VodDescriptor(vodAddress5,
                    new UtilityVod(100, 0), 0, 0);

            TimeoutId timeoutId = UUID.nextUUID();

            List<VodDescriptor> publicView = new ArrayList<VodDescriptor>();
            List<VodDescriptor> privateView = new ArrayList<VodDescriptor>();

            DescriptorBuffer descBuffer = new DescriptorBuffer(gSrc, publicView, privateView);
            ShuffleMsg.Response response = new ShuffleMsg.Response(vodAddress1, vodAddress2, timeoutId,descBuffer, nodeDescriptor);
            response.setTimeoutId(UUID.nextUUID());
            ByteBuf channelBuffer = response.toByteArray();
            opCodeCorrect(channelBuffer, response);
            ShuffleMsg.Response fromBuffer = ShuffleMsgFactory.Response.fromBuffer(channelBuffer);
            compareNatMsgs(response, fromBuffer);

            assert (fromBuffer.getBuffer().getPublicDescriptors().isEmpty());

            publicView.add(desc3);
            publicView.add(desc4);

            privateView.add(desc5);


            ShuffleMsg.Response response2 = new ShuffleMsg.Response(vodAddress1, vodAddress2, timeoutId, descBuffer, nodeDescriptor);
            response2.setTimeoutId(UUID.nextUUID());
            ByteBuf channelBuffer2 = response2.toByteArray();
            opCodeCorrect(channelBuffer2, response2);
            ShuffleMsg.Response fromBuffer2 = ShuffleMsgFactory.Response.fromBuffer(channelBuffer2);

            assert (fromBuffer2.getBuffer().getPublicDescriptors().size() == 2);
            assert (fromBuffer2.getBuffer().getPrivateDescriptors().size() == 1);
            assert (fromBuffer2.getBuffer().getPublicDescriptors().get(0).equals(desc3));
            assert (fromBuffer2.getBuffer().getPublicDescriptors().get(1).equals(desc4));
            assert (fromBuffer2.getBuffer().getPrivateDescriptors().get(0).equals(desc5));
            compareNatMsgs(response2, fromBuffer2);

        } catch (MessageDecodingException ex) {
            assert (false);
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MessageEncodingException ex) {
            assert (false);
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnknownHostException ex) {
            assert (false);
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    

    @Test
    public void setsExchangeRequestMsg() {
        SetsExchangeMsg.Request msg = new SetsExchangeMsg.Request(
                gSrc, gSrc, gSrc.getId(), gSrc.getId(), id);
        try {
            ByteBuf buffer = msg.toByteArray();
            opCodeCorrect(buffer, msg);
            SetsExchangeMsg.Request res =
                    SetsExchangeMsgFactory.Request.fromBuffer(buffer);
            compareNatMsgs(msg, res);
        } catch (MessageDecodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        } catch (MessageEncodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        }
    }

    @Test
    public void setsExchangeResponseMsg() {
        SetsExchangeMsg.Response msg = new SetsExchangeMsg.Response(
                gSrc, gSrc, gSrc.getId(), gSrc.getId(), gDest, id, descriptors, descriptors);
        try {
            ByteBuf buffer = msg.toByteArray();
            opCodeCorrect(buffer, msg);
            SetsExchangeMsg.Response res =
                    SetsExchangeMsgFactory.Response.fromBuffer(buffer);
            compareNatMsgs(msg, res);
        } catch (MessageDecodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        } catch (MessageEncodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        }
    }

    @Test
    public void GoMsg() {
        GoMsg.Request msg = new GoMsg.Request(gSrc, gDest, gDest,
                hpMechanism, hpRole, 1,
                UUID.nextUUID());
        try {
            ByteBuf buffer = msg.toByteArray();
            opCodeCorrect(buffer, msg);
            GoMsg.Request res = GoMsgFactory.Request.fromBuffer(buffer);
            compareNatMsgs(msg, res);
        } catch (MessageDecodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        } catch (MessageEncodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        }
    }

    @Test
    public void HolePunchingMsg() {
        HolePunchingMsg.Request msg = new HolePunchingMsg.Request(gSrc, gDest, 
                UUID.nextUUID());
        msg.setTimeoutId(UUID.nextUUID());
        try {
            ByteBuf buffer = msg.toByteArray();
            opCodeCorrect(buffer, msg);
            HolePunchingMsg.Request res =
                    HolePunchingMsgFactory.Request.fromBuffer(buffer);
            assert (msg.getRemoteClientId() == res.getRemoteClientId());
            compareNatMsgs(msg, res);
            assert(msg.getRemoteClientId() == res.getRemoteClientId());
        } catch (MessageDecodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        } catch (MessageEncodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        }
    }

    @Test
    public void HolePunchingResponseMsg() {
        HolePunchingMsg.Response msg = new HolePunchingMsg.Response(gSrc, gDest,
                UUID.nextUUID(), UUID.nextUUID());
        msg.setTimeoutId(UUID.nextUUID());
        try {
            ByteBuf buffer = msg.toByteArray();
            opCodeCorrect(buffer, msg);
            HolePunchingMsg.Response res =
                    HolePunchingMsgFactory.Response.fromBuffer(buffer);
            compareNatMsgs(msg, res);
        } catch (MessageDecodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        } catch (MessageEncodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        }
    }

    @Test
    public void HolePunchingResponseAckMsg() {
        HolePunchingMsg.ResponseAck msg = new HolePunchingMsg.ResponseAck(gSrc, gDest,
                UUID.nextUUID(), UUID.nextUUID());
        try {
            ByteBuf buffer = msg.toByteArray();
            opCodeCorrect(buffer, msg);
            HolePunchingMsg.ResponseAck res =
                    HolePunchingMsgFactory.ResponseAck.fromBuffer(buffer);
            compareNatMsgs(msg, res);
        } catch (MessageDecodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        } catch (MessageEncodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        }
    }

    @Test
    public void HpFinishedMsg() {
        HpFinishedMsg msg = new HpFinishedMsg(gSrc, gDest, remoteClientId, true,
                UUID.nextUUID());
        try {
            ByteBuf buffer = msg.toByteArray();
            opCodeCorrect(buffer, msg);
            HpFinishedMsg res =
                    HpFinishedMsgFactory.Request.fromBuffer(buffer);
            compareNatMsgs(msg, res);
        } catch (MessageDecodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        } catch (MessageEncodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        }
    }

    @Test
    public void HpFeasibilityMsg() {
        HpConnectMsg.Request msg = new HpConnectMsg.Request(gSrc, gDest, remoteClientId,
                1, 1000, UUID.nextUUID());
        msg.setTimeoutId(UUID.nextUUID());
        try {
            ByteBuf buffer = msg.toByteArray();
            opCodeCorrect(buffer, msg);
            HpConnectMsg.Request res =
                    HpConnectMsgFactory.Request.fromBuffer(buffer);
            compareNatMsgs(msg, res);
        } catch (MessageDecodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        } catch (MessageEncodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        }

        HpConnectMsg.Response response = new HpConnectMsg.Response(gDest, gSrc, remoteClientId,
                OpenConnectionResponseType.NAT_COMBINATION_NOT_TRAVERSABLE,
                UUID.nextUUID(), HPMechanism.PRP_PRC, false, UUID.nextUUID());
        try {
            ByteBuf buffer = response.toByteArray();
            opCodeCorrect(buffer, response);
            HpConnectMsg.Response res2 =
                    HpConnectMsgFactory.Response.fromBuffer(buffer);
            compareNatMsgs(response, res2);
        } catch (MessageDecodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        } catch (MessageEncodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        }
    }

    @Test
    public void HpRegisterMsg() {
        HpRegisterMsg.Request msg = new HpRegisterMsg.Request(gSrc, gDest, 100l);
        msg.setTimeoutId(UUID.nextUUID());
        try {
            ByteBuf buffer = msg.toByteArray();
            opCodeCorrect(buffer, msg);
            HpRegisterMsg.Request res = HpRegisterMsgFactory.Request.fromBuffer(buffer);
            compareNatMsgs(msg, res);
        } catch (MessageDecodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        } catch (MessageEncodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        }

        Set<Integer> ports = new HashSet<Integer>();
        ports.add(12121);
        ports.add(2222);
        HpRegisterMsg.Response resp = new HpRegisterMsg.Response(gSrc, gDest,
                HpRegisterMsg.RegisterStatus.REJECT, UUID.nextUUID(), ports);
        try {
            ByteBuf buffer = resp.toByteArray();
            opCodeCorrect(buffer, resp);
            HpRegisterMsg.Response res2 = HpRegisterMsgFactory.Response.fromBuffer(buffer);
            compareNatMsgs(resp, res2);
        } catch (MessageDecodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        } catch (MessageEncodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        }

        Set<Integer> ports2 = new HashSet<Integer>();
        HpRegisterMsg.Response resp2 = new HpRegisterMsg.Response(gSrc, gDest,
                HpRegisterMsg.RegisterStatus.ACCEPT, UUID.nextUUID(), ports2);
        try {
            ByteBuf buffer = resp2.toByteArray();
            opCodeCorrect(buffer, resp2);
            HpRegisterMsg.Response res3 = HpRegisterMsgFactory.Response.fromBuffer(buffer);
            compareNatMsgs(resp2, res3);
        } catch (MessageDecodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        } catch (MessageEncodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        }

    }

    @Test
    public void HpUnregisterMsg() {
        HpUnregisterMsg.Request msg = new HpUnregisterMsg.Request(gSrc, gDest, 10000,
                HpRegisterMsg.RegisterStatus.BETTER_PARENT);
        msg.setTimeoutId(UUID.nextUUID());
        try {
            ByteBuf buffer = msg.toByteArray();
            opCodeCorrect(buffer, msg);
            HpUnregisterMsg.Request res = HpUnregisterMsgFactory.Request.fromBuffer(buffer);
            compareNatMsgs(msg, res);
            assert(msg.getDelay() == res.getDelay());
            assert(msg.getStatus() == res.getStatus());
        } catch (MessageDecodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        } catch (MessageEncodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        }

        HpUnregisterMsg.Response resp = new HpUnregisterMsg.Response(gSrc, gDest,
                HpUnregisterMsg.Response.Status.NOT_REGISTERED, UUID.nextUUID());
        try {
            ByteBuf buffer = resp.toByteArray();
            opCodeCorrect(buffer, resp);
            HpUnregisterMsg.Response res2 = HpUnregisterMsgFactory.Response.fromBuffer(buffer);
            compareNatMsgs(resp, res2);
        } catch (MessageDecodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        } catch (MessageEncodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        }
    }

    @Test
    public void Interleaved_PRC_OpenHoleMsg() {
        Interleaved_PRC_OpenHoleMsg.Request msg =
                new Interleaved_PRC_OpenHoleMsg.Request(gSrc, gDest, remoteClientId, UUID.nextUUID());
        msg.setTimeoutId(UUID.nextUUID());
        try {
            ByteBuf buffer = msg.toByteArray();
            opCodeCorrect(buffer, msg);
            Interleaved_PRC_OpenHoleMsg.Request res = Interleaved_PRC_OpenHoleMsgFactory.Request.fromBuffer(buffer);
            compareNatMsgs(msg, res);
        } catch (MessageDecodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        } catch (MessageEncodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        }

        Interleaved_PRC_OpenHoleMsg.Response resp = new Interleaved_PRC_OpenHoleMsg.Response(gSrc, gDest,
                UUID.nextUUID(), Interleaved_PRC_OpenHoleMsg.ResponseType.OK, remoteClientId,
                UUID.nextUUID());
        try {
            ByteBuf buffer = resp.toByteArray();
            opCodeCorrect(buffer, resp);
            Interleaved_PRC_OpenHoleMsg.Response res2 = Interleaved_PRC_OpenHoleMsgFactory.Response.fromBuffer(buffer);
            compareNatMsgs(resp, res2);
        } catch (MessageDecodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        } catch (MessageEncodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        }
    }

    @Test
    public void Interleaved_PRC_ServersRequestForPredictionMsg() {
        Interleaved_PRC_ServersRequestForPredictionMsg.Request msg =
                new Interleaved_PRC_ServersRequestForPredictionMsg.Request(gSrc, gDest,
                remoteClientId, HPMechanism.PRP_PRP, HPRole.PRC_INITIATOR, gDest, UUID.nextUUID());
        try {
            ByteBuf buffer = msg.toByteArray();
            opCodeCorrect(buffer, msg);
            Interleaved_PRC_ServersRequestForPredictionMsg.Request res =
                    Interleaved_PRC_ServersRequestForPredictionMsgFactory.Request.fromBuffer(buffer);
            compareNatMsgs(msg, res);
        } catch (MessageDecodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        } catch (MessageEncodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        }

    }

    @Test
    public void Interleaved_PRP_SendAvailablePortsTozServerMsg() {
        Set<Integer> setPorts = new HashSet<Integer>();
        setPorts.add(1222);
        setPorts.add(65535);
        Interleaved_PRP_ConnectMsg.Request msg = new Interleaved_PRP_ConnectMsg.Request(gSrc, gDest, remoteClientId,
                setPorts, UUID.nextUUID());
        msg.setTimeoutId(UUID.nextUUID());
        try {
            ByteBuf buffer = msg.toByteArray();
            opCodeCorrect(buffer, msg);
            Interleaved_PRP_ConnectMsg.Request res = Interleaved_PRP_ConnectMsgFactory.Request.fromBuffer(buffer);
            compareNatMsgs(msg, res);
        } catch (MessageDecodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        } catch (MessageEncodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        }

        Interleaved_PRP_ConnectMsg.Response resp = new Interleaved_PRP_ConnectMsg.Response(gSrc, gDest,
                UUID.nextUUID(), Interleaved_PRP_ConnectMsg.ResponseType.OK, remoteClientId,
                UUID.nextUUID());
        try {
            ByteBuf buffer = resp.toByteArray();
            opCodeCorrect(buffer, resp);
            Interleaved_PRP_ConnectMsg.Response res2 = Interleaved_PRP_ConnectMsgFactory.Response.fromBuffer(buffer);
            compareNatMsgs(resp, res2);
        } catch (MessageDecodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        } catch (MessageEncodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        }
    }

    @Test
    public void Interleaved_PRP_ServerRequestForAvailablePortsMsg() {
        Interleaved_PRP_ServerRequestForAvailablePortsMsg.Request msg = new Interleaved_PRP_ServerRequestForAvailablePortsMsg.Request(
                gSrc, gDest, remoteClientId,
                HPMechanism.PRP_PRP, HPRole.PRC_INITIATOR, UUID.nextUUID());
        try {
            ByteBuf buffer = msg.toByteArray();
            opCodeCorrect(buffer, msg);
            Interleaved_PRP_ServerRequestForAvailablePortsMsg.Request res =
                    Interleaved_PRP_ServerRequestForAvailablePortsMsgFactory.Request.fromBuffer(buffer);
            compareNatMsgs(msg, res);
        } catch (MessageDecodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        } catch (MessageEncodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        }
    }

    @Test
    public void PRC_OpenHoleMsg() {
        PRC_OpenHoleMsg.Request msg = new PRC_OpenHoleMsg.Request(gSrc, gDest,
                remoteClientId, UUID.nextUUID());
        msg.setTimeoutId(UUID.nextUUID());
        try {
            ByteBuf buffer = msg.toByteArray();
            opCodeCorrect(buffer, msg);
            PRC_OpenHoleMsg.Request res = PRC_OpenHoleMsgFactory.Request.fromBuffer(buffer);
            compareNatMsgs(msg, res);
        } catch (MessageDecodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        } catch (MessageEncodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        }

        PRC_OpenHoleMsg.Response resp = new PRC_OpenHoleMsg.Response(gSrc, gDest,
                UUID.nextUUID(), PRC_OpenHoleMsg.ResponseType.OK, remoteClientId,
                UUID.nextUUID());
        try {
            ByteBuf buffer = resp.toByteArray();
            opCodeCorrect(buffer, resp);
            PRC_OpenHoleMsg.Response res2 = PRC_OpenHoleMsgFactory.Response.fromBuffer(buffer);
            compareNatMsgs(resp, res2);
        } catch (MessageDecodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        } catch (MessageEncodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        }
    }

    @Test
    public void PRC_ServerRequestForConsecutiveMsg() {
        PRC_ServerRequestForConsecutiveMsg.Request msg =
                new PRC_ServerRequestForConsecutiveMsg.Request(gSrc, gDest, remoteClientId,
                HPMechanism.PRP_PRP, HPRole.PRC_INITIATOR, gSrc, UUID.nextUUID());
        try {
            ByteBuf buffer = msg.toByteArray();
            opCodeCorrect(buffer, msg);
            PRC_ServerRequestForConsecutiveMsg.Request res =
                    PRC_ServerRequestForConsecutiveMsgFactory.Request.fromBuffer(buffer);
            compareNatMsgs(msg, res);
        } catch (MessageDecodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        } catch (MessageEncodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        }
    }

    @Test
    public void PRP_SendAvailablePortsTozServerMsg() {
        Set<Integer> setPorts = new HashSet<Integer>();
        setPorts.add(1222);
        setPorts.add(65535);
        PRP_ConnectMsg.Request msg =
                new PRP_ConnectMsg.Request(gSrc, gDest, remoteClientId,
                setPorts, UUID.nextUUID());
        msg.setTimeoutId(UUID.nextUUID());
        try {
            ByteBuf buffer = msg.toByteArray();
            opCodeCorrect(buffer, msg);
            PRP_ConnectMsg.Request res = PRP_ConnectMsgFactory.Request.fromBuffer(buffer);
            compareNatMsgs(msg, res);
        } catch (MessageDecodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        } catch (MessageEncodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        }

        PRP_ConnectMsg.Response res =
                new PRP_ConnectMsg.Response(gSrc, gDest, UUID.nextUUID(),
                PRP_ConnectMsg.ResponseType.OK, remoteClientId,
                gDest, 1088, false, UUID.nextUUID());
        try {
            ByteBuf buffer = res.toByteArray();
            opCodeCorrect(buffer, res);
            PRP_ConnectMsg.Response res2 = PRP_ConnectMsgFactory.Response.fromBuffer(buffer);
            compareNatMsgs(res, res2);
        } catch (MessageDecodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        } catch (MessageEncodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        }
    }

    @Test
    public void PRP_ServerRequestForAvailablePortsMsg() {
        PRP_ServerRequestForAvailablePortsMsg.Request msg = new PRP_ServerRequestForAvailablePortsMsg.Request(gSrc, gDest, remoteClientId,
                HPMechanism.PRP_PRP, HPRole.PRC_INITIATOR, UUID.nextUUID());
        try {
            ByteBuf buffer = msg.toByteArray();
            opCodeCorrect(buffer, msg);
            PRP_ServerRequestForAvailablePortsMsg.Request res =
                    PRP_ServerRequestForAvailablePortsMsgFactory.Request.fromBuffer(buffer);
            compareNatMsgs(msg, res);
        } catch (MessageDecodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        } catch (MessageEncodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        }

    }

    @Test
    public void RelayRequestMsg() {
        // Have to set timeoutId of Request msgs
        ConnectMsg.Request req = new ConnectMsg.Request(gSrc, gSrc, utility, true, age);
        TimeoutId timeoutId = UUID.nextUUID();
        req.setTimeoutId(timeoutId);
        RelayRequestMsg.ClientToServer msg = new RelayRequestMsg.ClientToServer(gSrc, gDest, remoteClientId,
                req);
        msg.setTimeoutId(timeoutId);
        try {
            ByteBuf buffer = msg.toByteArray();
            opCodeCorrect(buffer, msg);
            RelayRequestMsg.ClientToServer res = RelayRequestMsgFactory.Request.fromBuffer(buffer);
            compareNatMsgs(msg, res);
        } catch (MessageDecodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        } catch (MessageEncodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        }

        RelayRequestMsg.ServerToClient msg2 = new RelayRequestMsg.ServerToClient(gSrc, gDest, remoteClientId,
                req);
        msg2.setTimeoutId(UUID.nextUUID());
        try {
            ByteBuf buffer = msg2.toByteArray();
            opCodeCorrect(buffer, msg2);
            RelayRequestMsg.ServerToClient res = RelayRequestMsgFactory.Response.fromBuffer(buffer);
            compareNatMsgs(msg2, res);
        } catch (MessageDecodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        } catch (MessageEncodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        }
    }

    @Test
    public void SHP_InitiateSimpleHolePunchingMsg() {
        SHP_InitiateSimpleHolePunchingMsg.Request msg = new SHP_InitiateSimpleHolePunchingMsg.Request(gSrc, gDest, remoteClientId,
                HPMechanism.PRP_PRP, HPRole.PRC_INITIATOR, UUID.nextUUID());
        try {
            ByteBuf buffer = msg.toByteArray();
            opCodeCorrect(buffer, msg);
            SHP_InitiateSimpleHolePunchingMsg.Request res =
                    SHP_InitiateSimpleHolePunchingMsgFactory.Request.fromBuffer(buffer);
            compareNatMsgs(msg, res);
        } catch (MessageDecodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        } catch (MessageEncodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        }

    }

    @Test
    public void SHP_OpenHoleMsg() {

        SHP_OpenHoleMsg.Initiator msg = new SHP_OpenHoleMsg.Initiator(gSrc, gDest, gDest,
                SHP_OpenHoleMsg.ResponseType.OK, UUID.nextUUID());
        try {
            ByteBuf buffer = msg.toByteArray();
            opCodeCorrect(buffer, msg);
            SHP_OpenHoleMsg.Initiator res = SHP_OpenHoleMsgFactory.Initiator.fromBuffer(buffer);
            compareNatMsgs(msg, res);
        } catch (MessageDecodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        } catch (MessageEncodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        }
    }

    @Test
    public void keepAliveMsgPing() {
        ParentKeepAliveMsg.Ping msg = new ParentKeepAliveMsg.Ping(gSrc, gDest);
        msg.setTimeoutId(UUID.nextUUID());
        try {
            ByteBuf buffer = msg.toByteArray();
            opCodeCorrect(buffer, msg);
            ParentKeepAliveMsg.Ping res =
                    ParentKeepAliveMsgFactory.Request.fromBuffer(buffer);
            compareNatMsgs(msg, res);
        } catch (MessageDecodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        } catch (MessageEncodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        }

    }

    @Test
    public void keepAliveMsgPong() {
        ParentKeepAliveMsg.Pong msg = new ParentKeepAliveMsg.Pong(gSrc, gDest, UUID.nextUUID());
        try {
            ByteBuf buffer = msg.toByteArray();
            opCodeCorrect(buffer, msg);
            ParentKeepAliveMsg.Pong res =
                    ParentKeepAliveMsgFactory.Response.fromBuffer(buffer);
            compareNatMsgs(msg, res);
        } catch (MessageDecodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        } catch (MessageEncodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        }

    }

    @Test
    public void hpKeepAliveMsg() {
        HpKeepAliveMsg.Ping msg = new HpKeepAliveMsg.Ping(gSrc, gDest);
        msg.setTimeoutId(UUID.nextUUID());
        try {
            ByteBuf buffer = msg.toByteArray();
            opCodeCorrect(buffer, msg);
            HpKeepAliveMsg.Ping res =
                    HpKeepAliveMsgFactory.Request.fromBuffer(buffer);
            compareNatMsgs(msg, res);
        } catch (MessageDecodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        } catch (MessageEncodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        }

    }

    @Test
    public void hpKeepAliveMsgPong() {
        HpKeepAliveMsg.Pong msg = new HpKeepAliveMsg.Pong(gSrc, gDest, UUID.nextUUID());
        try {
            ByteBuf buffer = msg.toByteArray();
            opCodeCorrect(buffer, msg);
            HpKeepAliveMsg.Pong res =
                    HpKeepAliveMsgFactory.Response.fromBuffer(buffer);
            compareNatMsgs(msg, res);
        } catch (MessageDecodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        } catch (MessageEncodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        }

    }

    @Test
    public void echoMsgRequest() {
        EchoMsg.Request msg = new EchoMsg.Request(gSrc, gDest, EchoMsg.Test.UDP_BLOCKED, remoteClientId);
        msg.setTimeoutId(UUID.nextUUID());
        try {
            ByteBuf buffer = msg.toByteArray();
            opCodeCorrect(buffer, msg);
            EchoMsg.Request res =
                    EchoMsgFactory.Request.fromBuffer(buffer);
            compareNatMsgs(msg, res);
        } catch (MessageDecodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        } catch (MessageEncodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        }
    }

    @Test
    public void echoMsgResponse() {
        Set<Address> partners = new HashSet<Address>();
        partners.add(dest);
        EchoMsg.Response msg = new EchoMsg.Response(gSrc, gDest, partners, 100,
                EchoMsg.Test.UDP_BLOCKED, 1234, UUID.nextUUID(), 1234);
        try {
            ByteBuf buffer = msg.toByteArray();
            opCodeCorrect(buffer, msg);
            EchoMsg.Request res =
                    EchoMsgFactory.Request.fromBuffer(buffer);
            compareNatMsgs(msg, res);
        } catch (MessageDecodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        } catch (MessageEncodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        }
    }

    @Test
    public void echoChangeIpRequest() {
        EchoChangeIpAndPortMsg.Request msg = new EchoChangeIpAndPortMsg.Request(
                gSrc, gDest, 100);
        msg.setTimeoutId(UUID.nextUUID());
        try {
            ByteBuf buffer = msg.toByteArray();
            opCodeCorrect(buffer, msg);
            EchoChangeIpAndPortMsg.Request res =
                    EchoChangeIpAndPortMsgFactory.Request.fromBuffer(buffer);
            compareNatMsgs(msg, res);
        } catch (MessageDecodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        } catch (MessageEncodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        }
    }

    @Test
    public void echoChangeIpResponse() {
        EchoChangeIpAndPortMsg.Response msg = new EchoChangeIpAndPortMsg.Response(
                gSrc, gDest, gDest.getPeerAddress(), 100, UUID.nextUUID());
        try {
            ByteBuf buffer = msg.toByteArray();
            opCodeCorrect(buffer, msg);
            EchoChangeIpAndPortMsg.Response res =
                    EchoChangeIpAndPortMsgFactory.Response.fromBuffer(buffer);
            compareNatMsgs(msg, res);
        } catch (MessageDecodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        } catch (MessageEncodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        }
    }

    @Test
    public void echoChangePortRequest() {
        EchoChangePortMsg.Request msg = new EchoChangePortMsg.Request(
                gSrc, gDest, 100);
        msg.setTimeoutId(UUID.nextUUID());
        try {
            ByteBuf buffer = msg.toByteArray();
            opCodeCorrect(buffer, msg);
            EchoChangePortMsg.Request res =
                    EchoChangePortMsgFactory.Request.fromBuffer(buffer);
            compareNatMsgs(msg, res);
        } catch (MessageDecodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        } catch (MessageEncodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        }
    }

    @Test
    public void echoChangePortResponse() {
        EchoChangePortMsg.Response msg = new EchoChangePortMsg.Response(
                gSrc, gDest, 100, UUID.nextUUID());
        try {
            ByteBuf buffer = msg.toByteArray();
            opCodeCorrect(buffer, msg);
            EchoChangePortMsg.Response res =
                    EchoChangePortMsgFactory.Response.fromBuffer(buffer);
            compareNatMsgs(msg, res);
        } catch (MessageDecodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        } catch (MessageEncodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        }
    }

    @Test
    public void echoServerChangeRequest() {
        ServerHostChangeMsg.Request msg = new ServerHostChangeMsg.Request(
                gSrc, gDest, dest, 100, UUID.nextUUID());
        msg.setTimeoutId(UUID.nextUUID());
        try {
            ByteBuf buffer = msg.toByteArray();
            opCodeCorrect(buffer, msg);
            ServerHostChangeMsg.Request res =
                    ServerHostChangeMsgFactory.Request.fromBuffer(buffer);
            compareNatMsgs(msg, res);
        } catch (MessageDecodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        } catch (MessageEncodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        }
    }

    @Test
    public void echoServerChangeResponse() {
        ServerHostChangeMsg.Response msg = new ServerHostChangeMsg.Response(
                gSrc, gDest, 100, UUID.nextUUID());
        try {
            ByteBuf buffer = msg.toByteArray();
            opCodeCorrect(buffer, msg);
            ServerHostChangeMsg.Response res =
                    ServerHostChangeMsgFactory.Response.fromBuffer(buffer);
            compareNatMsgs(msg, res);
        } catch (MessageDecodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        } catch (MessageEncodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        }
    }

    @Test
    public void prpPreAllocatedPortsRequest() {
        PRP_PreallocatedPortsMsg.Request msg = new PRP_PreallocatedPortsMsg.Request(
                gSrc, gDest, UUID.nextUUID());
        msg.setTimeoutId(UUID.nextUUID());
        try {
            ByteBuf buffer = msg.toByteArray();
            opCodeCorrect(buffer, msg);
            PRP_PreallocatedPortsMsg.Request res =
                    PRP_PreallocatedPortsMsgFactory.Request.fromBuffer(buffer);
            compareNatMsgs(msg, res);
        } catch (MessageDecodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        } catch (MessageEncodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        }
    }

    @Test
    public void prpPreAllocatedPortsResponse() {
        Set<Integer> prpPorts = new HashSet<Integer>();
        prpPorts.add(11212);
        prpPorts.add(50505);
        PRP_PreallocatedPortsMsg.Response msg = new PRP_PreallocatedPortsMsg.Response(
                gSrc, gDest, UUID.nextUUID(),
                PRP_PreallocatedPortsMsg.ResponseType.NO_PORTS_AVAILABLE,
                prpPorts, UUID.nextUUID());
        try {
            ByteBuf buffer = msg.toByteArray();
            opCodeCorrect(buffer, msg);
            PRP_PreallocatedPortsMsg.Response res =
                    PRP_PreallocatedPortsMsgFactory.Response.fromBuffer(buffer);
            compareNatMsgs(msg, res);
        } catch (MessageDecodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        } catch (MessageEncodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        }
    }

    @Test
    public void stunReportRequest() {
        String report = "bbbbbbbbbbbbbbbbbbb";
        ReportMsg.Request msg = new ReportMsg.Request(gSrc, gDest, UUID.nextUUID(), report);
        try {
            ByteBuf buffer = msg.toByteArray();
            opCodeCorrect(buffer, msg);
            ReportMsg.Request request =
                    ReportMsgFactory.Request.fromBuffer(buffer);
            assert (report.equals(request.getReport()));
            compareNatMsgs(msg, request);
        } catch (MessageDecodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        } catch (MessageEncodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        }
    }

    @Test
    public void stunReportResponse() {
        TimeoutId id = UUID.nextUUID();
        ReportMsg.Response msg = new ReportMsg.Response(gSrc, gDest, id);
        try {
            ByteBuf buffer = msg.toByteArray();
            opCodeCorrect(buffer, msg);
            ReportMsg.Response response =
                    ReportMsgFactory.Response.fromBuffer(buffer);
            compareNatMsgs(msg, response);
        } catch (MessageDecodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        } catch (MessageEncodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        }
    }

    @Test
    public void tConnectionMsgPing() {
        TimeoutId id = UUID.nextUUID();
        TConnectionMsg.Ping msg = new TConnectionMsg.Ping(gSrc, gDest, id);
        try {
            ByteBuf buffer = msg.toByteArray();
            opCodeCorrect(buffer, msg);
            TConnectionMsg.Ping ping =
                    TConnectionMsgFactory.Ping.fromBuffer(buffer);
            compareNatMsgs(msg, ping);
        } catch (MessageDecodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        } catch (MessageEncodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        }
    }

    @Test
    public void tConnectionMsgPong() {
        TimeoutId id = UUID.nextUUID();
        TConnectionMsg.Pong msg = new TConnectionMsg.Pong(gSrc, gDest, id);
        try {
            ByteBuf buffer = msg.toByteArray();
            opCodeCorrect(buffer, msg);
            TConnectionMsg.Pong pong =
                    TConnectionMsgFactory.Pong.fromBuffer(buffer);
            compareNatMsgs(msg, pong);
        } catch (MessageDecodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        } catch (MessageEncodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        }
    }
    
    @Test
    public void tConnectionMsgPang() {
        TimeoutId id = UUID.nextUUID();
        TConnectionMsg.Pang msg = new TConnectionMsg.Pang(gSrc, gDest, id);
        try {
            ByteBuf buffer = msg.toByteArray();
            opCodeCorrect(buffer, msg);
            TConnectionMsg.Pang pang =
                    TConnectionMsgFactory.Pang.fromBuffer(buffer);
            compareNatMsgs(msg, pang);
            assert(msg.getMsgTimeoutId().equals(id));
        } catch (MessageDecodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        } catch (MessageEncodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        }
    }

    @Test
    public void natReportMsgTest() {
        List<NatReportMsg.NatReport> natReports = new ArrayList<NatReportMsg.NatReport>();
        natReports.add(new NatReportMsg.NatReport(1234, gDest, true, 11111, "hi there"));
        natReports.add(new NatReportMsg.NatReport(12346,gDest, true, 0,"hi there awtastas asdfsdf"));
        NatReportMsg msg = new NatReportMsg(gSrc, gSrc, natReports);
        try {
            ByteBuf buffer = msg.toByteArray();
            opCodeCorrect(buffer, msg);
            NatReportMsg res =
                    NatReportMsgFactory.fromBuffer(buffer);
            compareNatMsgs(msg, res);
            assert(msg.getNatReports().size() == res.getNatReports().size());
        } catch (MessageDecodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        } catch (MessageEncodingException ex) {
            Logger.getLogger(EncodingDecodingTest.class.getName()).log(Level.SEVERE, null, ex);
            assert (false);
        }
    }
    
    
    
    
    private void compareNatMsgs(NatMsg a, NatMsg b) {
        if (a instanceof DirectMsgNetty.Oneway == false &&
                a instanceof RelayMsgNetty.Oneway == false &&
                a instanceof DirectMsgNetty.SystemOneway == false) {
            assert (a.getTimeoutId().equals(b.getTimeoutId()));
        }
        assert (a.getVodSource().equals(b.getVodSource()));
        assert (a.getVodDestination().equals(b.getVodDestination()));
        // Note, we don't compare Address objects, as they have                                                                                                          
        // their ip and port set by the NettyHandler object.                                                                                                             
        // ip is null and port is 0 after the factory deserializes                                                                                                       
        // objects. It is up to NettyHandler to set ip and port.                                                                                                         
    }

}
