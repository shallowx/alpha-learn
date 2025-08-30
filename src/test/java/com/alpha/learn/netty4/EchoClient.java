package com.alpha.learn.netty4;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

@Slf4j
public class EchoClient {

    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup group = new MultiThreadIoEventLoopGroup(NioIoHandler.newFactory());
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new  ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new EchoClientHandler());
                        }
                    });
            ChannelFuture f = bootstrap.connect("127.0.0.1", 8000).sync();
            f.addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    log.info("Server connected to Echo server successfully...");
                } else  {
                    log.info("Server connected to Echo server failed...");
                }
            });
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }


   static class EchoClientHandler extends ChannelInboundHandlerAdapter {
        private final ByteBuf firstMessage;
        public EchoClientHandler() {
            firstMessage = Unpooled.buffer(256);
            for (int i = 0; i < firstMessage.capacity(); i ++) {
                firstMessage.writeByte((byte) i);
            }
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            ctx.writeAndFlush(firstMessage);
        }
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf buf = (ByteBuf) msg;
            log.info("Client received echo: {}", buf.toString(CharsetUtil.UTF_8));
            ByteBuf buffer = ctx.alloc().buffer();
            buffer.writeBytes("Hello Server".getBytes(StandardCharsets.UTF_8));
            ctx.write(buffer);
            buf.release();
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            ctx.flush();
        }
    }
}
