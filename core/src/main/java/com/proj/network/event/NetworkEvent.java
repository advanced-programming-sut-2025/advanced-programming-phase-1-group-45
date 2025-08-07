package com.proj.network.event;

public class NetworkEvent {
    public enum Type {
        CONNECTED,
        DISCONNECTED,
        AUTH_REQUEST,
        AUTH_SUCCESS,
        AUTH_FAILED,
        SYSTEM_MESSAGE,
        PRIVATE_MESSAGE,
        ERROR,
        UNKNOWN_MESSAGE
    }

    private final Type type;
    private final String message;

    public NetworkEvent(Type type, String message) {
        this.type = type;
        this.message = message;
    }

    public Type getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }
}
