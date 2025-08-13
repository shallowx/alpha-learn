package com.alpha.learn.jdk;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.List;

@Slf4j
public class ArrayListTest {

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
     * }
     */
    //     49: invokeinterface #44,  2           // InterfaceMethod java/util/List.remove:(Ljava/lang/Object;)Z
    // modCount++
    @Test
    public void testFastFail() {
        List<String> containers = new java.util.ArrayList<>(List.of("foo", "bar", "test", "oo", "xx"));
        for(String container : containers) {
            containers.remove(container);
        }
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
     * }
     */
    //  *         55: invokeinterface #51,  1           // InterfaceMethod java/util/Iterator.remove:()V
    // modCount++, expectedModCount = modCount;
    @Test
    public void testFastFail1() {
        List<String> containers = new java.util.ArrayList<>(List.of("foo", "bar", "test", "oo", "xx"));
        Iterator<String> iterator = containers.iterator();
        while (iterator.hasNext()) {
            if (!iterator.next().equals("foo")) {
                iterator.remove();
            }
        }
       log.info("containers:{}", containers);
    }

}
