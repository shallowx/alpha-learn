package com.alpha.learn.jdk;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.IOException;

@Slf4j
@SuppressWarnings("ALL")
public class AppendableTests {

    @Test
    public void test() throws IOException {
        TestAppendable testAppendable = new TestAppendable();
        log.info("testAppendable: {}", testAppendable);
    }

    static class TestAppendable implements Appendable {
        String value ;

        public TestAppendable() {
            this.value = new String();
        }

        @Override
        public TestAppendable append(CharSequence csq) throws IOException {
            value.concat(csq.toString());
            return this;
        }

        @Override
        public TestAppendable append(CharSequence csq, int start, int end) throws IOException {
            // do nothing and only for test
            return null;
        }

        @Override
        public TestAppendable append(char c) throws IOException {
            // do nothing and only for test
            return null;
        }

        @Override
        public String toString() {
            return "TestAppendable{" +
                    "value='" + value + '\'' +
                    '}';
        }
    }
}
