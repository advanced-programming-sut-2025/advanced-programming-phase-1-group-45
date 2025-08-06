package com.proj.Model.inventoryItems.inventoryItems.seeds;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;

public class SeedOverlay implements InputProcessor {
    private final Array<Integer> seeds;
    private boolean visible = false;
    private final BitmapFont font;
    private final SpriteBatch batch;
    private final ShapeRenderer shapeRenderer;

    public SeedOverlay(Array<Integer> seeds) {
        this.seeds = seeds;
        font = new BitmapFont();                  // فونت پیش‌فرض
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
    }

    public boolean isVisible() {
        return visible;
    }

    @Override
    public boolean keyDown(int keycode) {
        // وقتی K فشرده شد و Shift (چپ یا راست) نگه داشته شده
        if (keycode == Input.Keys.K &&
            (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) ||
                Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT)))
        {
            visible = !visible;  // نمایش/مخفی کردن Overlay
            return true;
        }
        return false;
    }

    // سایر متدهای InputProcessor که اینجا نیازی به پیاده‌سازی ندارند
    @Override public boolean keyUp(int key) {return false;}
    @Override public boolean keyTyped(char c) {return false;}
    @Override public boolean touchDown(int x, int y, int pointer, int button) {return false;}
    @Override public boolean touchUp(int x, int y, int pointer, int button) {return false;}

    @Override
    public boolean touchCancelled(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override public boolean touchDragged(int x, int y, int pointer) {return false;}
    @Override public boolean mouseMoved(int x, int y) {return false;}
    @Override public boolean scrolled(float amountX, float amountY) {return false;}

    public void render() {
        if (!visible) return;

        // رسم پس‌زمینه‌ی نیمه‌شفاف
        Gdx.gl.glEnable(GL20.GL_BLEND);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 0.7f);
        shapeRenderer.rect(30, 30, Gdx.graphics.getWidth() - 60, Gdx.graphics.getHeight() - 60);
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        // رسم متن دانه‌ها
        batch.begin();
        font.setColor(Color.WHITE);
        float startY = Gdx.graphics.getHeight() - 50;
        for (int i = 0; i < seeds.size; i++) {
            font.draw(batch,
                String.format("Seed %d: %d", i + 1, seeds.get(i)),
                50,
                startY - i * 20);
        }
        batch.end();
    }

    public void dispose() {
        font.dispose();
        batch.dispose();
        shapeRenderer.dispose();
    }
}

