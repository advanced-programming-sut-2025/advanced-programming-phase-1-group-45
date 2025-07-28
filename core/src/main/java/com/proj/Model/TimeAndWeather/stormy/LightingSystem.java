package com.proj.Model.TimeAndWeather.stormy;//package com.proj.Model.TimeAndWeather.stormy;
//
//import com.badlogic.gdx.Gdx;
//import com.badlogic.gdx.audio.Sound;
//import com.badlogic.gdx.graphics.Camera;
//import com.badlogic.gdx.graphics.Texture;
//import com.badlogic.gdx.graphics.g2d.Batch;
//import com.badlogic.gdx.graphics.g2d.ParticleEffect;
//import com.badlogic.gdx.maps.tiled.TiledMap;
//import com.badlogic.gdx.maps.tiled.TiledMapTile;
//import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
//import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
//import com.badlogic.gdx.math.MathUtils;
//
//public class LightingSystem {
//    private final TiledMapTileLayer treesLayer;
//    private final TiledMapTileLayer groundLayer;
//    private final TiledMapTile burntTile;
//    private final Sound lightningSound;
//    private final Texture flashTexture;
//    private final ParticleEffect burnEffect;
//    private final float interval;
//
//    private float timeAcc = 0f;
//    private TileCoordinate lastStrike = null;
//    private float flashAcc = 0f, flashDur = 0.2f;
//
//    public LightingSystem(
//        TiledMap map,
//        String treesLayerName,
//        String groundLayerName,
//        String burntTileType,
//        float strikeInterval
//    ) {
//        this.treesLayer = (TiledMapTileLayer) map.getLayers().get(treesLayerName);
//        this.groundLayer = (TiledMapTileLayer) map.getLayers().get(groundLayerName);
//        this.interval = strikeInterval;
//
//        // پیدا کردن تایل سوخته از tileset بر اساس پراپرتی
//        TiledMapTileSet set = map.getTileSets().getTileSet("myTileset");
//        this.burntTile = set.getTiles()
//            .stream()
//            .filter(t -> "burnt".equals(t.getProperties().get("type")))
//            .findFirst()
//            .orElseThrow(() -> new RuntimeException("Burnt tile not found"));
//
//        lightningSound = Gdx.audio.newSound(Gdx.files.internal("sounds/lightning.ogg"));
//        flashTexture = new Texture(Gdx.files.internal("images/lightning_flash.png"));
//
//        burnEffect = new ParticleEffect();
//        burnEffect.load(Gdx.files.internal("particles/burn.p"), Gdx.files.internal(""));
//    }
//
//    public void update(float delta) {
//        timeAcc += delta;
//        if (timeAcc >= interval) {
//            timeAcc -= interval;
//            strikeRandomTile();
//        }
//
//        // پایان فلش
//        if (lastStrike != null) {
//            flashAcc += delta;
//            if (flashAcc >= flashDur) lastStrike = null;
//        }
//
//        burnEffect.update(delta);
//    }
//
//    public void render(Batch batch, Camera camera) {
//        batch.setProjectionMatrix(camera.combined);
//
//        if (lastStrike != null) {
//            float tw = treesLayer.getTileWidth();
//            float th = treesLayer.getTileHeight();
//            float wx = lastStrike.x * tw;
//            float wy = lastStrike.y * th;
//            batch.draw(flashTexture, wx, wy, tw, th);
//        }
//
//        burnEffect.draw(batch);
//    }
//
//    private void strikeRandomTile() {
//        int w = treesLayer.getWidth();
//        int h = treesLayer.getHeight();
//
//        int tx = MathUtils.random(0, w - 1);
//        int ty = MathUtils.random(0, h - 1);
//        TiledMapTileLayer.Cell cell = treesLayer.getCell(tx, ty);
//
//        lightningSound.play(1f);
//
//        lastStrike = new TileCoordinate(tx, ty);
//        flashAcc = 0f;
//
//        if (cell != null && Boolean.TRUE.equals(cell.getTile().getProperties().get("isTree"))) {
//            // حذف تایل درخت
//            treesLayer.setCell(tx, ty, null);
//
//            // تغییر زمین به سوخته
//            TiledMapTileLayer.Cell groundCell = groundLayer.getCell(tx, ty);
//            if (groundCell != null) groundCell.setTile(burntTile);
//
//            // شروع افکت ذرات روی مختصات دنیایی
//            float px = tx * groundLayer.getTileWidth() + groundLayer.getTileWidth() / 2f;
//            float py = ty * groundLayer.getTileHeight() + groundLayer.getTileHeight() / 2f;
//            burnEffect.setPosition(px, py);
//            burnEffect.start();
//        }
//    }
//
//    public void dispose() {
//        lightningSound.dispose();
//        flashTexture.dispose();
//        burnEffect.dispose();
//    }
//
//    // کلاس کمکی برای ذخیره مختصات
//    private static class TileCoordinate {
//        int x, y;
//
//        TileCoordinate(int x, int y) {
//            this.x = x;
//            this.y = y;
//        }
//    }
//}
//
