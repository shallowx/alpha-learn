package com.alpha.learn.netty4;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class TrackerTest  {

    @Test
    public void test() {
        System.setProperty("io.netty.leakDetection.level", "PARANOID");
        MessagePacket packet = MessagePacket.newInstance(Unpooled.wrappedBuffer("test".getBytes(CharsetUtil.UTF_8)));
        packet.release();
    }

    static class MessagePacket extends AbstractReferenceCounted {
        private static final Recycler<MessagePacket> RECYCLER = new Recycler<MessagePacket>() {
            @Override
            protected MessagePacket newObject(Handle<MessagePacket> handle) {
                return new MessagePacket(handle);
            }
        };

        private static final ResourceLeakDetector<MessagePacket> leakDetector = ResourceLeakDetectorFactory.instance()
                .newResourceLeakDetector(MessagePacket.class);
        private static ResourceLeakTracker<MessagePacket> tracker;

        private MessagePacket(Recycler.Handle<MessagePacket> handle) {
            this.handle = handle;
        }
        private final Recycler.Handle<MessagePacket> handle;;
        private ByteBuf buf;

        static MessagePacket newInstance(ByteBuf buf) {
            MessagePacket packet = RECYCLER.get();
            packet.buf = buf;
            packet.setRefCnt(1);
            tracker = leakDetector.track(packet);
            return packet;
        }

        @Override
        protected void deallocate() {
            log.info("deallocate starting....");
            if (tracker != null) {
                tracker.close(this);
            }
            buf.release();
            buf = null;
            handle.recycle(this);
        }

        @Override
        public ReferenceCounted touch(Object hint) {
            if (buf != null) {
                buf.touch(hint);
            }
            return this;
        }
    }
}
