package com.alpha.learn.jdk25;

import org.junit.jupiter.api.Test;

public class InstanceOfTests {


    @SuppressWarnings("ALL")
    @Test
    public void test() {
        One one = new One("test", 1);
        if (one instanceof One(String name, int age)) {
            System.out.println("name:" + name + ",age:" + age);
        }

        Object object = one;
        Object o = switch (object) {
            case One(String name, int age) -> 1;
            case String s -> 2;
            default -> 3;
        };

        System.out.println(object);
    }

    @Test
    public void testFormat() {
        String hello = "hello";
        System.out.println(format(hello));
    }

    private static String format(Object obj) {
        return switch (obj) {
            case Integer i -> "int: " + i;
            case String s -> "string: " + s;
            default -> "unknown";
        };
    }

    record One(String name, int age) {
        @Override
        public String toString() {
            return "One{" +
                    "name='" + name + '\'' +
                    ", age=" + age +
                    '}';
        }
    }
}
