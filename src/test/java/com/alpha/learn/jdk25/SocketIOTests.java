package com.alpha.learn.jdk25;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
public class SocketIOTests {

    @Test
    public void testServer() throws IOException, InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        Socket socket;
        try (ServerSocket serverSocket = new ServerSocket(8888);) {
            serverSocket.setReuseAddress(true);
            serverSocket.setSoTimeout(5000);
            while (!executor.isShutdown()) {
                try {
                    socket = serverSocket.accept();
                }  catch (Exception e) {
                    log.info("error...");
                    break;
                }

                Socket finalSocket = socket;
                executor.execute(() -> {
                    try {
                        InputStream in = finalSocket.getInputStream();
                        byte[] bytes = in.readAllBytes();
                        log.info("receive:{}", new  String(bytes));
                        finalSocket.shutdownInput();
                        OutputStream out = finalSocket.getOutputStream();
                        String respond = "world" + Thread.currentThread().getName();
                        out.write(respond.getBytes());
                        out.flush();

                        finalSocket.shutdownOutput();
                    } catch (IOException ignored) {}
                });
            }

            executor.shutdown();
            while (!executor.isTerminated() && !executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
            log.info("ending...");
        }
    }

    @Test
    public void testClient() throws IOException {
        try (Socket socket = new Socket()) {
            socket.setReuseAddress(true);
            socket.setKeepAlive(true);
            socket.setTcpNoDelay(true);
            socket.setSoLinger(true, 0);

            socket.connect(new InetSocketAddress("localhost", 8888));
            OutputStream out = socket.getOutputStream();
            out.write("Hello".getBytes());
            out.flush();
            socket.shutdownOutput();

            InputStream in = socket.getInputStream();
            byte[] bytes = in.readAllBytes();
            log.info("respond: {}", new String(bytes));
            socket.shutdownInput();
        }
    }
}
