package com.proj.Model.TimeAndWeather.rainy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

public class RainyWeather {
    private int DROP_COUNT;
    private List<Rain> drops;
    private ShapeRenderer shapeRenderer;

    private float windForce;
    private float windTimer;
    private float windChangeInterval;

    public RainyWeather() {
        DROP_COUNT = 200;
        drops = new ArrayList<>(DROP_COUNT);
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);
        initializeWind();
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    private void initializeWind() {
        windForce = MathUtils.random(-25f, 25f);
        windChangeInterval = MathUtils.random(3f, 7f);
        windTimer = 0;
    }

    public void update(float delta) {
        updateWind(delta);
        for (Rain drop : drops) {
         drop.update(windForce, delta);
        }
    }

    private void updateWind(float delta) {
        windTimer += delta;
        if (windTimer >= windChangeInterval) {
            float targetWind = MathUtils.random(-30f, 30f);
            windForce += (targetWind - windForce) * 0.5f;
            windChangeInterval = MathUtils.random(4f, 8f);
            windTimer = 0;
        }
    }

    public void render(SpriteBatch batch) {
        batch.end();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.begin();
        shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
        for (Rain drop : drops) {
                Vector2 endPos = new Vector2(drop.position).mulAdd(drop.speed.cpy().nor(), -drop.length);
                shapeRenderer.rectLine(
                    drop.position.x, drop.position.y,
                    endPos.x, endPos.y,
                    drop.scale
                );
        }
        shapeRenderer.end();
        batch.setColor(Color.WHITE);
        Gdx.gl.glDisable(GL20.GL_BLEND);
        batch.begin();
    }

    public void resize(int width, int height) {
        drops.clear();
        for (int i = 0; i < DROP_COUNT; i++) {
            drops.add(new Rain(width, height));
        }
    }

    public void dispose() {
        shapeRenderer.dispose();
    }
}
