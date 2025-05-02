package controllers;
import managers.GameManager;
import managers.UserManager;
import models.Player;
import models.GameSession;
import views.Menu;
import views.StartMenu;


import java.util.*;

public class MenuController {
    private Menu currentMenu = new StartMenu();
    private Scanner scanner = new Scanner(System.in);
    private GameManager gameManager = new GameManager();
    private UserManager userManager = new UserManager();
    private Player currentPlayer;
    private GameSession currentSession;
    private String pendingRecoveryUsername;


    public UserManager getUserManager() {return userManager;}
    public GameManager getGameManager() {return gameManager;}
    public Player getCurrentUser() {return currentPlayer;}
    public GameSession getCurrentSession() {return currentSession;};
    public void setCurrentUser(Player u) {this.currentPlayer = u;}
    public void setCurrentMenu(Menu menu) {this.currentMenu = menu;}
    public void setCurrentSession(GameSession s) {this.currentSession = s;};//save user, map
    public void setPendingRecoveryUsername(String p) {this.pendingRecoveryUsername = p;}
    public String getPendingRecoveryUsername() {return this.pendingRecoveryUsername;}

    public void run(){
        while(true){
            System.out.println("> ");
            String line = scanner.nextLine();
            currentMenu.handleCommand(line, this);
        }
    }
}