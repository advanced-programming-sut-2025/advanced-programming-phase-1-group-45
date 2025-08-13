package com.proj.network.multiplayerGame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.proj.Model.GameAssetManager;

public class CompositeMapSystem {
    private final Stage stage;
    private  Window mapWindow;
    private boolean isWindowOpen = false;

    private final ObjectMap<String, MapConfig> mapConfigs = new ObjectMap<>();
    private final Array<PlayerMarker> playerMarkers = new Array<>();
    private final Texture background;
    private final Texture markerTexture;
    private  CompositeMapActor mapActor;

    // ساختار تنظیمات هر نقشه
    private static class MapConfig {
        final Texture mapTexture;
        final Rectangle position = new Rectangle(); // موقعیت و ابعاد در نقشه ترکیبی
        final float gameMinX, gameMinY; // حداقل مختصات در بازی
        final float gameMaxX, gameMaxY; // حداکثر مختصات در بازی
        float scaleX, scaleY; // فاکتورهای تبدیل مختصات

        public MapConfig(Texture texture,
                         float minX, float minY, float maxX, float maxY) {
            this.mapTexture = texture;
            this.gameMinX = minX;
            this.gameMinY = minY;
            this.gameMaxX = maxX;
            this.gameMaxY = maxY;
        }

        public void updateScales() {
            if (position.width > 0 && position.height > 0) {
                this.scaleX = position.width / (gameMaxX - gameMinX);
                this.scaleY = position.height / (gameMaxY - gameMinY);
            }
        }

        public Vector2 convertToMapPosition(float gameX, float gameY) {
            float normalizedX = (gameX - gameMinX) / (gameMaxX - gameMinX);
            float normalizedY = (gameY - gameMinY) / (gameMaxY - gameMinY);

            return new Vector2(
                position.x + normalizedX * position.width,
                position.y + normalizedY * position.height
            );
        }
    }

    // ساختار نشانگر بازیکنان
    private static class PlayerMarker {
        final String playerId;
        String mapName;
        final Vector2 position;
        final Color color;

        public PlayerMarker(String id, String map, float x, float y, Color color) {
            this.playerId = id;
            this.mapName = map;
            this.position = new Vector2(x, y);
            this.color = color;
        }
    }

    public CompositeMapSystem(Stage stage) {
        this.stage = stage;
        background = new Texture(Gdx.files.internal("menu_bg.png")); // حذف assets/ از مسیر
        markerTexture = createCircleTexture(12, Color.WHITE);
        createMapWindow();
        GameAssetManager assetManager = GameAssetManager.getGameAssetManager();

        try {
            addMapConfig("Farm", new Texture("map_pic/farm.jpg"), 0, 0, 80 * 16, 65 * 16);
            addMapConfig("Town", new Texture("map_pic/town.jpg"), 0, 0, 130 * 16, 110 * 16);
            addMapConfig("Mountain", new Texture("map_pic/mountain.jpg"), 0, 0, 135 * 16, 41 * 16);
            addMapConfig("Beach", new Texture("map_pic/beach.jpg"), 0, 0, 104 * 16, 50 * 16);
            addMapConfig("BusStop", new Texture("map_pic/busstop.jpg"), 0, 0, 65 * 16, 30 * 16);
            addMapConfig("Forest", new Texture("map_pic/forest.jpg"), 0, 0, 120 * 16, 120 * 16);
            addMapConfig("BackWoods", new Texture("map_pic/backWoods.jpg"), 0, 0, 50 * 16, 40 * 16);
        } catch (Exception e) {
            Gdx.app.error("CompositeMapSystem", "Error loading map textures", e);
        }

        // محاسبه چیدمان نقشه‌ها
        arrangeMaps();
    }

