package com.proj.Map;


import java.awt.*;

public class Tile {
    private Point location;
    private TileType type;
    private LandObject landObject = null;

    private boolean passable = false;
    private boolean hasForaging = false;
    private String foraging;
    private boolean isOccupied = false;
    private boolean isHomeGate = false;
    private boolean tilled = false;
    private boolean watered = false;
    private boolean fertilized = false;
    private boolean thundered = false;

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

    public void setOccupied(boolean occupied) {
        this.isOccupied = occupied;
    }

    public boolean isOccupied() {
        return isOccupied;
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

    public void setTilled(boolean tilled) {
        this.tilled = tilled;
    }

    public boolean isTilled() {
        return tilled;
    }

    public void setWatered(boolean watered) {
        this.watered = watered;
    }

    public boolean isWatered() {
        return watered;
    }

    public void setFertilized(boolean fertilized) {
        this.fertilized = fertilized;
    }

    public boolean isFertilized() {
        return fertilized;
    }

    public void setThundered(boolean thundered) {
        this.thundered = thundered;
    }

    public boolean isThundered() {
        return thundered;
    }

}
