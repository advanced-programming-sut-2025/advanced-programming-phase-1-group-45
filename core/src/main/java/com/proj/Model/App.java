package com.proj.Model;

import java.util.ArrayList;
import java.util.List;

public class App {
    public static List<User> users = new ArrayList<>();

    // adder
    public static void addUser(User user) {
        users.add(user);
    }

    // finder
    public static User findUserByUsername(String username) {
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }
    public static List<User> getUsers() {
        return users;
    }
}
