package com.proj.network.client;

import org.json.JSONObject;

public interface LobbyListListener {
    void onLobbiesReceived(JSONObject lobbiesData);
}
