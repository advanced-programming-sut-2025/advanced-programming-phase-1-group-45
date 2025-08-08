package com.proj.network.message;

import org.json.JSONObject;

public class AuthRequest {
    private final String username;
    private final String password;
    private final String securityQuestion;

    public AuthRequest(String username, String password, String securityQuestion) {
        this.username = username;
        this.password = password;
        this.securityQuestion = securityQuestion;
    }

    public static AuthRequest fromJson(JSONObject data) {
        return new AuthRequest(
            JsonParser.getString(data, "username", ""),
            JsonParser.getString(data, "password", ""),
            JsonParser.getString(data, "securityQuestion", "")
        );
    }

    public JSONObject toJson() {
        return new JsonBuilder()
            .put("username", username)
            .put("password", password)
            .put("securityQuestion", securityQuestion)
            .build();
    }

    // Getters
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getSecurityQuestion() { return securityQuestion; }
}
