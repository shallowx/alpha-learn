package com.alpha.learn.jdk25;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.stream.IntStream;

// mock benign data race, and like string hash
@Slf4j
@SuppressWarnings("ALL")
public class BenignDataRaceTests {

    @Test
    public void test() {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        IntStream.range(1, 8).forEach(i -> executor.submit(() -> {
            String v = getElement("" + 1);
            log.debug("key={}, v={}", i, v);
        }));
    }

    private static final ConcurrentHashMap<String, String> mockCache = new ConcurrentHashMap<>();
    private static final Semaphore semaphore = new Semaphore(4);
    private String getElement(String key) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("key is blank");
        }

        if (mockCache.containsKey(key)) {
            log.info("from mock cache, key={}, thread-name: {}", key, Thread.currentThread().getName());
            return mockCache.get(key);
        }

        try {
            if (semaphore.tryAcquire()) {
                log.info("key[{}] get semaphore, thread-name:{}", key, Thread.currentThread().getName());
                if (mockCache.containsKey(key)) {
                    log.info("second from mock cache, key={}, thread-name: {}", key, Thread.currentThread().getName());
                    return mockCache.get(key);
                }

                String v = mockGet(key);
                mockCache.put(key, v);
                log.info("from mock get, key={}, thread-name: {}", key, Thread.currentThread().getName());
                return v;
              }}catch (Exception e) {
            // handle exception
          } finally {
            semaphore.release();
        }
        // if key is not exists and return mock value
        return mockCache.computeIfAbsent(key, v -> "DEFAULT_MOCK_VALUE");
    }

    private String mockGet(String key) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("key is blank");
        }

        return "MOCK_" + key;
    }
}
