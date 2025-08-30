package com.alpha.learn.jdk25;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import java.lang.constant.*;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

/**
 * <p>use Unicode represent String, so every character has only Unicode value.
 * Unicode can represent all characters in the world, including emoticons</p>
 *
 * ASCII
 * <p>standard characters set(ASCII 7bit): https://www.ascii-code.com/ASCII, and value range is [0, 127], the real length is 128</p>
 * <p>ASCII 8bit: https://www.ascii-code.com/, and value range is [0, 255], the real length is 256</p>
 *
 * <p>benign data race about hashcode cache</p>
 */
@Slf4j
@SuppressWarnings("ALL")
public class StringTests {

    private String minWindow(String s, String t) {
        if ((s == null || s.length() == 0) || (t == null || t.length() == 0) || s.length() < t.length()) {
            return "";
        }
        int[] container = new int[256];
        for(int i = 0; i < t.length(); i++) {
            container[t.charAt(i)]--;
        }
        int start = 0, minLen = Integer.MAX_VALUE, count = t.length();
        for(int l  = 0, r = 0; r < s.length(); r++) {
            if(container[s.charAt(r)]++ < 0) {
                count--;
            }

            if (count == 0) {
                while(container[s.charAt(l)] > 0) {
                    container[s.charAt(l)]--;
                    l++;
                }

                if (r - l + 1 < minLen) {
                    minLen = r - l + 1;
                    start = l;
                }
            }
        }
        return minLen == Integer.MAX_VALUE ? "" : s.substring(start, start + minLen);
    }

