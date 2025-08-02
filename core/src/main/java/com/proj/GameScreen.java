package com.proj;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.proj.Control.AnimalManager;
import com.proj.Control.WorldController;
import com.proj.Control.AnimalBuildingController;
import com.proj.Model.Animal;
import com.proj.Model.Inventory.InventoryItem;
import com.proj.Model.Inventory.InventoryManager;
import com.proj.Model.Inventory.PlayerBag;
import com.proj.Model.Inventory.Tool;
import com.proj.Model.TimeAndWeather.time.Time;
import com.proj.map.farmName;

public class GameScreen implements Screen {
    private Player player;
    private OrthographicCamera camera;
    private String mapName;
    private Time gameTime;

    private WorldController worldController;
    private Viewport viewport;
    private int mapPixelWidth;
    private int mapPixelHeight;
    private boolean initialized = false;
    private InventoryManager inventoryManager;
    private boolean timeIsPaused = false;
    private Stage uistage;
    private PlayerBag playerBag;
    private AnimalBuildingController animalBuildingController;
    private AnimalManager animalManager;
    public GameScreen(farmName farm) {
        mapName = farm.getFarmName();
    }

    @Override
    public void show() {
        if (!initialized) {
            try {
                gameTime = new Time();
                uistage = new Stage();
                worldController = new WorldController(mapName, gameTime, uistage);
                inventoryManager = new InventoryManager();
                animalBuildingController = new AnimalBuildingController(worldController.getGameMap());
                animalManager = new AnimalManager();
                camera = new OrthographicCamera();
                viewport = new FitViewport(640, 480, camera);
                viewport.apply();
                camera.update();
                mapPixelWidth = worldController.getMapWidth() * worldController.getTileWidth();
                mapPixelHeight = worldController.getMapHeight() * worldController.getTileHeight();

                float startX = 20 * 16 + 8;
                float startY = 28 * 16 + 8;

                if (worldController.getPlayerSpawnPoint() != null) {
                    startX = worldController.getPlayerSpawnPoint().x * worldController.getTileWidth() +
                        (float) worldController.getTileWidth() / 2;
                    startY = worldController.getPlayerSpawnPoint().y * worldController.getTileHeight() +
                        (float) worldController.getTileHeight() / 2;
                }

                player = new Player(worldController, startX, startY);

                playerBag = new PlayerBag(player, inventoryManager.getPlayerInventory());
                playerBag.setScale(0.7f);

                worldController.setPlayer(player);
                camera.position.set(player.getPosition().x, player.getPosition().y, 0);
                camera.update();

                initialized = true;

                Gdx.app.log("GameScreen", "Game initialized successfully");

                inventoryManager.getPlayerInventory().selectNoTool();
            } catch (Exception e) {
                Gdx.app.error("GameScreen", "Error in show method", e);
                e.printStackTrace();
            }
        }
    }

    @Override
    public void render(float delta) {
        try {
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            mapPixelWidth = worldController.getMapWidth() * worldController.getTileWidth();
            mapPixelHeight = worldController.getMapHeight() * worldController.getTileHeight();

            handleAnimalBuildingInput();

            if (animalBuildingController != null) {
                animalBuildingController.update(delta);
            }

            if (animalBuildingController == null ||
                (!animalBuildingController.isPlacingBarn() &&
                    !animalBuildingController.isPlacingCoop())) {
                handleInput();
                player.update(delta);
            }

            updateCamera();
            gameTime.update(delta, timeIsPaused);
            worldController.update(delta);
            inventoryManager.update(delta, player);
            animalManager.update(delta);
            worldController.getSpriteBatch().begin();

            worldController.renderMap(camera);
            player.render(worldController.getSpriteBatch());
            animalManager.render(worldController.getSpriteBatch());
            if (animalBuildingController != null) {
                animalBuildingController.render(worldController.getSpriteBatch());
            }

            if (playerBag != null) {
                playerBag.render(worldController.getSpriteBatch(), camera);
            }

            worldController.renderAfterPlayer();

            worldController.getSpriteBatch().end();

            uistage.act(delta);
            uistage.draw();

            if (animalBuildingController == null ||
                (!animalBuildingController.isPlacingBarn() &&
                    !animalBuildingController.isPlacingCoop())) {
                handleToolUse();
            }
        } catch (Exception e) {
            if (worldController.getSpriteBatch().isDrawing()) {
                worldController.getSpriteBatch().end();
            }
            Gdx.app.error("GameScreen", "Error in render method", e);
            e.printStackTrace();
        }
    }


    private void renderPlayer() {
        player.render(worldController.getSpriteBatch());
    }


