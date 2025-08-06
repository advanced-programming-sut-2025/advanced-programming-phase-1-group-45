package com.proj.Model.inventoryItems.inventoryItems.trees;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.proj.Model.inventoryItems.trees.TreeData;
import com.proj.map.Season;

public class TreeRegistry {
    private static TreeRegistry instance;
    private  Array<com.proj.Model.inventoryItems.trees.TreeData> allData;

    private TreeRegistry() {
        FileHandle file = Gdx.files.internal("assets/data/tree_data.json");
        Json json = new Json();
        try {
            json.setElementType(com.proj.Model.inventoryItems.trees.TreeData.class, "season", Season.class);
            json.setElementType(com.proj.Model.inventoryItems.trees.TreeData.class, "texturePaths", String.class);
            json.setElementType(com.proj.Model.inventoryItems.trees.TreeData.class, "chopProduct", String.class);
            allData = file.exists()
                ? json.fromJson(Array.class, com.proj.Model.inventoryItems.trees.TreeData.class, file)
                : new Array<>();

            if (allData.size == 0) System.exit(0);
            for (com.proj.Model.inventoryItems.trees.TreeData data : allData) {
                data.loadTextures();
            }
        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static synchronized TreeRegistry getInstance() {
        if (instance == null) {
            instance = new TreeRegistry();
        }
        return instance;
    }

    public com.proj.Model.inventoryItems.trees.TreeData get(String treeId) {
        if (treeId == null || treeId.isEmpty()) return null;

        for (com.proj.Model.inventoryItems.trees.TreeData td : allData) {
            if (treeId.equals(td.getId())) return td;
        }
        return null;
    }

    public com.proj.Model.inventoryItems.trees.TreeData getBySeed(String seedId) {
        if (seedId == null || seedId.isEmpty()) return null;

        for (com.proj.Model.inventoryItems.trees.TreeData td : allData) {
            if (seedId.equals(td.getSource())) return td;
        }
        return null;
    }

    public Array<TreeData> getAllTreeData() {
        return allData;
    }
}
