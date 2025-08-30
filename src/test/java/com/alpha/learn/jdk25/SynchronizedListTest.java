package com.alpha.learn.jdk25;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;

@Slf4j
@SuppressWarnings("ALL")
public class SynchronizedListTest {

    /** <p>snapshot element data array</p>
     *  <p>not thread safe, only use their own iterator is thread safe.Even the internal iterator has some edge-case limitations. For example:
     *  <pre>{@code
     *   public boolean hasNext() {
     *      return cursor < snapshot.length;
     *   }}
     *  </pre>
     *
     *  When the cursor has already traversed all elements, if another thread performs a write operation but hasn’t yet copied the original array,
     *  the current thread may read incomplete data. Or, if the modification affects an index before the current cursor,
     *  the current thread will not see the updated data during this iteration.
     *  </p>
     *
     */
    @Test
    public void testCopyOnWriteArrayList() throws Exception {
        CopyOnWriteArrayList<String> copyOnWriteArrayList = new CopyOnWriteArrayList<>();
        copyOnWriteArrayList.addAll(Arrays.asList("a", "b", "c", "d", "e","f"));

        CountDownLatch countDownLatch = new CountDownLatch(1);
        CountDownLatch countDownLatch1 = new CountDownLatch(1);

        Thread t1 = new Thread(() -> {
            try {
                countDownLatch.await();
            } catch (InterruptedException ignore) {}
            copyOnWriteArrayList.remove("c");
            countDownLatch1.countDown();
        });

        Thread t2 = new Thread(() -> {
            int size = copyOnWriteArrayList.size();
            for (int i = 0; i < size; i++) {
                // 0, 1
                if (i == 1) {
                    countDownLatch.countDown();
                    try {
                        countDownLatch1.await();
                    } catch (InterruptedException ignore) {}
                }
                log.info("e: {}", copyOnWriteArrayList.get(i));
            }
        });

        t1.start();
        t2.start();
        t2.join();
        t1.join();
    }

    /**
     * boundary issues
     */
    @Test
    public void testCopyOnWriteArrayList2() throws Exception {
        CopyOnWriteArrayList<String> copyOnWriteArrayList = new CopyOnWriteArrayList<>();
        copyOnWriteArrayList.addAll(Arrays.asList("a", "b", "c", "d", "e","f"));
        CountDownLatch countDownLatch = new CountDownLatch(1);
        CountDownLatch countDownLatch1 = new CountDownLatch(1);

        Thread t1 = new Thread(() -> {
            try {
                countDownLatch.await();
            } catch (InterruptedException ignore) {}
            // [a, b, c, d, e, f]
            log.info("Before copyOnWriteArrayList: {}", copyOnWriteArrayList.toString());
            copyOnWriteArrayList.set(1,"bb");
            countDownLatch1.countDown();
        });

        List<String> temp = new ArrayList<>();
        Thread t2 = new Thread(() -> {
            Iterator<String> iterator = copyOnWriteArrayList.iterator();
            int count = 0;
            while (iterator.hasNext()) {
                String next = iterator.next();
                temp.add(next);
                count++;
                if (count == 2) {
                    countDownLatch.countDown();
                    try {
                        countDownLatch1.await();
                    } catch (InterruptedException ignore) {}
                }
            }
        });

        t1.start();
        t2.start();
        t2.join();
        t1.join();

        // [a, b, c, d, e, f]
        log.info("After temp: {}", Arrays.toString(temp.toArray()));
        // [a, bb, c, d, e, f]
        log.info("After copyOnWriteArrayList: {}", copyOnWriteArrayList.toString());
    }
}
