package com.alpha.learn.jdk;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@Slf4j
@SuppressWarnings("ALL")
public class BlockingQueueTests {

    @Test
    public void testArrayBlockingQueue() throws InterruptedException {
        BlockingQueue<String> queue = new ArrayBlockingQueue<>(1);
        queue.offer("a");
        Assertions.assertThrows(IllegalStateException.class, () -> queue.add("b"));

        String v = queue.poll();
        Assertions.assertNotNull(v);
        Assertions.assertEquals(v, "a");

        Thread t1 = new Thread(() -> {
            try {
                log.info("take starting and will blocking");
                String take = queue.take();
                log.info("take outcome: {}", take);
            } catch (InterruptedException ignored) {
            }
        });

        queue.offer("b");
        queue.offer("c");

        Thread t2 = new Thread(() -> {
            try {
                log.info("put starting and will blocking");
                queue.put("d");
                log.info("put successfully");
            } catch (InterruptedException ignored) {
            }
        });

        while (queue.isEmpty() == false) {
            log.info("poll outcome: {}", queue.poll());
        }

        Assertions.assertThrows(NoSuchElementException.class, () -> queue.remove());
        boolean c = queue.remove("d");
        log.info("remove successfully: {}", c);

        t1.start();
        t2.start();
        t1.join();
        t1.join();
    }
}
