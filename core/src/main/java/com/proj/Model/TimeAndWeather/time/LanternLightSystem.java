package com.proj.Model.TimeAndWeather.time;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Vector2;
import com.proj.Model.GameAssetManager;

import java.util.HashMap;

public class LanternLightSystem {
    private final HashMap<Vector2, Boolean> lightPositions = new HashMap<>();
    private final TextureRegion lanternTexture;
    private final TiledMap map;
    private final int tileWidth;
    private final int tileHeight;
    private boolean isNight = false;
    private final TextureRegion windowLightTexture;

    public LanternLightSystem(TiledMap map, TextureRegion lanternTexture,
                              int tileWidth, int tileHeight) {
        this.map = map;
        this.lanternTexture = lanternTexture;
        this.windowLightTexture = new TextureRegion(GameAssetManager.getGameAssetManager().getLanternLight());
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        parseLightProperties();
    }

    private void parseLightProperties() {
        String lightProperty = map.getProperties().get("Light", String.class);
        if (lightProperty != null) {
            String[] parts = lightProperty.split(" ");
            for (int i = 0; i < parts.length; i += 3) {
                int x = Integer.parseInt(parts[i]);
                int y = map.getProperties().get("height", Integer.class) - Integer.parseInt(parts[i + 1]) - 1;
                int num = Integer.parseInt(parts[i + 2]);
                boolean isWindow = num == 8;
                lightPositions.put(new Vector2(x, y), isWindow);
            }
        }
    }

    public void setNightMode(boolean isNight) {
        if (this.isNight != isNight) {
            this.isNight = isNight;
            updateTileLayers();
        }
    }

    private void updateTileLayers() {
        String tileProperty = isNight ? "NightTiles" : "DayTiles";
        String tileData = map.getProperties().get(tileProperty, String.class);

        if (tileData != null) {
            String[] parts = tileData.split(" ");
            for (int i = 0; i < parts.length; i += 4) {
                String layerName = parts[i];
                int x = Integer.parseInt(parts[i + 1]);
                int y = map.getProperties().get("height", Integer.class) - Integer.parseInt(parts[i + 2]) - 1;
                int tileId = Integer.parseInt(parts[i + 3]);

                TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(layerName);
                if (layer != null) {
                    TiledMapTileLayer.Cell cell = layer.getCell(x, y);
                    if (cell != null) {
                        TiledMapTile tile = cell.getTile();
                        TiledMapTileSet source = null;
                        if (tile != null) {
                            for (TiledMapTileSet ts : map.getTileSets()) {
                                if (ts.getTile(tile.getId()) == tile) {
                                    source = ts;
                                    break;
                                }
                            }
                            if (source != null) {
                                int first = source.getProperties().get("firstgid", Integer.class);
                                TiledMapTile newTile = source.getTile(tileId + first);
                                if (newTile != null) {
                                    cell.setTile(newTile);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void render(SpriteBatch batch) {
        if (!isNight) return;

        for (Vector2 position : lightPositions.keySet()) {
            float worldX = position.x * tileWidth - (float) lanternTexture.getRegionWidth() / 2;
            float worldY = position.y * tileHeight - (float) lanternTexture.getRegionHeight() / 2;
            if (lightPositions.get(position)) {
                batch.draw(windowLightTexture, worldX, worldY);
            } else {
                batch.draw(lanternTexture, worldX, worldY);
            }
        }
    }
}
