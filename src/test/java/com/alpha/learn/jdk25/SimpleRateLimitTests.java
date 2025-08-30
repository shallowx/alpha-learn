package com.alpha.learn.jdk25;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import java.io.Serial;
import java.io.Serializable;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.VarHandle;
import java.util.concurrent.*;
import java.util.stream.IntStream;

@SuppressWarnings("ALL")
@Slf4j
public class SimpleRateLimitTests {

    @Test
    public void test() throws InterruptedException {
        SimpleTokenQueueRateLimit mock = new SimpleTokenQueueRateLimit(10);
        ExecutorService executor = Executors.newFixedThreadPool(100);
        IntStream.range(0, 100).forEach(i -> {
            while (mock.get()) {
                executor.submit(() -> {
                    log.info("Thread-name: {}", Thread.currentThread().getName());
                    mock.release();
                });
            }
        });

        Thread t1 = new Thread(() -> {
            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
            scheduler.scheduleWithFixedDelay(() -> mock.put(10), 0, 100, TimeUnit.MILLISECONDS);
        });

        t1.start();
        t1.join();
    }

    @Test
    public void test2() throws InterruptedException {
        CounterTokenRateLimit mock = new CounterTokenRateLimit(10);
        ExecutorService executor = Executors.newFixedThreadPool(100);
        IntStream.range(0, 100).forEach(i -> {
            while (mock.get()) {
                executor.submit(() -> {
                    log.info("Thread-name: {}", Thread.currentThread().getName());
                    mock.release();
                });
            }
        });

        Thread t1 = new Thread(() -> {
            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
            scheduler.scheduleWithFixedDelay(() -> mock.put(10), 0, 100, TimeUnit.MILLISECONDS);
        });

        t1.start();
        t1.join();
    }

    static class CounterTokenRateLimit implements RateLimit {
        private static final VarHandle VALUE = MhUtil.findVarHandle(MethodHandles.lookup(), "tokens", int.class);
        private volatile int tokens;
        private final int DEFAULT_MAX_CAPACITY = 1000;

        public CounterTokenRateLimit(int tokens) {
            this.tokens = Math.min(DEFAULT_MAX_CAPACITY, tokens);
            VALUE.set(this, tokens);
        }

        @Override
        public boolean get() {
            return (int)VALUE.getVolatile(this) > 0;
        }

        @Override
        public void put(int capacity) {
            int diff = DEFAULT_MAX_CAPACITY - size();
            int need = Math.min(capacity, diff);
            VALUE.getAndAdd(this, need);
        }

        @Override
        public void release() {
            if ((int)VALUE.getVolatile(this) > 0) {
                VALUE.getAndAddRelease(this, 1);
            }
        }

        @Override
        public int size() {
            return (int) VALUE.getVolatile(this);
        }
    }

    static class MhUtil {

        private MhUtil() {}

        public static VarHandle findVarHandle(MethodHandles.Lookup lookup,
                                              String name,
                                              Class<?> type) {
            return findVarHandle(lookup, lookup.lookupClass(), name, type);
        }

        public static VarHandle findVarHandle(MethodHandles.Lookup lookup,
                                              Class<?> recv,
                                              String name,
                                              Class<?> type) {
            try {
                return lookup.findVarHandle(recv, name, type);
            } catch (ReflectiveOperationException e) {
                throw new InternalError(e);
            }
        }

        public static MethodHandle findVirtual(MethodHandles.Lookup lookup,
                                               Class<?> refc,
                                               String name,
                                               MethodType type) {
            try {
                return lookup.findVirtual(refc, name, type);
            } catch (ReflectiveOperationException e) {
                throw new InternalError(e);
            }
        }
    }

    static class SimpleTokenQueueRateLimit implements RateLimit {

        @Serial
        private static final long serialVersionUID = -7905202338789198315L;

        private static final BlockingQueue<Object> queue = new LinkedBlockingDeque<>();
        private static final Object DEFAULT_OBJECT = new Object();
        private final int capacity;
        private final int DEFAULT_MAX_CAPACITY = 1000;

        public SimpleTokenQueueRateLimit(int capacity) {
            this.capacity = Math.min(capacity, DEFAULT_MAX_CAPACITY);
            this.put(capacity);
        }

        @SneakyThrows
        @Override
        public boolean get() {
            return queue.take() != null;
        }

        @Override
        public void put(int limit) {
            if (capacity <= 0) {
                throw new IllegalArgumentException("capacity must be greater than zero");
            }

            int diff = DEFAULT_MAX_CAPACITY - queue.size();
            int need = Math.min(limit, diff);
            for (int i = 0; i < need; i++) {
                queue.offer(DEFAULT_OBJECT);
            }
        }

        @Override
        public void release() {
            queue.offer(DEFAULT_OBJECT);
        }

        @Override
        public int size() {
            return queue.size();
        }
    }

    interface RateLimit extends Serializable {
        boolean get();
        void put(int capacity);
        void release();
        int size();
    }
}
