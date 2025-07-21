package com.proj.Model;

// SnowRenderer.java

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

public class SnowRender {
    private Array<SnowFlake> snowflakes;
    private Texture snowflakeTexture;
    private TextureRegion snowflakeVariants;
    private int snowflakeCount = 1000;
    private float windForce = 0f;
    private float windChangeTimer = 0f;

    public SnowRender() {
        snowflakes = new Array<>();

        // Create snowflake texture with multiple variants
        createSnowFlakeTexture();

        // Initialize snowflakes
        for (int i = 0; i < snowflakeCount; i++) {
            snowflakes.add(new SnowFlake(
                Gdx.graphics.getWidth(),
                Gdx.graphics.getHeight()
            ));
        }
    }

    private void createSnowFlakeTexture() {
        // Create procedural snowflake texture with 4 variants
        snowflakeVariants = new TextureRegion();

//        for (int i = 0; i < 2; i++) {
            snowflakeVariants = generateSnowFlakeTexture();
//        }
    }

    private TextureRegion generateSnowFlakeTexture() {
        // In a real game, you'd want to pre-render these
        // For now, we'll use simple shapes
        // In practice, you should create actual textures
        return new TextureRegion(new Texture("snowflake1.png"));
    }

    public void update(float delta) {
        // Update wind effects
        updateWind(delta);

        // Update all snowflakes
        for (SnowFlake flake : snowflakes) {
            // Apply wind force
            flake.velocity.x += windForce * delta;

            // Apply slight random movement
            flake.velocity.x += MathUtils.random(-0.2f, 0.2f);

            flake.update(delta);
        }
    }

    private void updateWind(float delta) {
        windChangeTimer -= delta;

        if (windChangeTimer <= 0) {
            // Change wind direction and strength
            windForce = MathUtils.random(-8f, 8f);
            windChangeTimer = MathUtils.random(3f, 8f);
        }
    }

    public void render(SpriteBatch batch) {
        // Enable blending for transparency
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        for (SnowFlake flake : snowflakes) {
            TextureRegion texture = snowflakeVariants;//[(int)(flake.size) % 4];

            batch.setColor(flake.color);
            batch.draw(
                texture,
                flake.position.x - flake.size,
                flake.position.y - flake.size,
                flake.size,
                flake.size,
                flake.size * 2,
                flake.size * 2,
                1,
                1,
                flake.rotation
            );
        }

        // Reset batch color
        batch.setColor(1, 1, 1, 1);
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    public void resize(int width, int height) {
        // Reset all snowflakes for new screen size
        snowflakes.clear();
        for (int i = 0; i < snowflakeCount; i++) {
            snowflakes.add(new SnowFlake(width, height));
        }
    }

    public void dispose() {
//        for (TextureRegion region : snowflakeVariants) {
            snowflakeVariants.getTexture().dispose();
//        }
    }
}
