package com.proj.map;


import java.awt.*;

public class Tile {
    private Point location;
    private TileType type;
    private LandObject landObject = null;

    private boolean passable = false;
    private boolean hasForaging = false;
    private String foraging;

    private boolean isHomeGate = false;


    public Tile(Point location, TileType type) {
        this.location = location;
        this.type = type;
    }


    public Tile(Point point) {
        this.location = point;
    }

    public TileType getType() {
        return type;
    }

    public void setObject(LandObject landObject) {
        this.landObject = landObject;
    }
    public void removeObject() {
        landObject = null;
    }

    public void setPassable(boolean passable) {
        this.passable = passable;
    }

    public void setType(TileType type) {
        this.type = type;
    }

    public boolean isPassable() {
        return passable;
    }

    public void setHomeGate(boolean homeGate) {
        isHomeGate = homeGate;
    }

    public boolean isHomeGate() {
        return isHomeGate;
    }

    public Point getLocation() {
        return location;
    }

    public LandObject getLandObject() {
        return landObject;
    }
}
