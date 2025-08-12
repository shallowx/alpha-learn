package com.alpha.learn.jdk;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@SuppressWarnings("ALL")
public class IntegerTests {

    @Test
    public void testCompareable() {
        Integer a = 1;
        Integer b = 2;
        Integer c = 0;
        Integer d = 1;
        log.info("a={}, b={}, c={}, d={}", a, b, c, d);
        log.info("a compareTo b: {}", a.compareTo(b));
        log.info("a compareTo c: {}", a.compareTo(c));
        log.info("a compareTo d: {}", a.compareTo(d));
    }

    @Test
    public void testMinSubArrayLen() {
        int sum = sum(10);
        int[] array = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

        for (int i = 0; i < 100_000; i++) {
            int target = ThreadLocalRandom.current().nextInt(0, sum);
            int len = minSubArrayLen(array, target);
            log.info("target:{}, len: {}", len);
        }

        for (int i = 0; i < 100_000; i++) {
            int target = ThreadLocalRandom.current().nextInt(0, sum);
            int[] result = minSubArray(array, target);
            log.info("target:{}, result: {}", target, result);
        }
    }

    private int sum(int len) {
        if (len <= 0) {
            throw new IllegalArgumentException("len must be greater than zero");
        }
        return (len * (len + 1)) >> 1;
    }

    private int[] minSubArray(int[] array, int target) {
        int result = Integer.MAX_VALUE;
        int l = 0;
        for (int left = 0, right = 0, sum = 0; right < array.length; right++) {
            sum += array[right];
            while (sum - array[left] >= target && left < right) {
                sum -= array[left++];
            }

            if (sum >= target) {
                l = left;
                result = Math.min(result, right - left + 1);
            }
        }
        int[] res = new int[result];
        System.arraycopy(array, l, res, 0, result);
        return res;
    }

    private int minSubArrayLen(int[] array, int target) {
        int result = Integer.MAX_VALUE;
        for (int left = 0, right = 0, sum = 0; right < array.length; right++) {
            sum += array[right];
            while (sum - array[left] >= target && left < right) {
                sum -= array[left++];
            }

            if (sum >= target) {
                result = Math.min(result, right - left + 1);
            }
        }
        return result == Integer.MAX_VALUE ? 0 : result;
    }

    @Test
    public void testBinary() {
        log.info("outcome: {}", ((~5) + 1));
        log.info("outcome: {}", ((~(-5)) + 1));

        log.info("outcome: {}", 1 & 2);
        log.info("outcome: {}", 3 & -2);
        log.info("outcome: {}", 4 & 0x1);
        log.info("outcome: {}", 4 | 2);
        log.info("outcome: {}", 3 ^ 2);
    }
}
