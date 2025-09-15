package com.alpha.learn.netty4;

import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.AttributeMap;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

public class AttributeMapTests {


    static class AttributeMapContinues implements AttributeMap, Iterable<AttributeKey<?>> {
        @Override
        public <T> Attribute<T> attr(AttributeKey<T> key) {
            return null;
        }

        @Override
        public <T> boolean hasAttr(AttributeKey<T> key) {
            return false;
        }

        @Override
        public Iterator<AttributeKey<?>> iterator() {
            return null;
        }

        @Override
        public void forEach(Consumer<? super AttributeKey<?>> action) {
            Iterable.super.forEach(action);
        }

        @Override
        public Spliterator<AttributeKey<?>> spliterator() {
            return Iterable.super.spliterator();
        }
    }
}
