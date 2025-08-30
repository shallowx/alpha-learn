package com.alpha.learn.jdk25;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAccumulator;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.IntStream;

//  @jdk.internal.vm.annotation.Contended
@SuppressWarnings("ALL")
@Slf4j
public class LongAdderTests {

    @Test
    public void testForDebug() {
        // low busy
        LongAdder adder = new LongAdder();
        ExecutorService executor = Executors.newFixedThreadPool(10);
        IntStream.range(0, 100).forEach(i -> {executor.execute(() -> adder.add(1));});

        log.info("{}", adder);
        log.info("sum: {}", adder.sum());
        log.info("long: {}", adder.longValue());
        log.info("int: {}", adder.intValue());
        log.info("sumThenReset: {}", adder.sumThenReset());

        adder.reset();
        log.info("{}", adder);
    }

    @Test
    public void test() throws InterruptedException {
        LongAccumulator accumulator = new LongAccumulator((x, y) -> x + y, 0);
        ExecutorService executor = Executors.newFixedThreadPool(8);
        IntStream.range(1, 10).forEach(i -> executor.submit(() -> accumulator.accumulate(i)));

        TimeUnit.SECONDS.sleep(2);
        executor.shutdown();
        if (!executor.isTerminated() && !executor.awaitTermination(1, TimeUnit.SECONDS)) {
            executor.shutdownNow();
        }
        log.info("{}", accumulator.getThenReset());
    }
}
