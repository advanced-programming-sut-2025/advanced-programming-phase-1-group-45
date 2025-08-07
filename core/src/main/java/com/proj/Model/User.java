package com.proj.Model;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

public class User {
    private String username;
    private String password;
    private String securityAnswer;
    private int avatar;
    private String nickname;  
    private static final Map<String, User> users = new HashMap<>();

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSecurityAnswer() {
        return securityAnswer;
    }

    public void setSecurityAnswer(String securityAnswer) {
        this.securityAnswer = securityAnswer;
    }

    public int getAvatar() {
        return avatar;
    }

    public void setAvatar(int avatar) {
        this.avatar = avatar;
    }
    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }


    public static boolean changeUsername(User user, String newUsername) {

        if (newUsername == null || !newUsername.matches("^[A-Za-z0-9]+$")) {

            System.out.println("Invalid username");

            return false;

        }

        if (users.containsKey(newUsername)) {
            System.out.println("This username is already taken");
            return false;
        }
        String oldUsername = user.getUsername();
        user.setUsername(newUsername);
        users.remove(oldUsername);
        users.put(newUsername, user);
        System.out.println("Username changed to " + newUsername + " successfully");
        return true;
    }

    public static boolean changeNickname(User user, String newNickname) {
        if (newNickname == null) {
            System.out.println("Nickname can't be empty");
            return false;
        }
        user.setNickname(newNickname);
        System.out.println("Nickname changed to " + newNickname + " successfully");
        return true;
    }

    public static boolean changePassword(User user, String oldPassword, String newPassword) {
        if (!user.password.equals(hash(oldPassword))) {
            System.out.println("Current password is incorrect");
            return false;
        }
        if (newPassword.length() < 8
            || !newPassword.matches(".*[A-Z].*")
            || !newPassword.matches(".*[a-z].*")
            || !newPassword.matches(".*\\d.*")
            || !newPassword.matches(".*[!@#$%^&*()].*")) {
            System.out.println("New password doesn't meet strength requirements");
            return false;
        }

        user.setPassword(newPassword);
        System.out.println("Password changed successfully");
        return true;
    }
    
    private static String hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(input.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Hashing failed", e);
        }
    }

    public static void addUser(User user) {
        users.put(user.getUsername(), user);
    }

    public static User getUser(String username) {
        return users.get(username);
    }
}
