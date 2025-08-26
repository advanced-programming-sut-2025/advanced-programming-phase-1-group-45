package com.proj.network.message;

import org.json.JSONException;
import org.json.JSONObject;

public class Command {
    private final String type;
    private final JSONObject data;

    public Command(String type, JSONObject data) {
        this.type = type;
        this.data = data != null ? data : new JSONObject();
    }

    public static Command parse(String jsonStr) throws Exception {
        try {
            JSONObject json = new JSONObject(jsonStr);
            String type = json.getString("type");
            JSONObject data = json.optJSONObject("data");
            if (data == null) {
                data = new JSONObject();
            }
            return new Command(type, data);
        } catch (JSONException e) {
            throw new
                Exception("Invalid command format", e);
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
