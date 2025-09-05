package com.alpha.learn.netty4;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.resolver.AddressResolver;
import io.netty.resolver.AddressResolverGroup;
import io.netty.resolver.NameResolver;
import io.netty.resolver.SimpleNameResolver;
import io.netty.resolver.dns.DnsNameResolver;
import io.netty.resolver.dns.DnsNameResolverBuilder;
import io.netty.util.NettyRuntime;
import io.netty.util.concurrent.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@Slf4j
@SuppressWarnings("ALL")
public class ServiceDiscoveryNameResolverTest {

    @Test
    public void test() throws InterruptedException {
        EventLoopGroup group = new MultiThreadIoEventLoopGroup(1, NioIoHandler.newFactory());
        CountDownLatch countDownLatch = new CountDownLatch(1);
        try {
            DnsNameResolver resolver = new DnsNameResolverBuilder(group.next())
                    .channelType(io.netty.channel.socket.nio.NioDatagramChannel.class)
                    .resolveCache(new io.netty.resolver.dns.DefaultDnsCache())
                    .cnameCache(new io.netty.resolver.dns.DefaultDnsCnameCache())
                    .queryTimeoutMillis(5000)
                    .build();

            Future<List<InetAddress>> future = resolver.resolveAll("google.com");
            future.addListener((GenericFutureListener<Future<? super List<InetAddress>>>) f -> {
                if (f.isSuccess()) {
                    List<InetAddress> addresses = (List<InetAddress>) f.getNow();
                    addresses.forEach(address -> System.out.println("Resolved IP: " + address));
                } else {
                    System.err.println("DNS resolution failed: " + f.cause());
                }
                countDownLatch.countDown();
                group.shutdownGracefully();
            });
        } catch (Exception e) {
            e.printStackTrace();
            group.shutdownGracefully();
        }
        countDownLatch.await();
    }

    public static void main(String[] args) throws Exception {
        EventLoopGroup group = new MultiThreadIoEventLoopGroup(NettyRuntime.availableProcessors(), NioIoHandler.newFactory());
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .resolver(new AddressResolverGroup<InetSocketAddress>() {
                        @Override
                        protected AddressResolver<InetSocketAddress> newResolver(EventExecutor eventExecutor) throws Exception {
                            return new ServiceAddressNameResolver(new ServiceDiscoveryNameResolver(new DefaultEventExecutor(),
                                    new DefaultServiceDiscoveryClient()));
                        }
                    })
                    .handler(new  ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new EchoClient.EchoClientHandler());
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

    // todo
    static class ServiceAddressNameResolver implements AddressResolver<InetSocketAddress> {

        private final NameResolver<InetSocketAddress> nameResolver;

        public ServiceAddressNameResolver(NameResolver<InetSocketAddress> nameResolver) {
            this.nameResolver = nameResolver;
        }

        @Override
        public boolean isSupported(SocketAddress socketAddress) {
            return true;
        }

        @Override
        public boolean isResolved(SocketAddress socketAddress) {
            return true;
        }

        @Override
        public Future<InetSocketAddress> resolve(SocketAddress socketAddress) {
            Promise<InetSocketAddress> promise = ImmediateEventExecutor.INSTANCE.newPromise();
            promise.setSuccess(InetSocketAddress.createUnresolved("127.0.0.1", 8000));
            return promise;
        }

        @Override
        public Future<InetSocketAddress> resolve(SocketAddress socketAddress, Promise<InetSocketAddress> promise) {
            promise.setSuccess(InetSocketAddress.createUnresolved("127.0.0.1", 8000));
            return promise;
        }

        @Override
        public Future<List<InetSocketAddress>> resolveAll(SocketAddress socketAddress) {
            Promise<List<InetSocketAddress>> promise = ImmediateEventExecutor.INSTANCE.newPromise();
            promise.setSuccess(Collections.singletonList(InetSocketAddress.createUnresolved("127.0.0.1", 8000)));
            return promise;
        }

        @Override
        public Future<List<InetSocketAddress>> resolveAll(SocketAddress socketAddress, Promise<List<InetSocketAddress>> promise) {
            promise.setSuccess(Collections.singletonList(InetSocketAddress.createUnresolved("127.0.0.1", 8000)));
            return promise;
        }

        @Override
        public void close() {

        }
    }

    static class ServiceDiscoveryNameResolver extends SimpleNameResolver<InetSocketAddress> {
        private final ServiceDiscoveryClient client;
        protected ServiceDiscoveryNameResolver(EventExecutor executor, ServiceDiscoveryClient client) {
            super(executor);
            this.client = client;
        }

        @Override
        protected void doResolve(String s, Promise promise) throws Exception {
            List<InetSocketAddress> addresses = client.resolve(s);
            if (addresses == null || addresses.isEmpty()) {
                promise.setFailure(new IllegalStateException("No instance found for service: " + s));
            } else {
                promise.setSuccess(addresses.get(0));
            }
        }

        @Override
        protected void doResolveAll(String s, Promise promise) throws Exception {
            List<InetSocketAddress> addresses = client.resolve(s);
            if (addresses == null || addresses.isEmpty()) {
                promise.setFailure(new IllegalStateException("No instance found for service: " + s));
            } else {
                promise.setSuccess(addresses);
            }
        }
    }

    static class DefaultServiceDiscoveryClient implements ServiceDiscoveryClient {
        @Override
        public List<InetSocketAddress> resolve(String domain) {
            return List.of(new  InetSocketAddress("127.0.0.1", 8889));
        }
    }

    interface ServiceDiscoveryClient {
        List<InetSocketAddress> resolve(String domain);
    }
}
