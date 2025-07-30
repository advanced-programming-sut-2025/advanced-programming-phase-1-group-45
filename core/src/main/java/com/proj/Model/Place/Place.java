package com.proj.Model.Place;

import java.awt.Point;

public abstract class Place {
    protected Point position;
    protected int height;
    protected int width;

    public Place(Point position, int height, int width) {
        this.position = position;
        this.height = height;
        this.width = width;
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }


    public boolean contains(Point pos) {
        int px = pos.x;
        int py = pos.y;
        return px >= position.x && px < position.x + width &&
            py >= position.y && py < position.y + height;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }
}
