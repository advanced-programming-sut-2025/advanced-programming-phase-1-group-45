package com.proj.network.message;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonParser {
    public static String getString(JSONObject json, String key, String defaultValue) {
        try {
            return json.has(key) ? json.getString(key) : defaultValue;
        } catch (JSONException e) {
            return defaultValue;
        }
    }

    public static int getInt(JSONObject json, String key, int defaultValue) {
        try {
            return json.has(key) ? json.getInt(key) : defaultValue;
        } catch (JSONException e) {
            return defaultValue;
        }
    }

    public static boolean getBoolean(JSONObject json, String key, boolean defaultValue) {
        try {
            return json.has(key) ? json.getBoolean(key) : defaultValue;
        } catch (JSONException e) {
            return defaultValue;
        }
    }

    public static JSONObject getObject(JSONObject json, String key) {
        try {
            return json.has(key) ? json.getJSONObject(key) : new JSONObject();
        } catch (JSONException e) {
            return new JSONObject();
        }
    }

    public static JSONArray getArray(JSONObject json, String key) {
        try {
            return json.has(key) ? json.getJSONArray(key) : new JSONArray();
        } catch (JSONException e) {
            return new JSONArray();
        }
    }
}
