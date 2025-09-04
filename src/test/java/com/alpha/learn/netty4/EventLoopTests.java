package com.alpha.learn.netty4;

import io.netty.channel.DefaultEventLoop;
import io.netty.channel.EventLoop;
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
}
