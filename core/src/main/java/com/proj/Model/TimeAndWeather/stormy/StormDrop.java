package com.proj.Model.TimeAndWeather.stormy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class StormDrop {
    public Vector2 position, velocity;
    public float dimension;
    public Color color;

    public StormDrop(float screenWidth, float screenHeight) {
        reset(screenWidth, screenHeight);
        dimension = MathUtils.random(3f, 6f);
        color = new Color(0.8f, 0.8f, 1f, MathUtils.random(0.5f, 0.9f));
    }

    public void update(float wind, float delta) {
        velocity.x += wind * delta;
        position.x += velocity.x * delta;
        position.y += velocity.y * delta;

        if (isOut()) {
            reset(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        }
    }

    private boolean isOut() {
        return position.y < -dimension || position.x < -dimension * 2 ||
            position.x > Gdx.graphics.getWidth() + dimension * 2;
    }

    private void reset(float w, float h) {
        position = new Vector2(
            MathUtils.random(0, w),
            MathUtils.random(h, h * 2f)
        );
        velocity = new Vector2(
            MathUtils.random(-10f, 10f),
            MathUtils.random(-250f, -180f)
        );
    }
}

