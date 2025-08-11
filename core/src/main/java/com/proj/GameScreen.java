package com.proj;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.Color;
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
import com.proj.Model.Animal.Animal;
import com.proj.Model.Cooking.*;
import com.proj.Model.GameAssetManager;
import com.proj.Model.Inventory.InventoryItem;
import com.proj.Model.Inventory.InventoryManager;
import com.proj.Model.Inventory.PlayerBag;
import com.proj.Model.Inventory.Tool;
import com.proj.Model.TimeAndWeather.time.Time;
import com.proj.Model.mapObjects.NPCObject;
import com.proj.map.farmName;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.proj.Model.EnergyBar;

import java.util.ArrayList;
import java.util.List;

import java.awt.*;

public class GameScreen implements Screen {
    private Player player;
    private EnergyBar energyBar;
    private OrthographicCamera hudCamera;
    private OrthographicCamera camera;
    private String mapName;
    private Time gameTime;

    private WorldController worldController;
    private Viewport viewport;
    private int mapPixelWidth;
    private int mapPixelHeight;
    private boolean initialized = false;
    public InventoryManager inventoryManager;
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
    //private StoreManager storeManager;
    //private Store currentStore = null;
    private boolean inStoreInterface = false;
    private BitmapFont font;
    private GlyphLayout glyphLayout = new GlyphLayout();

    public Refrigerator refrigerator;
    public CookingManager cookingManager;

    private boolean showRefrigeratorUI = false;
    private int selectedPlayerSlot = -1;
    private int selectedRefrigeratorSlot = -1;
    private String transferMessage = "";
    private float messageTimer = 0;



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
                hudCamera = new OrthographicCamera();
                hudCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
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
                player = new Player(worldController, startX, startY);

                refrigerator = new Refrigerator(player.getPosition().x + 50, player.getPosition().y + 50);
                cookingManager = new CookingManager(inventoryManager.getPlayerInventory(), refrigerator.getInventory());

                energyBar = new EnergyBar(
                    player,
                    Gdx.graphics.getWidth() - 10,
                    50,
                    60,  // width
                    105   // height
                );
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
                addInitialIngredients();
                Gdx.app.log("GameScreen", "Game initialized successfully");
                Gdx.app.log("EnergyBar", "Position: " + (Gdx.graphics.getWidth() - 250) + ", " + (Gdx.graphics.getHeight() - 40));

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
            worldController.getSpriteBatch().setProjectionMatrix(camera.combined);

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
                }
            }
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
            worldController.getSpriteBatch().setProjectionMatrix(hudCamera.combined);
            worldController.getSpriteBatch().begin();
            energyBar.render(worldController.getSpriteBatch());
            if (player.getActiveBuff() != null) {
                Buff activeBuff = player.getActiveBuff();
                TextureRegion buffIcon = GameAssetManager.getGameAssetManager().getBuffIconTexture();
                if (buffIcon != null) {
                    worldController.getSpriteBatch().draw(buffIcon, Gdx.graphics.getWidth() - 100, Gdx.graphics.getHeight() - 50, 32, 32);
                }
                String buffText = activeBuff.effect + " (" + (int)player.getBuffRemainingTime() + "s)";
                font.draw(worldController.getSpriteBatch(), buffText, Gdx.graphics.getWidth() - 100, Gdx.graphics.getHeight() - 60);
            }
            worldController.getSpriteBatch().end();

            uistageViewport.apply();
            uistage.act(delta);
            // رندر کردن رابط کاربری یخچال
            if (showRefrigeratorUI) {
                renderRefrigeratorUI();
            }

