package com.alpha.learn.jdk25;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("ALL")
@Slf4j
public class MapTests {

    @Test
    public void testConstantString() {
        String s1 = "abc";
        String s2 = new  String("abc");

        Map<String, Object> hashMap = new HashMap<>();
        hashMap.put(s1,"test_hashmap");
        hashMap.put(s2,"test_hashmap");

        Assertions.assertEquals(s1, s2);
        Assertions.assertEquals(s1.hashCode(), s2.hashCode());
        log.info("hashMap:{}",hashMap.size() == 1);

        IdentityHashMap<String, Object> identityHashMap = new IdentityHashMap<>();
        identityHashMap.put(s1,"test_identityHashMap");
        identityHashMap.put(s2,"test_identityHashMap");
        Assertions.assertEquals(s1, s2);
        log.info("hashMap:{}",identityHashMap.size() == 2);
    }

    @Test
    public void testObjectAsKey () {
        Key key = new Key("k");
        Key key1 = new Key("k");

        Map<Object,String> hashMap = new HashMap<>();
        hashMap.put(key, "test_hashmap");
        hashMap.put(key1, "test_hashmap");
        Assertions.assertEquals(key, key1);
        log.info("hashMap:{}",hashMap.size() == 1);

        IdentityHashMap<Object, Object> identityHashMap = new IdentityHashMap<>();
        identityHashMap.put(key, "test_identityHashMap");
        identityHashMap.put(key1, "test_identityHashMap");
        log.info("hashMap:{}",hashMap.size() == 2);
    }

    @AllArgsConstructor
    static class Key {
        @Getter
        private String name;

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            Key key = (Key) o;
            return Objects.equals(name, key.name);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(name);
        }
    }
}
