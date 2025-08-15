package com.alpha.learn.jdk;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;

@SuppressWarnings("ALL")
@Slf4j
public class IteratorTests {

    @Test
    public void test() {
        TestIterator<String> testIterator = new TestIterator<>(5);
        testIterator.add("Apple");
        testIterator.add("Banana");
        testIterator.add("Cherry");

        Iterator<String> iterator = testIterator.iterator();
        while (iterator.hasNext()) {
            String next = iterator.next();
            log.info("next: {}", next);
        }

        for (int i = 0; i < testIterator.size(); i++) {
            log.info("for: {}", testIterator.get(i));
        }
    }

    //  Iterable<T> and Iterator<T>
    static class TestIterator<T> implements Iterable<T>, RandomAccess {
        private final T[] items;
        private int size;

        @SuppressWarnings("unchecked")
        public TestIterator(int capacity) {
            items = (T[]) new Object[capacity];
            size = 0;
        }

        public void add(T item) {
            if (size >= items.length) {
                throw new IllegalStateException("Full");
            }
            items[size++] = item;
        }

        public T get(int i) {
            return items[i];
        }

        public int size() {
            return size;
        }

        @NonNull
        @Override
        public Iterator<T> iterator() {
            return new MyIterator();
        }

        private class MyIterator implements Iterator<T> {
            private int cursor = 0;

            @Override
            public boolean hasNext() {
                return cursor < size;
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return items[cursor++];
            }

            @Override
            public void remove() {
                // do nothing and only for test interface implement to show things
            }
        }
    }
}
