package com.alpha.learn.netty4;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.junit.jupiter.api.Test;

import java.util.List;

public class PacketDecoderTests {

    @Test
    public void test() {
        PacketDecoder decoder = new PacketDecoder();
        decoder.setCumulator(ByteToMessageDecoder.COMPOSITE_CUMULATOR);
        decoder.setDiscardAfterReads(3); // discardAfterReads ? why not support set discardAfterComponents
    }

    // use {@code ChannelInboundHandlerAdapter} is direct and control
    static class PacketDecoder extends ByteToMessageDecoder {

        // List<Object> ？ Cumulative increase delay
        @Override
        protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        }
    }
}
