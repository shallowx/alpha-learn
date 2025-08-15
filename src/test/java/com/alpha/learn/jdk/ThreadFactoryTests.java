package com.alpha.learn.jdk;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

@Slf4j
public class ThreadFactoryTests {

    @Test
    public void test() throws InterruptedException {
        ThreadFactoryTest factoryTest = new ThreadFactoryTest(false);
        Thread t = factoryTest.newThread(() -> log.info("thread name: {}", Thread.currentThread().getName()));
        Thread t1= factoryTest.newThread(() -> log.info("thread1 name: {}", Thread.currentThread().getName()));

        ThreadFactoryTest factoryTes1 = new ThreadFactoryTest(true);
        Thread t2 = factoryTes1.newThread(() -> log.info("thread2 name: {}", Thread.currentThread().getName()));
        Thread t3 = factoryTes1.newThread(() -> log.info("thread3 name: {}", Thread.currentThread().getName()));


        t.start();
        t1.start();
        t.join();
        t1.join();

        t2.start();
        t3.start();
        t2.join();
        t3.join();
        log.info("thread(0-1) is virtual: {} {}", t.isVirtual(), t1.isVirtual());
        log.info("thread(2-3) is virtual: {} {}", t2.isVirtual(), t3.isVirtual());
    }

    static class ThreadFactoryTest implements ThreadFactory {
        private static final AtomicIntegerFieldUpdater<ThreadFactoryTest> THREAD_NUMBER_UPDATER = AtomicIntegerFieldUpdater.newUpdater(ThreadFactoryTest.class, "threadNumber");
        private final ThreadGroup group;
        private final String namePrefix;
        private static final AtomicInteger POOL_NUMBER = new AtomicInteger(0);
        private volatile int threadNumber;
        private final boolean isVirtual;

        public ThreadFactoryTest(boolean virtual) {
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
