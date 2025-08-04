package com.proj.View;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.proj.Model.GameAssetManager;

public class SpaceImageActor extends Actor {
    private TextureRegion image;
    private boolean visible = false;

    public SpaceImageActor() {
        // Load your PNG image
        GameAssetManager assetManager = GameAssetManager.getGameAssetManager();
        this.image = new TextureRegion(assetManager.getSpaceImageTexture());

        // Set size based on texture
        setSize(image.getRegionWidth(), image.getRegionHeight());
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (!visible) return;

        // Center on screen
        float x = (Gdx.graphics.getWidth() - getWidth()) / 2;
        float y = (Gdx.graphics.getHeight() - getHeight()) / 2;

        batch.draw(image, x, y);
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
