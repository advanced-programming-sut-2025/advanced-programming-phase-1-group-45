package views;

import models.Player;

public class AppView {
    Player currentPlayer;
    public void setCurrentUser(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }
    public Player getCurrentUser() {
        return currentPlayer;
    }

}
