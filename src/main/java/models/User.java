package models;

import java.util.HashMap;
import java.util.Map;
import managers.TimeManager;
import models.Events.TurnChangedEvent;
import models.Animal.ProductInfo;
import java.util.List;

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
    private double money;
    private Map<String, Integer> inventory = new HashMap<>();
    private Player currentPlayer;
    private String avatar = "@";
     private String currentShop = null;

    public User(String username, String passwordHash, String nickname, String email, String gender) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.nickname = nickname;
        this.email = email;
        this.gender = gender;
        this.money = 1000.0;
        this.currentPlayer = new Player(200);
        this.currentPlayer.user = this; // اضافه کردن ارجاع به User
        this.avatar = "@";
    }

    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public String getNickname() { return nickname; }
    public String getEmail() { return email; }
    public String getGender() { return gender; }
    public int getGamesPlayed() { return gamesPlayed; }
    public double getMaxMoney() { return maxMoney; }
    public String getSecurityQuestion() { return securityQuestion; }
    public String getSecurityAnswer() { return securityAnswer; }
    public double getMoney() { return money; }
    public int getInventoryCount(String item) { return inventory.getOrDefault(item, 0); }
    public String getCurrentShop(){ return currentShop; }

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

    public void setUsername(String username) { this.username = username; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public void setEmail(String email) { this.email = email; }
    public void setGender(String gender) { this.gender = gender; }
    public void setGamesPlayed(int gamesPlayed) { this.gamesPlayed = gamesPlayed; }
    public void setMaxMoney(double maxMoney) { this.maxMoney = maxMoney; }
    public void setSecurityQuestion(String securityQuestion) { this.securityQuestion = securityQuestion; }
    public void setSecurityAnswer(String securityAnswer) { this.securityAnswer = securityAnswer; }
    public void setCurrentShop(String currentShop){ this.currentShop = currentShop; }

    // Friendships
    private final Map<String, Friendship> friendships = new HashMap<>();

    public void addFriend(User friend) {
//        String key = generateFriendshipKey(this, friend);
//        if (!friendships.containsKey(key)) {
//            friendships.put(key, new Friendship(this, friend));
//        }
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

    public void applyEnergyPenalty() {
        if (currentPlayer != null && currentPlayer.getEnergy() != null) {
            currentPlayer.getEnergy().reduceByHalf();
        }
    }

    public void onTurnEnd() {
        gamesPlayed++;
        System.out.println(this.username + "'s turn ended");
        System.out.println(TimeManager.getInstance().getTimeString());
    }

    public void onNewTurn(TurnChangedEvent event) {
        System.out.println(this.username + "'s turn started");
        System.out.println(TimeManager.getInstance().getTimeString());
    }

    public Player getPlayer() { return currentPlayer; }

    // مدیریت حیوانات از طریق Player
    public String createAnimalBuilding(String buildingName, String type, String level, int x, int y) {
        return currentPlayer.createAnimalBuilding(buildingName, type, level, x, y);
    }

    public boolean upgradeAnimalBuilding(String buildingName) {
        return currentPlayer.upgradeAnimalBuilding(buildingName);
    }

    public String getBuildingInfo(String buildingName) {
        return currentPlayer.getBuildingInfo(buildingName);
    }

    public List<String> getBuildingsList() {
        return currentPlayer.getBuildingsList();
    }

    public String addAnimal(String name, String type, String buildingName) {
        return currentPlayer.addAnimal(name, type, buildingName);
    }

    public boolean petAnimal(String name) {
        return currentPlayer.petAnimal(name);
    }

    public ProductInfo collectAnimalProduct(String name, String toolName) {
        return currentPlayer.collectAnimalProduct(name, toolName);
    }

    public boolean feedAnimal(String name) {
        return currentPlayer.feedAnimal(name);
    }

    public boolean shepherdAnimal(String name, int x, int y, boolean toOutside) {
        return currentPlayer.shepherdAnimal(name, x, y, toOutside);
    }

    public int sellAnimal(String name) {
        return currentPlayer.sellAnimal(name);
    }

    public boolean setAnimalFriendship(String name, int amount) {
        return currentPlayer.setAnimalFriendship(name, amount);
    }

    public List<String> getAnimalsList() {
        return currentPlayer.getAnimalsList();
    }

    public List<String> getAnimalsWithProduce() {
        return currentPlayer.getAnimalsWithProduce();
    }

    public String getAnimalInfo(String name) {
        return currentPlayer.getAnimalInfo(name);
    }

    public boolean cookRecipe(String recipeName, boolean useRefrigerator) {
        return currentPlayer.cookRecipe(recipeName, useRefrigerator);
    }

    public List<String> getLearnedCookingRecipes() {
        return currentPlayer.getLearnedCookingRecipes();
    }

    public List<String> getCookableRecipes(boolean useRefrigerator) {
        return currentPlayer.getCookableRecipes(useRefrigerator);
    }

    public String getCookingRecipeDetails(String recipeName) {
        return currentPlayer.getCookingRecipeDetails(recipeName);
    }

    public boolean learnCookingRecipe(String recipeName) {
        return currentPlayer.learnCookingRecipe(recipeName);
    }

    public boolean eatFood(String foodName) {
        return currentPlayer.eatFood(foodName);
    }

    public void addToRefrigerator(String item, int quantity) {
        currentPlayer.addToRefrigerator(item, quantity);
    }

    public boolean removeFromRefrigerator(String item, int quantity) {
        return currentPlayer.removeFromRefrigerator(item, quantity);
    }

    public Map<String, Integer> getRefrigeratorContents() {
        return currentPlayer.getRefrigeratorContents();
    }

    public boolean isAtHome() {
        return currentPlayer.isAtHome;
    }

    public void setAtHome(boolean atHome) {
        currentPlayer.isAtHome = atHome;
    }
}
