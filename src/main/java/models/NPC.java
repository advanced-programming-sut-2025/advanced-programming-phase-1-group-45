package models;

import controllers.MovementController;
import models.Enums.NPCCharacters;
import models.Enums.Season;
import models.Enums.Weather;
import managers.time.TimeManager;

public class NPC {
    private String name;
    private String job;
    private String location;
    private NPCCharacters character;

    Friendship friendship;
    MovementController movementController;
    TimeManager gameTimeAndDate;

    public void dialogue() {
        Season season;
        Weather weather;
    }
    public void gift(){
        Friendship friendship;
    }
    public void friendship() {
        Friendship newFriendship = new Friendship();
    }

    public void quest(){

    }
}
