package models;
import models.Enums.Tile;

import java.util.Random;

public class GameMap {
    private int size;
    private Tile[][] grid;
    public GameMap(int size, boolean random) {
        this.size = size;
        grid = new Tile[size][size];
        if(random) generateRandomMap();
        else fillPlain();
    }
    private void generateRandomMap() {
        Random rand = new Random();
        fillPlain();
        for (Tile t : new Tile[]{Tile.LAKE, Tile.GREENHOUSE, Tile.COTTAGE, Tile.QUARRY}){
            placeRandom(t, rand);
        }
    }
    private void placeRandom(Tile t, Random rand) {
        int x, y;
        do{
            x = rand.nextInt(size);
            y = rand.nextInt(size);
        } while (grid[y][x] != Tile.PLAIN);
        grid[y][x] = t;
    }
    public Tile getTile(int x, int y) {
        if(x < 0 || y < 0 || x >= size || y >= size){
            return null;
        }
        return grid[y][x];
    }

    public int getSize() {return size;}

    public void setTile(int x, int y, Tile tile) {
        if(x < 0 || y < 0 || x >= size || y >= size){
            return ;
        }
         grid[y][x] = tile;
    }

    private void fillPlain() {
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                grid[y][x] = Tile.PLAIN;
            }
        }
    }
}