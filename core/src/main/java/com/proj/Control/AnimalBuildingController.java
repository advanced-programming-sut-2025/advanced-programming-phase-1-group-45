package com.proj.Control;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.proj.map.GameMap;
import com.proj.Model.Animal;

import java.util.ArrayList;
import java.util.List;

public class AnimalBuildingController {
    private Texture barnTexture;
    private Texture coopTexture;
    private Texture barnInteriorTexture;
    private Texture coopInteriorTexture;

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

    private List<Animal>[] animalsInBarns = new List[100];
    private List<Animal>[] animalsInCoops = new List[100];

    private boolean showingInterior = false;
    private boolean showingBarnInterior = false;
    private boolean showingCoopInterior = false;
    private int selectedBuildingIndex = -1;

    private Animal draggingAnimal = null;
    private float dragOffsetX = 0;
    private float dragOffsetY = 0;

    public AnimalBuildingController(GameMap gameMap) {
        try {
            barnTexture = new Texture(Gdx.files.internal("assets/Animals/Barn/Barn.png"));
            coopTexture = new Texture(Gdx.files.internal("assets/Animals/Coop/Coop.png"));
            barnInteriorTexture = new Texture(Gdx.files.internal("assets/Animals/Barn/barninside.png"));
            coopInteriorTexture = new Texture(Gdx.files.internal("assets/Animals/Coop/coopinside.png"));

            for (int i = 0; i < 100; i++) {
                animalsInBarns[i] = new ArrayList<>();
                animalsInCoops[i] = new ArrayList<>();
            }
        } catch (Exception e) {
            Gdx.app.error("AnimalBuildingController", "Error loading textures", e);
        }
    }

