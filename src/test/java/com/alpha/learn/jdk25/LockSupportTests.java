package com.alpha.learn.jdk25;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.concurrent.locks.LockSupport;

// Unsafe and not AbstractQueuedSynchronizer type
@Slf4j
public class LockSupportTests {

    @Test
    public void test1() throws InterruptedException {
        Thread thread = new Thread(() -> {
            log.info("start...");
            LockSupport.park();
            log.info("end...");
        });

        thread.start();
        LockSupport.unpark(thread);
        thread.join();
    }

    @Test
    public void test2() throws InterruptedException {
        Thread thread = new Thread(() -> {
            log.info("start 2...");
            LockSupport.park();
            log.info("end 2...");
        });

        thread.start();
        thread.interrupt();
        thread.join();
    }

    @Test
    public void test3() throws InterruptedException {
        WhyPark whyPark = new WhyPark("test park blocker");
        Thread thread = new Thread(() -> {
            LockSupport.park(whyPark);
        });
        thread.start();
        Thread.sleep(1000);
        log.info("why park: {}", LockSupport.getBlocker(thread));
        LockSupport.unpark(thread);
    }

    @Test
    public void test4() throws InterruptedException {
        Thread.Builder.OfVirtual virtual = Thread.ofVirtual();
        WhyPark whyPark = new WhyPark("test park blocker");
        Thread t1 = virtual.unstarted(() -> {
            LockSupport.park(whyPark);
        });
        t1.start();
        Thread.sleep(1000);
        log.info("why t1 park: {}", LockSupport.getBlocker(t1));
        LockSupport.unpark(t1);
    }

    static record WhyPark(String why) {

        @Override
        @NonNull
        public String toString() {
            return why;
        }
    }
}
