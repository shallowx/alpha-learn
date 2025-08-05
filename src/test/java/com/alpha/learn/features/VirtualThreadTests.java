package com.alpha.learn.features;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.*;

@Slf4j
public class VirtualThreadTests {

    @Test
    public void testBuildVirtualThread(){
        Thread.Builder.OfVirtual virtualBuilder = Thread.ofVirtual();
        Thread t = virtualBuilder.name("virtualThread-test").uncaughtExceptionHandler(TestUncaughtExceptionHandler.INSTANCE)
                .unstarted(new TestTask("test-task", ThreadLocalRandom.current().nextInt(0, 2)));
        t.start();
    }

    @Test
    public void testPerVirtualThread() throws ExecutionException, InterruptedException {
        final CountDownLatch countDownLatch = new CountDownLatch(2);
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            Future<?> future = executor.submit(new Runnable() {
                @Override
                public void run() {
                    log.info("thread: {}", Thread.currentThread());
                    countDownLatch.countDown();
                }
            });

            Future<?> future1 = executor.submit(new Runnable() {
                @Override
                public void run() {
                    log.info("thread-1: {}", Thread.currentThread());
                    countDownLatch.countDown();
                }
            });
            countDownLatch.await();
            System.out.println(future.isDone());
            System.out.println(future1.isDone());
        }
    }

    record TestTask(String name, int id) implements Runnable {
        @Override
        public void run() {
            log.info("Running test task {}, thread[{}]", name, Thread.currentThread());
        }
    }

    static class TestUncaughtExceptionHandler implements UncaughtExceptionHandler  {

        static TestUncaughtExceptionHandler  INSTANCE = new TestUncaughtExceptionHandler();

        @Override
        public void uncaughtException(Thread t, Throwable e) {
            log.warn("Uncaught exception in thread {}", t.toString(), e);
        }
    }
}
