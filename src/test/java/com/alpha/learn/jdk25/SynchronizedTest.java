package com.alpha.learn.jdk25;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import java.util.concurrent.TimeUnit;

@Slf4j
public class SynchronizedTest {

    @Test
    public void test() throws InterruptedException {
        SynchronizedTest synchronizedTest = new SynchronizedTest();
        Thread t1 = new Thread(synchronizedTest::print2);
        Thread t2 = new Thread(SynchronizedTest::print);

        t1.start();
        t2.start();
        TimeUnit.SECONDS.sleep(10);
    }

    @Test
    public void test1() throws InterruptedException {
        SynchronizedTest synchronizedTest = new SynchronizedTest();
        Thread t1 = new Thread(synchronizedTest::print2);
        Thread t2 = new Thread(synchronizedTest::print3);

        t1.start();
        t2.start();
        TimeUnit.SECONDS.sleep(10);
    }

    @Test
    public void test2() throws InterruptedException {
        Thread t1 = new Thread(SynchronizedTest::print);
        Thread t2 = new Thread(SynchronizedTest::print1);

        t1.start();
        t2.start();
        TimeUnit.SECONDS.sleep(10);
    }

    private synchronized void print2() {
        for (int i = 0; i < 10; i++) {
            log.info("print2");
        }
    }

    private synchronized void print3() {
        for (int i = 0; i < 10; i++) {
            log.info("print3");
        }
    }

    private static synchronized void print() {
        for (int i = 0; i < 10; i++) {
            log.info("print");
        }
    }

    private static synchronized void print1() {
        for (int i = 0; i < 10; i++) {
            log.info("print1");
        }
    }
}
