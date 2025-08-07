package com.proj;

import com.proj.Model.User;

public class Session {
    private static User currentUser;
    private static User userManager;

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setUserManager(User manager) {
        userManager = manager;
    }

    public static User getUserManager() {
        return userManager;
    }
}
