package com.alpha.learn.jdk;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import java.util.concurrent.CountDownLatch;

@Slf4j
public class ObjectTests {

    private static final ObjectTests OBJECT_TESTS = new ObjectTests();

    @Test
    public void testGetClass() {
        ObjectTests obj = new ObjectTests();
        Class<? extends ObjectTests> clz = obj.getClass();
        log.info("getClass: {}, simple name: {}, type name: {}, name: {}", clz, clz.getSimpleName(), clz.getTypeName(), clz.getName());
    }

    @Test
    public void testHashCode() {
        ObjectTests obj = new ObjectTests();
        int hashCode = obj.hashCode();
        log.info("hashcode == : {}", System.identityHashCode(obj) == hashCode);
        log.info("hashcode: {}", System.identityHashCode(obj));
    }

    @Test
    public void testEquals() {
        ObjectTests obj = new ObjectTests();
        boolean equals = obj.equals(new Object());
        log.info("equals: {}", equals);
    }

    @Test
    public void testToString() {
        ObjectTests obj = new ObjectTests();
        log.info("toString: {}", obj.toString());
    }

    @Test
    public void testWait() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Thread t1 = Thread.ofVirtual().start(() -> {
            synchronized (OBJECT_TESTS) {
               countDownLatch.countDown();
                try {
                    OBJECT_TESTS.wait();
                    log.info("wait");
                } catch (InterruptedException e) {
                    log.error(e.getMessage());
                }
            }
        });

        Thread t2 = Thread.ofVirtual().start(() -> {
            try {
                countDownLatch.await();
            } catch (InterruptedException ignore) {}

            synchronized (OBJECT_TESTS) {
                OBJECT_TESTS.notifyAll();
                log.info("notify");
            }
        });

        t1.join();
        t2.join();
    }
}
