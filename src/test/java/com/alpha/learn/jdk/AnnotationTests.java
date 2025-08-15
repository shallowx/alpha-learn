package com.alpha.learn.jdk;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.lang.annotation.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Objects;

@SuppressWarnings("ALL")
@Slf4j
public class AnnotationTests {

    @Test
    public void test() throws NoSuchMethodException {
        Field[] fields = TestForAnnotation.class.getDeclaredFields();
        log.info("field name:{}", Arrays.toString(fields));
        (fields[0]).setAccessible(true);
        TestAnnotation annotation1 = (fields[0]).getAnnotation(TestAnnotation.class);
        log.info("field annotation:{}", annotation1);

        Constructor<TestForAnnotation> declaredConstructor = TestForAnnotation.class.getDeclaredConstructor(String.class);
        declaredConstructor.setAccessible(true);
        Parameter[] parameters = declaredConstructor.getParameters();
        log.info("parameters:{}", Arrays.toString(parameters));
        TestAnnotation annotation = (parameters[0]).getAnnotation(TestAnnotation.class);
        log.info("parameter annotation:{}", annotation);
    }

    static class TestForAnnotation {
        @TestAnnotation()
        private String name;

        public TestForAnnotation(@TestAnnotation String name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            TestForAnnotation that = (TestForAnnotation) o;
            return Objects.equals(name, that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(name);
        }
    }

    @Target(value = {ElementType.LOCAL_VARIABLE, ElementType.PARAMETER, ElementType.METHOD, ElementType.FIELD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface TestAnnotation {
        String name() default "";
    }
}
