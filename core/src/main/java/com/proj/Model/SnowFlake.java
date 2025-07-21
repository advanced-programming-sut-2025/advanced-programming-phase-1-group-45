package com.proj.Model;

// Snowflake.java

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class SnowFlake {
    public Vector2 position;
    public Vector2 velocity;
    public float size;
    public float rotation;
    public float rotationSpeed;
    public float alpha;
    public Color color;
    public float sway;
    public float swaySpeed;
    public float swayAmount;

    public SnowFlake(float screenWidth, float screenHeight) {
        position = new Vector2(
            MathUtils.random(0, screenWidth),
            MathUtils.random(screenHeight, screenHeight * 1.5f)
        );

        velocity = new Vector2(
            MathUtils.random(-5f, 5f),
            MathUtils.random(-30f, -15f)
        );

        size = MathUtils.random(1.5f, 4f);
        rotation = MathUtils.random(0f, 360f);
        rotationSpeed = MathUtils.random(-45f, 45f);
        alpha = MathUtils.random(0.6f, 0.95f);
        sway = MathUtils.random(0f, 100f);
        swaySpeed = MathUtils.random(0.5f, 2f);
        swayAmount = MathUtils.random(5f, 15f);

        // Color with slight variations
        float colorVar = MathUtils.random(0.9f, 1.0f);
        color = new Color(colorVar, colorVar, 1.0f, alpha);
    }

    public void update(float delta) {
        // Apply gravity and movement
        position.x += velocity.x * delta;
        position.y += velocity.y * delta;

        // Apply swaying motion
        sway += swaySpeed * delta;
        position.x += MathUtils.sin(sway) * swayAmount * delta;

        // Apply rotation
        rotation += rotationSpeed * delta;

        // Reset if out of bounds
        if (position.y < -size ||
            position.x < -size * 2 ||
            position.x > Gdx.graphics.getWidth() + size * 2) {
            reset(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }
    }

    private void reset(float screenWidth, float screenHeight) {
        position.set(
            MathUtils.random(0, screenWidth),
            MathUtils.random(screenHeight, screenHeight * 1.5f)
        );
        velocity.set(
            MathUtils.random(-5f, 5f),
            MathUtils.random(-30f, -15f)
        );
    }
}
