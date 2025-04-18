package controllers;
import Managers.GameManager;
import Managers.UserManager;
import models.User;
import models.GameSession;
import views.Menu;
import views.StartMenu;


import java.util.*;

public class MenuController {
    private Menu currentMenu = new StartMenu();
    private Scanner scanner = new Scanner(System.in);
    private GameManager gameManager = new GameManager();
    private UserManager userManager = new UserManager();
    private User currentUser;
    private GameSession currentSession;

    public UserManager getUserManager() {return userManager;}
    public GameManager getGameManager() {return gameManager;}
    public User getCurrentUser() {return currentUser;}
    public GameSession getCurrentSession() {return currentSession;};
    public void setCurrentUser(User u) {this.currentUser = u;}
    public void setCurrentMenu(Menu menu) {this.currentMenu = menu;}
    public void setCurrentSession(GameSession s) {this.currentSession = s;};//save user, map

    public void run(){
        while(true){
            System.out.println("> ");
            String line = scanner.nextLine();
            currentMenu.handleCommand(line, this);
        }
    }
}