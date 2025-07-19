package com.proj.Model;

public class PasswordGenerator {
    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL = "!@#$%^&*()_+";

    public static String generateRandomPassword() {
        StringBuilder password = new StringBuilder();
        String allChars = UPPER + LOWER + DIGITS + SPECIAL;

        password.append(randomChar(UPPER));
        password.append(randomChar(LOWER));
        password.append(randomChar(DIGITS));
        password.append(randomChar(SPECIAL));

        for (int i = 0; i < 8; i++) { 
            password.append(randomChar(allChars));
        }

        return shuffleString(password.toString());
    }

    private static char randomChar(String characterSet) {
        return characterSet.charAt((int) (Math.random() * characterSet.length()));
    }

    private static String shuffleString(String input) {
        char[] characters = input.toCharArray();
        for (int i = 0; i < characters.length; i++) {
            int randomIndex = (int) (Math.random() * characters.length);
            char temp = characters[i];
            characters[i] = characters[randomIndex];
            characters[randomIndex] = temp;
        }
        return new String(characters);
    }
}
