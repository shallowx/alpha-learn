package com.alpha.learn.jdk25;

import java.io.IOException;

public class ReactorServer {
    private final int port;
    private final int workerCount;
    private final ReactorWorker[] workers;
    private final ReactorAcceptor acceptor;

    public ReactorServer(int port, int workerCount) throws IOException {
        this.port = port;
        this.workerCount = Math.max(1, workerCount);
        this.workers = new ReactorWorker[this.workerCount];
        for (int i = 0; i < this.workerCount; i++) {
            workers[i] = new ReactorWorker("worker-" + i);
        }
        this.acceptor = new ReactorAcceptor(port, workers);
    }

    public void start() {
        for (ReactorWorker w : workers) new Thread(w, w.getName()).start();
        new Thread(acceptor, "acceptor").start();
        System.out.printf("ReactorServer started on port %d with %d workers%n", port, workerCount);
    }
}
