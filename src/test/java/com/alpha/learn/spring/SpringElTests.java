package com.alpha.learn.spring;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

@Slf4j
public class SpringElTests {

    @Test
    public void test() {
        ExpressionParser parser = new SpelExpressionParser();
        Expression expression = parser.parseExpression("'hello world'");
        log.info("expression : {}", expression.getValue(String.class));

        Expression expression1 = parser.parseExpression("'hello world'.concat('!')");
        log.info("expression : {}", expression1.getValue(String.class));
    }
}
