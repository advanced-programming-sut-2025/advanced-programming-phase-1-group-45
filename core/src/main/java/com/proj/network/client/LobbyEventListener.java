package com.proj.network.client;

import com.proj.network.event.LobbyEvent;

public interface LobbyEventListener extends NetworkEventListener {
    void handleLobbyEvent(LobbyEvent event);
}
