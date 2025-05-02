package managers;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import models.Player;
import java.io.*;
import java.nio.file.*;
import java.security.*;
import java.util.*;
import java.util.stream.Collectors;
public class UserManager {
    private final Map<String, Player> users = new HashMap<>();
    private final Path storage = Paths.get("users.json");
    private final Gson gson = new Gson();
    private Player currentPlayer;
    private Player signingPlayer;

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
            users.put(u, new Player(u, hash(pw), nick, email, gen));
            save();
            signingPlayer = users.get(u);
            System.out.println(signingPlayer.getSecurityQuestion());
            save();
            return true;
        } catch(Exception ex) {
            System.out.println("error.");
            return false;
        }
    }

    public Player login(String cmd) {
        String[] p = cmd.split("\\s+");
        String u=null, pw=null;
        for (int i=1; i<p.length; i++) {
            if (p[i].equals("-u")) u=p[++i];
            if (p[i].equals("-p")) pw=p[++i];
        }
        Player player = users.get(u);
        if (player ==null || !player.getPasswordHash().equals(hash(pw))) {
            System.out.println("user name or password is incorrect.");
            return null;
        }
        return player;
    }

    public String startPasswordRecovery(String cmd) {
        String u = cmd.split("\\s+")[3];
        currentPlayer = users.get(u);
        if (currentPlayer ==null) {
            System.out.println("invalid username.");
            return null;
        } else {
            System.out.println("security question: " + currentPlayer.getSecurityQuestion());
            return u;
        }
    }
    public boolean checkSecurityAnswer(String answer) {
        if (currentPlayer == null) return false;
        String Ans = hash(answer.trim().toLowerCase());
        return Ans.equals(currentPlayer.getSecurityAnswer());
    }

    public String resetPasswordRandom(String username) {
        Player u = users.get(username);
        if (u == null) return null;
        String newPw = generateRandomPassword(12);
        u.setPasswordHash(hash(newPw));
        save();
        return newPw;
    }

    public boolean resetPasswordManual(String username, String newPassword) {
        Player u = users.get(username);
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
        String answer = cmd.split("\\s+")[3];
        if (checkSecurityAnswer(answer)){
            System.out.println("how do want to set new password?");}
        else{
            System.out.println("incorrect answer");
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


    public String getUserInfo(Player u) {
        return String.format("username: %s\n nickname: %s\n max money: %.2f\n game played: %d",
                u.getUsername(), u.getNickname(), u.getMaxMoney(), u.getGamesPlayed());
    }

    public boolean changeUsername(Player player, String newUsername) {
        if (newUsername == null || !newUsername.matches("^[A-Za-z0-9]+$")) {
            System.out.println("invalid username");
            return false;
        }
        if (users.containsKey(newUsername)) {
            System.out.println("this username is already used by another person.");
            return false;
        }
        String old = player.getUsername();
        player.setUsername(newUsername);
        users.remove(old);
        users.put(newUsername, player);
        save();
        System.out.println("your username changed to" + newUsername + "successfully.");
        return true;
    }

    public boolean changeNickname(Player player, String newNickname) {
        if (newNickname == null || newNickname.isBlank()) {
            System.out.println("your nickname can't be empty");
            return false;
        }
        player.setNickname(newNickname);
        save();
        System.out.println("your nick name changed to " + newNickname + " succesfully.");
        return true;
    }

    public boolean changeEmail(Player player, String newEmail) {
        if (newEmail == null || !newEmail.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            System.out.println("invalid email format");
            return false;
        }
        for (Player u : users.values()) {
            if (u.getEmail().equalsIgnoreCase(newEmail)) {
                System.out.println("this email is already used.");
                return false;
            }
        }
        player.setEmail(newEmail);
        save();
        System.out.println("your email changed to " + newEmail + "succesfully");
        return true;
    }

    public boolean changePassword(Player player, String oldPassword, String newPassword) {
        String oldHash = hash(oldPassword);
        if (!player.getPasswordHash().equals(oldHash)) {
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
        player.setPasswordHash(hash(newPassword));
        save();
        System.out.println("password changed successfully.");
        return true;
    }

    private void load() {
        try {
            if (Files.exists(storage)) {
                List<Player> list = gson.fromJson(Files.readString(storage),
                        new TypeToken<List<Player>>(){}.getType());
                for (Player u : list) {
                    users.put(u.getUsername(), u);
                }
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

    public void setAnswer(String command){
        String  answer = command.split("\\s+")[1];
        signingPlayer.setSecurityAnswer(answer);
    }

}
