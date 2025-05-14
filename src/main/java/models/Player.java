package models;

import models.MapElements.Tile.Tile;
import models.Tools.Backpack.Backpack;

public class Player {
    private UserInfo UserInfo;
    private Backpack backpack = new Backpack();
    private int PlayerX;
    private int PlayerY;
    private Tile currentTile;
    private Energy energy;

    public UserInfo getUserInfo() {
        return UserInfo;
    }

    public Backpack getInventory() {
        return backpack;
    }

    public int getPlayerX() {
        return PlayerX;
    }

    public int getPlayerY() {
        return PlayerY;
    }

    public Tile getCurrentTile() {
        return currentTile;
    }

    public Energy getEnergy() {
        return energy;
    }
}
