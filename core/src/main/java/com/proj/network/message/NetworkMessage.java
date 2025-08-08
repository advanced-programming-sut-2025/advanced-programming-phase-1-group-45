package com.proj.network.message;

import org.json.JSONException;
import org.json.JSONObject;

public class NetworkMessage {
    private final String type;
    private final JSONObject data;

    public NetworkMessage(String type, JSONObject data) {
        this.type = type;
        this.data = data != null ? data : new JSONObject();
    }

    public static NetworkMessage parse(String jsonStr) throws Exception {
        try {
            JSONObject json = new JSONObject(jsonStr);
            String type = json.getString("type");
            JSONObject data = json.optJSONObject("data");
            if (data == null) {
                data = new JSONObject();
            }
            return new NetworkMessage(type, data);
        } catch (JSONException e) {
            throw new
                Exception("Invalid message format", e);
        }
    }

    public String toJsonString() {
        return toJson().toString();
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("type", type);
        json.put("data", data);
        return json;
    }

    public String getType() {
        return type;
    }

    public JSONObject getData() {
        return data;
    }
}
