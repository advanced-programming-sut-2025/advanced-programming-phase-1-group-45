package com.proj.network.message;

import org.json.JSONObject;

public class JsonBuilder {
    private final JSONObject json = new JSONObject();

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

    public String buildString() {
        return json.toString();
    }

    // ----------------- Static Helpers -----------------

    public static JSONObject create(String key, Object value) {
        return new JsonBuilder().put(key, value).build();
    }

    public static JSONObject createMessage(String type, JSONObject data) {
        return new JsonBuilder()
            .put("type", type)
            .put("data", data)
            .build();
    }

    public static JSONObject error(String code, String message) {
        return new JsonBuilder()
            .put("code", code)
            .put("message", message)
            .build();
    }

    public static JSONObject wrapContent(Object content) {
        return new JsonBuilder().put("content", content).build();
    }
}
