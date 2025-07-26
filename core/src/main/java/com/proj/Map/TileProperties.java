package com.proj.Map;

public class TileProperties {
    public static int Passable = 1 << 0;
    private int flag;
    public TileProperties(int flag) {
        this.flag = flag;
    }
    public boolean isPassable() {
        return (flag & Passable) != 0;
    }
}
