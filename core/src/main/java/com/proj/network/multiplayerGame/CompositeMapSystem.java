package com.proj.network.multiplayerGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class CompositeMapSystem {
    private Texture background;
    private Texture circleTexture;
    private ObjectMap<String, MapConfig> mapConfigs = new ObjectMap<>();
    private Array<PlayerMarker> playerMarkers = new Array<>();
    private float canvasWidth, canvasHeight;

    private static class MapConfig {
        Texture mapTexture;
        float x, y, width, height;
        float gameMinX, gameMinY, gameMaxX, gameMaxY;
        float scaleX, scaleY;

        MapConfig(Texture texture, float x, float y, float w, float h,
                  float minX, float minY, float maxX, float maxY) {
            this.mapTexture = texture;
            this.x = x; this.y = y;
            this.width = w; this.height = h;
            this.gameMinX = minX; this.gameMinY = minY;
            this.gameMaxX = maxX; this.gameMaxY = maxY;
            this.scaleX = width / (gameMaxX - gameMinX);
            this.scaleY = height / (gameMaxY - gameMinY);
        }

        Vector2 convertToCanvasPosition(float gameX, float gameY) {
            float nx = (gameX - gameMinX) / (gameMaxX - gameMinX);
            float ny = (gameY - gameMinY) / (gameMaxY - gameMinY);
            return new Vector2(x + nx * width, y + ny * height);
        }
    }

    private static class PlayerMarker {
        String playerId, mapName;
        Vector2 position;
        Color color;

        PlayerMarker(String id, String map, float x, float y, Color c) {
            playerId = id; mapName = map;
            position = new Vector2(x, y);
            this.color = c;
        }
    }

    public CompositeMapSystem() {
        background = new Texture(Gdx.files.internal("background.png"));
        circleTexture = createCircleTexture(12, Color.WHITE);

        // نمونه‌ی پیکربندی نقشه‌ها
        addMapConfig("Farm",     new Texture("map_pic/farm.jpg"),     100, 300, 1024, 831,  0,0,80*16,65*16);
        addMapConfig("Town",     new Texture("map_pic/town.jpg"),     400, 100, 600, 550,  0,0,130*16,110*16);
        addMapConfig("Mountain", new Texture("map_pic/mountain.jpg"),  700, 250, 350, 400,  0,0,1500*16,2000*16);
        addMapConfig("Beach",    new Texture("map_pic/beach.jpg"),    400, 500, 800, 243,  0,0,135*16,41*16);
        addMapConfig("BusStop",  new Texture("map_pic/busstop.jpg"),   500, 500, 800, 686,  0,0,65*16,30*16);
        addMapConfig("Forest",   new Texture("map_pic/forest.jpg"),    500, 500, 800, 800,  0,0,120*16,120*16);
        addMapConfig("Backwoods",new Texture("map_pic/backwoods.jpg"), 500, 500, 800, 800,  0,0,120*16,120*16);

        // محاسبه اندازه‌ی کل بوم
        for (MapConfig cfg : mapConfigs.values()) {
            canvasWidth  = Math.max(canvasWidth,  cfg.x + cfg.width);
            canvasHeight = Math.max(canvasHeight, cfg.y + cfg.height);
        }
    }

    public void addMapConfig(String name, Texture tex,
                             float x, float y, float w, float h,
                             float minX, float minY, float maxX, float maxY) {
        mapConfigs.put(name, new MapConfig(tex, x, y, w, h, minX, minY, maxX, maxY));
    }

    public void updatePlayerPosition(String playerId, String mapName, float x, float y) {
        PlayerMarker m = null;
        for (PlayerMarker pm : playerMarkers) {
            if (pm.playerId.equals(playerId)) { m = pm; break; }
        }
        if (m == null) {
            Color c = new Color((float)Math.random()*0.5f+0.5f,
                (float)Math.random()*0.5f+0.5f,
                (float)Math.random()*0.5f+0.5f,1);
            m = new PlayerMarker(playerId, mapName, x, y, c);
            playerMarkers.add(m);
        } else {
            m.mapName = mapName;
            m.position.set(x, y);
        }
    }

    public void render(Batch batch, float winX, float winY, float winW, float winH) {
        float sx = winW / canvasWidth;
        float sy = winH / canvasHeight;

        // بک‌گراند
        batch.draw(background, winX, winY, winW, winH);

        // نقشه‌های فرعی
        for (MapConfig cfg : mapConfigs.values()) {
            float dx = winX + cfg.x * sx;
            float dy = winY + cfg.y * sy;
            float dw = cfg.width * sx;
            float dh = cfg.height * sy;
            batch.draw(cfg.mapTexture, dx, dy, dw, dh);
        }

        // نشانگرها
        for (PlayerMarker pm : playerMarkers) {
            MapConfig cfg = mapConfigs.get(pm.mapName);
            if (cfg == null) continue;
            Vector2 pos = cfg.convertToCanvasPosition(pm.position.x, pm.position.y);
            float dx = winX + pos.x * sx - 6;
            float dy = winY + pos.y * sy - 6;
            batch.setColor(pm.color);
            batch.draw(circleTexture, dx, dy, 12, 12);
            batch.setColor(Color.WHITE);
        }
    }

    private Texture createCircleTexture(int size, Color color) {
        Pixmap pm = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        pm.setColor(color);
        pm.fillCircle(size/2, size/2, size/2);
        Texture tex = new Texture(pm);
        pm.dispose();
        return tex;
    }

    public void dispose() {
        background.dispose();
        circleTexture.dispose();
        for (MapConfig cfg : mapConfigs.values()) {
            cfg.mapTexture.dispose();
        }
    }
}
