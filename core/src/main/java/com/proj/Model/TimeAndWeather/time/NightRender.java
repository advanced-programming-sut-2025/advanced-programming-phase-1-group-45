

package com.proj.Model.TimeAndWeather.time;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class NightRender {
    private int STAR_COUNT = 300;
    private float darknessDegree = 0f;
    private Texture starTexture;
    private Array<Vector2> stars = new Array<>();
    private ShapeRenderer shapeRenderer = new ShapeRenderer();
    private float blueDegree = 0.1f;

    private final float screenWidth = Gdx.graphics.getWidth();
    private final float screenHeight = Gdx.graphics.getHeight();

    public NightRender() {
        makeStarTexture();
        initialize();
    }

    private void initialize() {
        for (int x = 0; x < STAR_COUNT; x++) {
            stars.add(new Vector2(MathUtils.random(-50, screenWidth + 50),
                MathUtils.random(screenHeight, screenHeight * 2f)));
        }
    }

    private void makeStarTexture() {
        Pixmap pixmap = new Pixmap(20, 20, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        starTexture = new Texture(pixmap);
        pixmap.dispose();
    }

    public void render(SpriteBatch batch) {
        if (darknessDegree <= 0f) {
            return;
        }
        batch.end();
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, blueDegree, darknessDegree);
        shapeRenderer.rect(0, 0, screenWidth, screenHeight);
        shapeRenderer.end();
        batch.begin();
        if (darknessDegree > 0.3f) {
//            batch.setColor(1, 1, 1, Math.min(1f, (darknessDegree - 0.3f) * 2f));
            for (Vector2 star : stars) {
                batch.draw(starTexture, star.x, star.y, 1, 1);
            }
        }
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    public void update(Time time) {
        if (!time.isNight()) {
            darknessDegree = 0f;
            return;
        }

        switch (time.getSeason()) {
            case SPRING:
                blueDegree = 0.2f;
                break;
                case SUMMER:
                    blueDegree = 0.1f;
                    break;
                    case WINTER:
                        blueDegree = 0.5f;
                        break;
                        case FALL:
                            blueDegree = 0.3f;
                            break;
        }

        float nightDegree = ((time.getHour() - 18) * 60 + time.getMinute()) / (4f * 60f);
        darknessDegree = Math.min(0.81f, nightDegree);

        for (Vector2 star : stars) {
            star.x += MathUtils.random(-0.2f, 0.2f);
            star.y += MathUtils.random(-0.1f, 0.1f);

            if (star.x < 0 || star.y < 0 || star.x >= screenWidth || star.y >= screenHeight) {
                updateStar(star);
            }
        }
    }


    private void updateStar(Vector2 star) {
        star = new Vector2(MathUtils.random(-50, screenWidth + 50), MathUtils.random(0, screenHeight + 50));
    }

    public void resize(int width, int height) {
        stars.clear();
        initialize();
    }

    public void dispose() {
        stars.clear();
        starTexture.dispose();
        shapeRenderer.dispose();
    }
}
