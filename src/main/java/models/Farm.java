package models;

import models.MapElements.Tile.Tile;
import models.MapElements.Tile.TileCreate;
import models.MapElements.Tile.TileType;

import java.util.Random;

public class Farm {
    public static GameMap farm(int size) {
        GameMap map = new GameMap(size, false);
        placeElement(map, TileCreate.create(TileType.COTTAGE), 1, 1, 4, 4);
        placeElement(map, TileCreate.create(TileType.GREENHOUSE), size - 1 - 6, 1, 6, 5);
        placeElement(map, TileCreate.create(TileType.LAKE), 1, size - 1 - 4, 6, 4);
        placeElement(map, TileCreate.create(TileType.QUARRY), size - 1 - 5, size - 1 - 3, 5, 3);
        Random rnd = new Random();
        Scatter(map, TileType.TREE, size * size / 20, rnd);
        Scatter(map, TileType.STONE, size * size / 30, rnd);
        //  Scatter(map, TileType.FORAGING, size * size / 25, rnd);
        return map;
    }

    private static void placeElement(GameMap map, Tile tile, int x, int y, int w, int h) {
        for (int j = y; j < y + h; j++) {
            for (int i = x; i < x + w; i++) {
                map.setTile(i, j, tile);
            }
        }
    }

    private static void Scatter(GameMap map, TileType tile, int count, Random rnd) {
        int n = map.getSize();
        for (int i = 0; i < count; i++) {
            int x, y;
            do {
                x = rnd.nextInt(n);
                y = rnd.nextInt(n);
            } while (map.getTile(x, y).getTileType() != TileType.PLAIN);
            map.setTile(x, y, TileCreate.create(tile));
        }
    }
}

