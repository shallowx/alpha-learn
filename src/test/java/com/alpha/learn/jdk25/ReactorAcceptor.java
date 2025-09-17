package com.alpha.learn.jdk25;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
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
                log.info("[Acceptor] Accepted {} and handed to {}", sc.getRemoteAddress(), worker.getName());
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
