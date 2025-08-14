package com.proj.Model;

public class ScoreboardEntry {
    private String playerName;
    private int money;
    private int questsCompleted;
    private int skillLevel;

    public ScoreboardEntry(String playerName, int money, int questsCompleted, int skillLevel) {
        this.playerName = playerName;
        this.money = money;
        this.questsCompleted = questsCompleted;
        this.skillLevel = skillLevel;
    }

    public String getPlayerName() { return playerName; }
    public int getMoney() { return money; }
    public int getQuestsCompleted() { return questsCompleted; }
    public int getSkillLevel() { return skillLevel; }
}
