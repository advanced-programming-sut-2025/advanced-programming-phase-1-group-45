package com.proj.map;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FarmInOutPoint {
    private String farmName;
    private int mapId;
    public HashMap<Point, Integer> farmExitPoints = new HashMap();
    public HashMap<Point, Integer> farmEnterPoints = new HashMap();

    public FarmInOutPoint(String farmName, int mapId) {
        this.farmName = farmName;
        this.mapId = mapId;
    }

    public void addFarmExitPoint(Point point, Integer targetMapId) {
        farmExitPoints.put(point, targetMapId);
    }

    public void addFarmEnterPoint(Point point, Integer targetMapId) {
        farmEnterPoints.put(point, targetMapId);
    }

    public String getFarmName() {
        return farmName;
    }

    public List<Point> getEnterPoints() {
        return new ArrayList<Point>(farmEnterPoints.keySet());
    }

    public int getMapId() {
        return mapId;
    }
}
