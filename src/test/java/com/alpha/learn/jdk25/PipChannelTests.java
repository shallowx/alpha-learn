package com.alpha.learn.jdk25;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Pipe;

@Slf4j
public class PipChannelTests {
    @Test
    public void testPipChannel() throws IOException {
        Pipe p = Pipe.open();
        Pipe.SinkChannel sinkChannel = p.sink();
        sinkChannel.write(ByteBuffer.wrap("Hello".getBytes()));

        Pipe.SourceChannel sourceChannel = p.source();
        ByteBuffer buf = ByteBuffer.allocate(5);
        sourceChannel.read(buf);
        log.info("Read: {}", new String(buf.array()));
    }
}
