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
    private Stage stage;
    private Window mapWindow;
    private boolean isWindowOpen = false;

    private ObjectMap<String, MapConfig> mapConfigs = new ObjectMap<>();
    private Array<PlayerMarker> playerMarkers = new Array<>();
    private Texture background;
    private Texture whiteCircle;
    private CompositeMapActor mapActor;

    // ساختار تنظیمات هر نقشه
    private static class MapConfig {
        Texture mapTexture;
        Rectangle position; // موقعیت و ابعاد در نقشه ترکیبی
        float gameMinX, gameMinY; // حداقل مختصات در بازی
        float gameMaxX, gameMaxY; // حداکثر مختصات در بازی
        float scaleX, scaleY; // فاکتورهای تبدیل مختصات

        public MapConfig(Texture texture,
                         float minX, float minY, float maxX, float maxY) {
            this.mapTexture = texture;
            this.gameMinX = minX;
            this.gameMinY = minY;
            this.gameMaxX = maxX;
            this.gameMaxY = maxY;
            this.position = new Rectangle();

            this.scaleX = position.width / (gameMaxX - gameMinX);
            this.scaleY = position.height / (gameMaxY - gameMinY);
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
        String playerId;
        String mapName;
        Vector2 position;
        Color color;

        public PlayerMarker(String id, String map, float x, float y, Color color) {
            this.playerId = id;
            this.mapName = map;
            this.position = new Vector2(x, y);
            this.color = color;
        }
    }

    public CompositeMapSystem(Stage stage) {
        this.stage = stage;

        // ایجاد بکگراند برای نقشه ترکیبی
        background = new Texture(Gdx.files.internal("assets/background.png"));

        // ایجاد دایره سفید برای نشانگرها
        whiteCircle = createCircleTexture(60, Color.WHITE);

        // ایجاد پنجره نقشه
        createMapWindow();

        // تنظیم نقشه‌ها
        addMapConfig("Farm", new Texture("map_pic/farm.jpg"), 0, 0, 80 * 16, 65 * 16);
        addMapConfig("Town", new Texture("map_pic/town.jpg"), 0, 0, 130 * 16, 110 * 16);
        addMapConfig("Mountain", new Texture("map_pic/mountain.jpg"), 0, 0, 1500 * 16, 2000 * 16);
        addMapConfig("Beach", new Texture("map_pic/beach.jpg"), 0, 0, 135 * 16, 41 * 16);
        addMapConfig("BusStop", new Texture("map_pic/busstop.jpg"), 0, 0, 65 * 16, 30 * 16);
        addMapConfig("Forest", new Texture("map_pic/forest.jpg"), 0, 0, 120 * 16, 120 * 16);
        addMapConfig("Backwoods", new Texture("map_pic/backwoods.jpg"), 0, 0, 120 * 16, 120 * 16);


        // محاسبه چیدمان نقشه‌ها
        arrangeMaps();
    }

    private void createMapWindow() {
        Skin skin = GameAssetManager.getGameAssetManager().getStardewSkin(); // نیاز به فایل پوست دارید

        mapWindow = new Window("Map Overview", skin);
        mapWindow.setKeepWithinStage(true);
        mapWindow.setResizable(true);
        mapWindow.setMovable(true);
        mapWindow.setSize(800, 600);
        mapWindow.setPosition(100, 100);
        mapWindow.setVisible(false);

        // دکمه بستن
        TextButton closeButton = new TextButton("X", skin);
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                closeMapWindow();
            }
        });
        mapWindow.getTitleTable().add(closeButton).size(30, 30);

        // Actor برای نمایش نقشه
        mapActor = new CompositeMapActor();
        ScrollPane scrollPane = new ScrollPane(mapActor, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(false, false);

        mapWindow.add(scrollPane).expand().fill().pad(10);
        stage.addActor(mapWindow);

        // کشیدن پنجره با نگه داشتن هر جای آن
        mapWindow.addListener(new InputListener() {
            private float startX, startY;

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                startX = x;
                startY = y;
                return true;
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                mapWindow.moveBy(x - startX, y - startY);
            }
        });
    }

    private class CompositeMapActor extends Actor {
        @Override
        public void draw(Batch batch, float parentAlpha) {
            // رسم پس‌زمینه ترکیبی
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

                    // رسم نشانگر
                    batch.draw(whiteCircle,
                        getX() + mapPos.x - 6,
                        getY() + mapPos.y - 6,
                        12, 12);

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
        float padding = 30; // فاصله بین نقشه‌ها
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

            // به‌روزرسانی موقعیت برای نقشه بعدی
            currentX += config.mapTexture.getWidth() + padding;
            rowHeight = Math.max(rowHeight, config.mapTexture.getHeight());
        }
    }

    public void addMapConfig(String mapName, Texture texture,
                             float minX, float minY, float maxX, float maxY) {
        mapConfigs.put(mapName, new MapConfig(texture, minX, minY, maxX, maxY));
    }

    public void updatePlayerPosition(String playerId, String mapName, float x, float y) {
        PlayerMarker marker = findPlayerMarker(playerId);
        if (marker == null) {
            Color color = new Color(
                (float) Math.random() * 0.5f + 0.5f,
                (float) Math.random() * 0.5f + 0.5f,
                (float) Math.random() * 0.5f + 0.5f,
                1
            );
            marker = new PlayerMarker(playerId, mapName, x, y, color);
            playerMarkers.add(marker);
        } else {
            marker.mapName = mapName;
            marker.position.set(x, y);
        }
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

    public void render() {
        if (isWindowOpen) {
            stage.act(Gdx.graphics.getDeltaTime());
            stage.draw();
        }
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    public void dispose() {
        stage.dispose();
        background.dispose();
        whiteCircle.dispose();
        for (MapConfig config : mapConfigs.values()) {
            config.mapTexture.dispose();
        }
    }

    private Texture createCircleTexture(int size, Color color) {
        Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fillCircle(size / 2, size / 2, size / 2);
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }
}
