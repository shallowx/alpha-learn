package com.alpha.learn.jdk25;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

@Slf4j
public class AtomicTests {

    @Test
    public void test() throws InterruptedException {
        TestAtomicUpdater testAtomicUpdater = new TestAtomicUpdater();
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                testAtomicUpdater.increment();
            }
        });

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                testAtomicUpdater.increment();
            }
        });
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        Assertions.assertEquals(10, testAtomicUpdater.counter);
    }

    static class TestAtomicUpdater {
        private static final AtomicIntegerFieldUpdater<TestAtomicUpdater> UPDATER = AtomicIntegerFieldUpdater.newUpdater(TestAtomicUpdater.class, "counter");
        private volatile int counter = 0;

        public void increment() {
            UPDATER.incrementAndGet(this);
        }

        @Override
        public String toString() {
            return "TestAtomicUpdater{" +
                    "counter=" + counter +
                    '}';
        }
    }
}
