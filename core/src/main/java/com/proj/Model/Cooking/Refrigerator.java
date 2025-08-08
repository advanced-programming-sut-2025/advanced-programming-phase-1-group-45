// Refrigerator.java
package com.proj.Model.Cooking;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.proj.Model.Inventory.Inventory;
import com.proj.Model.GameAssetManager;

public class Refrigerator {
    private Inventory inventory;
    private Vector2 position;
    private TextureRegion texture;
    private Rectangle bounds;

    private static final float WIDTH = 32;
    private static final float HEIGHT = 32;

    public Refrigerator(float x, float y) {
        this.inventory = new Inventory(24);
        this.position = new Vector2(x, y);
        this.texture = GameAssetManager.getGameAssetManager().getRefrigeratorTexture();
        this.bounds = new Rectangle(x, y, WIDTH, HEIGHT);
    }

    public Inventory getInventory() {
        return inventory;
    }

    public Vector2 getPosition() {
        return position;
    }

    public TextureRegion getTexture() {
        return texture;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void render(SpriteBatch batch) {
        if (texture != null) {
            batch.draw(texture, position.x, position.y, WIDTH, HEIGHT);
        }
    }

    public void dispose() {
        // Texture is managed by GameAssetManager, no need to dispose here.
    }
}
