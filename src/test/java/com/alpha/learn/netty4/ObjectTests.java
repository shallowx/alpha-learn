package com.alpha.learn.netty4;

import io.netty.buffer.ByteBuf;
import io.netty.util.AbstractReferenceCounted;
import io.netty.util.Recycler;
import io.netty.util.ReferenceCounted;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ObjectTests {

    @Test
    public void test() {
        NettyObjects nettyObjects = NettyObjects.newInstance("test", null);
        String name = nettyObjects.name;
        Assertions.assertEquals(name, nettyObjects.name);
    }


    static class NettyObjects extends AbstractReferenceCounted {

        private static final Recycler<NettyObjects> recycler = new Recycler<NettyObjects>() {
            @Override
            protected NettyObjects newObject(Handle<NettyObjects> handle) {
                return new NettyObjects(handle);
            }
        };

        private final Recycler.Handle<NettyObjects> handle;
        public NettyObjects(Recycler.Handle<NettyObjects> handle) {
            this.handle = handle;
        }

        private String name;
        private ByteBuf buf;

        public static NettyObjects newInstance(String name, ByteBuf buf) {
            NettyObjects poolObject = recycler.get();
            poolObject.setRefCnt(1);
            poolObject.name = name;
            poolObject.buf = buf;
            return poolObject;
        }

        @Override
        protected void deallocate() {
            if (this.name != null) {
                this.name = null;
            }

            if (this.buf != null) {
                this.buf.release();
                this.buf = null;
            }
            handle.recycle(this);
        }

        @Override
        public ReferenceCounted touch(Object o) {
            if (this.buf != null) {
                buf.touch(o);
            }
            return this;
        }
    }
}
