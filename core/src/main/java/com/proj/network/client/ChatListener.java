package com.proj.network.client;

public interface ChatListener extends NetworkEventListener {
    void onChatMessage(String sender, String message, boolean isPrivate);
    void onReceivedPlayerList(String players);
}
