package com.alpha.learn.netty4;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.NettyRuntime;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;
import java.net.InetSocketAddress;

@Slf4j
public class HandleChainTestServer {
    public static void main(String[] args){
        EventLoopGroup boosGroup = new MultiThreadIoEventLoopGroup(1, NioIoHandler.newFactory());
        EventLoopGroup workerGroup = new MultiThreadIoEventLoopGroup(NettyRuntime.availableProcessors(), NioIoHandler.newFactory());
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();

            WriteBufferWaterMark mark = new WriteBufferWaterMark(1024,1024*1024);

            bootstrap.option(ChannelOption.SO_BACKLOG, 1024)
                    .group(boosGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.TRACE))
                    .childOption(ChannelOption.WRITE_BUFFER_WATER_MARK, mark)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(new FirstChannelOutboundHandler());
                            p.addLast(new InboundForWriteTestingHandler());
                            p.addLast(new SecondChannelOutboundHandler());
                            p.addLast(new ThreeChannelOutboundHandler());
                        }
                    });
            ChannelFuture f = bootstrap.bind(new InetSocketAddress("127.0.0.1", 8888)).sync();
            f.addListener(new GenericFutureListener<Future<? super Void>>() {
                @Override
                public void operationComplete(Future<? super Void> future) throws Exception {
                    if (future.isSuccess()) {
                        log.info("Server started on port 8888");
                    } else  {
                        log.error("Server start failed");
                    }
                }
            });
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } finally {
            boosGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    static class InboundForWriteTestingHandler extends ChannelInboundHandlerAdapter {

        private final ByteBuf buf;
        public InboundForWriteTestingHandler() {
            buf = Unpooled.buffer(1024);
            buf.writeByte(1);
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            log.info("Server inbound channel active");
            ctx.writeAndFlush(buf); // ctx.channel().writeAndFlush(buf);
        }
    }

    static class FirstChannelOutboundHandler extends ChannelOutboundHandlerAdapter {
        @Override
        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
            log.info("First...");
            super.write(ctx, msg, promise);
        }
    }

    static class SecondChannelOutboundHandler extends ChannelOutboundHandlerAdapter {
        @Override
        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
            log.info("Second...");
            super.write(ctx, msg, promise);
        }
    }

    static class ThreeChannelOutboundHandler extends ChannelOutboundHandlerAdapter {
        @Override
        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
            log.info("Three...");
            super.write(ctx, msg, promise);
        }
    }
}