    private void createMapWindow() {
        Skin skin = GameAssetManager.getGameAssetManager().getStardewSkin();

        mapWindow = new Window("Map Overview", skin);
        mapWindow.setKeepWithinStage(true);
        mapWindow.setResizable(true);
        mapWindow.setMovable(true);
        mapWindow.setSize(1500, 600);
        mapWindow.setPosition(
            (Gdx.graphics.getWidth() - mapWindow.getWidth()) / 2,
            (Gdx.graphics.getHeight() - mapWindow.getHeight()) / 2
        );
        mapWindow.setVisible(false);

        // دکمه بستن
        TextButton closeButton = new TextButton("X", skin);
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                closeMapWindow();
            }
        });
        mapWindow.getTitleTable().add(closeButton).size(30, 30).padRight(5);

        // Actor برای نمایش نقشه
        mapActor = new CompositeMapActor();
        ScrollPane scrollPane = new ScrollPane(mapActor, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(false, false);

        mapWindow.add(scrollPane).expand().fill().pad(10);
        stage.addActor(mapWindow);

        // کشیدن پنجره با نگه داشتن هر جای آن
        mapWindow.addListener(new InputListener() {
            private Vector2 dragOffset = new Vector2();

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                dragOffset.set(x, y);
                return true;
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                mapWindow.moveBy(x - dragOffset.x, y - dragOffset.y);
            }
        });
    }

    private class CompositeMapActor extends Actor {
        @Override
        public void draw(Batch batch, float parentAlpha) {
            batch.end();
            batch.begin();
            batch.draw(background, getX(), getY(), getWidth(), getHeight());

            // رسم نقشه‌های فردی
            for (MapConfig config : mapConfigs.values()) {
                batch.draw(config.mapTexture,
                    getX() + config.position.x,
                    getY() + config.position.y,
                    config.position.width,
                    config.position.height);
            }

            // رسم نشانگر بازیکنان
            for (PlayerMarker marker : playerMarkers) {
                MapConfig config = mapConfigs.get(marker.mapName);
                if (config != null) {
                    Vector2 mapPos = config.convertToMapPosition(marker.position.x, marker.position.y);

                    // تنظیم رنگ بازیکن
                    batch.setColor(marker.color);

                    // رسم نشانگر (اندازه ثابت)
                    batch.draw(markerTexture,
                        getX() + mapPos.x - 6,
                        getY() + mapPos.y - 6,
                        12, 12);
                    System.out.println("keshidam");

                    // بازگردانی رنگ پیش‌فرض
                    batch.setColor(Color.WHITE);
                }
            }
        }

        @Override
        public void act(float delta) {
            super.act(delta);
            float width = 0, height = 0;
            for (MapConfig config : mapConfigs.values()) {
                width = Math.max(width, config.position.x + config.position.width);
                height = Math.max(height, config.position.y + config.position.height);
            }
            setSize(width + 50, height + 50); // حاشیه اضافه
        }
    }

    private void arrangeMaps() {
        float padding = 30;
        float currentX = 20;
        float currentY = 20;
        float rowHeight = 0;

        for (MapConfig config : mapConfigs.values()) {
            // اگر نقشه در سطر فعلی جا نشود، به سطر بعد برو
            if (currentX + config.mapTexture.getWidth() > 1200) {
                currentX = 20;
                currentY += rowHeight + padding;
                rowHeight = 0;
            }

            // تنظیم موقعیت نقشه
            config.position.set(
                currentX,
                currentY,
                config.mapTexture.getWidth(),
                config.mapTexture.getHeight()
            );

            // 4. محاسبه مقیاس‌ها بعد از تنظیم موقعیت
            config.updateScales();

            // به‌روزرسانی موقعیت برای نقشه بعدی
            currentX += config.mapTexture.getWidth() + padding;
            rowHeight = Math.max(rowHeight, config.mapTexture.getHeight());
        }
    }

    public void addMapConfig(String mapName, Texture texture,
                             float minX, float minY, float maxX, float maxY) {
        if (texture != null) {
            mapConfigs.put(mapName, new MapConfig(texture, minX, minY, maxX, maxY));
        }
    }

    public void updatePlayerPosition(String playerId, String mapName, float x, float y) {
        PlayerMarker marker = findPlayerMarker(playerId);
        if (marker == null) {
            Color color = generateDistinctColor(playerId.hashCode());
            marker = new PlayerMarker(playerId, mapName, x, y, color);
            playerMarkers.add(marker);
        } else {
            marker.mapName = mapName;
            marker.position.set(x, y);
        }
    }

    // 5. تولید رنگ‌های متمایز بر اساس ID بازیکن
    private Color generateDistinctColor(int seed) {
        float hue = (seed & 0xFFFFFF) / 16777215.0f; // محدوده 0-1
//        return Color.rgb888(1f, new float[]{hue * 360, 0.8f, 0.9f});
        return Color.WHITE;
    }

    private PlayerMarker findPlayerMarker(String playerId) {
        for (PlayerMarker marker : playerMarkers) {
            if (marker.playerId.equals(playerId)) {
                return marker;
            }
        }
        return null;
    }

    public void openMapWindow() {
        mapWindow.setVisible(true);
        isWindowOpen = true;
        mapWindow.toFront(); // پنجره را به جلو بیاور
    }

    public void closeMapWindow() {
        mapWindow.setVisible(false);
        isWindowOpen = false;
    }

    public void toggleMapWindow() {
        if (isWindowOpen) {
            closeMapWindow();
        } else {
            openMapWindow();
        }
    }

//    public void render() {
//        if (isWindowOpen) {
//            stage.act(Gdx.graphics.getDeltaTime());
//            stage.draw();
//        }
//    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        // بازنشانی موقعیت پنجره هنگام تغییر سایز
        mapWindow.setPosition(
            (width - mapWindow.getWidth()) / 2,
            (height - mapWindow.getHeight()) / 2
        );
    }

    public void dispose() {
        background.dispose();
        markerTexture.dispose();
        for (MapConfig config : mapConfigs.values()) {
            config.mapTexture.dispose();
        }
        // نکته: stage توسط سازنده خارجی مدیریت می‌شود
    }

    private Texture createCircleTexture(int radius, Color color) {
        int size = radius * 2;
        Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fillCircle(radius, radius, radius);
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }
}
