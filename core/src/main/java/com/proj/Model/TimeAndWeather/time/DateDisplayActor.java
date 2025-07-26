package com.proj.Model.TimeAndWeather.time;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.proj.Model.GameAssetManager;

public class DateDisplayActor extends Actor {
    private final BitmapFont font;
    private final Time timer;
    private final GlyphLayout layout = new GlyphLayout();

    public DateDisplayActor(Time timer) {
        this.timer = timer;
        this.font = GameAssetManager.getGameAssetManager().getSmallFont();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        String time = timer.getDateForDisplay();
        layout.setText(font, time);

        float x = getX() - layout.width/2;
        float y = getY() - layout.height/2;
        font.setColor(Color.BLUE);
        font.draw(batch, layout, x, y);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }
}
