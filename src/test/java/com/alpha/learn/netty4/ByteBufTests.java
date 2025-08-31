package com.alpha.learn.netty4;

import io.netty.buffer.*;
import io.netty.util.IllegalReferenceCountException;
import io.netty.util.ResourceLeakDetector;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
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

    @Test
    public void testDirect() {
        UnpooledByteBufAllocator unpooledAlloc = UnpooledByteBufAllocator.DEFAULT;
        boolean directBufferPooled = unpooledAlloc.isDirectBufferPooled();
        Assertions.assertFalse(directBufferPooled);
        ByteBuf buf = unpooledAlloc.buffer(1024);
        buf.writeByte(1);
        Assertions.assertEquals(1, buf.refCnt());
        Assertions.assertTrue(buf.isDirect());

        PooledByteBufAllocator pooledAlloc = PooledByteBufAllocator.DEFAULT;
        Assertions.assertTrue(pooledAlloc.isDirectBufferPooled());
        ByteBuf buf1 = pooledAlloc.buffer(1024);
        Assertions.assertEquals(1, buf1.refCnt());
        Assertions.assertTrue(buf1.isDirect());

        byte b = buf.readByte();
        Assertions.assertEquals(1, b);
        buf.release();
        buf1.release();
    }

    @Test
    public void testByteBufDefaultAllocator() {
        ByteBufAllocator alloc = ByteBufAllocator.DEFAULT; // default pooling
        ByteBuf buf = alloc.buffer(1024);
        Assertions.assertEquals(1, buf.refCnt());
        Assertions.assertTrue(buf.isDirect());
        buf.release();
    }

    @Test
    public void testWrappedByteBuf() {
        byte[] bytes = new byte[]{1,2,3,4,5,6,7,8,9,10};
        ByteBuf byteBuf = Unpooled.wrappedBuffer(bytes);
        Assertions.assertEquals(1, byteBuf.refCnt());
        byteBuf.release();
    }

    @Test
    public void testCompositeByteBuf() {
        PooledByteBufAllocator pooledAlloc = PooledByteBufAllocator.DEFAULT;
        CompositeByteBuf composited = new CompositeByteBuf(pooledAlloc, true, 32);
        ByteBuf buf1 = pooledAlloc.buffer(1024);
        buf1.writeByte(1);
        ByteBuf buf2 = pooledAlloc.buffer(1024);
        buf2.writeByte(1);

        composited.addFlattenedComponents(true, buf1);
        composited.addFlattenedComponents(true, buf2);

        composited.readByte();
        Assertions.assertEquals(1, composited.refCnt());
        Assertions.assertEquals(2, composited.writerIndex());
        Assertions.assertEquals(1, composited.readerIndex());

        composited.release();
        Assertions.assertEquals(0, composited.refCnt());
        Assertions.assertEquals(0, buf2.refCnt());
        Assertions.assertEquals(0, buf1.refCnt());
    }
    @Test
    public void testEnsureCapacity() {
        ByteBufAllocator alloc = ByteBufAllocator.DEFAULT;
        ByteBuf byteBuf = alloc.directBuffer(1);
        byteBuf.writeByte(1);
        log.info("buf capacity is {}", byteBuf.capacity());
        Assertions.assertEquals(1, byteBuf.refCnt());

        byteBuf.writeInt(4);
        log.info("buf new capacity is {}", byteBuf.capacity());
        byteBuf.release();
    }
}