// کاهش تایمر پیام
            if (messageTimer > 0) {
                messageTimer -= delta;
            }

            uistage.draw();

            if (animalBuildingController == null ||
                (!animalBuildingController.isPlacingBarn() &&
                    !animalBuildingController.isPlacingCoop())) {
                handleToolUse();
            }

            if (animalBuildingController != null) {
                animalBuildingController.updateCameraPosition(camera.position.x, camera.position.y);
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

        // اگر در حال انتخاب ساختمان برای حیوان هستیم، کلیدها B و K برای قرار دادن ساختمان جدید کار نکنند
        if (animalBuildingController.selectingBuildingForAnimal) {
            return;
        }

        // شروع قرار دادن قفس
        if (Gdx.input.isKeyJustPressed(Input.Keys.K) &&
            !animalBuildingController.isPlacingCoop() &&
            !animalBuildingController.isPlacingBarn()) {
            animalBuildingController.startPlacingCoop(player.getPosition().x, player.getPosition().y);
        }

        // شروع قرار دادن طویله
        if (Gdx.input.isKeyJustPressed(Input.Keys.B) &&
            !animalBuildingController.isPlacingBarn() &&
            !animalBuildingController.isPlacingCoop()) {
            animalBuildingController.startPlacingBarn(player.getPosition().x, player.getPosition().y);
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
            hudCamera.setToOrtho(false, width, height);
            hudCamera.update();
            energyBar.setPosition(width - 250, height - 250);
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
                    animalBuildingController.isShowingInterior() ||
                    animalBuildingController.isShowingAnimalList())) {  // اضافه کردن این شرط
                System.out.println("Skipping player movement - special state active");
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

            if (animalBuildingController != null && animalBuildingController.isShowingAnimalList()) {
                Gdx.app.log("GameScreen", "Animal list is showing, skipping tool selection");
            } else {
                // انتخاب ابزار فقط زمانی که لیست حیوانات نمایش داده نمی‌شود
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
                } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_0)) {
                    if (inventoryManager != null && inventoryManager.getPlayerInventory() != null) {
                        inventoryManager.getPlayerInventory().selectNoTool();
                        Gdx.app.log("GameScreen", "Selected no tool");
                    }
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
//                Gdx.app.log("GameScreen", "Player started moving");
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


            // باز/بسته کردن رابط کاربری یخچال
            if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
                showRefrigeratorUI = !showRefrigeratorUI;
                selectedPlayerSlot = -1;
                selectedRefrigeratorSlot = -1;
                if (showRefrigeratorUI) {
                    Gdx.app.log("GameScreen", "Refrigerator UI opened");
                } else {
                    Gdx.app.log("GameScreen", "Refrigerator UI closed");
                }
            }

