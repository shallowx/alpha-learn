package com.alpha.learn.jdk25;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

@Slf4j
public class CyclicBarrierTests {

    @Test
    public void test() throws InterruptedException {
        CyclicBarrier barrier = new CyclicBarrier(3, () -> log.info("CyclicBarrier completed"));
        StringBuilder result = new StringBuilder();

        Runnable task = () -> {
            try {
                result.append(Thread.currentThread().getName()).append(" reached | ");
                barrier.await();
                result.append(Thread.currentThread().getName()).append(" , > continue ");
            } catch (InterruptedException | BrokenBarrierException e) {
                throw new RuntimeException(e);
            }
        };

        Thread[] threads = new Thread[3];
        for (int i = 0; i < 3; i++) {
            threads[i] = new Thread(task, "T" + i);
            threads[i].start();
        }

        for (Thread t : threads) {
            t.join();
        }
        String output = result.toString();
        log.info("CyclicBarrier result: {}", output);
    }
}
