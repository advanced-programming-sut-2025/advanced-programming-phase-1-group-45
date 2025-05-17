package models;

import com.google.common.eventbus.Subscribe;
import controllers.WeatherController;
import managers.PlayerTurnManager;
import managers.TimeManager;
import managers.ToolManager;
import models.Enums.Season;
import models.Events.GameEventBus;
import models.Events.SeasonChangedEvent;

import java.util.*;
import java.util.Map;

public class GameSession {
    private static List<String> players;
    List<User> users = new ArrayList<>();
    private int mapNumber = 0;
    private PlayerTurnManager turnManager;
    private TimeManager timeManager = new TimeManager();
    private WeatherController weatherController;
    private int turn = 0;
    private boolean voteInProgress = false;
    private String voteStarter;
    private Map<String, Boolean> votes = new LinkedHashMap<>();
    private GameMap map;
    private ToolManager toolManager ;
    private int playerX, playerY;
    private int energy = 100;
    private Season season = Season.SPRING;

    public GameSession(HashMap<String, User> players) {
        this.players = new ArrayList<>(players.keySet());
        this.users = new ArrayList<>(players.values());
        GameEventBus.INSTANCE.register(this);
    }

    @Subscribe
    public void changeSeason(SeasonChangedEvent event) {
        season = event.newSeason();
    }

    public Season getSeason() {
        return season;
    }

    public void nextTurn() {
        turnManager.endTurn();
    }

    public void setMapNumber(int mapNumber) {
        this.mapNumber = mapNumber;
    }

    public int getMapNumber() {
        return mapNumber;
    }

    public int getTurn() {
        return turnManager.getCurrentTurn();
    }

    public List<String> getPlayers() {
        return players;
    }

    // public static String getCurrentPlayer(){return players.get(turn%players.size());}
    public boolean isVoteInProgress() {
        return voteInProgress;
    }

    public void setMap(GameMap map) {
        this.map = map;
        int mid = map.getSize() / 2;
        this.playerX = mid;
        this.playerY = mid;
        users.get(this.getTurn()-1).getPlayer().setGameMap(map);
        turnManager = new PlayerTurnManager(users);
        toolManager = new ToolManager(this, users.get(this.getTurn() - 1).getPlayer());
    }

    public GameMap getMap() {
        return map;
    }

    public int getPlayerX() {
        return playerX;
    }

    public int getPlayerY() {
        return playerY;
    }

    public void setPlayerPosition(int x, int y) {
        this.playerX = x;
        this.playerY = y;
    }

    public int getEnergy() {
        return energy;
    }

    public void reduceEnergy(int amount) {
        energy -= amount;
    }

    public void startVote(String starter) {
        voteInProgress = true;
        voteStarter = starter;
        votes.clear();
        votes.put(starter, true);
    }

    public boolean hasVoted(String user) {
        return votes.containsKey(user);
    }

    public void recordVote(String user, boolean yes) {
        votes.put(user, yes);
    }

    public boolean allVoted() {
        return votes.size() == players.size();
    }

    public boolean isVoteSuccessful() {
        for (boolean v : votes.values()) {
            if (!v) return false;
        }
        return true;
    }

    public void clearVote() {
        voteInProgress = false;
        voteStarter = null;
        votes.clear();
    }

    public TimeManager getTimeManager() {
        return timeManager;
    }

    public void setTimeManager(TimeManager timeManager) {
        this.timeManager = timeManager;
    }

    public ToolManager getToolManager() {
        return toolManager;
    }
}
