package com.example.game;
package models;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import models.User;

import java.io.*;
import java.nio.file.*;
import java.security.*;
import java.util.*;
import java.util.stream.Collectors;


public class UserManager {
    private final Map<String, User> users = new HashMap<>();
    private final Path storage = Paths.get("users.json");
    private final Gson gson = new Gson();

    public UserManager() { load(); }

    public boolean register(String cmd) {
        try {
            String[] p = cmd.split("\\s+");
            String u=null, pw=null, pwc=null, nick=null, email=null, gen=null;
            for (int i=1; i<p.length; i++) {
                switch(p[i]) {
                    case "-u": u=p[++i]; break;
                    case "-p": pw=p[++i]; pwc=p[++i]; break;
                    case "-n": nick=p[++i]; break;
                    case "-e": email=p[++i]; break;
                    case "-g": gen=p[++i]; break;
                }
            }
            if (users.containsKey(u)) { System.out.println("username already used."); return false; }
            if (!email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                System.out.println("invalid email format."); return false;
            }
            if (pw.length()<8 || !pw.matches(".*[A-Z].*") || !pw.matches(".*[a-z].*")
                    || !pw.matches(".*\\d.*") || !pw.matches(".*[!@#$%^&*()].*")) {
                System.out.println("your password is not strong."); return false;
            }
            if (!pw.equals(pwc)) { System.out.println("make sure that you repeated your password correctly."); return false; }

            users.put(u, new User(u, hash(pw), nick, email, gen));
            save();
            return true;
        } catch(Exception ex) {
            System.out.println("error.");
            return false;
        }
    }

    public User login(String cmd) {
        String[] p = cmd.split("\\s+");
        String u=null, pw=null;
        for (int i=1; i<p.length; i++) {
            if (p[i].equals("-u")) u=p[++i];
            if (p[i].equals("-p")) pw=p[++i];
        }
        User user = users.get(u);
        if (user==null || !user.getPasswordHash().equals(hash(pw))) {
            System.out.println("user name or password is incorrect.");
            return null;
        }
        return user;
    }

    public void startPasswordRecovery(String cmd) {
        String u = cmd.split("\\s+")[3];
        User user = users.get(u);
        if (user==null) {
            System.out.println("invalid username.");
        } else {
            System.out.println("security question: " + user.getSecurityQuestion());
        }
    }
    public boolean checkSecurityAnswer(String username, String answer) {
        User u = users.get(username);
        if (u == null) return false;
        String Ans = hash(answer.trim().toLowerCase());
        return Ans.equals(u.getSecurityAnswe());
    }

    public String resetPasswordRandom(String username) {
        User u = users.get(username);
        if (u == null) return null;
        String newPw = generateRandomPassword(12);
        u.setPasswordHash(hash(newPw));
        save();
        return newPw;
    }

    public boolean resetPasswordManual(String username, String newPassword) {
        User u = users.get(username);
        if (u == null) return false;
        if (newPassword.length() < 8
                || !newPassword.matches(".*[A-Z].*")
                || !newPassword.matches(".*[a-z].*")
                || !newPassword.matches(".*\\d.*")
                || !newPassword.matches(".*[!@#$%^&*()].*")) {
            return false;
        }
        u.setPasswordHash(hash(newPassword));
        save();
        return true;
    }


    public void completePasswordRecovery(String cmd) {
        if (checkSecurityAnswer){
            System.out.println("how do want to set new password?");}
        else{
            System.out.println("incorrect answer")
        }

    }
    private String generateRandomPassword(int length) {
        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String symbols = "!@#$%^&*()-_=+[]{};:,.<>?";
        String all = upper + lower + digits + symbols;

        SecureRandom rnd = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);

        sb.append(upper.charAt(rnd.nextInt(upper.length())));
        sb.append(lower.charAt(rnd.nextInt(lower.length())));
        sb.append(digits.charAt(rnd.nextInt(digits.length())));
        sb.append(symbols.charAt(rnd.nextInt(symbols.length())));

        for (int i = 4; i < length; i++) {
            sb.append(all.charAt(rnd.nextInt(all.length())));
        }

        List<Character> pwdChars = sb.chars()
                .mapToObj(c -> (char)c)
                .collect(Collectors.toList());
        Collections.shuffle(pwdChars, rnd);

        StringBuilder pwd = new StringBuilder();
        pwdChars.forEach(pwd::append);
        return pwd.toString();
    }


    public String getUserInfo(User u) {
        return String.format("username: %s\n nickname: %s\n max money: %.2f\n game played: %d",
                u.getUsername(), u.getNickname(), u.getMaxMoney(), u.getGamesPlayed());
    }

    public boolean changeUsername(User user, String newUsername) {
        if (newUsername == null || !newUsername.matches("^[A-Za-z0-9]+$")) {
            System.out.println("invalid username");
            return false;
        }
        if (users.containsKey(newUsername)) {
            System.out.println("this username is already used by another person.");
            return false;
        }
        String old = user.getUsername();
        user.setUsername(newUsername);
        users.remove(old);
        users.put(newUsername, user);
        save();
        System.out.println("your username changed to" + newUsername + "successfully.");
        return true;
    }

    public boolean changeNickname(User user, String newNickname) {
        if (newNickname == null || newNickname.isBlank()) {
            System.out.println("your nickname can't be empty");
            return false;
        }
        user.setNickname(newNickname);
        save();
        System.out.println("your nick name changed to " + newNickname + " succesfully.");
        return true;
    }

    public boolean changeEmail(User user, String newEmail) {
        if (newEmail == null || !newEmail.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            System.out.println("invalid email format");
            return false;
        }
        for (User u : users.values()) {
            if (u.getEmail().equalsIgnoreCase(newEmail)) {
                System.out.println("this email is already used.");
                return false;
            }
        }
        user.setEmail(newEmail);
        save();
        System.out.println("your email changed to " + newEmail + "succesfully");
        return true;
    }

    public boolean changePassword(User user, String oldPassword, String newPassword) {
        String oldHash = hash(oldPassword);
        if (!user.getPasswordHash().equals(oldHash)) {
            System.out.println("incorrect password.");
            return false;
        }
        if (newPassword.length() < 8
                || !newPassword.matches(".*[A-Z].*")
                || !newPassword.matches(".*[a-z].*")
                || !newPassword.matches(".*\\d.*")
                || !newPassword.matches(".*[!@#$%^&*()].*")) {
            System.out.println("new password is not strong.");
            return false;
        }
        user.setPasswordHash(hash(newPassword));
        save();
        System.out.println("password changed successfully.");
        return true;
    }

    private void load() {
        try {
            if (Files.exists(storage)) {
                var list = gson.fromJson(Files.readString(storage),
                        new TypeToken<List<User>>(){}.getType());
                for (User u : list) users.put(u.getUsername(), u);
            }
        } catch(IOException ignored) {}
    }

    private void save() {
        try (Writer w = Files.newBufferedWriter(storage)) {
            gson.toJson(new ArrayList<>(users.values()), w);
        } catch(IOException ignored) {}
    }

    private String hash(String pw) {
        try {
            var md = MessageDigest.getInstance("SHA-256");
            var bytes = md.digest(pw.getBytes("UTF-8"));
            var sb = new StringBuilder();
            for (byte b : bytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch(Exception e) { return ""; }
    }

}
