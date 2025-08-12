package com.proj.network.multiplayerGame;

import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;

public class CompositeMapWindow extends Window {
    private CompositeMapSystem mapSystem;
    private static final float PADDING = 8f;

    public CompositeMapWindow(Skin skin) {
        super("Composite Map", skin);
        mapSystem = new CompositeMapSystem();

        defaults().pad(PADDING);
        CompositeMapActor mapActor = new CompositeMapActor(mapSystem);
        getContentTable().add(mapActor).expand().fill().row();

        TextButton closeBtn = new TextButton("Close", skin);
        closeBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                setVisible(false);
            }
        });
        getButtonTable().add(closeBtn);

        setResizable(true);
        setSize(600, 450);
        setPosition(100, 100);
        pack();
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        // در صورت نیاز بروزرسانی سیستم نقشه
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    @Override
    public void dispose() {
        mapSystem.dispose();
    }

    private static class CompositeMapActor extends Actor {
        private CompositeMapSystem mapSystem;
        private Rectangle scissors = new Rectangle();

        CompositeMapActor(CompositeMapSystem sys) {
            this.mapSystem = sys;
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            ScissorStack.calculateScissors(
                getStage().getCamera(),
                batch.getTransformMatrix(),
                getX(), getY(), getWidth(), getHeight(),
                scissors
            );
            ScissorStack.pushScissors(scissors);

            mapSystem.render(batch, getX(), getY(), getWidth(), getHeight());

            ScissorStack.popScissors();
        }

        @Override
        public float getPrefWidth() { return 600; }
        @Override
        public float getPrefHeight() { return 450; }
    }
}
