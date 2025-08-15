package com.alpha.learn.jdk;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class AutoCloseableTests {

    @Test
    public void test() {

        try (TestResource  testResource = new TestResource()) {
            testResource.printWithThrowable();
        } catch (Throwable t) {
            log.error("test exception", t);
        }finally {
            log.info("finally");
        }
    }

    static class TestResource implements AutoCloseable, Cloneable {

        void printWithThrowable() throws Throwable {
            log.info("printWithThrowable");
        }

        @Override
        public void close() {
            log.info("try-with-resource by auto close");
        }

        @Override
        public TestResource clone() {
            try {
                return (TestResource) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new AssertionError();
            }
        }
    }
}
