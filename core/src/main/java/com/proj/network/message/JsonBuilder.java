package com.proj.network.message;

import org.json.JSONObject;

public class JsonBuilder {
    private final JSONObject json = new JSONObject();

    public static JsonBuilder create() {
        return new JsonBuilder();
    }

    public static JSONObject empty() {
        return new JSONObject();
    }

    public JsonBuilder put(String key, Object value) {
        if (value != null) {
            json.put(key, value);
        }
        return this;
    }

    public JsonBuilder putIf(boolean condition, String key, Object value) {
        if (condition && value != null) {
            json.put(key, value);
        }
        return this;
    }

    public JSONObject build() {
        return json;
    }
}
