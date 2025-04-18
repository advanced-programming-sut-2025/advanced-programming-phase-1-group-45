package models;

import java.util.*;
import java.util.Map;

public class GameSession {
    private List<String> players;
    private int mapNumber = 0;
    private int turn = 0;
    private boolean voteInProgress = false;
    private String voteStarter;
    private Map<String, Boolean> votes = new LinkedHashMap<>();

    public GameSession(List<String> players) {
        this.players = new ArrayList<>(players);
    }
    public void nextTurn() {turn++;}
    public void setMapNumber(int mapNumber) {this.mapNumber = mapNumber;}
    public int getMapNumber() {return mapNumber;}
    public int getTurn(){return turn;}
    public List<String> getPlayers(){return players;}
    public String getCurrentPlayer(){return players.get(turn%players.size());}
    public boolean isVoteInProgress(){return voteInProgress;}
    public void startVote(String starter){
        voteInProgress = true;
        voteStarter = starter;
        votes.clear();
        votes.put(starter, true);
    }
    public boolean hasVoted(String user){return votes.containsKey(user);}
    public void recordVote(String user, boolean yes){votes.put(user, yes);}
    public boolean allVoted(){return votes.size() == players.size();}
    public boolean isVoteSuccessful(){
        for(boolean v : votes.values()){
            if(!v) return false;
        }
        return true;
    }
    public void clearVote(){
        voteInProgress = false;
        voteStarter = null;
        votes.clear();
    }
}
