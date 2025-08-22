package com.alpha.learn.jdk;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serial;
import java.util.concurrent.locks.AbstractQueuedLongSynchronizer;
import java.util.concurrent.locks.Condition;

public class AbstractQueuedLongSynchronizerTests {

    @Test
    public void testExclusiveSync() throws InterruptedException {
        ExclusiveLock counterLock = new ExclusiveLock();
        Runnable task = () -> {
            for (int i = 0; i < 5; i++) {
                counterLock.increment();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        };

        Thread t1 = new Thread(task, "T1");
        Thread t2 = new Thread(task, "T2");

        t1.start();
        t2.start();
        t1.join();
        t2.join();
    }

    @Test
    public void testSharedSync() throws InterruptedException {
        SharedLock sharedLock = new SharedLock(2);

        Runnable task = () -> {
            for (int i = 0; i < 5; i++) {
                sharedLock.increment();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        };

        Thread t1 = new Thread(task, "T1");
        Thread t2 = new Thread(task, "T2");
        Thread t3 = new Thread(task, "T3");

        t1.start();
        t2.start();
        t3.start();
        t1.join();
        t2.join();
        t3.join();
    }

    static class ExclusiveSync extends AbstractQueuedLongSynchronizer {
        @Serial
        private static final long serialVersionUID = -3274110589395933106L;

        public ExclusiveSync() {
            super();
        }

        @Override
        protected boolean tryAcquire(long arg) {
            assert arg == 1;
            if (compareAndSetState(0, 1)) {
                setExclusiveOwnerThread(Thread.currentThread());
                return true;
            }
            return false;
        }

        @Override
        protected boolean tryRelease(long arg) {
            assert arg == 1;
            if (!isHeldExclusively()) {
                throw new IllegalStateException();
            }
            setExclusiveOwnerThread(null);
            setState(0);
            return true;
        }

        public boolean isLocked() {
            return getState() != 0;
        }

        public Condition newCondition() {
            return new ConditionObject();
        }

        @Serial
        private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
            s.defaultReadObject();
            setState(0);
        }

        @Override
        protected boolean isHeldExclusively() {
            return getState() == 1 &&  getExclusiveOwnerThread() == Thread.currentThread();
        }
    }

    static class SharedSync extends AbstractQueuedLongSynchronizer {
        @Serial
        private static final long serialVersionUID = 5360208063869607504L;

        public SharedSync(long permits) {
            setState(permits);
        }

        @Override
        protected long tryAcquireShared(long acquires) {
            for (;;) {
                long available = getState();
                long remaining = available - acquires;
                if (remaining < 0) return -1;
                if (compareAndSetState(available, remaining)) {
                    return remaining;
                }
            }
        }

        @Override
        protected boolean tryReleaseShared(long releases) {
            for (;;) {
                long current = getState();
                long next = current + releases;
                if (compareAndSetState(current, next)) {
                    return true;
                }
            }
        }
    }

    static class SharedLock {
        private final SharedSync sync;
        public SharedLock(long permits) {
            sync = new SharedSync(permits);
        }

        public void acquire() {
            sync.acquireShared(1);
        }

        public void release() {
            sync.releaseShared(1);
        }

        private long counter = 0;

        public void increment() {
            acquire();
            try {
                counter++;
                System.out.println(Thread.currentThread().getName() + " incremented counter to " + counter);
            } finally {
                release();
            }
        }
    }


    static class ExclusiveLock {
        private final ExclusiveSync sync = new ExclusiveSync();

        private long counter = 0;
        public void lock() {
            sync.acquire(1);
        }

        public void unlock() {
            sync.release(1);
        }

        public void increment() {
            lock();
            try {
                counter++;
                System.out.println(Thread.currentThread().getName() + " incremented counter to " + counter);
            } finally {
                unlock();
            }
        }
    }
}
