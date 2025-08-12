package com.proj.Radio;

import com.badlogic.gdx.Gdx;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class RadioNetwork {
    private Socket socket;
    private DataOutputStream output;
    private DataInputStream input;
    private boolean connected = false;
    private Thread listenerThread;
    private final RadioMenuScreen screen;

    public RadioNetwork(RadioMenuScreen screen) {
        this.screen = screen;
    }

    public void host(int port) throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        socket = serverSocket.accept();
        setupStreams();
    }

    public void connect(String ip, int port) throws IOException {
        socket = new Socket(ip, port);
        setupStreams();
    }

    private void setupStreams() throws IOException {
        output = new DataOutputStream(socket.getOutputStream());
        input = new DataInputStream(socket.getInputStream());
        connected = true;

        listenerThread = new Thread(this::listenForPackets);
        listenerThread.start();
    }

    private void listenForPackets() {
        try {
            while (connected) {
                int command = input.readInt();
                switch (command) {
                    case 0:
                        int trackIndex = input.readInt();
                        float position = input.readFloat();

                        Gdx.app.postRunnable(() ->
                            ((RadioMenuScreen) screen).playTrack(trackIndex, position)
                        );
                        break;

                    case 1:
                        Gdx.app.postRunnable(() ->
                            ((RadioMenuScreen) screen).stopTrack()
                        );
                        break;
                }
            }
        } catch (IOException e) {
            Gdx.app.log("RadioNetwork", "Connection closed");
            close();
        }
    }

    public void sendPlayCommand(int trackIndex, float position) {
        if (!connected) return;
        try {
            output.writeInt(0);
            output.writeInt(trackIndex);
            output.writeFloat(position);
            output.flush();
        } catch (IOException e) {
            Gdx.app.error("RadioNetwork", "Send error", e);
            close();
        }
    }

    public void sendStopCommand() {
        if (!connected) return;
        try {
            output.writeInt(1);
            output.flush();
        } catch (IOException e) {
            Gdx.app.error("RadioNetwork", "Send error", e);
            close();
        }
    }

    public void close() {
        connected = false;
        try {
            if (socket != null) socket.close();
            if (listenerThread != null) listenerThread.interrupt();
        } catch (IOException e) {
            Gdx.app.error("RadioNetwork", "Close error", e);
        }
    }
}