    /**
     * public static void main(java.lang.String[]);
     *     Code:
     *          0: ldc           #26                 // String hello
     *          2: astore_1
     *          3: iconst_0
     *          4: istore_2
     *          5: iload_2
     *          6: bipush        10
     *          8: if_icmpge     24
     *         11: aload_1
     *         12: invokedynamic #28,  0             // InvokeDynamic #0:makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;
     *         17: astore_1
     *         18: iinc          2, 1
     *         21: goto          5
     *         24: return
     */
    public static void main(String[] args) {
        String s = "hello";
        s += "world";
        s += "world";
    }

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
        System.out.println(new String(reverse2(s.toCharArray())));
    }

    // 😊🌍
    @Test
    public void testReverseUnicode() {
        String s = "你好😊World🌍";
        log.info("support unicode:{} ", reverseUnicode(s));
        log.info("not support unicode:{} ", new String(reverse(s.toCharArray())));
        log.info("not support unicode:{} ", new String(reverse2(s.toCharArray())));
    }

    private String reverseUnicode(String s) {
        if (s == null || s.isEmpty()) {
            return s;
        }

        int[] codePoints = s.codePoints().toArray();
        int left = 0, right = codePoints.length - 1;
        while (left < right) {
            int temp = codePoints[left];
            codePoints[left] = codePoints[right];
            codePoints[right] = temp;
            left++;
            right--;
        }
        return new String(codePoints, 0, codePoints.length);
    }

    private char[] reverse(char[] s) {
        int n = s.length;
        char[] result = new char[n];
        int index = 0;
        for (int i = n; i > 0; i--) {
            char c = s[i - 1];
            result[index++] = c;
        }
        return result;
    }

    private char[] reverse2(char[] s) {
        int n = s.length;
        for (int left = 0, right = n - 1; left < right; left++, right--) {
            char temp = s[left];
            s[left] = s[right];
            s[right] = temp;
        }
        return s;
    }

    @Test
    public void testSubOfContent() {
        String a = "abcdefghijklmnopqrstwxyzABCDEFGHIJKLMNOPQRSTWXYZ";
        List<Boolean> results = new ArrayList<>(10000);
        for (int i = 0; i < 100_000_000; i++) {
            int random = ThreadLocalRandom.current().nextInt(0, a.length());
            String dst = a.substring(random);
            boolean result = subOfContent(a, dst);
            results.add(result);
        }

        int count = 0;
        for (boolean b : results) {
            if (b == false) {
                log.info("result: {}", b);
                break;
            } else {
                count++;
            }
        }
        log.info("---- count: {} ----", count);
    }

    private boolean subOfContent(String src, String dst) {
        if (src == null || dst == null) {
            return true;
        }

        boolean right = false;
        int srcLen = src.length(), dstLen = dst.length();
        char c = dst.charAt(0);
        int first = src.indexOf(c);
        if (srcLen < dstLen || (first == -1)) {
            return right;
        }

        int k = 0;
        while (first <  srcLen || k < dstLen) {
            if (src.charAt(first++) != dst.charAt(k++)) {
                first = src.indexOf(c, first, srcLen);
                if (first == -1) {
                    right = false;
                    break;
                }
            } else {
                right = true;
            }
        }
        return right;
    }

    @Test
    public void testCounter() {
        String a = "hello";
        HashMap<Character, Integer> counter = counter(a);
        log.info("counter: {}", counter);
    }

    private HashMap<Character, Integer> counter(String dst) {
        if (dst == null || dst.length() == 0) {
            throw new IllegalArgumentException();
        }

        HashMap<Character, Integer> map = new HashMap<>();
        for (int i = 0; i < dst.length(); i++) {
            char c = dst.charAt(i);
            if (!map.containsKey(c)) {
                map.put(c, 1);
            } else {
                Integer count = map.get(c);
                map.put(c, ++count);
            }
        }
        return map;
    }

    @Test
    public void testExchange() {
        exchange(2,4);
        exchange0(5,4);
    }

    @Test
    public void testStringHascode() {
        String a = new String();
        int hashCode = a.hashCode();
        log.info("hashcode: {}", hashCode);

        String b = new String("hello");
        int hashCode2 = b.hashCode();
        log.info("hashCode2: {}", hashCode2);
    }

    @Test
    public void testDescribeConstable() {
        String a = "hello";
        Optional<String> optional = a.describeConstable();
        log.info("b's outcome: {}", optional.get());

        String b = new String("hello");
        Optional<String> optional1 = b.describeConstable();
        log.info("a's outcome: {}", optional1.get());
    }

    @Test
    public void testResolveConstantDesc() throws ReflectiveOperationException {
        String a = "hello";
        String s = a.resolveConstantDesc(MethodHandles.lookup());
        log.info("s's outcome: {}", s);

        MethodHandle concatHandle = MethodHandles.lookup()
                .findVirtual(String.class, "concat",
                        MethodType.methodType(String.class, String.class));

        DynamicConstantDesc<String> dynamicDesc = DynamicConstantDesc.ofNamed(
                ConstantDescs.BSM_INVOKE,
                "concatString",
                ConstantDescs.CD_String,
                MethodHandleDesc.ofMethod(
                        DirectMethodHandleDesc.Kind.VIRTUAL,
                        ClassDesc.of("java.lang.String"),
                        "concat",
                        MethodTypeDesc.of(ConstantDescs.CD_String, ConstantDescs.CD_String)
                ),
                "Hello, ", "World!"
        );

        String result = dynamicDesc.resolveConstantDesc(MethodHandles.lookup());
        log.info("result: {}", result);
    }

    @Test
    public void testStringMethods() throws ReflectiveOperationException {
        String a = "\n  hello, world!  \n";
        log.info("strip: ---{}---", a.strip());
        log.info("strip indent: ---{}---", a.stripIndent());
        log.info("strip leading: ---{}---", a.stripLeading());
        log.info("strip trailing: ---{}---", a.stripTrailing());
        log.info("trim: ---{}---",  a.trim());

        String b = "hello,world!";
        log.info("substring with begin: {}", b.substring(1));
        log.info("substring with end and begin: {}", b.substring(1, 4));

        log.info("charAt: {}", b.charAt(0));
        IntStream stream = b.chars();
        log.info("stream: {}",  stream.toString());

        String upperCase = stream.map(Character::toUpperCase)
                .collect(
                        StringBuilder::new,
                        StringBuilder::appendCodePoint,
                        StringBuilder::append
                ).toString();
        log.info("upperCase: {}", upperCase);


        long count =  upperCase.chars().filter(Character::isDigit).count();
        log.info("count: {}", count);

        StringBuilder sb = new StringBuilder();
        sb = sb.appendCodePoint(upperCase.codePointAt(0))
                .appendCodePoint(upperCase.codePointBefore(3))
                .appendCodePoint(upperCase.codePointAt(2))
                .append(upperCase.offsetByCodePoints(0, 2))
                .append(upperCase.codePointCount(0, upperCase.length() - 1));
        log.info("sb: {}", sb.toString());
        log.info("regionMatches: {}", b.regionMatches( true,0, upperCase, 0, upperCase.length()));
        log.info("offsetCodePoint: {}", upperCase.offsetByCodePoints(1, 2));
    }

    @Test
    public void testIntern() {
        String s =  new String("hello");
        String t = "hello";
        String intern = s.intern();

        log.info("==: {}", s == t);
        log.info("==: {}", s == intern);
        log.info("==: {}", t == intern);
        log.info("s: {}, t: {}, intern: {}", System.identityHashCode(s), System.identityHashCode(t), System.identityHashCode(intern));
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
