package com.proj.network.client;

import com.proj.network.event.GameEvent;

public interface GameEventListener extends NetworkEventListener {
    void handleGameEvent(GameEvent event);
}
