package com.proj.network.client;

import com.proj.network.event.NetworkEvent;

public interface NetworkEventListener {
    void handleNetworkEvent(NetworkEvent event);
}