    public void update(float delta) {
        if (isPlacingBarn) {
            if (Gdx.input.isKeyPressed(Input.Keys.W)) {
                barnY += MOVEMENT_SPEED;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                barnY -= MOVEMENT_SPEED;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                barnX -= MOVEMENT_SPEED;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                barnX += MOVEMENT_SPEED;
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                placeBarn();
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                isPlacingBarn = false;
            }
        }

        if (isPlacingCoop) {
            if (Gdx.input.isKeyPressed(Input.Keys.W)) {
                coopY += MOVEMENT_SPEED;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                coopY -= MOVEMENT_SPEED;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                coopX -= MOVEMENT_SPEED;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                coopX += MOVEMENT_SPEED;
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                placeCoop();
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                isPlacingCoop = false;
            }
        }

        if (!isPlacingBarn && !isPlacingCoop && !showingInterior && Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            float mouseX = Gdx.input.getX();
            float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();

            for (int i = 0; i < barnCount; i++) {
                if (isClickOnBuilding(placedBarnsX[i], placedBarnsY[i], barnTexture.getWidth(), barnTexture.getHeight(), mouseX, mouseY)) {
                    showingInterior = true;
                    showingBarnInterior = true;
                    showingCoopInterior = false;
                    selectedBuildingIndex = i;
                    return;
                }
            }

            for (int i = 0; i < coopCount; i++) {
                if (isClickOnBuilding(placedCoopsX[i], placedCoopsY[i], coopTexture.getWidth(), coopTexture.getHeight(), mouseX, mouseY)) {
                    showingInterior = true;
                    showingBarnInterior = false;
                    showingCoopInterior = true;
                    selectedBuildingIndex = i;
                    return;
                }
            }
        }

        if (showingInterior) {
            float mouseX = Gdx.input.getX();
            float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();

            if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                if (draggingAnimal == null) {
                    List<Animal> animals = showingBarnInterior ?
                        animalsInBarns[selectedBuildingIndex] :
                        animalsInCoops[selectedBuildingIndex];

                    for (Animal animal : animals) {
                        if (isClickOnAnimal(animal, mouseX, mouseY)) {
                            draggingAnimal = animal;
                            dragOffsetX = mouseX - animal.getX();
                            dragOffsetY = mouseY - animal.getY();
                            break;
                        }
                    }
                }
            }

            if (draggingAnimal != null && Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                draggingAnimal.setPosition(mouseX - dragOffsetX, mouseY - dragOffsetY);
            }

            if (draggingAnimal != null && !Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                boolean isInValidArea = isInInteriorBounds(draggingAnimal.getX(), draggingAnimal.getY());

                if (!isInValidArea) {
                    removeAnimalFromBuilding(draggingAnimal);
                }

                draggingAnimal = null;
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                showingInterior = false;
                showingBarnInterior = false;
                showingCoopInterior = false;
                selectedBuildingIndex = -1;
                draggingAnimal = null;
            }
        }
    }

    public void render(SpriteBatch batch) {
        if (showingInterior) {
            renderInterior(batch);
        } else {
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
    }

    private void renderInterior(SpriteBatch batch) {
        Texture interiorTexture = showingBarnInterior ? barnInteriorTexture : coopInteriorTexture;
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        float scale = Math.min(screenWidth / interiorTexture.getWidth(), screenHeight / interiorTexture.getHeight());
        float scaledWidth = interiorTexture.getWidth() * scale;
        float scaledHeight = interiorTexture.getHeight() * scale;
        float x = (screenWidth - scaledWidth) / 2;
        float y = (screenHeight - scaledHeight) / 2;

        batch.draw(interiorTexture, x, y, scaledWidth, scaledHeight);

        List<Animal> animals = showingBarnInterior ?
            animalsInBarns[selectedBuildingIndex] :
            animalsInCoops[selectedBuildingIndex];

        for (Animal animal : animals) {
            renderAnimal(batch, animal);
        }
    }

    private void renderAnimal(SpriteBatch batch, Animal animal) {
        Texture animalTexture = getAnimalTexture(animal.getType());
        if (animalTexture != null) {
            batch.draw(animalTexture, animal.getX(), animal.getY());
        }
    }

    private Texture getAnimalTexture(String type) {
        return null;
    }

    public void startPlacingBarn(float x, float y) {
        isPlacingBarn = true;
        isPlacingCoop = false;
        barnX = x;
        barnY = y;
    }

    public void startPlacingCoop(float x, float y) {
        isPlacingCoop = true;
        isPlacingBarn = false;
        coopX = x;
        coopY = y;
    }

    private void placeBarn() {
        if (barnCount < placedBarnsX.length) {
            placedBarnsX[barnCount] = barnX;
            placedBarnsY[barnCount] = barnY;
            barnCount++;
            isPlacingBarn = false;
        }
    }

    private void placeCoop() {
        if (coopCount < placedCoopsX.length) {
            placedCoopsX[coopCount] = coopX;
            placedCoopsY[coopCount] = coopY;
            coopCount++;
            isPlacingCoop = false;
        }
    }

    public boolean isPlacingBarn() {
        return isPlacingBarn;
    }

    public boolean isPlacingCoop() {
        return isPlacingCoop;
    }

    public boolean isShowingInterior() {
        return showingInterior;
    }

    private boolean isClickOnBuilding(float buildingX, float buildingY, float width, float height, float mouseX, float mouseY) {
        return mouseX >= buildingX && mouseX <= buildingX + width &&
            mouseY >= buildingY && mouseY <= buildingY + height;
    }

    private boolean isClickOnAnimal(Animal animal, float mouseX, float mouseY) {
        Texture animalTexture = getAnimalTexture(animal.getType());
        if (animalTexture == null) return false;

        float width = animalTexture.getWidth();
        float height = animalTexture.getHeight();

        return mouseX >= animal.getX() && mouseX <= animal.getX() + width &&
            mouseY >= animal.getY() && mouseY <= animal.getY() + height;
    }

    private boolean isInInteriorBounds(float x, float y) {
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        Texture interiorTexture = showingBarnInterior ? barnInteriorTexture : coopInteriorTexture;
        float scale = Math.min(screenWidth / interiorTexture.getWidth(), screenHeight / interiorTexture.getHeight());
        float scaledWidth = interiorTexture.getWidth() * scale;
        float scaledHeight = interiorTexture.getHeight() * scale;
        float interiorX = (screenWidth - scaledWidth) / 2;
        float interiorY = (screenHeight - scaledHeight) / 2;

        return x >= interiorX && x <= interiorX + scaledWidth &&
            y >= interiorY && y <= interiorY + scaledHeight;
    }

    public void addAnimalToBuilding(Animal animal, boolean isBarn, int index) {
        if (isBarn) {
            if (index >= 0 && index < barnCount) {
                animalsInBarns[index].add(animal);
            }
        } else {
            if (index >= 0 && index < coopCount) {
                animalsInCoops[index].add(animal);
            }
        }
    }

    private void removeAnimalFromBuilding(Animal animal) {
        if (showingBarnInterior && selectedBuildingIndex >= 0) {
            animalsInBarns[selectedBuildingIndex].remove(animal);
        } else if (showingCoopInterior && selectedBuildingIndex >= 0) {
            animalsInCoops[selectedBuildingIndex].remove(animal);
        }
    }

    public void dispose() {
        if (barnTexture != null) barnTexture.dispose();
        if (coopTexture != null) coopTexture.dispose();
        if (barnInteriorTexture != null) barnInteriorTexture.dispose();
        if (coopInteriorTexture != null) coopInteriorTexture.dispose();
    }
}
