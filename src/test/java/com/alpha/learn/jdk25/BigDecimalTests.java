package com.alpha.learn.jdk25;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@Slf4j
public class BigDecimalTests {

    @Test
    public void testBigDecimal() {
        BigDecimal bigDecimal1 = new BigDecimal("3.14159265358979", MathContext.UNLIMITED).setScale(3,  RoundingMode.DOWN);
        log.info("bigDecimal1 = {}", bigDecimal1);

        BigDecimal bigDecimal2 = new BigDecimal("10");
        BigDecimal bigDecimal3 = new BigDecimal("3");
        log.info("= {}", bigDecimal2.divide(bigDecimal3,  MathContext.DECIMAL64));
    }

    @Test
    public void testCreation() {
        BigDecimal bd1 = new BigDecimal("123.45");
        BigDecimal bd2 = BigDecimal.valueOf(123.45); // 注意 double 转换可能有精度问题
        assertEquals("123.45", bd1.toString());
        System.out.println("bd2 = " + bd2);
    }

    @Test
    public void testAdditionSubtraction() {
        BigDecimal bd1 = new BigDecimal("10.5");
        BigDecimal bd2 = new BigDecimal("2.3");

        BigDecimal sum = bd1.add(bd2);
        BigDecimal diff = bd1.subtract(bd2);

        assertEquals("12.8", sum.toString());
        assertEquals("8.2", diff.toString());
    }

    @Test
    public void testMultiplicationDivision() {
        BigDecimal bd1 = new BigDecimal("10");
        BigDecimal bd2 = new BigDecimal("3");

        BigDecimal product = bd1.multiply(bd2);
        assertEquals("30", product.toString());

        BigDecimal quotient = bd1.divide(bd2, 2, RoundingMode.HALF_UP);
        assertEquals("3.33", quotient.toString());
    }

    @Test
    public void testRounding() {
        BigDecimal bd = new BigDecimal("2.34567");
        BigDecimal rounded = bd.setScale(3, RoundingMode.HALF_UP);
        assertEquals("2.346", rounded.toString());
    }

    @Test
    public void testMathContextPrecision() {
        BigDecimal bd1 = new BigDecimal("123.456");
        BigDecimal bd2 = new BigDecimal("7.89");

        MathContext mc = new MathContext(4); // 4 位有效数字
        BigDecimal result = bd1.multiply(bd2, mc);
        assertEquals("974.1", result.toString());
    }

    @Test
    public void testComparison() {
        BigDecimal bd1 = new BigDecimal("10.0");
        BigDecimal bd2 = new BigDecimal("10.00");

        assertEquals(0, bd1.compareTo(bd2));
        assertNotEquals(bd1, bd2);
    }

    @Test
    public void testNegateAndAbs() {
        BigDecimal bd = new BigDecimal("-5.5");
        assertEquals("5.5", bd.abs().toString());
        assertEquals("-5.5", bd.negate().negate().toString());
    }
}
