package com.alpha.learn.vt;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ThreadFactory;

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
}
