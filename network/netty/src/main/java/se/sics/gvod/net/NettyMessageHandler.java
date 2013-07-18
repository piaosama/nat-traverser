package se.sics.gvod.net;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.sics.gvod.net.msgs.RewriteableMsg;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * @author Steffen Grohsschmiedt
 */
public class NettyMessageHandler extends NettyBaseHandler<DatagramPacket> {

    private static final Logger logger = LoggerFactory.getLogger(NettyMessageHandler.class);

    private final MsgFrameDecoder decoder;

    public NettyMessageHandler(NettyNetwork component, Transport protocol, Class<? extends MsgFrameDecoder> msgDecoderClass) {
        super(component, protocol);

        try {
            this.decoder = msgDecoderClass.newInstance();
        } catch (Exception e) {
            throw new Error(e.getMessage());
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
        RewriteableMsg rewrittenMsg = (RewriteableMsg) decoder.parse(msg.content());

        // session-less UDP means that remoteAddresses cannot be found in
        // the channel object, but only in the MessageEvent object.
        SocketAddress remoteAddress = msg.sender();

        if (remoteAddress instanceof InetSocketAddress) {
            updateAddress(rewrittenMsg, ctx, (InetSocketAddress) remoteAddress);
            getComponent().deliverMessage(rewrittenMsg);
        }
    }
}
