package com.proj.Control;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.proj.map.GameMap;
import com.proj.Model.Place.Barn;
import com.proj.Model.Place.Coop;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class AnimalBuildingController {
    private final Sprite coop;
    private final Sprite barn;
    private final Sprite coopInside;
    private final Sprite barnInside;

    private boolean isPlacingCoop = false;
    private float tempCoopX = 0;
    private float tempCoopY = 0;
    private final List<Coop> placedCoops = new ArrayList<>();

    private Coop selectedCoop = null;
    private boolean showingCoopInterior = false;

    private boolean isPlacingBarn = false;
    private float tempBarnX = 0;
    private float tempBarnY = 0;
    private final List<Barn> placedBarns = new ArrayList<>();

    private Barn selectedBarn = null;
    private boolean showingBarnInterior = false;

    private final float MOVEMENT_SPEED = 5.0f;
    private GameMap gameMap;

    private float interiorDisplayTime = 0;
    private final float INTERIOR_DISPLAY_DURATION = 5.0f;
    private boolean autoHideInterior = true;

    private ShapeRenderer shapeRenderer;

    private float interiorX;
    private float interiorY;
    private float interiorScale = 1.5f;

    public AnimalBuildingController(GameMap gameMap) {
        this.coop = new Sprite(new Texture(Gdx.files.internal("buildings/coop.png")));
        this.barn = new Sprite(new Texture(Gdx.files.internal("buildings/barn.png")));
        this.coopInside = new Sprite(new Texture(Gdx.files.internal("buildings/coopinside.png")));
        this.barnInside = new Sprite(new Texture(Gdx.files.internal("buildings/barninside.png")));

        this.shapeRenderer = new ShapeRenderer();
        this.gameMap = gameMap;
    }

    public void update(SpriteBatch batch, float delta) {
        if (isShowingInterior()) {
            renderInterior(batch);
        } else {
            renderBuildings(batch);
            renderPlacingBuilding(batch);
        }

        handleInput(delta);
        updateInteriorDisplayTime(delta);
    }

    public void renderBuildings(SpriteBatch batch) {
        for (Coop coop : placedCoops) {
            Point pos = coop.getPosition();
            batch.draw(this.coop, pos.x, pos.y);
        }

        for (Barn barn : placedBarns) {
            Point pos = barn.getPosition();
            batch.draw(this.barn, pos.x, pos.y);
        }
    }

    public void renderInterior(SpriteBatch batch) {
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        batch.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.rect(0, 0, screenWidth, screenHeight);
        shapeRenderer.end();

        batch.begin();

        if (showingCoopInterior && selectedCoop != null) {
            Sprite interiorSprite = coopInside;
            float spriteWidth = interiorSprite.getWidth() * interiorScale;
            float spriteHeight = interiorSprite.getHeight() * interiorScale;

            interiorX = (screenWidth - spriteWidth) / 2;
            interiorY = (screenHeight - spriteHeight) / 2;

            batch.draw(interiorSprite, interiorX, interiorY, spriteWidth, spriteHeight);

        } else if (showingBarnInterior && selectedBarn != null) {
            Sprite interiorSprite = barnInside;
            float spriteWidth = interiorSprite.getWidth() * interiorScale;
            float spriteHeight = interiorSprite.getHeight() * interiorScale;

            interiorX = (screenWidth - spriteWidth) / 2;
            interiorY = (screenHeight - spriteHeight) / 2;

            batch.draw(interiorSprite, interiorX, interiorY, spriteWidth, spriteHeight);
        }
    }

    public void renderPlacingBuilding(SpriteBatch batch) {
        if (isPlacingCoop) {
            coop.setAlpha(0.7f);
            coop.setPosition(tempCoopX, tempCoopY);
            coop.draw(batch);
            coop.setAlpha(1.0f);
        }

        if (isPlacingBarn) {
            barn.setAlpha(0.7f);
            barn.setPosition(tempBarnX, tempBarnY);
            barn.draw(batch);
            barn.setAlpha(1.0f);
        }
    }

    private void updateInteriorDisplayTime(float delta) {
        if (autoHideInterior && (showingCoopInterior || showingBarnInterior)) {
            interiorDisplayTime += delta;

            if (interiorDisplayTime >= INTERIOR_DISPLAY_DURATION) {
                closeInteriorView();
            }
        }
    }

    private void handleInput(float delta) {
        if (isShowingInterior()) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                closeInteriorView();
                return;
            }

            if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            }

            return;
        }

        System.out.println("Calling handleBuildingPlacement");
        handleBuildingPlacement();
    }

    private void handleBuildingPlacement() {
        if (isPlacingCoop) {
            handleBuildingMovement(true);

            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                placeCoop();
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                isPlacingCoop = false;
            }
        }

        if (isPlacingBarn) {
            System.out.println("Handling barn movement");
            handleBuildingMovement(false);

            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                placeBarn();
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                isPlacingBarn = false;
            }
        }
    }

    private void handleBuildingMovement(boolean isCoop) {
        float currentX = isCoop ? tempCoopX : tempBarnX;
        float currentY = isCoop ? tempCoopY : tempBarnY;

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            if (isCoop) tempCoopY += MOVEMENT_SPEED;
            else tempBarnY += MOVEMENT_SPEED;
            System.out.println("W pressed: Moving " + (isCoop ? "coop" : "barn") + " up");
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            if (isCoop) tempCoopY -= MOVEMENT_SPEED;
            else tempBarnY -= MOVEMENT_SPEED;
            System.out.println("S pressed: Moving " + (isCoop ? "coop" : "barn") + " down");
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            if (isCoop) tempCoopX -= MOVEMENT_SPEED;
            else tempBarnX -= MOVEMENT_SPEED;
            System.out.println("A pressed: Moving " + (isCoop ? "coop" : "barn") + " left");
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            if (isCoop) tempCoopX += MOVEMENT_SPEED;
            else tempBarnX += MOVEMENT_SPEED;
            System.out.println("D pressed: Moving " + (isCoop ? "coop" : "barn") + " right");
        }

        if (currentX != (isCoop ? tempCoopX : tempBarnX) || currentY != (isCoop ? tempCoopY : tempBarnY)) {
            System.out.println("Position changed from (" + currentX + "," + currentY +
                ") to (" + (isCoop ? tempCoopX : tempBarnX) + "," +
                (isCoop ? tempCoopY : tempBarnY) + ")");
        } else {
            System.out.println("No movement detected for " + (isCoop ? "coop" : "barn"));
        }
    }



    public void startPlacingCoop(float playerX, float playerY) {
        isPlacingCoop = true;
        tempCoopX = playerX;
        tempCoopY = playerY;
    }

    public void startPlacingBarn(float playerX, float playerY) {
        isPlacingBarn = true;
        tempBarnX = playerX;
        tempBarnY = playerY;
    }

    private void placeCoop() {
        if (isValidPlacement(tempCoopX, tempCoopY, true)) {
            Point position = new Point((int)tempCoopX, (int)tempCoopY);
            int coopWidth = (int) this.coop.getWidth();
            int coopHeight = (int) this.coop.getHeight();

            Coop newCoop = new Coop(position, coopHeight, coopWidth);
            placedCoops.add(newCoop);

            int tileWidth = gameMap.getTileWidth();
            int tileHeight = gameMap.getTileHeight();

            int startTileX = (int) Math.floor(tempCoopX / tileWidth);
            int startTileY = (int) Math.floor(tempCoopY / tileHeight);
            int coopWidthInTiles = (int) Math.ceil((double) coopWidth / tileWidth);
            int coopHeightInTiles = (int) Math.ceil((double) coopHeight / tileHeight);

            System.out.println("Coop placed at tile: " + startTileX + ", " + startTileY);
            isPlacingCoop = false;
        } else {
            System.out.println("Cannot place coop here. Invalid position.");
        }
    }

    private void placeBarn() {
        if (isValidPlacement(tempBarnX, tempBarnY, false)) {
            Point position = new Point((int)tempBarnX, (int)tempBarnY);
            int barnWidth = (int) this.barn.getWidth();
            int barnHeight = (int) this.barn.getHeight();

            Barn newBarn = new Barn(position, barnHeight, barnWidth);
            placedBarns.add(newBarn);

            int tileWidth = gameMap.getTileWidth();
            int tileHeight = gameMap.getTileHeight();

            int startTileX = (int) Math.floor(tempBarnX / tileWidth);
            int startTileY = (int) Math.floor(tempBarnY / tileHeight);
            int barnWidthInTiles = (int) Math.ceil((double) barnWidth / tileWidth);
            int barnHeightInTiles = (int) Math.ceil((double) barnHeight / tileHeight);

            System.out.println("Barn placed at tile: " + startTileX + ", " + startTileY);
            isPlacingBarn = false;
        } else {
            System.out.println("Cannot place barn here. Invalid position.");
        }
    }

    private boolean isValidPlacement(float x, float y, boolean isCoop) {
        Sprite buildingSprite = isCoop ? this.coop : this.barn;

        int tileWidth = gameMap.getTileWidth();
        int tileHeight = gameMap.getTileHeight();
        int mapWidth = gameMap.getMapWidth();
        int mapHeight = gameMap.getMapHeight();

        float mapPixelWidth = mapWidth * tileWidth;
        float mapPixelHeight = mapHeight * tileHeight;

        if (x < 0 || y < 0 || x + buildingSprite.getWidth() > mapPixelWidth ||
            y + buildingSprite.getHeight() > mapPixelHeight) {
            System.out.println("Invalid placement: Out of map boundaries");
            return false;
        }

        int startTileX = (int) Math.floor(x / tileWidth);
        int startTileY = (int) Math.floor(y / tileHeight);

        int buildingWidthInTiles = (int) Math.ceil((double) buildingSprite.getWidth() / tileWidth);
        int buildingHeightInTiles = (int) Math.ceil((double) buildingSprite.getHeight() / tileHeight);

        if (startTileX + buildingWidthInTiles >= mapWidth || startTileY + buildingHeightInTiles >= mapHeight) {
            System.out.println("Invalid placement: Building extends beyond map boundaries");
            return false;
        }

        for (int tileX = startTileX; tileX < startTileX + buildingWidthInTiles; tileX++) {
            for (int tileY = startTileY; tileY < startTileY + buildingHeightInTiles; tileY++) {
                if (!gameMap.isPassable(tileX * tileWidth, tileY * tileHeight)) {
                    System.out.println("Invalid placement: Tile is not passable");
                    return false;
                }
            }
        }

        Rectangle newBuildingBounds = new Rectangle(x, y, buildingSprite.getWidth(), buildingSprite.getHeight());

        for (Coop placedCoop : placedCoops) {
            Point placedPos = placedCoop.getPosition();
            Rectangle placedBounds = new Rectangle(placedPos.x, placedPos.y,
                this.coop.getWidth(),
                this.coop.getHeight());

            if (newBuildingBounds.overlaps(placedBounds)) {
                System.out.println("Invalid placement: Overlaps with another coop");
                return false;
            }
        }

        for (Barn placedBarn : placedBarns) {
            Point placedPos = placedBarn.getPosition();
            Rectangle placedBounds = new Rectangle(placedPos.x, placedPos.y,
                this.barn.getWidth(),
                this.barn.getHeight());

            if (newBuildingBounds.overlaps(placedBounds)) {
                System.out.println("Invalid placement: Overlaps with a barn");
                return false;
            }
        }

        return true;
    }

    public void closeInteriorView() {
        showingCoopInterior = false;
        showingBarnInterior = false;
        selectedCoop = null;
        selectedBarn = null;
        interiorDisplayTime = 0;
    }

    public boolean isPlacingCoop() {
        return isPlacingCoop;
    }

    public boolean isPlacingBarn() {
        return isPlacingBarn;
    }

    public boolean isShowingCoopInterior() {
        return showingCoopInterior;
    }

    public boolean isShowingBarnInterior() {
        return showingBarnInterior;
    }

    public boolean isShowingInterior() {
        return showingCoopInterior || showingBarnInterior;
    }

    public Coop getSelectedCoop() {
        return selectedCoop;
    }

    public Barn getSelectedBarn() {
        return selectedBarn;
    }

    public void setAutoHideInterior(boolean autoHide) {
        this.autoHideInterior = autoHide;
    }

    public void dispose() {
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }

        // آزادسازی تصاویر
        if (coop != null && coop.getTexture() != null) {
            coop.getTexture().dispose();
        }
        if (barn != null && barn.getTexture() != null) {
            barn.getTexture().dispose();
        }
        if (coopInside != null && coopInside.getTexture() != null) {
            coopInside.getTexture().dispose();
        }
        if (barnInside != null && barnInside.getTexture() != null) {
            barnInside.getTexture().dispose();
        }
    }
}


