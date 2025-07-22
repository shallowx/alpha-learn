package com.alpha.learn.vt;

import org.junit.jupiter.api.Test;

public class StringTests {

    @Test
    public void test() {
        String a = "hello";
        String b = "he" + "llo";
        String c = new String("hello");

        System.out.println(a == b);
        System.out.println(a == c);
        System.out.println(a.equals(c));
    }

    @Test
    public void testReverse() {
        String s = "hello";
        System.out.println(new String(reverse(s.toCharArray())));
    }

    private char[] reverse(char[] s) {
        int n = s.length;
        for (int left = 0, right = n - 1; left < right; ++left, --right) {
            char tmp = s[left];
            s[left] = s[right];
            s[right] = tmp;
        }

        return s;
    }

    @Test
    public void testExchange() {
        exchange(2,4);
        exchange0(5,4);
    }

    private void exchange(int a, int b) {
        a = a ^ b ;
        b = a ^ b ;
        a = a ^ b ;

        System.out.println("a:" + a + " b:" + b);
    }

    private void exchange0(int a, int b) {
        a = a + b ;
        b = a - b ;
        a = a - b ;

        System.out.println("a:" + a + " b:" + b);
    }
}
