package com.alpha.learn.jdk;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.Serializable;

@Slf4j
public class RunnableTests {

    @Test
    public void test() throws InterruptedException {
        Task task = new Task();
        Thread thread = new Thread(task);
        thread.start();
        thread.join();
    }

    static class Task implements Runnable, Serializable {
        @Override
        public void run() {
            log.info("running...");
        }
    }
}
