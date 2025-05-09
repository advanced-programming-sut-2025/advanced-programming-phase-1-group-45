package controllers;

import managers.GameManager;
import managers.ShopManager;
import managers.UserManager;
import models.User;
import models.GameSession;
import views.Menu;
import views.StartMenu;

public class MenuController {
    private Menu currentMenu = new StartMenu();
    private GameManager gameManager = new GameManager();
    private UserManager userManager = new UserManager();
    private ShopManager shopManager = new ShopManager(userManager);
    private User currentUser;
    private GameSession currentSession;
    private String pendingRecoveryUsername;

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
