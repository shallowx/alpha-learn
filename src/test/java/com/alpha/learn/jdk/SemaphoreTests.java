package com.alpha.learn.jdk;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class SemaphoreTests {

    @Test
    public void test() throws InterruptedException {
        final int permits = 2;
        final Semaphore semaphore = new Semaphore(permits);
        final AtomicInteger concurrentCounter = new AtomicInteger(0);
        final AtomicInteger maxConcurrentSeen = new AtomicInteger(0);

        Runnable task = () -> {
            try {
                semaphore.acquire();
                int current = concurrentCounter.incrementAndGet();
                maxConcurrentSeen.updateAndGet(prev -> Math.max(prev, current));
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                concurrentCounter.decrementAndGet();
                semaphore.release();
            }
        };

        int threadCount = 5;
        Thread[] threads = new Thread[threadCount];
        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(task, "T" + i);
            threads[i].start();
        }

        for (Thread t : threads) {
            t.join();
        }

        log.info("maxConcurrentSeen: {}", maxConcurrentSeen.get());
    }
}
