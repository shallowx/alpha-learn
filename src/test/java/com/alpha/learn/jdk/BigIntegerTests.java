package com.alpha.learn.jdk;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import java.math.BigInteger;
import java.util.Random;

@SuppressWarnings("ALL")
@Slf4j
public class BigIntegerTests {

    @Test
    public void testBigInteger(){
        BigInteger bi = new BigInteger("-123");
        BigInteger bi1 = new BigInteger("+123");
        BigInteger bi2 = new BigInteger("1236456474574745756875487564745684554645654764575467654858568567867867587568");
        log.info("bi: {}", bi);
        log.info("bi1: {}", bi1);
        log.info("bi2: {}", bi2);

        BigInteger add = bi.add(bi1);
        BigInteger subtract = bi.subtract(bi1);
        BigInteger multiply = bi.multiply(bi1);
        BigInteger divide = bi.divide(bi2);
        BigInteger abs = bi.abs();
        log.info("add: {}", add);
        log.info("subtract: {}", subtract);
        log.info("multiply: {}", multiply);
        log.info("divide: {}", divide);
        log.info("abs: {}", abs);

        int compared = bi.compareTo(bi1);
        log.info("i: {}", compared);

        BigInteger bigInteger = new BigInteger(100, new Random());
        log.info("bigInteger: {}", bigInteger);
    }

    @Test
    public void testForDebug() {
        BigInteger bi = new BigInteger("111111111111111111111111111111111");
        log.info("bi: {}", bi);
    }

    @Test
    public void test() {
        byte[] mag = { (byte)0x12, (byte)0x34 };
        BigInteger bi0 = new BigInteger(1, mag);
        BigInteger bi1 = new BigInteger(-1, mag);
        log.info("bi0: {}", bi0);
        log.info("bi1: {}", bi1);

        byte[] positiveBytes = { 0x12, 0x34 };
        byte[] negativeBytes = { (byte)0xFF, (byte)0x38 };
        BigInteger bp = new BigInteger(positiveBytes);
        BigInteger bn = new BigInteger(negativeBytes);
        log.info("bp: {}", bp);
        log.info("bn: {}", bn);
    }
}
