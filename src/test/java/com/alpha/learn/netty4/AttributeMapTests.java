package com.alpha.learn.netty4;

import io.netty.channel.ChannelFuture;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@Slf4j
public class AttributeMapTests {
    @Test
    public void test() {
        AttributeKey<String> key = AttributeKey.valueOf("hello");
        EmbeddedChannel channel = new EmbeddedChannel();
        channel.attr(key).set("test");

        Attribute<String> v = channel.attr(key);
        log.info("v={}", v.get());

        ChannelFuture f = channel.writeOneInbound("hello");
        f.addListener(new GenericFutureListener<ChannelFuture>() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                log.info("f={}", future.isSuccess());
            }
        });
    }

    @Test
    public void testExists() {
        String name = "test";
        assertFalse(AttributeKey.exists(name));
        AttributeKey<String> attr = AttributeKey.valueOf(name);

        assertTrue(AttributeKey.exists(name));
        assertNotNull(attr);
    }

    @Test
    public void testValueOf() {
        String name = "test1";
        assertFalse(AttributeKey.exists(name));
        AttributeKey<String> attr = AttributeKey.valueOf(name);
        AttributeKey<String> attr2 = AttributeKey.valueOf(name);

        assertSame(attr, attr2);
    }

    @Test
    public void testNewInstance() {
        String name = "test2";
        assertFalse(AttributeKey.exists(name));
        AttributeKey<String> attr = AttributeKey.newInstance(name);
        assertTrue(AttributeKey.exists(name));
        assertNotNull(attr);

        try {
            AttributeKey.<String>newInstance(name);
            fail();
        } catch (IllegalArgumentException e) {
            // expected
        }
    }
}
