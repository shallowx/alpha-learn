package com.alpha.learn.jdk25;

import lombok.Getter;
import org.junit.jupiter.api.Test;

import java.lang.ref.Cleaner;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class CleanerTests {

    @Test
    public void test() {
        List<OffBuffer> buffers = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            OffBuffer buf = new OffBuffer(1024 * 1024);
            buffers.add(buf);
        }

        buffers.getFirst().free();
        buffers.remove(1);
        System.gc();
        System.out.println("Main done");
    }

    private static final Cleaner cleaner = Cleaner.create();

    static class OffBuffer {
        @Getter
        private final ByteBuffer buffer;
        private final Cleaner.Cleanable cleanable;

        private static class Deallocator implements Runnable {
            private ByteBuffer buf;

            Deallocator(ByteBuffer buf) {
                this.buf = buf;
            }

            @Override
            public void run() {
                if (buf != null) {
                    System.out.println("Cleaning buffer: " + buf);
                    buf = null;
                }
            }
        }

    public OffBuffer(int size) {
        this.buffer = ByteBuffer.allocateDirect(size);
        this.cleanable = cleaner.register(this, new Deallocator(buffer));
    }

    public void free() {
        cleanable.clean();
    }
  }
}
