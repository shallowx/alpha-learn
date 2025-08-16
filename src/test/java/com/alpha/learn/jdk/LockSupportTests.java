package com.alpha.learn.jdk;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.concurrent.locks.LockSupport;

@Slf4j
public class LockSupportTests {

    @Test
    public void t1() throws InterruptedException {
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
    public void t2() throws InterruptedException {
        Thread thread = new Thread(() -> {
            log.info("start 2...");
            LockSupport.park();
            log.info("end 2...");
        });

        thread.start();
        thread.interrupt();
        thread.join();
    }
}
