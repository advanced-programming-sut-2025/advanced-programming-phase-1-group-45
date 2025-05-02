package models.time;

import managers.TurnAdvancedEvent;
import models.Events.DayAdvancedEvent;
import models.Events.GameEventBus;
import models.Enums.Season;
import models.Events.SeasonChangedEvent;

public class TimeManager {
   private static final TimeManager instance = new TimeManager();
   private int hour;
   private int day = 1;
   private Season season = Season.SPRING;
   private int turnsTaken;
   public static TimeManager getInstance() {
       return instance;
   }
   public void advanceTurn() {
       hour = (hour + 1) % 24;
       turnsTaken++;
       GameEventBus.INSTANCE.post(new TurnAdvancedEvent(hour, day, season));
       if(turnsTaken % 3 == 0){
           advanceDay();
       }
   }
   private void advanceDay() {
       day++;
       GameEventBus.INSTANCE.post(new DayAdvancedEvent(day, season));
       if(day > 28){
           advanceSeason();
       }
   }

   private void advanceSeason() {
       Season previousSeason = season;
       season = season.next();
       day = 1;
       GameEventBus.INSTANCE.post(new SeasonChangedEvent(previousSeason, season));
   }

   public String getTimeString() {
       return String.format("Day %d , %02d:00 - %s", day, hour, season.toString());
   }
}
