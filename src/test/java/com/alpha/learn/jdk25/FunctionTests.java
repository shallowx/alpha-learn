package com.alpha.learn.jdk25;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import java.util.Random;
import java.util.UUID;
import java.util.function.*;

@Slf4j
public class FunctionTests {

    @Test
    public void testBooleanSupplier() {
        BooleanSupplier supplier = () -> true;
        log.info("boolean supplier: {}", supplier.getAsBoolean());
    }

    @Test
    public void testConsumer() {
        Consumer<String> consumer = s -> log.info("consumer : {}", s);
        consumer.andThen(new Consumer<String>() {
            @Override
            public void accept(String s) {
                log.info("andThen accept: {}", "hello " + s);
            }
        }).accept("world");
    }

    @Test
    public void testSupplier() {
        Supplier<String> supplier = () -> "hello world";
        String s = supplier.get();
        log.info("supplier: {}", s);
    }
    @Test
    public void testFunction() {
        Function<String, Integer> function = (s) -> {
            log.info("function : {}", s);
            return s.length() * 31;
        };

        String s = function.andThen(new Function<Number, String>() {
            @Override
            public String apply(Number number) {
                return number.toString() + 1;
            }
        }).apply("world");
        log.info("ret : {}", s);
    }

    @Test
    public void testPredicate() {
        Predicate<String> p =  (s) -> {
            log.info("p : {}", s);
            return s.length() > 30;
        };
        String uuid = UUID.randomUUID().toString();
        boolean b = p.test(uuid.substring(0, new Random().nextInt(uuid.length() + 1)));
        log.info("b : {}", b);
    }
}
