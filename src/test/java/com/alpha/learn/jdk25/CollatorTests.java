package com.alpha.learn.jdk25;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.text.Collator;
import java.util.Locale;

@SuppressWarnings("ALL")
@Slf4j
public class CollatorTests {

    @Test
    public void test() {
        String s = "café";
        String t = "cafe";
        log.info("equals: {}", s.equals(t));

        Collator collator = Collator.getInstance(Locale.FRANCE);
        collator.setStrength(Collator.PRIMARY);
        log.info("compare: {}", collator.compare(s, t));
    }
}
