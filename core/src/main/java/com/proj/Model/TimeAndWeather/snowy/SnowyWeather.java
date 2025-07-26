package com.proj.Model.TimeAndWeather.snowy;

// SnowRenderer.java

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.proj.Model.GameAssetManager;

public class SnowyWeather {
    private Array<SnowFlake> snowflakes;
    private final TextureRegion crystalTexture;
    private final int flakeCount = 1200;
    private float windForce = 0f;
    private float windShiftTime = 0f;

    public SnowyWeather() {
        snowflakes = new Array<>(flakeCount);
        crystalTexture = GameAssetManager.getGameAssetManager().getSnowflake();
        initialize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    private void initialize(int width, int height) {
        for (int i = 0; i < flakeCount; i++) {
            snowflakes.add(new SnowFlake(width, height));
        }
    }


    public void update(float delta) {
        updateWindPattern(delta);
        for (SnowFlake flake : snowflakes) {
            flake.updateMovement(windForce, delta);
        }
    }

    private void updateWindPattern(float delta) {
        windShiftTime -= delta;
        if (windShiftTime <= 0) {
            windForce = MathUtils.random(-8f, 8f);
            windShiftTime = MathUtils.random(2f, 5f);
        }
    }

    public void render(SpriteBatch batch) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        for (SnowFlake flake : snowflakes) {
            batch.setColor(flake.color);
            batch.draw(
                crystalTexture,
                flake.position.x - flake.dimension,
                flake.position.y - flake.dimension,
                flake.dimension,
                flake.dimension,
                flake.dimension * 2,
                flake.dimension * 2,
                1,
                1,
                flake.rotation
            );
        }
        batch.setColor(Color.WHITE);
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    public void resize(int width, int height) {
        snowflakes.clear();
        initialize(width, height);
    }

    public void dispose() {
        crystalTexture.getTexture().dispose();
    }
}
