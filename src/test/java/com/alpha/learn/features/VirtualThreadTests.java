package com.alpha.learn.features;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public class VirtualThreadTests {

    @Test
    public void testBuildVirtualThread(){
        Thread.Builder.OfVirtual virtualBuilder = Thread.ofVirtual();
        Thread t = virtualBuilder.name("virtualThread-test").uncaughtExceptionHandler(TestUncaughtExceptionHandler.INSTANCE)
                .unstarted(new TestTask("test-task", ThreadLocalRandom.current().nextInt(0, 2)));
        t.start();
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
