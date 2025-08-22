package com.alpha.learn.jdk;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.openjdk.jol.info.ClassLayout;

@Slf4j
public class JOLObjectTests {

    @Test
    public void test() {
        /**
         * 00:40:34.392 [main] INFO com.alpha.learn.jdk.JOLObjectTests -- com.alpha.learn.jdk.JOLObjectTests$TestObject object internals:
         * OFF  SZ                                             TYPE DESCRIPTION               VALUE
         *   0   8                                                  (object header: mark)     0x0000000000000001 (non-biasable; age: 0)
         *   8   4                                                  (object header: class)    0x0108d220
         *  12   4                                              int TestObject1.number        0
         *  16   8                                             long TestObject1.timestamp     0
         *  24   1                                             byte TestObject1.count         0
         *  25   3                                                  (alignment/padding gap)
         *  28   4                                 java.lang.String TestObject.name          null
         *  32   4   com.alpha.learn.jdk.JOLObjectTests.InnerObject TestObject.innerObject   null
         *  36   4                                                  (object alignment gap)
         * Instance size: 40 bytes
         * Space losses: 3 bytes internal + 4 bytes external = 7 bytes total
         */
        log.info( ClassLayout.parseInstance(new TestObject()).toPrintable());
        log.info("-----------------------------");

        /**
         * 00:40:34.389 [main] INFO com.alpha.learn.jdk.JOLObjectTests -- com.alpha.learn.jdk.JOLObjectTests$TestObject1 object internals:
         * OFF  SZ                                             TYPE DESCRIPTION               VALUE
         *   0   8                                                  (object header: mark)     0x0000000000000001 (non-biasable; age: 0)
         *   8   4                                                  (object header: class)    0x0108d000
         *  12   4                                              int TestObject.number         1
         *  16   8                                             long TestObject.timestamp      1755880833932
         *  24   1                                             byte TestObject.count          1
         *  25   3                                                  (alignment/padding gap)
         *  28   4   com.alpha.learn.jdk.JOLObjectTests.InnerObject TestObject1.innerObject    (object)
         *  32   4                                 java.lang.String TestObject1.name           (object)
         *  36   4                                                  (object alignment gap)
         * Instance size: 40 bytes
         */
        log.info(ClassLayout.parseInstance(new TestObject1()).toPrintable());
    }

    static class TestObject {
        private InnerObject innerObject;
        private String name;
        private byte count;
        private int number;
        private long timestamp;
    }

    static class TestObject1 {
        private String name;
        private byte count;
        private long timestamp;
        private int number;
        private InnerObject innerObject;
    }

    static class InnerObject {
        private String name;
    }
}
