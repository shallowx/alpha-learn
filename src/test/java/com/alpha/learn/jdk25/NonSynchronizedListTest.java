package com.alpha.learn.jdk25;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Field;
import java.lang.reflect.InaccessibleObjectException;
import java.util.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings("ALL")
@Slf4j
public class NonSynchronizedListTest {

    /**
     *  public void testFastFail();
     *     Code:
     *          0: new           #7                  // class java/util/ArrayList
     *          3: dup
     *          4: ldc           #9                  // String foo
     *          6: ldc           #11                 // String bar
     *          8: ldc           #13                 // String test
     *         10: ldc           #15                 // String oo
     *         12: ldc           #17                 // String xx
     *         14: invokestatic  #19                 // InterfaceMethod java/util/List.of:(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/List;
     *         17: invokespecial #25                 // Method java/util/ArrayList."<init>":(Ljava/util/Collection;)V
     *         20: astore_1
     *         21: aload_1
     *         22: invokeinterface #28,  1           // InterfaceMethod java/util/List.iterator:()Ljava/util/Iterator;
     *         27: astore_2
     *         28: aload_2
     *         29: invokeinterface #32,  1           // InterfaceMethod java/util/Iterator.hasNext:()Z
     *         34: ifeq          58
     *         37: aload_2
     *         38: invokeinterface #38,  1           // InterfaceMethod java/util/Iterator.next:()Ljava/lang/Object;
     *         43: checkcast     #42                 // class java/lang/String
     *         46: astore_3
     *         47: aload_1
     *         48: aload_3
     *         49: invokeinterface #44,  2           // InterfaceMethod java/util/List.remove:(Ljava/lang/Object;)Z
     *         54: pop
     *         55: goto          28
     *         58: return
     *
     */
    // 49: invokeinterface #44,  2           // InterfaceMethod java/util/List.remove:(Ljava/lang/Object;)Z
    // modCount++
    @Test
    public void testFastFail() {
        List<String> containers = new ArrayList<>(List.of("foo", "bar", "test", "oo", "xx"));
        assertThrows(ConcurrentModificationException.class ,() -> {
            for(String container : containers) {
                containers.remove(container);
            }
        });
    }

    /**
     * public void testFastFail1();
     *     Code:
     *          0: new           #7                  // class java/util/ArrayList
     *          3: dup
     *          4: ldc           #9                  // String foo
     *          6: ldc           #11                 // String bar
     *          8: ldc           #13                 // String test
     *         10: ldc           #15                 // String oo
     *         12: ldc           #17                 // String xx
     *         14: invokestatic  #19                 // InterfaceMethod java/util/List.of:(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/List;
     *         17: invokespecial #25                 // Method java/util/ArrayList."<init>":(Ljava/util/Collection;)V
     *         20: astore_1
     *         21: aload_1
     *         22: invokeinterface #28,  1           // InterfaceMethod java/util/List.iterator:()Ljava/util/Iterator;
     *         27: astore_2
     *         28: aload_2
     *         29: invokeinterface #32,  1           // InterfaceMethod java/util/Iterator.hasNext:()Z
     *         34: ifeq          63
     *         37: aload_2
     *         38: invokeinterface #38,  1           // InterfaceMethod java/util/Iterator.next:()Ljava/lang/Object;
     *         43: checkcast     #42                 // class java/lang/String
     *         46: ldc           #9                  // String foo
     *         48: invokevirtual #48                 // Method java/lang/String.equals:(Ljava/lang/Object;)Z
     *         51: ifne          28
     *         54: aload_2
     *         55: invokeinterface #51,  1           // InterfaceMethod java/util/Iterator.remove:()V
     *         60: goto          28
     *         63: return
     *
     */
    // 55: invokeinterface #51,  1           // InterfaceMethod java/util/Iterator.remove:()V
    // modCount++ && expectedModCount = modCount;
    @Test
    public void testFastFail1() {
        List<String> containers = new ArrayList<>(List.of("foo", "bar", "test", "oo", "xx"));
        Iterator<String> iterator = containers.iterator();
        while (iterator.hasNext()) {
            if (!iterator.next().equals("foo")) {
                iterator.remove();
            }
        }
       log.info("containers:{}", containers);
    }

    // --add-opens java.base/jdk.internal.misc=ALL-UNNAMED --add-opens java.base/java.util=ALL-UNNAMED
    @Test
    public void testTrimToSize() throws Exception {
        ArrayList<String> list = new ArrayList<>(100);
        list.add("a");
        list.add("b");

        Assertions.assertThrows(InaccessibleObjectException.class ,() -> {
            log.info("Before trim, capacity: {}", capacity(list));
            list.trimToSize();
            log.info("After trim, capacity: {}", capacity(list));
        });
    }

    private static int capacity(ArrayList<?> list) throws Exception {
        Field elementDataField = ArrayList.class.getDeclaredField("elementData");
        elementDataField.setAccessible(true);
        Object[] elementData = (Object[]) elementDataField.get(list);
        return elementData.length;
    }

    @Test
    public void testToArray() throws Exception {
        ArrayList<String> list = new ArrayList<>();
        list.add("a");
        list.add("b");
        Object[] array = list.toArray();
        String[] stringArray = list.toArray(new String[]{});
        log.info("array:{}", Arrays.toString(stringArray));
        log.info("object array:{}", Arrays.toString(array));
    }

    // cursor mark
    Object[] objects =  new Object[]{10,"20",30};
    @Test
    public void testListIterator() {
        List<String> containers = new ArrayList<>(List.of("foo", "bar", "test", "oo", "xx"));
        ListIterator<String> iterator = containers.listIterator();
        while (iterator.hasNext()) {
            String s = iterator.next();
            log.info("e: {}, hasPrevious: {}", s,  iterator.hasPrevious());
        }
        log.info("---------------------------");
        iterator.remove();
        ListIterator<String> iterator1 = containers.listIterator(2);
        while (iterator1.hasNext()) {
            String s = iterator1.next();
            log.info("e: {}, hasPrevious: {}", s,  iterator1.hasPrevious());
        }
        log.info("---------------------------");
        List<String> strings = containers.subList(2, 3);
        ListIterator<String> iterator2 = strings.listIterator();
        while (iterator2.hasNext()) {
            String s = iterator2.next();
            log.info("e: {}, hasPrevious: {}", s,  iterator2.hasPrevious());
        }
        containers.clear();
        log.info("size:{}", containers.size());
        Object[] copy = objects;
        copy[0] = "test";
        log.info("copy:{}", Arrays.toString(copy));
        log.info("objects:{}", Arrays.toString(objects));
    }

    @Test
    public void testEmptyElementData() throws Exception {
        ArrayList<String> empty = new ArrayList<>(0);
        empty.add("test for common element data");

        ArrayList<String> defaultEmpty = new ArrayList<>();
        defaultEmpty.add("test for default element data");
    }
}
