package com.proj.Model.TimeAndWeather.rainy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Rain {
    public Vector2 position;
    public Vector2 speed;
    public float length;
    public float scale;
    private float gravityForce = 10f;

    private final float screenWidth, screenHeight;

    public Rain(float width, float height) {
        this.screenWidth = width;
        this.screenHeight = height;
        reset();
    }

    public void reset() {
        position = new Vector2(
            MathUtils.random(-50, screenWidth + 50),
            MathUtils.random(screenHeight, screenHeight * 1.5f)
        );
        speed = new Vector2(
            MathUtils.random(-15, 15),
            MathUtils.random(-600, -400)
        );
        length = MathUtils.random(12f, 25f);
        scale = MathUtils.random(1f, 2f);

    }

    public void update(float windForce, float delta) {
        speed.x += (float) (windForce * delta * 0.4);

        position.x += speed.x * delta;
        position.y += speed.y * delta;

        speed.y -= gravityForce * delta;

        if (position.y <= 0) {
            position.y = 0;
            reset();
        }else if (position.x < -100 || position.x > Gdx.graphics.getWidth() + 100) {
            reset();
        }
    }
}
