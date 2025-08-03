package com.proj.Model.mapObjects;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.awt.Point;

public class NPCObject {
    private final TextureRegion textureRegion;
    private Point position;
    private float pixelX, pixelY;
    private float scale = 1.0f;

    public NPCObject(TextureRegion texture) {
        this.textureRegion = texture;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public Point getPosition() {
        return position;
    }

    public void setPixelPosition(float x, float y) {
        this.pixelX = x;
        this.pixelY = y;
    }

    public float getPixelX() {
        return pixelX;
    }

    public float getPixelY() {
        return pixelY;
    }

    public TextureRegion getTextureRegion() {
        return textureRegion;
    }

    public float getScale() {
        return scale;
    }
}
