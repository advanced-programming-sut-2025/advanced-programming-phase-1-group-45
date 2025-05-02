package models;

import controllers.TradingController;
import models.time.TimeManager;

public class Friendship implements Ability {
   // private Map<NPC, Integer> npcRelationships;

    public void increaseFriendship(NPC npc, int amount) { /* Relationship logic */ }

    TradingController trade;

    public void buyGift() {
    }

    public void hug() {
    }

    public void buyFlower() {
        Backpack backpack = new Backpack();
    }

    @Override
    public void level1() {
    }

    public void level2() {
    }

    Backpack backpack = new Backpack();

    public void level3() {
    }

    public void level4() {
        marriage();

    }

    public void marriage() {
        Energy energy = new Energy();
        TimeManager gameTimeAndDate;
    }

}
