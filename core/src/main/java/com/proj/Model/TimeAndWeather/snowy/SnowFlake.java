package com.proj.Model.TimeAndWeather.snowy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class SnowFlake {
    public Vector2 position, speed;
    public float dimension;
    public float rotation, rotationSpeed;
    public float alpha;
    public Color color;
    public float oscillation, oscillationSpeed, oscillationRange;

    public SnowFlake(float width, float height) {
        initialize(width, height);
    }

    private void initialize(float screenWidth, float screenHeight) {
        reset(screenWidth, screenHeight);
        dimension = MathUtils.random(2f, 5f);
        rotation = MathUtils.random(180f, 360f);
        rotationSpeed = MathUtils.random(-35, 35f);
        alpha = MathUtils.random(0.62f, 0.96f);
        oscillation = MathUtils.random(0f, 100f);
        oscillationSpeed = MathUtils.random(0.5f, 2f);
        oscillationRange = MathUtils.random(5f, 15f);
        color = new Color(0.95f, 0.9f, 1.0f, alpha);
    }

    public void updateMovement(float windForce, float delta) {
        speed.x += windForce * delta;
        speed.x += MathUtils.random(-0.18f, 0.2f);
        position.x += MathUtils.cos(oscillation) * oscillationRange * delta;
        oscillation += oscillationSpeed * delta;

        position.x += speed.x * delta;
        position.y += speed.y * delta;
        rotation += rotationSpeed * delta;

        if (isOutOfBounds()) {
            reset(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }
    }

    private boolean isOutOfBounds() {
        return position.y < -dimension || position.x < -dimension * 2 ||
            position.x > Gdx.graphics.getWidth() + dimension * 2;
    }

    private void reset(float screenWidth, float screenHeight) {
        position = new Vector2(
            MathUtils.random(0, screenWidth),
            MathUtils.random(0, screenHeight * 2f)
        );

        speed = new Vector2(
            MathUtils.random(-5f, 5f),
            MathUtils.random(-30f, -15f)
        );
    }

}
