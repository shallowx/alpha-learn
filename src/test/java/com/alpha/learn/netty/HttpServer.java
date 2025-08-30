package com.alpha.learn.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.compression.CompressionOptions;
import io.netty.handler.codec.http.*;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

@Slf4j
public class HttpServer {
    public static void main(String[] args) {
        EventLoopGroup group = new MultiThreadIoEventLoopGroup(NioIoHandler.newFactory());
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.option(ChannelOption.SO_BACKLOG, 1024);
            b.group(group)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.DEBUG))
                    .childHandler(new HttpServerInitializer());
            ChannelFuture future = b.bind(8000).sync();
            future.addListener(new GenericFutureListener<Future<? super Void>>() {
                @Override
                public void operationComplete(Future<? super Void> future) throws Exception {
                    if (future.isSuccess()) {
                        log.info(("Open your web browser and navigate to http://127.0.0.1:8000/"));
                    } else {
                        log.error("Http Server not started...");
                    }
                }
            });
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            group.shutdownGracefully();
        }
    }

    static class HttpServerInitializer extends ChannelInitializer<SocketChannel> {

        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
            ChannelPipeline p = ch.pipeline();
            p.addLast(new HttpServerCodec());
            p.addLast(new HttpObjectAggregator(10 * 1024 * 1024));
            p.addLast(new HttpContentCompressor((CompressionOptions[]) null));
            p.addLast(new HttpServerExpectContinueHandler());
            p.addLast(new HttpServerHandler());
        }
    }

    static class HttpServerHandler extends ChannelInboundHandlerAdapter {

        private static final byte[] defaultRep;
        static {
            String content = """
                    {
                      "code": 200,
                      "success": true,
                      "data": [
                        {
                          "name": "tom",
                          "age": 18
                        },
                        {
                          "name": "jimmy",
                          "age": 18
                        }
                      ]
                    }
                    """;
            defaultRep = content.getBytes(StandardCharsets.UTF_8);
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            if (msg instanceof FullHttpRequest req) {
                String body = req.content().toString(Charset.defaultCharset());
                log.info("Request body is {}", body);
                FullHttpResponse response = new DefaultFullHttpResponse(req.protocolVersion(), OK,
                        Unpooled.wrappedBuffer(defaultRep));
                response.headers()
                        .set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
                        .setInt(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
                if (HttpUtil.isKeepAlive(req)) {
                    if (!req.protocolVersion().isKeepAliveDefault())
                        response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
                }  else {
                    response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
                }
                ChannelFuture f = ctx.write(response);
                f.addListener(new GenericFutureListener<Future<? super Void>>() {
                    @Override
                    public void operationComplete(Future<? super Void> f) throws Exception {
                        if (f.isSuccess()) {
                            log.info("Http request answer success");
                        }
                    }
                });
            } else {
                ctx.fireChannelRead(msg);
            }
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            ctx.flush();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
            ctx.close();
        }
    }
}
