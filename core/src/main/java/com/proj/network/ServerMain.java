package com.proj.network;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.headless.HeadlessApplication;

public class ServerMain {
    public static void main(String[] args) {
        // راه‌اندازی سرور در حالت بدون UI
        new HeadlessApplication(new ServerApplication());
    }

    static class ServerApplication implements ApplicationListener {
        private GameServer server;

        @Override
        public void create() {
            server = new GameServer();
            server.start(); // شروع سرور
        }

        @Override
        public void resize(int width, int height) {}

        @Override
        public void render() {}

        @Override
        public void pause() {}

        @Override
        public void resume() {}

        @Override
        public void dispose() {
            server.shutdown();
        }
    }
}
