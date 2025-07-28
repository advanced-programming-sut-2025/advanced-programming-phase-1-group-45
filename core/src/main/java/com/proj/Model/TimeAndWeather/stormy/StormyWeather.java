package com.proj.Model.TimeAndWeather.stormy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.proj.Model.GameAssetManager;

public class StormyWeather {
    private Array<StormDrop> drops;
    private TextureRegion dropTexture;
    private final int dropCount = 1000;
    private float windForce = 0f;
    private float windChangeTimer = 0f;
    private float lightningTimer = 0f;
    private boolean flash = false;
    private Color skyColor = new Color(0.18f, 0.18f, 0.26f, 0.4f);
    private ShapeRenderer shapeRenderer = new ShapeRenderer();

    public StormyWeather() {
        drops = new Array<>(dropCount);
        dropTexture = new TextureRegion(GameAssetManager.getGameAssetManager().getThunder());
        initialize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    private void initialize(int width, int height) {
        for (int i = 0; i < dropCount; i++) {
            drops.add(new StormDrop(width, height));
        }
    }

    public void update(float delta) {
        windChangeTimer -= delta;
        lightningTimer -= delta;

        if (windChangeTimer <= 0) {
            windForce = MathUtils.random(-40f, 40f);
            windChangeTimer = MathUtils.random(2f, 5f);
        }

        if (lightningTimer <= 0) {
            flash = true;
            lightningTimer = MathUtils.random(2f, 4f);
        }

        for (StormDrop drop : drops) {
            drop.update(windForce, delta);
        }
    }

    public void render(SpriteBatch batch) {
        batch.end();
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(skyColor);
        shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer.end();
        batch.begin();
        for (StormDrop drop : drops) {
            batch.setColor(drop.color);
            batch.draw(
                dropTexture,
                drop.position.x,
                drop.position.y,
                drop.dimension,
                drop.dimension * 2
            );
        }
        if (flash) {
            batch.setColor(1f, 1f, 1f, 0.4f);
            batch.draw(
                dropTexture,
                0, 0,
                Gdx.graphics.getWidth(),
                Gdx.graphics.getHeight()
            );
            flash = false;
        }

        batch.setColor(Color.WHITE);
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    public void resize(int width, int height) {
        drops.clear();
        initialize(width, height);
    }

    public void dispose() {
        dropTexture.getTexture().dispose();
    }
}

