package com.alpha.learn.jdk25;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Properties;

@Slf4j
public class SystemTests {

    @Test
    public void testSystem() {
        Map<String, String> envs = System.getenv();
        for (Map.Entry<String, String> entry : envs.entrySet()) {
            log.info("{} = {}", entry.getKey(), entry.getValue());
        }

        log.info("\n -----------spilt----------- \n");
        System.setProperty("test", "test");

        Properties properties = System.getProperties();
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            log.info("{} = {}", entry.getKey(), entry.getValue());
        }
    }
}
