package com.alpha.learn.netty4;

import io.netty.channel.DefaultEventLoop;
import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.util.NettyRuntime;
import io.netty.util.concurrent.DefaultEventExecutor;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.EventExecutor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.concurrent.*;

@SuppressWarnings("ALL")
@Slf4j
public class EventLoopTests {
    @Test
    public void testPendingTasks() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        EventExecutor eventExecutor = new DefaultEventExecutor(new DefaultThreadFactory("eventExecutor"));
            try (EventLoop loop = new DefaultEventLoop(eventExecutor)) {
                if (loop.inEventLoop()) {
                    log.info("event loop start");
                    countDownLatch.countDown();
                } else {
                    loop.execute(() -> {
                        if (loop.inEventLoop()) {
                            log.info("event loop start");
                            countDownLatch.countDown();
                        }
                    });
                }
            }
        countDownLatch.await();
    }

    @Test
    public void test() throws InterruptedException {
        EmbeddedChannel channel = new EmbeddedChannel();
        EventLoop loop = channel.eventLoop();
        loop.scheduleWithFixedDelay(new Task(),10,1, TimeUnit.SECONDS);
    }

    @Test
    public void test0() throws InterruptedException {
        EventLoopGroup group = new DefaultEventLoopGroup(NettyRuntime.availableProcessors(), new DefaultThreadFactory("test"));
        EventLoop loop = group.next();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        loop.execute(() -> {
            log.info("event loop start");
            countDownLatch.countDown();
        });
        countDownLatch.await();

        EventExecutor eventExecutor = new DefaultEventExecutor(new DefaultThreadFactory("eventExecutor"));
        eventExecutor.execute(() -> {
            log.info("event executor start");
            countDownLatch.countDown();
        });
        countDownLatch.await();
    }

    static class Task implements Runnable {
        @Override
        public void run() {
            log.info("task start");
        }
    }
}
