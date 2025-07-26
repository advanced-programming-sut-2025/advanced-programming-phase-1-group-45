package com.proj.Model.TimeAndWeather;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.proj.Model.TimeAndWeather.time.Time;

public class TimeRenderer {
    private final Time time;
    private final TextureRegion background;
    private final BitmapFont font;
    private final float x, y;

    public TimeRenderer(Time time, TextureRegion background, BitmapFont font, float x, float y) {
        this.time = time;
        this.background = background;
        this.font = font;
        this.x = x;
        this.y = y;
    }

    public void render(SpriteBatch batch) {
        // Draw background
        batch.draw(background, x, y);

        // Convert to 12-hour format with AM/PM
        int hour = time.getHour();
        String period = "AM";
        int displayHour = hour;

        if (hour >= 12) {
            period = "PM";
            displayHour = (hour > 12) ? hour - 12 : hour;
        }
        if (hour == 0) displayHour = 12; // Midnight

        // Format time string
        String timeString = String.format("%d:%02d", displayHour, time.getMinute());

        // Calculate positions
        float bgWidth = background.getRegionWidth();
        float bgHeight = background.getRegionHeight();

        // Draw time (centered)
        GlyphLayout timeLayout = new GlyphLayout(font, timeString);
        float timeX = x + (bgWidth - timeLayout.width) / 2;
        float timeY = y + (bgHeight + timeLayout.height) / 2;
        font.draw(batch, timeLayout, timeX, timeY);

        // Draw AM/PM (top-right corner)
        GlyphLayout periodLayout = new GlyphLayout(font, period);
        float periodX = x + bgWidth - periodLayout.width - 5; // 5px padding
        float periodY = y + bgHeight - 5; // 5px from top
        font.draw(batch, periodLayout, periodX, periodY);
    }
}
