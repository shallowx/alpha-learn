package com.alpha.learn.jdk25;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

@SuppressWarnings("ALL")
@Slf4j
public class VarHandleTests {

    @Test
    public void test() {
        TestVarHandle testVarHandle = new TestVarHandle();
        testVarHandle.compareAndSet(0,1);
        log.info("testVarHandle.compareAndSet(0,1): {}", testVarHandle.get());

        testVarHandle.compareAndSet(1,2);
        log.info("testVarHandle.compareAndSet(1,2): {}", testVarHandle.get());
    }

    static class TestVarHandle {
        private static final VarHandle VALUE =  Lookup.getVarHandle(MethodHandles.lookup(), "value", int.class);
        private volatile int value;

        public boolean compareAndSet(int expectedValue, int newValue) {
            return VALUE.compareAndSet(this, expectedValue, newValue);
        }
        public int get() {
            return value;
        }
    }

    static class Lookup {
        private static VarHandle getVarHandle(MethodHandles.Lookup lookup, String name, Class<?> clz) {
            try {
                return lookup.findVarHandle(lookup.lookupClass(), name, clz);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
