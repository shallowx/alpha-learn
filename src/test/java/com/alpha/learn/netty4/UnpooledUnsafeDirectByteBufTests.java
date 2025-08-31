package com.alpha.learn.netty4;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.buffer.UnpooledUnsafeDirectByteBuf;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UnpooledUnsafeDirectByteBufTests {

    @Test
    public void test() {
        ByteBuf byteBuf = new UnpooledUnsafeDirectByteBuf(UnpooledByteBufAllocator.DEFAULT, 256, 1024);
        Assertions.assertEquals(1, byteBuf.refCnt());

        byteBuf.writeByte(1);
        byteBuf.release();
        Assertions.assertEquals(0, byteBuf.refCnt());
    }
}
