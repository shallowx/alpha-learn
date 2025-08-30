package com.alpha.learn.netty4;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.util.IllegalReferenceCountException;
import io.netty.util.ResourceLeakDetector;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ByteBufTests {

    @Test
    public void testRefCnt() {
        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.PARANOID);
        UnpooledByteBufAllocator alloc = UnpooledByteBufAllocator.DEFAULT;
        ByteBuf buf = alloc.buffer(1024);
        Assertions.assertEquals(1, buf.refCnt());
        buf.release();
        Assertions.assertEquals(0, buf.refCnt());
        Assertions.assertThrows(IllegalReferenceCountException.class, () -> {
            buf.writeByte(1);
        });
    }

    @Test
    public void testRetain() {
        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.PARANOID);
        UnpooledByteBufAllocator alloc = UnpooledByteBufAllocator.DEFAULT;
        ByteBuf buf = alloc.buffer(1024);
        buf.writeByte(1);
        Assertions.assertEquals(1, buf.refCnt());
        ByteBuf retainedBuf = buf.retain(1);

        Assertions.assertEquals(2, retainedBuf.refCnt());
        Assertions.assertEquals(2, buf.refCnt());

        buf.release();
        Assertions.assertEquals(1, buf.refCnt());
        Assertions.assertEquals(1, retainedBuf.refCnt());

        byte b = retainedBuf.readByte();
        Assertions.assertEquals(1, b);
        retainedBuf.release();
        Assertions.assertEquals(0, retainedBuf.refCnt());
        Assertions.assertEquals(0, retainedBuf.refCnt());
    }

    @Test
    public void testCopy() {
        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.PARANOID);
        UnpooledByteBufAllocator alloc = UnpooledByteBufAllocator.DEFAULT;
        ByteBuf buf = alloc.buffer(1024);
        buf.writeByte(1);
        Assertions.assertEquals(1, buf.refCnt());

        ByteBuf copyBuf = buf.copy();
        Assertions.assertEquals(1, copyBuf.refCnt());
        Assertions.assertEquals(1, buf.refCnt());

        buf.readByte();
        buf.release();
        Assertions.assertEquals(0, buf.refCnt());
        Assertions.assertEquals(1, copyBuf.refCnt());

        byte b = copyBuf.readByte();
        Assertions.assertEquals(1, b);
        copyBuf.release();
        Assertions.assertEquals(0, copyBuf.refCnt());
    }

    @Test
    public void testDuplicate() {
        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.PARANOID);
        UnpooledByteBufAllocator alloc = UnpooledByteBufAllocator.DEFAULT;
        ByteBuf buf = alloc.buffer(1024);
        buf.writeByte(1);
        Assertions.assertEquals(1, buf.refCnt());

        ByteBuf duplicate = buf.duplicate();
        Assertions.assertEquals(1, duplicate.refCnt());
        Assertions.assertEquals(1, buf.refCnt());

        buf.readByte();
        duplicate.readByte();

        buf.release();
        Assertions.assertEquals(0, buf.refCnt());
        Assertions.assertEquals(0, duplicate.refCnt());
    }
}