    private void handleAnimalBuildingInput() {
        if (animalBuildingController == null) return;

        if (Gdx.input.isKeyJustPressed(Input.Keys.K) &&
            !animalBuildingController.isPlacingCoop() &&
            !animalBuildingController.isPlacingBarn()) {
            animalBuildingController.startPlacingCoop(player.getPosition().x, player.getPosition().y);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.B)) {
            if (!animalBuildingController.isPlacingBarn() && !animalBuildingController.isPlacingCoop()) {
                animalBuildingController.startPlacingBarn(player.getPosition().x, player.getPosition().y);
                System.out.println("Barn placement started at: " + player.getPosition().x + ", " + player.getPosition().y);
            } else if (animalManager != null) {
                animalManager.showBuyMenu();
            }
        }
    }


    @Override
    public void resize(int width, int height) {
        try {
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
        } catch (Exception e) {
            Gdx.app.error("GameScreen", "Error in resize method", e);
        }
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
        try {
            player.dispose();
            worldController.dispose();
            inventoryManager.dispose();
            if (playerBag != null) {
                playerBag.dispose();
            }
            if (animalBuildingController != null) {
                animalBuildingController.dispose();
            }
            if (animalManager != null) {
                animalManager.dispose();
            }
        } catch (Exception e) {
            Gdx.app.error("GameScreen", "Error in dispose method", e);
        }
    }

    private void updateCamera() {
        try {
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
        } catch (Exception e) {
            Gdx.app.error("GameScreen", "Error in updateCamera method", e);
        }
    }

    private void handleInput() {
        try {
            if (animalBuildingController != null &&
                (animalBuildingController.isPlacingBarn() ||
                    animalBuildingController.isPlacingCoop() ||
                    animalBuildingController.isShowingInterior())) {
                System.out.println("Skipping player movement - placing building: " +
                    animalBuildingController.isPlacingBarn() + ", " +
                    animalBuildingController.isPlacingCoop());
                return;
            }

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

            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
                selectToolSlot(0);
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
                selectToolSlot(1);
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
                selectToolSlot(2);
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) {
                selectToolSlot(3);
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_5)) {
                selectToolSlot(4);
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_6)) {
                selectToolSlot(5);
            }
            else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_0)) {
                if (inventoryManager != null && inventoryManager.getPlayerInventory() != null) {
                    inventoryManager.getPlayerInventory().selectNoTool();
                    Gdx.app.log("GameScreen", "Selected no tool");
                }
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.T)) {
                if (playerBag != null) {
                    playerBag.toggleOpen();
                    Gdx.app.log("GameScreen", "Toggled inventory: " + (playerBag.isOpen() ? "open" : "closed"));
                }
            }

            if (moved) {
                Gdx.app.log("GameScreen", "Player started moving");
            }

            // کدهای مربوط به حیوانات
            if (Gdx.input.isKeyJustPressed(Input.Keys.L)) {
                if (animalManager != null) {
                    animalManager.printAnimalsStatus();
                }
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.N)) {
                if (animalManager != null) {
                    animalManager.newDay();
                }
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
                if (animalManager != null && animalManager.getSelectedAnimal() != null) {
                    boolean isBarn = true; // یا false برای قفس
                    int buildingIndex = 0; // شاخص ساختمان مورد نظر
                    connectAnimalToBuilding(animalManager.getSelectedAnimal(), isBarn, buildingIndex);
                }
            }
        } catch (Exception e) {
            Gdx.app.error("GameScreen", "Error in handleInput method", e);
        }
    }


    private void connectAnimalToBuilding(Animal animal, boolean isBarn, int buildingIndex) {
        if (animalBuildingController != null) {
            animalBuildingController.addAnimalToBuilding(animal, isBarn, buildingIndex);
        }
    }

    private void selectToolSlot(int slot) {
        try {
            if (inventoryManager != null && inventoryManager.getPlayerInventory() != null) {
                inventoryManager.getPlayerInventory().selectSlot(slot);
                InventoryItem selectedItem = inventoryManager.getPlayerInventory().getSelectedItem();
                if (selectedItem != null) {
                    Gdx.app.log("GameScreen", "Selected tool in slot " + slot + ": " + selectedItem.getName());
                } else {
                    Gdx.app.log("GameScreen", "Selected empty slot " + slot);
                }
            }
        } catch (Exception e) {
            Gdx.app.error("GameScreen", "Error in selectToolSlot method", e);
        }
    }

    private void handleToolUse() {
        try {
            if (animalBuildingController != null && animalBuildingController.isShowingInterior()) {
                return;
            }

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

                        Gdx.app.log("GameScreen", "Used tool " + tool.getName() + " at tile: " + tileX + ", " + tileY);
                    }
                }
                if (inventoryManager.getPlayerInventory().isNoToolSelected()) {
                    Gdx.app.log("GameScreen", "No tool selected, cannot use");
                    return;
                }
            }
        } catch (Exception e) {
            Gdx.app.error("GameScreen", "Error in handleToolUse method", e);
        }
    }
}
