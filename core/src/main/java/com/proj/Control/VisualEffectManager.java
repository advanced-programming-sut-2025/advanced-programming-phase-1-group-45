package com.proj.Control;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class VisualEffectManager {
    private final Array<VisualEffect> activeEffects = new Array<>();
    private final int tileWidth;
    private final int tileHeight;

    public VisualEffectManager(int tileWidth, int tileHeight) {
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
    }

    public void addWateringEffect(int tileX, int tileY) {
        Vector2 position = new Vector2(
            tileX * tileWidth + tileWidth / 2,
            tileY * tileHeight + tileHeight / 2
        );
        try {
            activeEffects.add(new ParticleEffectWrapper("assets/particles/water_splash.p", position));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addFertilizeEffect(int tileX, int tileY, boolean isDeluxe) {
        Vector2 position = new Vector2(
            tileX * tileWidth + tileWidth / 2,
            tileY * tileHeight + tileHeight / 2
        );
        String effectPath = isDeluxe ? "assets/particles/deluxe_fertilize.p" : "assets/particles/basic_fertilize.p";
        try {
            activeEffects.add(new ParticleEffectWrapper(effectPath, position));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addHarvestEffect(int tileX, int tileY, TextureRegion itemTexture) {
        Vector2 position = new Vector2(
            tileX * tileWidth + tileWidth / 2,
            tileY * tileHeight + tileHeight / 2
        );
        activeEffects.add(new HarvestAnimation(itemTexture, position));
    }

    public void update(float delta) {
        for (int i = activeEffects.size - 1; i >= 0; i--) {
            VisualEffect effect = activeEffects.get(i);
            effect.update(delta);
            if (effect.isFinished()) {
                activeEffects.removeIndex(i);
            }
        }
    }

    public void render(SpriteBatch batch) {
        for (VisualEffect effect : activeEffects) {
            effect.render(batch);
        }
    }

    public void dispose() {
        for (VisualEffect effect : activeEffects) {
            if (effect instanceof ParticleEffectWrapper) {
                ((ParticleEffectWrapper) effect).particleEffect.dispose();
            }
        }
        activeEffects.clear();
    }

    private interface VisualEffect {
        void update(float delta);
        void render(SpriteBatch batch);
        boolean isFinished();
    }

    private static class ParticleEffectWrapper implements VisualEffect {
        private final ParticleEffect particleEffect;

        public ParticleEffectWrapper(String effectPath, Vector2 position) {
            particleEffect = new ParticleEffect();
            particleEffect.load(
                Gdx.files.internal(effectPath),
                Gdx.files.internal("assets/particles")
            );
            particleEffect.setPosition(position.x, position.y);
            particleEffect.start();
        }

        @Override
        public void update(float delta) {
            particleEffect.update(delta);
        }

        @Override
        public void render(SpriteBatch batch) {
            particleEffect.draw(batch);
        }

        @Override
        public boolean isFinished() {
            return particleEffect.isComplete();
        }
    }

    private static class HarvestAnimation implements VisualEffect {
        private final TextureRegion texture;
        private final Vector2 position;
        private final Vector2 velocity;
        private float alpha = 1.0f;
        private float duration = 1.0f;

        public HarvestAnimation(TextureRegion texture, Vector2 position) {
            this.texture = texture;
            this.position = position;
            this.velocity = new Vector2(0, 50);
        }

        @Override
        public void update(float delta) {
            duration -= delta;
            position.add(velocity.x * delta, velocity.y * delta);
            alpha = Math.max(0, duration);
        }

        @Override
        public void render(SpriteBatch batch) {
            batch.setColor(1, 1, 1, alpha);
            batch.draw(
                texture,
                position.x - 16,
                position.y - 16,
                32,
                32
            );
            batch.setColor(1, 1, 1, 1);
        }

        @Override
        public boolean isFinished() {
            return duration <= 0;
        }
    }
}
