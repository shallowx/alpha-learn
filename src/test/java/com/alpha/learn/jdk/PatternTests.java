package com.alpha.learn.jdk;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class PatternTests {
    // https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html
    @Test
    public void test() {
        String p = "[abc]";
        pattern(p, "a");

        String p1 = "[^abc]";
        pattern(p1, "d");

        String p2 = "[a-zA-Z&&[^a-c]].+\\d\\p{Lower}.+";
        pattern(p2, "hgj1ppP");

        String telePhone = "^[1][3-9]\\d{9}";
        pattern(telePhone, "13123456789");

        String email = "^[\\w_.]+@gmail\\.com$";
        pattern(email, "test.1_1@gmail.com");

        String phone = "^[0]\\d{2}[-]\\d{7}$";
        pattern(phone, "13");
        pattern(phone, "120-1234567");
        pattern(phone, "020-1234567");

        String space = "^[\\s+]|[\\s+]$";
        pattern(space, "    hello   ");
    }

    private void pattern(String regex, String s) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(s);
        log.info("matcher:{}", matcher.matches());
    }
}
