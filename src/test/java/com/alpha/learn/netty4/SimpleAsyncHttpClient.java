package com.alpha.learn.netty4;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.concurrent.CountDownLatch;

@SuppressWarnings("ALL")
@Slf4j
public class SimpleAsyncHttpClient {

    private final String host;
    private final int port;
    private final EventLoopGroup group = new MultiThreadIoEventLoopGroup(NioIoHandler.newFactory());
    private Channel channel;
    private static final AttributeKey<Promise<FullHttpResponse>> RESPONSE_PROMISE_KEY = AttributeKey.valueOf("responsePromise");

    public SimpleAsyncHttpClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void init() throws InterruptedException {
        Bootstrap b = new Bootstrap();
        b.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .localAddress(new InetSocketAddress(0))
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) {
                        ChannelPipeline p = ch.pipeline();
                        p.addLast(new HttpClientCodec());
                        p.addLast(new HttpObjectAggregator(10 * 1024 * 1024));
                        p.addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                if (msg instanceof FullHttpResponse response) {
                                    Promise<FullHttpResponse> promise = ctx.channel().attr(RESPONSE_PROMISE_KEY).getAndSet(null);
                                    if (promise != null) {
                                        promise.setSuccess(response.retain());
                                    }
                                }
                            }

                            @Override
                            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
                                Promise<FullHttpResponse> promise = ctx.channel().attr(RESPONSE_PROMISE_KEY).getAndSet(null);
                                if (promise != null) {
                                    promise.setFailure(cause);
                                }
                                ctx.close();
                            }
                        });
                    }
                });

        channel = b.connect(host, port).sync().channel();
    }

    public Promise<FullHttpResponse> sendRequest(String path) {
        Promise<FullHttpResponse> promise = channel.eventLoop().newPromise();
        FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, path);
        request.headers().set(HttpHeaderNames.HOST, host);
        request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        channel.attr(RESPONSE_PROMISE_KEY).set(promise);
        ChannelFuture writeFuture = channel.writeAndFlush(request);
        writeFuture.addListener(f -> {
            if (!f.isSuccess()) {
                promise.setFailure(f.cause());
            }
        });
        return promise;
    }

    public void close() {
        if (channel != null) channel.close();
        group.shutdownGracefully();
    }


    public static void main(String[] args) throws Exception {
        SimpleAsyncHttpClient client = new SimpleAsyncHttpClient("127.0.0.1", 8080);
        client.init();

        Promise<FullHttpResponse> responsePromise = client.sendRequest("/mock/123");
        CountDownLatch latch = new CountDownLatch(1);
        responsePromise.addListener(f -> {
            if (f.isSuccess()) {
                FullHttpResponse response = (FullHttpResponse) f.get();
                log.info("response: {}", response.content().toString(Charset.defaultCharset()));
                response.release();
                latch.countDown();
            } else {
                log.error("ERROR: ", f.cause());
                latch.countDown();
            }
        });
        latch.await();
        log.info("Request complete and waitting for shutdown gracefully");
        client.close();
    }
}
