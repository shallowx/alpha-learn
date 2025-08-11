package com.alpha.learn.jdk;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class VirtualThreadTests {


    @Test
    public void test0() {
        Thread.Builder.OfVirtual vt = Thread.ofVirtual().name("test0");
        Thread thread = vt.unstarted(() -> System.out.println("thread-name: " + Thread.currentThread().getName()));

        System.out.println(thread.getName());
        System.out.println(thread.isDaemon());
        System.out.println(thread.isAlive());
        System.out.println(thread.isInterrupted());

        thread.start();
        System.out.println("--------------------");
        System.out.println(thread.isAlive());
    }

    @Test
    public void test1() {
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            IntStream.range(0, 10_000).forEach(i -> {
                executor.submit(() -> {
                    Thread.sleep(Duration.ofMillis(100).toMillis());
                    return i;
                });
            });
        }
    }
}
