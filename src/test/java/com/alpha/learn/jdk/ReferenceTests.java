package com.alpha.learn.jdk;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import sun.misc.Unsafe;
import java.lang.ref.*;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

@SuppressWarnings("ALL")
@Slf4j
public class ReferenceTests {

    // need to set memory limit for gc
    @Test
    public void testSoftReference() {
        SoftReference<String> ref = new SoftReference<>("test");
        String s = ref.get();
        log.info("s: {}", s);

        ReferenceQueue<String> queue = new ReferenceQueue<>();
        SoftReference<String> softReference = new SoftReference<>("test", queue);
        String s1 = softReference.get();
        log.info("s: {}", s1);
    }

    /**
     * reference queue save reference object instance, for example: java.lang.ref.WeakReference@757277dc.
     * and it's not ensure can get real object (not reference instance) information
     */
    @Test
    public void testWeakReference() {
        ReferenceQueue<TestReference<String>> queue = new ReferenceQueue<>();
        WeakReference<TestReference<String>> weakReference = new WeakReference<>(new TestReference<>("test-weak-reference"), queue);
        TestReference<String> testReference = weakReference.get();

        Assertions.assertNotNull(testReference);
        String element = testReference.element;
        log.info("element: {}, weakReference: {}", element, weakReference);

        testReference = null;
        System.gc();

        for (;;) {
            Reference<? extends TestReference<String>> ref = queue.poll();
            if (ref != null) {
                log.info("object {}, ref: {}", ref, ref);
                break;
            }
        }
    }

    private static final Unsafe unsafe;
    static {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            unsafe = (Unsafe) f.get(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testPhantomReference() throws InterruptedException {
        final ConcurrentHashMap<PhantomReference<DirectMemory>, ByteBuffer> directMemoryContainers = new ConcurrentHashMap<>();
        final ReferenceQueue<DirectMemory> referenceQueue = new ReferenceQueue<>();

        ByteBuffer buffer = ByteBuffer.allocateDirect(1024 * 1024);
        log.info("buffer: {}", buffer);

        DirectMemory directMemory = new DirectMemory("test");
        directMemoryContainers.put(new PhantomReference<>(directMemory, referenceQueue), buffer);
        directMemory = null;

        CountDownLatch countDownLatch = new CountDownLatch(1);
        Thread cleanerThread = new Thread(() -> {
            while (true) {
                try {
                    Reference<? extends DirectMemory> ref = referenceQueue.remove();
                    log.info("ref: {}, get: {}", ref, ref.get());
                    ByteBuffer buf = directMemoryContainers.remove(ref);
                    if (buf != null) {
                        log.info("buf: {}", buf);
                        unsafe.invokeCleaner(buf);
                        log.info("buf is zero: {}", buf);
                        countDownLatch.countDown();
                    }
                    System.out.println("PhantomReference cleared: " + ref);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        cleanerThread.setDaemon(true);
        cleanerThread.start();

        System.gc();
        countDownLatch.await();
    }

    record DirectMemory(String name) {
        @Override
            public boolean equals(Object o) {
                if (o == null || getClass() != o.getClass()) return false;
                DirectMemory that = (DirectMemory) o;
                return Objects.equals(name, that.name);
            }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }
    }

    record TestReference<T>(T element) {}
}
