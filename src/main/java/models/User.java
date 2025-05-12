package models;

import java.util.HashMap;
import java.util.Map;

import managers.TimeManager;
import models.Events.TurnChangedEvent;


public class User {
    private String username;
    private String passwordHash;
    private String nickname;
    private String email;
    private String gender;
    private double maxMoney = 0.0;
    private int gamesPlayed = 0;
    private String securityQuestion = "What is your grandma's name? ";
    private String securityAnswer = null;
    private double money = 0.0;
    private Map<String, Integer> inventory = new HashMap<>();
    private Energy energy = new Energy(50);

    public Energy getEnergy() {
        return energy;
    }

    public User(String username, String passwordHash, String nickname, String email, String gender) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.nickname = nickname;
        this.email = email;
        this.gender = gender;
        this.money = 0.0;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getNickname() {
        return nickname;
    }

    public String getEmail() {
        return email;
    }

    public String getGender() {
        return gender;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public double getMaxMoney() {
        return maxMoney;
    }

    public String getSecurityQuestion() {
        return securityQuestion;
    }

    public String getSecurityAnswer() {
        return securityAnswer;
    }

    public double getMoney() {
        return money;
    }

    public int getInventoryCount(String item) {
        return inventory.getOrDefault(item, 0);
    }

    public void addMoney(double delta) {
        this.money += delta;
        if (this.money > this.maxMoney) this.maxMoney = this.money;
    }

    public void addItem(String item, int count) {
        int x = inventory.getOrDefault(item, 0);
        int y = count + x;
        if (y <= 0) inventory.remove(item);
        else inventory.put(item, y);
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setGamesPlayed(int gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    public void setMaxMoney(double maxMoney) {
        this.maxMoney = maxMoney;
    }

    public void setSecurityQuestion(String securityQuestion) {
        this.securityQuestion = securityQuestion;
    }

    public void setSecurityAnswer(String securityAnswer) {
        this.securityAnswer = securityAnswer;
    }

    //new additions
    private final Map<String, Friendship> friendships = new HashMap<>();

    public void addFriend(User friend) {
        String key = generateFriendshipKey(this, friend);
        if (!friendships.containsKey(key)) {
            friendships.put(key, new Friendship(this, friend));
        }
    }

    public Friendship getFriendship(User friend) {
        return friendships.get(generateFriendshipKey(this, friend));
    }

    private static String generateFriendshipKey(User a, User b) {
        return a.getUsername().compareTo(b.getUsername()) < 0 ?
                a.getUsername() + "|" + b.getUsername() :
                b.getUsername() + "|" + a.getUsername();
    }

    // Inventory management methods
    public boolean hasItem(String item) {
        return inventory.containsKey(item) && inventory.get(item) > 0;
    }

    public boolean removeItem(String item, int amount) {
        int current = inventory.getOrDefault(item, 0);
        if (current >= amount) {
            inventory.put(item, current - amount);
            return true;
        }
        return false;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    // Energy system
    public void applyEnergyPenalty() {
        this.energy *= 0.5;
    }

    public void onTurnEnd() {
        gamesPlayed++;
        //TODO
        System.out.println(this.username + "'s turn ended");
        System.out.println(TimeManager.getInstance().getTimeString());
    }

    public void onNewTurn(TurnChangedEvent event) {
        //TODO
        System.out.println(this.username + "'s turn started");
        System.out.println(TimeManager.getInstance().getTimeString());
    }
}
