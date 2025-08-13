package com.alpha.learn.jdk;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

@Slf4j
public class MathContextTests {

    @Test
    public void testMathContext() {
        MathContext context  = new  MathContext(3, RoundingMode.HALF_UP);
        int precision = context.getPrecision();
        log.info("precision = {}", precision);
        log.info("RoundingMode = {}", context.getRoundingMode());

        BigDecimal bigDecimal = new BigDecimal("3.14159265358979", context);
        BigDecimal bigDecimal1 = new BigDecimal("0.14159265358979", context);
        log.info("bigDecimal = {}", bigDecimal);
        log.info("bigDecimal1 = {}", bigDecimal1);

        BigDecimal bigDecimal2 = new BigDecimal("3.14159265358979", MathContext.UNLIMITED);
        log.info("bigDecimal2 = {}", bigDecimal2);
    }
}
