package models;

import models.Enums.Tile;

import java.util.Random;

public class Farm {
    public static GameMap farm(int size) {
        GameMap map = new GameMap(size);
        placeElement(map, Tile.COTTAGE, 1,1,4,4);
        placeElement(map, Tile.GREENHOUSE, size-1-6, 1, 6, 5);
        placeElement(map, Tile.LAKE, 1, size-1-4, 6, 4);
        placeElement(map, Tile.QUARRY, size-1-5, size-1-3, 5, 3);
        Random rand = new Random();
        Scatter(map, Tile.TREE, size * size / 20, rnd);
        Scatter(map, Tile.STONE, size * size / 30, rnd);
        Scatter(map, Tile.FORAGING, size * size / 25, rnd);
        return map;
    }

    private static void placeElement(GameMap map, Tile tile, int x, int y, int w, int h) {
        for (int j = y; j<y + h; j++) {
            for (int i = x; i<x + w; i++) {
                map.setTile(i, j, tile);
            }
        }
    }

    private static void Scatter(GameMap map, Tile tile, int count, Random rnd) {
        int n = map.getSize();
        for (int i = 0; i < count; i++) {
            int x, y ;
            do{
                x = rnd.nextInt(n);
                y = rnd.nextInt(n);
            } while (map.getTile(x, y) != Tile.PLAIN);
            map.setTile(x, y, tile);
        }
    }
}
