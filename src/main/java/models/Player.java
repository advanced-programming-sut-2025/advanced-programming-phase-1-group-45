package models;

import com.google.common.eventbus.Subscribe;
import models.Events.GameEventBus;
import managers.TurnAdvancedEvent;

public class Player {
    private String username;
    private String passwordHash;
    private String nickname;
    private String email;
    private String gender;
    private double maxMoney = 0;
    private int gamesPlayed = 0;
    private String securityQuestion = "What is your grandma's name? ";
    private String securityAnswer = null;

    public Player(String username, String passwordHash, String nickname, String email, String gender){
        this.username = username;
        this.passwordHash = passwordHash;
        this.nickname = nickname;
        this.email = email;
        this.gender = gender;
        GameEventBus.INSTANCE.register(this);
    }
    public String getUsername(){ return username; }
    public String getPasswordHash(){ return passwordHash; }
    public String getNickname(){ return nickname; }
    public String getEmail(){ return email; }
    public String getGender(){ return gender; }
    public int getGamesPlayed(){ return gamesPlayed; }
    public double getMaxMoney(){return maxMoney; }
    public String getSecurityQuestion(){ return securityQuestion; }
    public String getSecurityAnswer(){return securityAnswer;}

    public void setUsername(String username){ this.username = username; }
    public void setPasswordHash(String passwordHash){ this.passwordHash = passwordHash; }
    public void setNickname(String nickname){ this.nickname = nickname; }
    public void setEmail(String email){ this.email = email; }
    public void setGender(String gender){ this.gender = gender; }
    public void setGamesPlayed(int gamesPlayed){ this.gamesPlayed = gamesPlayed; }
    public void setMaxMoney(double maxMoney){ this.maxMoney = maxMoney; }
    public void setSecurityQuestion(String securityQuestion){ this.securityQuestion = securityQuestion; }
    public void setSecurityAnswer(String securityAnswer){this.securityAnswer = securityAnswer; }

    public void onTurnEnd(){
        gamesPlayed++;
    }
    @Subscribe
    public void onNewTurn(TurnAdvancedEvent event){
        //TODO
        System.out.println("New turn ended");
        //system.out.println("turn starting at time .... ");
    }
}
