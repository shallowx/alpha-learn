package com.alpha.learn.jdk25;

import lombok.NonNull;
import org.junit.jupiter.api.Test;
import java.io.Serializable;
import java.util.Comparator;

@SuppressWarnings("ALL")
public class CharSequenceTests {

    @Test
    public void test() {
        TestCharSequence sequence = new TestCharSequence("test");
        Comparator<? extends TestCharSequence> comparator = Comparator.naturalOrder();
        Comparator<? extends TestCharSequence> comparator2 = Comparator.reverseOrder();
        sequence.sort(Comparator.naturalOrder());
        sequence.sort(comparator);
        sequence.sort(comparator2);
        sequence.compareTo(new  TestCharSequence("test"));
    }

    static class TestCharSequence implements CharSequence, Serializable, Comparable<TestCharSequence> {

        private final String str;

        public TestCharSequence(String str) {
            this.str = str;
        }

        @Override
        public int length() {
            return str.length();
        }

        @Override
        public char charAt(int index) {
            return str.charAt(index);
        }

        @NonNull
        @Override
        public CharSequence subSequence(int start, int end) {
            return str.subSequence(start, end);
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString();
        }

        void sort(Comparator<? extends TestCharSequence> comparator) {
            // do nothing and only for view
        }

        @Override
        public int compareTo(TestCharSequence o) {
            return str.compareTo(o.str);
        }
    }
}
