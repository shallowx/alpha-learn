package com.alpha.learn.jdk;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * Why are the thread communication methods wait(), notify(), and notifyAll() defined in the Object class, rather than in the Thread class?
 * Why must wait(), notify(), and notifyAll() be called within a synchronized method or synchronized block?
 * What are the difference between the wait() method and the sleep() method ?
 * Why is it usually necessary to also override the hashCode() method when overriding the equals() method?
 * </p>
 */
@SuppressWarnings("ALL")
@Slf4j
public class ObjectTests {

    private static final ObjectTests OBJECT_TESTS = new ObjectTests();

    @Test
    public void testGetClass() {
        ObjectTests obj = new ObjectTests();
        Class<? extends ObjectTests> clz = obj.getClass();
        log.info("getClass: {}, simple name: {}, type name: {}, name: {}", clz, clz.getSimpleName(), clz.getTypeName(), clz.getName());

        ObjectTests obj1 = new ObjectTests();
        Class<? extends ObjectTests> clz1 = obj1.getClass();
        // The returned Class object is the object that is locked by static synchronized methods of the represented class.
        log.info("the one class is equals to other one: {}", clz1.equals(clz));
    }

    @Test
    public void testHashCode() {
        ObjectTests obj = new ObjectTests();
        int hashCode = obj.hashCode();
        log.info("hashcode == : {}", System.identityHashCode(obj) == hashCode);
        log.info("identityHashCode: {}, override hashcode: {}", System.identityHashCode(obj), hashCode);
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

    private static final Object LOCK = new Object();
    private static boolean numberTurn = true;

    @Test
    public void testPrint() throws InterruptedException {
        List<Integer> numbers = Lists.newArrayList();
        Thread t1 = new Thread(() -> {
            for(;;) {
                synchronized (LOCK) {
                    while (!numberTurn) {
                        try {
                            LOCK.wait();
                        } catch (InterruptedException ignore) {}
                    }

                    log.info("1");
                    numbers.addLast(1);
                    numberTurn = false;
                    LOCK.notifyAll();
                }
            }
        });

        Thread t2 = new Thread(() -> {
            for (;;) {
                synchronized (LOCK) {
                    while (numberTurn) {
                        try {
                            LOCK.wait();
                        } catch (InterruptedException ignore) {}
                    }
                    log.info("2");
                    numbers.addLast(2);
                    numberTurn = true;
                    LOCK.notifyAll();
                }
            }
        });

        t1.start();
        t2.start();

        TimeUnit.SECONDS.sleep(2);

        boolean compare = false;
        for (int i = 0; i < numbers.size(); i++) {
            int index = i;
            if ((index & 1) == 1 && numbers.get(i) != 2) {
                compare = true;
            } else if ((index & 1) == 0 && numbers.get(i) != 1) {
                compare = true;
            }
        }
        log.info("----------- compare: {} ------------", compare);
    }

    private static boolean conditionForSpuriousingWakeup = false;
    @Test
    public void testSpurioueWakeup() throws InterruptedException {
        Thread waiter = new Thread(() -> {
            synchronized (OBJECT_TESTS) {
                System.out.println("[Waiter] Waiting for condition...");
                try {
                    while (!conditionForSpuriousingWakeup) {
                        OBJECT_TESTS.wait();
                    }
                    System.out.println("[Waiter] Condition is true, continue work!");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        waiter.start();
        Thread.sleep(1000);

        // spurious wakeup
        synchronized (OBJECT_TESTS) {
            System.out.println("[Main] Fake notify without changing condition");
            OBJECT_TESTS.notify();
        }
        Thread.sleep(1000);

        // real wakeup
        synchronized (OBJECT_TESTS) {
            conditionForSpuriousingWakeup = true;
            System.out.println("[Main] Set condition = true and notify");
            OBJECT_TESTS.notify();
        }
    }

    @Test
    public void testClone() throws CloneNotSupportedException {
        ObjectTests obj = new ObjectTests();
        Object clone = obj.clone();
        log.info("clone: {}, original object: {}", clone, obj);
        log.info("clone == : {}", clone == obj);
        log.info("clone is equals: {}", clone.equals(obj));
        log.info("clone calss: {}, original class: {}", clone.getClass(), obj.getClass());
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return new ObjectTests();
    }

    private static final String TEST_HAHSCODE_VALUE = "TEST_HAHSCODE_VALUE";
    @Override
    public int hashCode() {
        return TEST_HAHSCODE_VALUE.hashCode() * 31;
    }

    @Override
    public boolean equals(Object obj) {
        return TEST_HAHSCODE_VALUE.equals(this.TEST_HAHSCODE_VALUE);
    }

    @Test
    public void testOverride() {
        TestObject t1 = new TestObject("test", 1);
        TestObject t2 = new TestObject("test", 2);
        log.info("t1 == t2: {}", t1 == t2);
        log.info("t1.equals(t2): {}", t1.equals(t2));
        log.info("t1.hashCode():{}, t2.hashCode(): {}", t1.hashCode(), t2.hashCode());
    }

    @Test
    public void testInnerClass() {
        Arena arena = global();
        MemorySegment segment = arena.allocate(10 * 4);
        log.info("arena = {}", arena);
        log.info("segment = {}", segment);
    }

    Arena global() {
        class Holder {
            static final Arena GLOBAL = Arena.global();
        }
        return Holder.GLOBAL;
    }

    class TestObject {
        private String name;
        private int id;

        public TestObject(String name, int id) {
            this.name = name;
            this.id = id;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            TestObject test = (TestObject) o;
            return Objects.equals(name, test.name);
        }

        // not must, according to the difference business decide if override nor not
        @Override
        public int hashCode() {
            return Objects.hash(name);
        }
    }
}
