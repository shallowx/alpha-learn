package com.alpha.learn.jdk25;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class ReentrantLockTests {
    @Test
    public void test() throws InterruptedException {
        ReentrantLock lock = new ReentrantLock();

        Thread t1 = new Thread(() -> {
            lock.lock();
            log.info("t1 get lock");
            lock.unlock();
        });

        Thread t2 = new Thread(() -> {
            lock.lock();
            log.info("t2 get lock");
            lock.unlock();
        });

        Thread t3 = Thread.ofVirtual().unstarted(() -> {
            lock.lock();
            log.info("t3 get lock");
            lock.unlock();
        });

        t1.start();
        t2.start();
        t3.start();

        t1.join();
        t2.join();
        t3.join();
    }

    @Test
    public void test2() throws InterruptedException {
        ReentrantLock lock = new ReentrantLock();
        Condition notFull = lock.newCondition();
        Condition notEmpty = lock.newCondition();
        Queue<String> queue = new PriorityQueue<>();

        Thread t1 = new Thread(() -> {
            try {
                while (true) {
                    lock.lock();
                    try {
                        while (queue.size() == 1) {
                            notFull.await();
                        }
                        queue.offer("a");
                        notEmpty.signalAll();
                    } finally {
                        lock.unlock();
                    }
                }
            } catch (InterruptedException ignored) {}
        });

        Thread t2 = new Thread(() -> {
            try {
                while (true) {
                    lock.lock();
                    try {
                        while (queue.isEmpty()) {
                            notEmpty.await();
                        }
                        String v = queue.poll();
                        System.out.println("v = " + v);
                        notFull.signalAll();
                    } finally {
                        lock.unlock();
                    }
                }
            } catch (InterruptedException ignored) {}
        });

        t1.start();
        t2.start();

        t1.join();
        t2.join();
    }
}
