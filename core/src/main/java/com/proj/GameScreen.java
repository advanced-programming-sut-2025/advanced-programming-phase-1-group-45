package com.proj;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.proj.Control.WorldController;
import com.proj.Model.Inventory.InventoryItem;
import com.proj.Model.Inventory.Tool;
import com.proj.Model.Weather;
import com.proj.Map.GameMap;
import com.proj.Map.farmName;
import com.proj.Model.Inventory.InventoryManager;

public class GameScreen implements Screen {
    private Player player;
    private OrthographicCamera camera;
    private GameMap gameMap;
    private WorldController worldController;
    private Viewport viewport;
    private int mapPixelWidth;
    private int mapPixelHeight;
    private boolean initialized = false;
    private String mapName;
    private InventoryManager inventoryManager;

    public GameScreen(farmName farm) {
        mapName = farm.getFarmName();
    }

    @Override
    public void show() {
        if (!initialized) {
            gameMap = new GameMap(mapName);
            worldController = new WorldController();

            camera = new OrthographicCamera();
            viewport = new FitViewport(640, 480, camera);
            viewport.apply();
            camera.update();
            mapPixelWidth = gameMap.getMapWidth() * gameMap.getTileWidth();
            mapPixelHeight = gameMap.getMapHeight() * gameMap.getTileHeight();

            float startX = 28 * 16 + 8;
            float startY = 12 * 16 + 8;

            player = new Player(gameMap, startX, startY);
            camera.position.set(player.getPosition().x, player.getPosition().y, 0);
            camera.update();

            // Initialize inventory system
            inventoryManager = InventoryManager.getInstance();
            inventoryManager.initialize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

            initialized = true;
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Handle input and update
        handleInput();
        player.update(delta);
        updateCamera();
        worldController.update(delta);
        inventoryManager.update(delta, player);

        gameMap.render(camera);

        // Render entities with one batch operation
        gameMap.getSpriteBatch().setProjectionMatrix(camera.combined);
        gameMap.getSpriteBatch().begin();
        worldController.render(gameMap.getSpriteBatch(), Weather.SNOWY);
        renderPlayer();
        gameMap.getSpriteBatch().end();

        gameMap.getSpriteBatch().begin();
        inventoryManager.render(gameMap.getSpriteBatch());
        gameMap.getSpriteBatch().end();

        // Check for T key to toggle inventory
        if (Gdx.input.isKeyJustPressed(Input.Keys.T)) {
            inventoryManager.getPlayerInventory().toggleVisibility();
        }

        // Handle tool usage
        handleToolUse();
    }

    private void renderPlayer() {
        player.render(gameMap.getSpriteBatch());
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        float halfViewportWidth = viewport.getWorldWidth() / 2;
        float halfViewportHeight = viewport.getWorldHeight() / 2;
        camera.position.x = MathUtils.clamp(
            camera.position.x,
            halfViewportWidth,
            mapPixelWidth - halfViewportWidth
        );
        camera.position.y = MathUtils.clamp(
            camera.position.y,
            halfViewportHeight,
            mapPixelHeight - halfViewportHeight
        );
        camera.update();
        worldController.resize(width, height);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        player.dispose();
        gameMap.dispose();
        worldController.dispose();
        inventoryManager.dispose();
    }

    private void updateCamera() {
        Vector3 targetPosition = new Vector3(player.getPosition().x, player.getPosition().y, 0);
        float halfViewportWidth = viewport.getWorldWidth() / 2;
        float halfViewportHeight = viewport.getWorldHeight() / 2;
        float clampedX = MathUtils.clamp(
            targetPosition.x,
            halfViewportWidth,
            mapPixelWidth - halfViewportWidth
        );

        float clampedY = MathUtils.clamp(
            targetPosition.y,
            halfViewportHeight,
            mapPixelHeight - halfViewportHeight
        );
        camera.position.lerp(new Vector3(clampedX, clampedY, 0), 0.1f);

        viewport.apply();
        camera.update();
    }

    private void handleInput() {
        if (player.isMoving() || player.isFainted() || player.isFainting()) {
            return;
        }

        float moveDistance = 16;
        boolean moved = false;

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            player.setDirection(PlayerDirection.UP);
            player.setTargetPosition(
                player.getPosition().x,
                player.getPosition().y + moveDistance
            );
            moved = true;
        } else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            player.setDirection(PlayerDirection.DOWN);
            player.setTargetPosition(
                player.getPosition().x,
                player.getPosition().y - moveDistance
            );
            moved = true;
        } else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            player.setDirection(PlayerDirection.LEFT);
            player.setTargetPosition(
                player.getPosition().x - moveDistance,
                player.getPosition().y
            );
            moved = true;
        } else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            player.setDirection(PlayerDirection.RIGHT);
            player.setTargetPosition(
                player.getPosition().x + moveDistance,
                player.getPosition().y
            );
            moved = true;
        }

        if (moved) {
            Gdx.app.log("GameScreen", "Player started moving");
        }
    }

    private void handleToolUse() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.C)) {
            InventoryItem selectedItem = inventoryManager.getPlayerInventory().getSelectedItem();
            if (selectedItem instanceof Tool) {
                Tool tool = (Tool) selectedItem;
                int tileX = (int) (player.getPosition().x / 16);
                int tileY = (int) (player.getPosition().y / 16);

                switch (player.getDirection()) {
                    case UP: tileY++; break;
                    case DOWN: tileY--; break;
                    case LEFT: tileX--; break;
                    case RIGHT: tileX++; break;
                }

                if (tool.useOnTile(tileX, tileY)) {
                    player.useEnergy(tool.getEnergyCost());
                }
            }
        }
    }
}
