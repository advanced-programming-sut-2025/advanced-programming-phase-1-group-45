package managers;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import models.Player;
import models.GameSession;
//import gson.*;
import java.io.*;
import java.util.*;
import java.nio.file.*;

public class GameManager {
    private Map<String, List<GameSession>> gameSessions = new HashMap<>();
    private Path storage = Paths.get("games.json");
    private Gson gson = new Gson();
    public PlayerTurnManager playerTurnManager;
    public GameManager() {load();}
    public void saveSession(GameSession session) {save();}
    public void endSession(GameSession session){
        for(String u: session.getPlayers()){
            List<GameSession> sessions = gameSessions.get(u);
            if(sessions != null) sessions.remove(session);
        }
        save();
    }
    public GameSession loadLastSession(Player u){
        List<GameSession> sessions = gameSessions.get(u.getUsername());
        if(sessions == null || sessions.isEmpty()) return null;
        return sessions.get(sessions.size()-1);
    }
    public GameSession createNewGame(String command, String currentUser){
        var parts = new ArrayList<>(Arrays.asList(command.split("\\s+")));
        parts.remove(0);
        parts.remove(0);
        parts.remove(0);
        var users = parts;
        if(users.size() < 1 || users.size() > 3){
            System.out.println("Number on users must be between 1 and 3");
        }
        users.add(0, currentUser);
        var s = new GameSession(users);
        for(var u: users){
            gameSessions.computeIfAbsent(u, k -> new ArrayList<>()).add(s);
        }
        save();
        return s;
    }
    public boolean selectMap(GameSession session, String command){
        int m = Integer.parseInt(command.split("\\s+")[2]);
        if(m < 1 || m > 3){
            System.out.println("invalid map number");
            return false;
        }
        session.setMapNumber(m);
        save();
        return true;
    }
    private void load(){
        try{
            if(Files.exists(storage)){
                var type = new TypeToken<Map<String,List<GameSession>>>(){}.getType();
                var m = gson.fromJson(Files.readString(storage), type);

            }
        } catch (IOException ignored) {}
    }
    private void save(){
        try(Writer w = Files.newBufferedWriter(storage)){
            gson.toJson(gameSessions, w);
        } catch (IOException ignored) {
        }
    }
}
