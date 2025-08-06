package com.proj.Control;

import com.proj.Model.mapObjects.NPCObject;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NPCManager {
    private final List<NPCObject> npcs = new ArrayList<>();
    private final Random random = new Random();

    public void placeNPCsRandomly(int mapWidth, int mapHeight, int tileWidth, int tileHeight) {
        for (NPCObject npc : npcs) {
            Point randomPos = new Point(
                random.nextInt(mapWidth),
                random.nextInt(mapHeight)
            );
            npc.setPosition(randomPos);
            // Convert to pixel position immediately
            npc.setPixelPosition(
                randomPos.x * tileWidth,
                randomPos.y * tileHeight
            );
        }
    }

    public void addNPC(NPCObject npc) {
        npcs.add(npc);
    }

    public List<NPCObject> getNPCs() {
        return npcs;
    }
}
