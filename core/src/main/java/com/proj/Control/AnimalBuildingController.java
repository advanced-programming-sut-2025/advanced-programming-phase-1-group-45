package com.proj.Control;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.proj.map.GameMap;

public class AnimalBuildingController {
    private Texture barnTexture;
    private Texture coopTexture;

    private boolean isPlacingBarn = false;
    private boolean isPlacingCoop = false;

    private float barnX = 0;
    private float barnY = 0;
    private float coopX = 0;
    private float coopY = 0;

    private final float MOVEMENT_SPEED = 5.0f;

    private float[] placedBarnsX = new float[100];
    private float[] placedBarnsY = new float[100];
    private int barnCount = 0;

    private float[] placedCoopsX = new float[100];
    private float[] placedCoopsY = new float[100];
    private int coopCount = 0;

    public AnimalBuildingController(GameMap gameMap) {
        try {
            barnTexture = new Texture(Gdx.files.internal("buildings/barn.png"));
            coopTexture = new Texture(Gdx.files.internal("buildings/coop.png"));
        } catch (Exception e) {
            Gdx.app.error("AnimalBuildingController", "Error loading textures", e);
        }
    }

    public void update(float delta) {
        if (isPlacingBarn) {
            if (Gdx.input.isKeyPressed(Input.Keys.W)) {
                barnY += MOVEMENT_SPEED;
                System.out.println("Moving barn up: " + barnY);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                barnY -= MOVEMENT_SPEED;
                System.out.println("Moving barn down: " + barnY);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                barnX -= MOVEMENT_SPEED;
                System.out.println("Moving barn left: " + barnX);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                barnX += MOVEMENT_SPEED;
                System.out.println("Moving barn right: " + barnX);
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                placeBarn();
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                isPlacingBarn = false;
                System.out.println("Cancelled placing barn");
            }
        }

        if (isPlacingCoop) {
            if (Gdx.input.isKeyPressed(Input.Keys.W)) {
                coopY += MOVEMENT_SPEED;
                System.out.println("Moving coop up: " + coopY);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                coopY -= MOVEMENT_SPEED;
                System.out.println("Moving coop down: " + coopY);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                coopX -= MOVEMENT_SPEED;
                System.out.println("Moving coop left: " + coopX);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                coopX += MOVEMENT_SPEED;
                System.out.println("Moving coop right: " + coopX);
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                placeCoop();
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                isPlacingCoop = false;
                System.out.println("Cancelled placing coop");
            }
        }
    }

    public void render(SpriteBatch batch) {
        for (int i = 0; i < barnCount; i++) {
            batch.draw(barnTexture, placedBarnsX[i], placedBarnsY[i]);
        }

        for (int i = 0; i < coopCount; i++) {
            batch.draw(coopTexture, placedCoopsX[i], placedCoopsY[i]);
        }

        if (isPlacingBarn) {
            batch.setColor(1, 1, 1, 0.7f);
            batch.draw(barnTexture, barnX, barnY);
            batch.setColor(1, 1, 1, 1);
        }

        if (isPlacingCoop) {
            batch.setColor(1, 1, 1, 0.7f);
            batch.draw(coopTexture, coopX, coopY);
            batch.setColor(1, 1, 1, 1);
        }
    }

    public void startPlacingBarn(float x, float y) {
        isPlacingBarn = true;
        isPlacingCoop = false;
        barnX = x;
        barnY = y;
        System.out.println("Started placing barn at: " + x + ", " + y);
    }

    public void startPlacingCoop(float x, float y) {
        isPlacingCoop = true;
        isPlacingBarn = false;
        coopX = x;
        coopY = y;
        System.out.println("Started placing coop at: " + x + ", " + y);
    }

    private void placeBarn() {
        if (barnCount < placedBarnsX.length) {
            placedBarnsX[barnCount] = barnX;
            placedBarnsY[barnCount] = barnY;
            barnCount++;
            isPlacingBarn = false;
            System.out.println("Barn placed at: " + barnX + ", " + barnY);
        }
    }

    private void placeCoop() {
        if (coopCount < placedCoopsX.length) {
            placedCoopsX[coopCount] = coopX;
            placedCoopsY[coopCount] = coopY;
            coopCount++;
            isPlacingCoop = false;
            System.out.println("Coop placed at: " + coopX + ", " + coopY);
        }
    }

    public boolean isPlacingBarn() {
        return isPlacingBarn;
    }

    public boolean isPlacingCoop() {
        return isPlacingCoop;
    }

    public boolean isShowingInterior() {
        return false;
    }

    public void dispose() {
        if (barnTexture != null) barnTexture.dispose();
        if (coopTexture != null) coopTexture.dispose();
    }
}
