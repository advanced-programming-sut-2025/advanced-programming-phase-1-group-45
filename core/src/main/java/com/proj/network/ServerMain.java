package com.proj.network;

public class ServerMain {
    public static void main(String[] args) {
        GameServer server = new GameServer();
        server.start();

        // مدیریت خاموشی با Ctrl+C
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down server...");
            server.shutdown();
        }));

        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
