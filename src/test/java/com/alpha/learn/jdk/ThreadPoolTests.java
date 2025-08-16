package com.alpha.learn.jdk;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

@Slf4j
public class ThreadPoolTests {

    @Test
    public void testThreadPoolExecutor() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        try (ThreadPoolExecutor pool = new ThreadPoolExecutor(2, 3, 10_000, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10))) {
            pool.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
            pool.prestartAllCoreThreads();
            pool.execute(() -> {
                for (int i = 0; i < 10; i++) {
                    log.info("pool running...");
                    try {
                        countDownLatch.await();
                    } catch (InterruptedException ignore) {}
                }
            });

            Thread.sleep(2000);

            Thread t1 = new Thread(() -> {
                pool.setCorePoolSize(3);
                pool.setMaximumPoolSize(5);
                pool.setKeepAliveTime(50_000, TimeUnit.SECONDS);
            });

            t1.start();
            t1.join();
            countDownLatch.countDown();

            log.info("pool core pool size: {}", pool.getCorePoolSize());

            pool.shutdown();
            while (pool.awaitTermination(10, TimeUnit.SECONDS)) {
                pool.shutdownNow();
            }
        }
    }

    @Test
    public void testExecutorService() {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        executor.execute(() -> {log.info("running...");});
    }

    @Test
    public void testScheduled() {
        try (ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(2, new ScheduledThreadFactoryTest(false));) {
            scheduledThreadPoolExecutor.prestartAllCoreThreads();
            scheduledThreadPoolExecutor.execute(() -> {
                log.info("Scheduled pool running...");
            });
        }
    }

    static class  ScheduledThreadFactoryTest implements ThreadFactory {
        private static final AtomicIntegerFieldUpdater<ScheduledThreadFactoryTest> THREAD_NUMBER_UPDATER = AtomicIntegerFieldUpdater.newUpdater(ScheduledThreadFactoryTest.class, "threadNumber");
        private final ThreadGroup group;
        private final String namePrefix;
        private static final AtomicInteger POOL_NUMBER = new AtomicInteger(0);
        private volatile int threadNumber;
        private final boolean isVirtual;

        public ScheduledThreadFactoryTest(boolean virtual) {
            this.group = Thread.currentThread().getThreadGroup();
            this.namePrefix = "pool-" + POOL_NUMBER.getAndIncrement() +"-thread-";
            this.isVirtual = virtual;
        }

        @Override
        public Thread newThread(@NonNull Runnable r) {
            String name = namePrefix + THREAD_NUMBER_UPDATER.getAndIncrement(this);
            Thread t;
            if (isVirtual) {
                t = Thread.ofVirtual().name(name).unstarted(r);
            } else {
                t = new Thread(group, r, name, 0);
                if (t.getPriority() != Thread.NORM_PRIORITY) {
                    t.setPriority(Thread.NORM_PRIORITY);
                }
                if (t.isDaemon()) {
                    t.setDaemon(false);
                }
            }
            return t;
        }
    }
}