// اگر رابط کاربری یخچال باز است
            if (showRefrigeratorUI) {
                // انتخاب آیتم با کلیک موس
                if (Gdx.input.justTouched()) {
                    int mouseX = Gdx.input.getX();
                    int mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();

                    // بررسی کلیک در موجودی کوله
                    if (mouseY > Gdx.graphics.getHeight() / 2) {
                        int itemsPerRow = 5;
                        int itemWidth = 200;
                        int itemHeight = 40;
                        int startX = 20;
                        int startY = Gdx.graphics.getHeight() - 100;

                        int col = (mouseX - startX) / itemWidth;
                        int row = (startY - mouseY) / itemHeight;

                        if (col >= 0 && col < itemsPerRow && row >= 0) {
                            int slot = row * itemsPerRow + col;
                            if (slot < inventoryManager.getPlayerInventory().getCapacity() &&
                                inventoryManager.getPlayerInventory().getItem(slot) != null) {
                                selectedPlayerSlot = slot;
                                selectedRefrigeratorSlot = -1;
                            }
                        }
                    }
                    // بررسی کلیک در موجودی یخچال
                    else {
                        int itemsPerRow = 5;
                        int itemWidth = 200;
                        int itemHeight = 40;
                        int startX = 20;
                        int startY = Gdx.graphics.getHeight() / 2 - 40;

                        int col = (mouseX - startX) / itemWidth;
                        int row = (startY - mouseY) / itemHeight;

                        if (col >= 0 && col < itemsPerRow && row >= 0) {
                            int slot = row * itemsPerRow + col;
                            if (slot < refrigerator.getInventory().getCapacity() &&
                                refrigerator.getInventory().getItem(slot) != null) {
                                selectedRefrigeratorSlot = slot;
                                selectedPlayerSlot = -1;
                            }
                        }
                    }
                }

                // انتقال آیتم از کوله به یخچال
                if (Gdx.input.isKeyJustPressed(Input.Keys.T) && selectedPlayerSlot != -1) {
                    InventoryItem selectedItem = inventoryManager.getPlayerInventory().getItem(selectedPlayerSlot);
                    if (selectedItem != null) {
                        InventoryItem transferItem = new InventoryItem(selectedItem.getId(), selectedItem.getName(),
                            selectedItem.getTexture(), selectedItem.isStackable(), selectedItem.getMaxStackSize()) {
                            @Override
                            public void use() {}
                        };
                        transferItem.setQuantity(1);

                        if (refrigerator.getInventory().addItem(transferItem)) {
                            selectedItem.decreaseQuantity(1);
                            if (selectedItem.getQuantity() <= 0) {
                                inventoryManager.getPlayerInventory().removeItem(selectedPlayerSlot);
                            }
                            transferMessage = "Transferred " + transferItem.getName() + " to refrigerator";
                            messageTimer = 3.0f;
                        } else {
                            transferMessage = "Refrigerator is full";
                            messageTimer = 3.0f;
                        }
                    }
                }

                // انتقال آیتم از یخچال به کوله
                if (Gdx.input.isKeyJustPressed(Input.Keys.F) && selectedRefrigeratorSlot != -1) {
                    InventoryItem selectedItem = refrigerator.getInventory().getItem(selectedRefrigeratorSlot);
                    if (selectedItem != null) {
                        InventoryItem transferItem = new InventoryItem(selectedItem.getId(), selectedItem.getName(),
                            selectedItem.getTexture(), selectedItem.isStackable(), selectedItem.getMaxStackSize()) {
                            @Override
                            public void use() {}
                        };
                        transferItem.setQuantity(1);

                        if (inventoryManager.getPlayerInventory().addItem(transferItem)) {
                            selectedItem.decreaseQuantity(1);
                            if (selectedItem.getQuantity() <= 0) {
                                refrigerator.getInventory().removeItem(selectedRefrigeratorSlot);
                            }
                            transferMessage = "Transferred " + transferItem.getName() + " to player inventory";
                            messageTimer = 3.0f;
                        } else {
                            transferMessage = "Inventory is full";
                            messageTimer = 3.0f;
                        }
                    }
                }

                // جلوگیری از پردازش سایر ورودی‌ها وقتی رابط کاربری یخچال باز است
                return;
            }





            if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
                InventoryItem selectedItem = inventoryManager.getPlayerInventory().getSelectedItem();
                if (selectedItem instanceof FoodItem) {
                    FoodItem food = (FoodItem) selectedItem;
                    if (food.getQuantity() > 0) {
                        food.use(player);
                        food.decreaseQuantity(1);
                        if (food.getQuantity() <= 0) {
                            inventoryManager.getPlayerInventory().removeItem(inventoryManager.getPlayerInventory().getSelectedSlot());
                        }
                    } else {
                        Gdx.app.log("GameScreen", "No " + food.getName() + " left to eat.");
                    }
                } else {
                    Gdx.app.log("GameScreen", "Selected item is not edible.");
                }
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.O)) {
                CookingScreen cookingScreen = new CookingScreen(
                    (Main)Gdx.app.getApplicationListener(),
                    GameAssetManager.getGameAssetManager().getSkin(),
                    inventoryManager,
                    cookingManager,
                    player
                );
                ((Main)Gdx.app.getApplicationListener()).setScreen(cookingScreen);
                Gdx.app.log("GameScreen", "Opening cooking screen.");
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
                        case UP:
                            tileY++;
                            break;
                        case DOWN:
                            tileY--;
                            break;
                        case LEFT:
                            tileX--;
                            break;
                        case RIGHT:
                            tileX++;
                            break;
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



    private void addInitialIngredients() {
        String[] basicIngredients = {
            "Egg", "Milk", "Wheat", "Wheat Flour", "Sugar", "Tomato", "Cheese",
            "Corn", "Rice", "Fiber", "Coffee", "Potato", "Oil", "Blueberry",
            "Melon", "Apricot", "Red Cabbage", "Radish", "Amaranth", "Kale",
            "Beet", "Parsnip", "Carrot", "Eggplant", "Sardine", "Salmon",
            "Flounder", "Midnight Carp", "Leek", "Dandelion", "Pumpkin"
        };

        TextureRegion tempTexture = new TextureRegion(new Texture(Gdx.files.internal("assets/foraging/Leek.png")));

        for (String ingredient : basicIngredients) {
            InventoryItem item = new InventoryItem(ingredient, ingredient, tempTexture, true, 99) {
                @Override
                public void use() {
                }
            };
            item.setQuantity(1);
            refrigerator.getInventory().addItem(item);
        }

        inventoryManager.getPlayerInventory().addItem(new FoodItem("FriedEgg", "Fried Egg", 50, null, 0));
        inventoryManager.getPlayerInventory().addItem(new FoodItem("Pizza", "Pizza", 150, null, 0));
        inventoryManager.getPlayerInventory().addItem(new FoodItem("TripleShotEspresso", "Triple Shot Espresso", 200, "Max Energy +100", 5));
    }

    private void renderRefrigeratorUI() {
        if (!showRefrigeratorUI) return;

        worldController.getSpriteBatch().begin();
        worldController.getSpriteBatch().setProjectionMatrix(hudCamera.combined);

        // رسم پس‌زمینه نیمه‌شفاف
        Texture whitePixel = new Texture(1, 1, Pixmap.Format.RGBA8888);
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0, 0, 0, 0.7f);
        pixmap.fill();
        whitePixel.draw(pixmap, 0, 0);
        pixmap.dispose();

        worldController.getSpriteBatch().draw(whitePixel, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        whitePixel.dispose();

        // رسم عنوان
        font.draw(worldController.getSpriteBatch(), "Refrigerator Contents", 20, Gdx.graphics.getHeight() - 20);
        font.draw(worldController.getSpriteBatch(), "Press R to close", Gdx.graphics.getWidth() - 150, Gdx.graphics.getHeight() - 20);

        // رسم خط جدا کننده
        worldController.getSpriteBatch().setColor(1, 1, 1, 0.5f);
        worldController.getSpriteBatch().draw(whitePixel, 20, Gdx.graphics.getHeight() - 40, Gdx.graphics.getWidth() - 40, 2);
        worldController.getSpriteBatch().setColor(1, 1, 1, 1);

        // رسم موجودی کوله
        font.draw(worldController.getSpriteBatch(), "Player Inventory:", 20, Gdx.graphics.getHeight() - 60);
        int x = 20;
        int y = Gdx.graphics.getHeight() - 100;
        int itemsPerRow = 5;
        int count = 0;

        for (int i = 0; i < inventoryManager.getPlayerInventory().getCapacity(); i++) {
            InventoryItem item = inventoryManager.getPlayerInventory().getItem(i);
            if (item != null) {
                // رسم آیتم
                if (item.getTexture() != null) {
                    worldController.getSpriteBatch().draw(item.getTexture(), x, y, 32, 32);
                }

                // رسم نام و تعداد
                font.draw(worldController.getSpriteBatch(), item.getName() + " x" + item.getQuantity(), x + 40, y + 16);

                // رسم کادر انتخاب
                if (i == selectedPlayerSlot) {
                    worldController.getSpriteBatch().setColor(1, 1, 0, 0.5f);
                    worldController.getSpriteBatch().draw(whitePixel, x - 2, y - 2, 36, 36);
                    worldController.getSpriteBatch().setColor(1, 1, 1, 1);
                }

                count++;
                if (count % itemsPerRow == 0) {
                    y -= 40;
                    x = 20;
                } else {
                    x += 200;
                }
            }
        }

        // رسم موجودی یخچال
        font.draw(worldController.getSpriteBatch(), "Refrigerator Contents:", 20, Gdx.graphics.getHeight() / 2);
        x = 20;
        y = Gdx.graphics.getHeight() / 2 - 40;
        count = 0;

        for (int i = 0; i < refrigerator.getInventory().getCapacity(); i++) {
            InventoryItem item = refrigerator.getInventory().getItem(i);
            if (item != null) {
                // رسم آیتم
                if (item.getTexture() != null) {
                    worldController.getSpriteBatch().draw(item.getTexture(), x, y, 32, 32);
                }

                // رسم نام و تعداد
                font.draw(worldController.getSpriteBatch(), item.getName() + " x" + item.getQuantity(), x + 40, y + 16);

                // رسم کادر انتخاب
                if (i == selectedRefrigeratorSlot) {
                    worldController.getSpriteBatch().setColor(1, 1, 0, 0.5f);
                    worldController.getSpriteBatch().draw(whitePixel, x - 2, y - 2, 36, 36);
                    worldController.getSpriteBatch().setColor(1, 1, 1, 1);
                }

                count++;
                if (count % itemsPerRow == 0) {
                    y -= 40;
                    x = 20;
                } else {
                    x += 200;
                }
            }
        }

        // رسم راهنما
        font.draw(worldController.getSpriteBatch(), "Click on items to select them", 20, 60);
        font.draw(worldController.getSpriteBatch(), "Press T to transfer from player to refrigerator", 20, 40);
        font.draw(worldController.getSpriteBatch(), "Press F to transfer from refrigerator to player", 20, 20);

        // رسم پیام انتقال
        if (messageTimer > 0) {
            font.draw(worldController.getSpriteBatch(), transferMessage, Gdx.graphics.getWidth() / 2 - 100, 80);
        }

        worldController.getSpriteBatch().end();
    }





}
