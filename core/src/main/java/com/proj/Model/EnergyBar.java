package com.proj.Model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.proj.Player;

public class EnergyBar {
    private Player player;
    private Vector2 position;
    private float width;
    private float height;
    private TextureRegion[] energyStates;

    public EnergyBar(Player player, float x, float y, float width, float height) {
        this.player = player;
        this.position = new Vector2(x, y);
        this.width = width;
        this.height = height;
        loadTextures();
    }

    private void loadTextures() {
        try {
            energyStates = new TextureRegion[3];
            energyStates[0] = new TextureRegion(new Texture(Gdx.files.internal("energy/energy_full.png")));
            energyStates[1] = new TextureRegion(new Texture(Gdx.files.internal("energy/energy_half.jpg")));
            energyStates[2] = new TextureRegion(new Texture(Gdx.files.internal("energy/energy_empty.jpg")));
        } catch (Exception e) {
            Gdx.app.error("EnergyBar", "Error loading textures", e);
            createFallbackTextures();
        }
    }

    private void createFallbackTextures() {
        energyStates = new TextureRegion[3];
        // Create colored fallback textures
        energyStates[0] = createColoredTexture(Color.GREEN);
        energyStates[1] = createColoredTexture(Color.YELLOW);
        energyStates[2] = createColoredTexture(Color.RED);
    }

    private TextureRegion createColoredTexture(Color color) {
        Pixmap pixmap = new Pixmap(16, 16, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return new TextureRegion(texture);
    }

    public void render(SpriteBatch batch) {
        float energyPercentage = player.getCurrentEnergy() / player.getMaxEnergy();
        int stateIndex;

        if (energyPercentage > 0.66f) {
            stateIndex = 0; // Full (green)
        } else if (energyPercentage > 0.33f) {
            stateIndex = 1; // Half (yellow)
        } else {
            stateIndex = 2; // Empty (red)
        }

        batch.draw(energyStates[stateIndex], position.x, position.y, width, height);
    }

    public void dispose() {
        for (TextureRegion region : energyStates) {
            if (region != null && region.getTexture() != null) {
                region.getTexture().dispose();
            }
        }
    }

    public void setPosition(float x, float y) {
        this.position.set(x, y);
    }
}
