package com.proj;

import com.proj.Model.User;
import com.proj.Database.DatabaseHelper;

public class Session {
    private static User currentUser;
    private static User userManager;
    private static DatabaseHelper dbHelper;

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
    public static void initialize(DatabaseHelper helper) {
        dbHelper = helper;
    }

    public static DatabaseHelper getDatabaseHelper() {
        return dbHelper;
    }
}
