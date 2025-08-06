package com.proj.network;


public class NetworkEvent {
    public String type;
    public Object data;

    public NetworkEvent() {}

    public NetworkEvent(String type, Object data) {
        this.type = type;
        this.data = data;
    }
}


