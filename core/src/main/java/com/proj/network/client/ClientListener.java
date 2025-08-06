package com.proj.network.client;

public interface ClientListener {
    void onConnected();
    void onConnectionFailed();
    void onDisconnected();

    void onAuthRequest();
    void onAuthSuccess(String message);
    void onAuthFailed(String reason);

    void onLobbiesListReceived(String lobbiesJson);
    void onLobbyCreated(String lobbyInfo);
    void onJoinLobbySuccess(String lobbyInfo);

    void onGameStarted(String message);

    void onChatMessage(String messageJson);
    void onPrivateChatMessage(String messageJson);
    void onSystemMessage(String message);
    void onErrorMessage(String error);

    void onDisconnectRequest(String reason);
}
