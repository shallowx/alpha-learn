package com.alpha.learn.jdk;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import java.nio.ByteBuffer;

@Slf4j
public class NIOBufferTests {

    @Test
    public void test() {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        log.info("mark:{}, limit:{}, capacity:{}, position:{}", buffer.mark(), buffer.limit(), buffer.capacity(), buffer.position());

        buffer.putInt(1);
        buffer.putInt(2);
        log.info("mark:{}, limit:{}, capacity:{}, position:{}", buffer.mark(), buffer.limit(), buffer.capacity(), buffer.position());

        buffer.flip();
        buffer.getInt();
        log.info("mark:{}, limit:{}, capacity:{}, position:{}", buffer.mark(), buffer.limit(), buffer.capacity(), buffer.position());

        int remaining = buffer.remaining();
        log.info("remaining:{}, position:{}", remaining, buffer.position());

        buffer.compact();
        int remaining1 = buffer.remaining();
        log.info("remaining1:{}, position:{}", remaining1, buffer.position());
        boolean b = buffer.hasRemaining();
        log.info("b:{}, position:{}", b, buffer.position());
    }

    @Test
    public void test2() {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.putInt(1);
        buffer.putInt(2);
        log.info("limit:{}, capacity:{}, position:{}", buffer.limit(), buffer.capacity(), buffer.position());

        buffer.flip();
        ByteBuffer slice = buffer.slice();
        log.info("limit:{}, position:{}, capacity: {}", slice.limit(), slice.position(), slice.capacity());

        buffer.compact();
        buffer.putInt(3);

        log.info("limit:{}, position:{}, capacity: {}", slice.limit(), slice.position(), slice.capacity());
    }
}
