package com.alpha.learn.jdk;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.AccessFlag;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;

@Slf4j
public class ClassTests {

    @Test
    public void test() throws Exception {
        Class<TestClass> clz = TestClass.class;
        log.info("clz: {}", clz);

        Set<AccessFlag> accessFlags = clz.accessFlags();
        log.info("accessFlags: {}", accessFlags);

        Class<?> aClass = clz.arrayType();
        log.info("aClass: {}", aClass);

        TestClass cast = TestSafeCast.cast(TestSubClass.class.getName(), TestClass.class);
        log.info("cast: {}", cast);

        Class<TestSubClass> subClassClass = TestSubClass.class;
        log.info("subClassClass: {}", subClassClass);
        Method[] methods = subClassClass.getMethods();
        log.info("methods: {}", Arrays.toString(methods));

        Constructor<TestSubClass> constructor = subClassClass.getDeclaredConstructor();
        log.info("constructor: {}", constructor);
        log.info("constructor.getDeclaringClass(): {}", constructor.getDeclaringClass());
        log.info("constructor.getModifiers(): {}", constructor.getModifiers());
        log.info("constructor.getParameterCount(): {}", constructor.getParameterCount());
        log.info("constructor.getParameterTypes(): {}", Arrays.toString(constructor.getParameterTypes()));
        log.info("constructor annotated types: {}", constructor.getAnnotatedReceiverType());
        log.info("constructor annotated types: {}", constructor.getAnnotatedReturnType());

        constructor.setAccessible(true);
        TestSubClass ct = constructor.newInstance();
        ct.setName("hello");
        log.info("ct: {}", ct);
    }

    @Test
    public void test2() throws Exception {
        TestSubClass ct = new TestSubClass();
        ct.setName("hello");
        log.info("ct: {}", ct);

        log.info("----------------------");

        TestClass ct2 = new TestClass();
        ct2.setName("hello");
        log.info("ct2: {}", ct2);
    }

    @Test
    public void test3() throws ClassNotFoundException {
        Class<?> cf1 = Class.forName("com.alpha.learn.jdk.ClassTests$TestSubClass", false, ClassLoader.getSystemClassLoader());
        log.info("cf1: {}", cf1);

        log.info("-----------spilt1-----------");
        Class<TestSubClass> subClassClass1 = TestSubClass.class;
        log.info("subClassClass1: {}", subClassClass1);

        log.info("-----------spilt-----------");
        Class<?> cf2 = Class.forName("com.alpha.learn.jdk.ClassTests$TestSubClass");
        log.info("cf2: {}", cf2);
    }

    @Setter
    @TestClassAnnotation
    private static class TestClass {
        @TestClassAnnotation String name;

        static {
            log.info("static TestClass");
        }

        {
            log.info("TestClass[]");
        }
        public TestClass() {
            log.info("TestClass()");
        }

        public void test(){}
        private void test1(){}
        protected void test2(){}

        @Override
        public String toString() {
            return "TestClass{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }

    @Target(value = {
            ElementType.FIELD,
            ElementType.METHOD,
            ElementType.PARAMETER,
            ElementType.ANNOTATION_TYPE,
            ElementType.TYPE,
            ElementType.CONSTRUCTOR
    })
    @Retention(value = RetentionPolicy.RUNTIME)
    @interface TestClassAnnotation{
    }

    static class TestSubClass extends TestClass {
        private void print(){}
        public void print1(){}
        protected void print2(){}

        static {
            log.info("static TestSubClass");
        }

        {
            log.info("TestSubClass[]");
        }

        public TestSubClass() {
            log.info("TestSubClass()");
        }

        @Override
        public String toString() {
            return "TestSubClass{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }

    static class TestSafeCast {
        public static <T> T cast(String className, Class<T> baseClass) throws Exception {
            Class<?> loadedClass = Class.forName(className);
            Class<? extends T> subclass = loadedClass.asSubclass(baseClass);
            return subclass.cast(subclass.getDeclaredConstructor().newInstance());
        }
    }
}
