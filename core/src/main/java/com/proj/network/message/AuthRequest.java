package com.proj.network.message;

import org.json.JSONObject;

public class AuthRequest {
    private final String username;
    private final String password;


    public AuthRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public static AuthRequest fromJson(JSONObject data) {
        return new AuthRequest(
            JsonParser.getString(data, "username", ""),
            JsonParser.getString(data, "password", "")
        );
    }

    public JSONObject toJson() {
        return new JsonBuilder()
            .put("username", username)
            .put("password", password)

            .build();
    }

    // Getters
    public String getUsername() { return username; }
    public String getPassword() { return password; }

}
