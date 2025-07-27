package com.alpha.learn.features;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class OneRecordClassTests {

    @Test
    public void test0() {
        OneRecordClass orc = new OneRecordClass("Tom", 18);
        Assertions.assertEquals("Tom", orc.name());
        Assertions.assertEquals(18, orc.age());
        System.out.println(orc.toString());
        System.out.println(orc.hashCode());
    }
}
