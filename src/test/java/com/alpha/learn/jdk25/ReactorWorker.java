package com.alpha.learn.jdk25;

import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ReactorWorker implements Runnable {
    @Getter
    private final String name;
    private final Selector selector;
    private final Queue<SocketChannel> registerQueue = new ConcurrentLinkedQueue<>();
    @Setter
    private volatile boolean started = false;

    public ReactorWorker(String name) throws IOException {
        this.name = name;
        this.selector = Selector.open();
    }

    public void registerChannel(SocketChannel sc) throws ClosedChannelException {
        registerQueue.add(sc);
        processRegistrations();
        selector.wakeup();
    }

    @Override
    public void run() {
        started = true;
        System.out.printf("[%s] started%n", name);
            while (true) {
                try {
                    int selected = selector.select(200);
                    if (selected > 0) {
                        Set<SelectionKey> keys = selector.selectedKeys();
                        Iterator<SelectionKey> it = keys.iterator();
                        while (it.hasNext()) {
                            SelectionKey key = it.next();
                            it.remove();
                            try {
                                if (!key.isValid()) continue;
                                if (key.isReadable()) handleRead(key);
                                if (key.isWritable()) handleWrite(key);
                            } catch (CancelledKeyException ignored) {
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        selector.close();
                    } catch (IOException ignored) {}
                }
            }
    }

    private void processRegistrations() throws ClosedChannelException {
        SocketChannel sc;
        while ((sc = registerQueue.poll()) != null) {
            try {
                sc.configureBlocking(false);
                ConnectionContext ctx = new ConnectionContext(sc);
                sc.register(selector, SelectionKey.OP_READ, ctx);
            } catch (IOException e) {
                System.err.printf("[%s] failed to register channel: %s%n", name, e.getMessage());
                try { sc.close(); } catch (IOException ignored) {}
            }
        }
    }

    private void handleRead(SelectionKey key) {
        ConnectionContext ctx = (ConnectionContext) key.attachment();
        SocketChannel sc = (SocketChannel) key.channel();
        ByteBuffer readBuf = ctx.getReadBuffer();
        try {
            int read = sc.read(readBuf);
            if (read == -1) {
                System.out.printf("[%s] remote closed connection %s%n", name, sc.getRemoteAddress());
                key.cancel();
                sc.close();
                return;
            } else if (read == 0) {
                return;
            }

            readBuf.flip();
            byte[] data = new byte[readBuf.remaining()];
            readBuf.get(data);
            System.out.printf("[%s] Received from %s: %s%n", name, sc.getRemoteAddress(), new String(data));
            ctx.enqueueWrite(ByteBuffer.wrap(data));
            key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
            readBuf.clear();
        } catch (IOException e) {
            try {
                System.out.printf("[%s] read error, closing %s: %s%n", name, getRemoteAddressSafe(sc), e.getMessage());
                key.cancel();
                sc.close();
            } catch (IOException ignored) {}
        }
    }

    private void handleWrite(SelectionKey key) {
        ConnectionContext ctx = (ConnectionContext) key.attachment();
        SocketChannel sc = (SocketChannel) key.channel();

        try {
            ByteBuffer buf;
            while ((buf = ctx.peekWrite()) != null) {
                sc.write(buf);
                if (buf.hasRemaining()) {
                    break;
                } else {
                    ctx.removeWrite();
                }
            }
            if (ctx.peekWrite() == null) {
                key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);
            }
        } catch (IOException e) {
            try {
                System.out.printf("[%s] write error, closing %s: %s%n", name, getRemoteAddressSafe(sc), e.getMessage());
                key.cancel();
                sc.close();
            } catch (IOException ignored) {}
        }
    }

    private String getRemoteAddressSafe(SocketChannel sc) {
        try { return sc.getRemoteAddress().toString(); } catch (IOException e) { return "?"; }
    }

    static class ConnectionContext {
        private static final int READ_BUF_SIZE = 4096;
        @Getter
        private final ByteBuffer readBuffer = ByteBuffer.allocate(READ_BUF_SIZE);
        private final Queue<ByteBuffer> writeQueue = new ConcurrentLinkedQueue<>();

        public ConnectionContext(SocketChannel channel) {
        }

        public void enqueueWrite(ByteBuffer buf) {
            writeQueue.add(buf);
        }

        public ByteBuffer peekWrite() {
            return writeQueue.peek();
        }

        public void removeWrite() {
            writeQueue.poll();
        }
    }
}
