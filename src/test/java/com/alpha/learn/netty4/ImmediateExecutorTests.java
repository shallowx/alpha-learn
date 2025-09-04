package com.alpha.learn.netty4;

import io.netty.buffer.Unpooled;
import io.netty.channel.DefaultChannelPromise;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.concurrent.FutureTask;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNull;

@Slf4j
public class ImmediateExecutorTests {

    @Test
    public void testImmediateExecutor() {
        ImmediateEventExecutor immediateExecutor = ImmediateEventExecutor.INSTANCE;
        EmbeddedChannel channel = new EmbeddedChannel();
        Promise<Void> promise = new DefaultChannelPromise(channel, immediateExecutor);
        channel.writeInbound(Unpooled.wrappedBuffer("hello".getBytes(CharsetUtil.UTF_8)));
        promise.addListener(new GenericFutureListener<Future<? super Void>>() {
            @Override
            public void operationComplete(Future<? super Void> future) throws Exception {
                if (future.isSuccess()) {
                    log.info("ImmediateExecutorTests testImmediateExecutor completed");
                } else {
                    log.info("ImmediateExecutorTests testImmediateExecutor failed");
                }
            }
        });
        promise.trySuccess(null);
    }

    @Test
    public void testExecuteNullRunnable() {
        assertThrows(NullPointerException.class, new Executable() {
            @Override
            public void execute() {
                ImmediateExecutor.INSTANCE.execute(null);
            }
        });
    }

    @Test
    public void testExecuteNonNullRunnable() throws Exception {
        FutureTask<Void> task = new FutureTask<Void>(new Runnable() {
            @Override
            public void run() {
                // NOOP
            }
        }, null);
        ImmediateExecutor.INSTANCE.execute(task);
        assertTrue(task.isDone());
        assertFalse(task.isCancelled());
        assertNull(task.get());
    }
}
