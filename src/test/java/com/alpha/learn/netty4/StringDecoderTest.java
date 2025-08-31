/*
 * Copyright 2021 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.alpha.learn.netty4;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.util.CharsetUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StringDecoderTest {

    @Test
    public void testDecode() {
        String msg = "abc";
        ByteBuf byteBuf = Unpooled.copiedBuffer(msg, CharsetUtil.UTF_8);
        EmbeddedChannel channel = new EmbeddedChannel(new StringDecoder());
        assertTrue(channel.writeInbound(byteBuf));
        String result = channel.readInbound();
        assertEquals(msg, result);
        assertNull(channel.readInbound());
        assertFalse(channel.finish());
    }
}
