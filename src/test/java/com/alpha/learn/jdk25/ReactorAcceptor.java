package com.alpha.learn.jdk25;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("ALL")
public class ReactorAcceptor implements Runnable {
    private final ServerSocketChannel serverChannel;
    private final ReactorWorker[] workers;
    private final AtomicInteger index = new AtomicInteger(0);

    public ReactorAcceptor(int port, ReactorWorker[] workers) throws IOException {
        this.serverChannel = ServerSocketChannel.open();
        this.serverChannel.configureBlocking(true);
        this.serverChannel.bind(new InetSocketAddress(port));
        this.workers = workers;
    }

    @Override
    public void run() {
        while (true) {
            try {
                SocketChannel sc = serverChannel.accept();
                sc.configureBlocking(false);
                ReactorWorker worker = nextWorker();
                worker.registerChannel(sc);
                System.out.printf("[Acceptor] Accepted %s and handed to %s%n", sc.getRemoteAddress(), worker.getName());
            } catch (ClosedChannelException cce) {
                System.out.println("[Acceptor] Server channel closed");
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try { serverChannel.close(); } catch (IOException ignored) {}
            }
        }
    }

    private ReactorWorker nextWorker() {
        int i = Math.abs(index.getAndIncrement() % workers.length);
        return workers[i];
    }
}
