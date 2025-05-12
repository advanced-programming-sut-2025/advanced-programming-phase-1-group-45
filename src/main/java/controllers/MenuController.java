package controllers;

import managers.GameManager;
import managers.ShopManager;
import managers.UserManager;
import models.GiftLogEntry;
import models.User;
import models.GameSession;
import views.Menu;
import views.StartMenu;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MenuController {
    private Menu currentMenu = new StartMenu();
    private GameManager gameManager = new GameManager();
    private UserManager userManager = new UserManager();
    private ShopManager shopManager = new ShopManager(userManager);
    private User currentUser;
    private GameSession currentSession;
    private String pendingRecoveryUsername;
    private List<GiftLogEntry> giftLogs = new ArrayList<>();
    private int giftIdCounter = 1;
    public Optional<GiftLogEntry> getGiftById(int id, String viewer){
        return giftLogs.stream().filter(g -> g.getId() == id && (g.sender.equals(viewer) || g.receiver.equals(viewer))).findFirst();
    }
    public void recordGift(String sender, String receiver, String item, int amount){
        giftLogs.add(new GiftLogEntry(giftIdCounter++, sender, receiver, item, amount));
    }
    public List<GiftLogEntry> getGiftLogs(){
        return giftLogs;
    }

    public void processCommand(String command) {
        currentMenu.handleCommand(command, this);
    }

    public ShopManager getShopManager() { return shopManager; }
    public UserManager getUserManager() { return userManager; }
    public GameManager getGameManager() { return gameManager; }
    public User getCurrentUser() { return currentUser; }
    public GameSession getCurrentSession() { return currentSession; }
    public String getPendingRecoveryUsername() { return pendingRecoveryUsername; }

    public void setCurrentUser(User u) { this.currentUser = u; }
    public void setCurrentMenu(Menu menu) { this.currentMenu = menu; }
    public void setCurrentSession(GameSession s) { this.currentSession = s; }
    public void setPendingRecoveryUsername(String s) { this.pendingRecoveryUsername = s; }
}
