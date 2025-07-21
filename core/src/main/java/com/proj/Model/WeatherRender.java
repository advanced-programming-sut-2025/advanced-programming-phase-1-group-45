package com.proj.Model;

// WeatherRenderer.java (Updated)

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class WeatherRender {
    private SnowRender snowRenderer;
    private Color skyColor;

    public WeatherRender() {
        snowRenderer = new SnowRender();
        skyColor = new Color(0.7f, 0.8f, 1.0f, 1.0f);
    }

    public void update(Weather weather, float delta) {
        // Update sky color based on weather
        switch (weather) {
            case SNOWY:
                skyColor.set(0.8f, 0.85f, 0.95f, 1.0f); // Light blue-gray
                snowRenderer.update(delta);
                break;
            // Other weather types...
        }
    }

    public void render(SpriteBatch batch, Weather weather) {
//        if (weather == Weather.SNOWY) {
            snowRenderer.render(batch);
//        }
        // Other weather rendering...
    }

    public void resize(int width, int height) {
        snowRenderer.resize(width, height);
    }

    public void dispose() {
        snowRenderer.dispose();
    }

    public Color getSkyColor() {
        return skyColor;
    }
}
