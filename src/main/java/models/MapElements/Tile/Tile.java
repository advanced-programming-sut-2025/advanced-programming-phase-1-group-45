package models.MapElements.Tile;

import models.MapElements.Tile.TileFeatures.TileFeature;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Tile {
    private TileType tileType;
    private int x;
    private int y;
    private Map<Class<? extends TileFeature>, TileFeature> features;

    Tile(TileType type) {
        this.tileType = type;
        features = new HashMap<>();
    }

    public void addFeature(Class<? extends TileFeature> featureClass, TileFeature feature) {
        features.put(featureClass, feature);
    }

    public <T extends TileFeature> Optional<T> getFeature(Class<T> featureClass) {
        return Optional.ofNullable(featureClass.cast(features.get(featureClass)));
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
}
