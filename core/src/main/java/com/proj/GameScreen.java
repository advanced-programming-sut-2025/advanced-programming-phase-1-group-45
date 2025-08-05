package com.proj;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.proj.Control.AnimalManager;
import com.proj.Control.NPCManager;
import com.proj.Control.WorldController;
import com.proj.Control.AnimalBuildingController;
import com.proj.Model.Animal;
import com.proj.Model.GameAssetManager;
import com.proj.Model.Inventory.InventoryItem;
import com.proj.Model.Inventory.InventoryManager;
import com.proj.Model.Inventory.PlayerBag;
import com.proj.Model.Inventory.Tool;
import com.proj.Model.TimeAndWeather.time.Time;
import com.proj.Model.mapObjects.NPCObject;
import com.proj.map.farmName;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List; 

import java.awt.*;

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
    private Viewport uistageViewport;
    private PlayerBag playerBag;
    private AnimalBuildingController animalBuildingController;
    private AnimalManager animalManager;

    private NPCManager npcManager;
    private Texture npcTexture;
    private Point customSpawnPoint;

    private Texture spaceImage;
    private boolean showSpaceImage = false;

    private NPCObject currentInteractingNPC;
    private boolean isInteracting = false;

    private farmName farm;

    private Texture aImage;
    private Texture sImage;
    private Texture dImage;
    private Texture fImage;
    private Texture gImage;

    private boolean showAImage = false;
    private boolean showSImage = false;
    private boolean showDImage = false;
    private boolean showFImage = false;
    private boolean showGImage = false;
    private StoreManager storeManager;
    private Store currentStore = null;
    private boolean inStoreInterface = false;
    private BitmapFont font;
    private GlyphLayout glyphLayout = new GlyphLayout();
    
    public GameScreen(farmName farm) {
        mapName = farm.getFarmName();
        this.farm = farm;
        this.mapName = farm.getFarmName();
    }
    public void setPlayer(Player player) {
        this.player = player;
        if (worldController != null) {

            worldController.setPlayer(player);

        }
        if (camera != null && player != null) {
            camera.position.set(player.getPosition().x, player.getPosition().y, 0);
            camera.update();
        }
    }
    public void setNPCManager(NPCManager npcManager) {
        this.npcManager = npcManager;
        if (npcManager != null && worldController != null) {
            placeNPCs();
        }
    }
    private Point getSpawnPoint() {
        if (customSpawnPoint != null) {
            return customSpawnPoint;
        }
        return worldController != null ? worldController.getPlayerSpawnPoint() : null;
    }
    private List<Texture> npcTextures = new ArrayList<>(); // Add this class field
    private void placeNPCs() {
        if (npcManager == null) {
            npcManager = new NPCManager();
        }
        if (npcManager.getNPCs().isEmpty()) {
            Texture leahTexture = new Texture(Gdx.files.internal("characters/leah.png"));
            Texture georgeTexture = new Texture(Gdx.files.internal("characters/george.jpg"));
            Texture lewisTexture = new Texture(Gdx.files.internal("characters/lewis.jpg"));
            Texture pierreTexture = new Texture(Gdx.files.internal("characters/pierre.jpg"));
            Texture robinTexture = new Texture(Gdx.files.internal("characters/robin.png"));
            NPCObject leah = new NPCObject(new TextureRegion(leahTexture));
            NPCObject alex = new NPCObject(new TextureRegion(georgeTexture));
            NPCObject penny = new NPCObject(new TextureRegion(lewisTexture));
            NPCObject sebastian = new NPCObject(new TextureRegion(pierreTexture));
            NPCObject abigail = new NPCObject(new TextureRegion(robinTexture));

            npcManager.addNPC(leah);
            npcManager.addNPC(alex);
            npcManager.addNPC(penny);
            npcManager.addNPC(sebastian);
            npcManager.addNPC(abigail);
            npcTextures.add(leahTexture);
            npcTextures.add(georgeTexture);
            npcTextures.add(lewisTexture);
            npcTextures.add(pierreTexture);
            npcTextures.add(robinTexture);

        }
        npcManager.placeNPCsRandomly(
            worldController.getMapWidth(),
            worldController.getMapHeight(),
            worldController.getTileWidth(),
            worldController.getTileHeight()
        );
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
                spaceImage = GameAssetManager.getGameAssetManager().getSpaceImageTexture();
                camera = new OrthographicCamera();
                viewport = new FitViewport(640, 480, camera);
                uistageViewport = new ScreenViewport();
                uistage.setViewport(uistageViewport);
                viewport.apply();
                uistageViewport.apply();
                camera.update();
                Gdx.input.setInputProcessor(new InputMultiplexer(uistage));
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
                aImage = new Texture(Gdx.files.internal("NPCDialogues/george_dialogue1.png"));
                sImage = new Texture(Gdx.files.internal("NPCDialogues/leah_dialogue1.png"));
                dImage = new Texture(Gdx.files.internal("NPCDialogues/lewis_dialogue1.png"));
                fImage = new Texture(Gdx.files.internal("NPCDialogues/pierre_dialogue1.png"));
                gImage = new Texture(Gdx.files.internal("NPCDialogues/robin_dialogue1.png"));

                playerBag = new PlayerBag(player, inventoryManager.getPlayerInventory());
                playerBag.setScale(0.7f);
                
                font = new BitmapFont();
                font.setColor(Color.WHITE);
                font.getData().setScale(1.5f);

                worldController.setPlayer(player);
                placeNPCs();
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
            if (npcManager != null) {

                for (NPCObject npc : npcManager.getNPCs()) {

                    TextureRegion texture = npc.getTextureRegion();

                    worldController.getSpriteBatch().draw(

                        texture,

                        npc.getPixelX(),

                        npc.getPixelY(),

                        texture.getRegionWidth() * npc.getScale(),

                        texture.getRegionHeight() * npc.getScale()

                    );
                }}
            player.render(worldController.getSpriteBatch());
            animalManager.render(worldController.getSpriteBatch());
            if (animalBuildingController != null) {
                animalBuildingController.render(worldController.getSpriteBatch());
            }

            if (playerBag != null) {
                playerBag.render(worldController.getSpriteBatch(), camera);
            }
            if (showSpaceImage && spaceImage != null) {
                // Save current projection matrix
                Matrix4 originalProjection = worldController.getSpriteBatch().getProjectionMatrix();

                // Switch to screen coordinates
                Matrix4 screenProjection = new Matrix4()
                    .setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                worldController.getSpriteBatch().setProjectionMatrix(screenProjection);

                // Draw centered
                float x = (Gdx.graphics.getWidth() - spaceImage.getWidth()) / 2;
                float y = (Gdx.graphics.getHeight() - spaceImage.getHeight()) / 2;
                worldController.getSpriteBatch().draw(spaceImage, x, y);

                // Restore original projection
                worldController.getSpriteBatch().setProjectionMatrix(originalProjection);
            }
            if (showAImage) drawCenteredImage(aImage);
            if (showSImage) drawCenteredImage(sImage);
            if (showDImage) drawCenteredImage(dImage);
            if (showFImage) drawCenteredImage(fImage);
            if (showGImage) drawCenteredImage(gImage);
            worldController.renderAfterPlayer();

            worldController.getSpriteBatch().end();

            uistageViewport.apply();
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

     private void drawCenteredImage(Texture image) {
        if (image == null) return;
        Matrix4 originalProjection = worldController.getSpriteBatch().getProjectionMatrix();

        Matrix4 screenProjection = new Matrix4()
            .setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        worldController.getSpriteBatch().setProjectionMatrix(screenProjection);
        float x = (Gdx.graphics.getWidth() - image.getWidth()) / 2;
        float y = (Gdx.graphics.getHeight() - image.getHeight()) / 2;
        worldController.getSpriteBatch().draw(image, x, y);
        worldController.getSpriteBatch().setProjectionMatrix(originalProjection);
    }


    private void renderPlayer() {
        player.render(worldController.getSpriteBatch());
    }

    public void setPlayerSpawnPoint(Point spawnPoint) {

        this.customSpawnPoint = spawnPoint;

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
            uistageViewport.update(width, height, true);
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
            if (npcTexture != null) {

                npcTexture.dispose();

            }

            for (Texture texture : npcTextures) {
                if (texture != null) {
                    texture.dispose();
                }
            }
            if (aImage != null) aImage.dispose();
            if (sImage != null) sImage.dispose();
            if (dImage != null) dImage.dispose();
            if (fImage != null) fImage.dispose();
            if (gImage != null) gImage.dispose();
            if (font != null) {
                font.dispose();
            }
            npcTextures.clear();
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

            // ADD THIS SPACE KEY HANDLING
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                showSpaceImage = !showSpaceImage; // Toggle visibility
                Gdx.app.log("GameScreen", "Space pressed. showSpaceImage: " + showSpaceImage);
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.T)) {
                if (playerBag != null) {
                    playerBag.toggleOpen();
                    Gdx.app.log("GameScreen", "Toggled inventory: " + (playerBag.isOpen() ? "open" : "closed"));
                }
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.X)) {
                showAImage = !showAImage;
                Gdx.app.log("GameScreen", "Toggled image: " + showAImage);
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.Y)) {
                showSImage = !showSImage;
                Gdx.app.log("GameScreen", "Toggled image: " + showSImage);
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.Z)) {
                showDImage = !showDImage;
                Gdx.app.log("GameScreen", "Toggled image: " + showDImage);
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.J)) {
                showFImage = !showFImage;
                Gdx.app.log("GameScreen", "Toggled image: " + showFImage);
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.H)) {
                showGImage = !showGImage;
                Gdx.app.log("GameScreen", "Toggled image: " + showGImage);
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.L)) {
                if (isInteracting) {
                    // Close current interaction
                    isInteracting = false;
                    currentInteractingNPC = null;
                } else {
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
