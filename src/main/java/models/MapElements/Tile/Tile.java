package models.MapElements.Tile;

import models.MapElements.Tile.TileFeatures.TileFeature;

import java.util.HashMap;
import java.util.Map;

public class Tile {
    private TileType tileType;
    private int x;
    private int y;
    private Map<Class<? extends TileFeature>, TileFeature> features;
    private char symbol;

    public Tile(TileType type) {
        this.tileType = type;
        features = new HashMap<>();
    }

    public char getSymbol() {
        return symbol;
    }

    public void setSymbol(char symbol) {
        this.symbol = symbol;
    }

    public void addFeature(Class<? extends TileFeature> featureClass, TileFeature feature) {
        features.put(featureClass, feature);
    }

    public <T extends TileFeature> T getFeature(Class<T> featureClass) {
        if (features.containsKey(featureClass)) {
            return featureClass.cast(features.get(featureClass));
        }
        return null;
    }

    public boolean hasFeature(Class<? extends TileFeature> featureClass) {
        return features.containsKey(featureClass);
    }

    public void removeFeature(Class<? extends TileFeature> featureClass) {
        features.remove(featureClass);
    }

    public TileType getTileType() {
        return tileType;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
    public void setX(int x) {
        this.x = x;
    }
    public void setY(int y) {
        this.y = y;
    }
}
