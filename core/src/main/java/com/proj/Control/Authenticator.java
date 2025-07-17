package com.proj.Control;

import com.proj.Model.App;
import com.proj.Model.User;

public class Authenticator {
    public static boolean isUsernameUnique(String username) {
        for (User user : App.users) {
            if (user.getUsername().equals(username)) return false;
        }

        return true;
    }

    public static boolean existsUsername(String username) {
        for (User user : App.users) {
            if (user.getUsername().equals(username)) return true;
        }

        return false;
    }

    public static boolean isPasswordStrong(String password) {
        return password.matches("^(?=.*[A-Z])(?=.*\\d)(?=.*[@_()*&%$#]).{8,}$");
    }

    public static User authenticate(String username, String password) {
        for (User user : App.users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) return user;
        }

        return null;
    }

}
