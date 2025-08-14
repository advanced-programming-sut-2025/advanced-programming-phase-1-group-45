package com.proj.Model.Animal;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Pixmap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnimalManager {
    private List<Animal> animals = new ArrayList<>();
    private Map<String, Texture> animalTextures = new HashMap<>();
    private Map<String, Texture> productTextures = new HashMap<>();

    private Animal selectedAnimal = null;
    private boolean showingAnimalMenu = false;
    private boolean showingBuyMenu = false;
    private FeedingController feedingController;
    private BitmapFont font;
    private GlyphLayout layout;
    private Texture whitePixelTexture;

    private Animal feedingAnimal = null;
    private float feedingTime = 0;
    private static final float FEEDING_DURATION = 2.0f;

    private Animal pettingAnimal = null;
    private float pettingTime = 0;
    private static final float PETTING_DURATION = 2.0f;

    private Texture hayHopperTexture;
    private Texture hayHopperFullTexture;

    public AnimalManager() {
        loadTextures();

        font = new BitmapFont();
        layout = new GlyphLayout();
        createWhitePixelTexture();
        feedingController = new FeedingController();

    }

    private void createWhitePixelTexture() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(1, 1, 1, 1);
        pixmap.fill();
        whitePixelTexture = new Texture(pixmap);
        pixmap.dispose();
    }

    private void loadTextures() {
        try {
            animalTextures.put("cow", new Texture(Gdx.files.internal("assets/Animals/Sprites/Cow.png")));
            animalTextures.put("chicken", new Texture(Gdx.files.internal("assets/Animals/Sprites/Chicken.png")));
            animalTextures.put("sheep", new Texture(Gdx.files.internal("assets/Animals/Sprites/Sheep.png")));
            animalTextures.put("goat", new Texture(Gdx.files.internal("assets/Animals/Sprites/Goat.png")));
            animalTextures.put("pig", new Texture(Gdx.files.internal("assets/Animals/Sprites/Pig.png")));
            animalTextures.put("duck", new Texture(Gdx.files.internal("assets/Animals/Sprites/Duck.png")));
            animalTextures.put("rabbit", new Texture(Gdx.files.internal("assets/Animals/Sprites/Rabbit.png")));
            animalTextures.put("dinosaur", new Texture(Gdx.files.internal("assets/Animals/Sprites/Dinosaur.png")));

            productTextures.put("Cow_Milk", new Texture(Gdx.files.internal("assets/Animals/AnimalProducts/Cow_Milk.png")));
            productTextures.put("Cow_Large_Milk", new Texture(Gdx.files.internal("assets/Animals/AnimalProducts/Cow_Large_Milk.png")));
            productTextures.put("Egg", new Texture(Gdx.files.internal("assets/Animals/AnimalProducts/Egg.png")));
            productTextures.put("Large_Egg", new Texture(Gdx.files.internal("assets/Animals/AnimalProducts/Large_Egg.png")));
            productTextures.put("Duck_Egg", new Texture(Gdx.files.internal("assets/Animals/AnimalProducts/Duck_Egg.png")));
            productTextures.put("Duck_Feather", new Texture(Gdx.files.internal("assets/Animals/AnimalProducts/Duck_Feather.png")));
            productTextures.put("Goat_Milk", new Texture(Gdx.files.internal("assets/Animals/AnimalProducts/Goat_Milk.png")));
            productTextures.put("Goat_Large_Milk", new Texture(Gdx.files.internal("assets/Animals/AnimalProducts/Goat_Large_Milk.png")));
            productTextures.put("Sheep_Wool", new Texture(Gdx.files.internal("assets/Animals/AnimalProducts/Sheep_Wool.png")));
            productTextures.put("Rabbit_Wool", new Texture(Gdx.files.internal("assets/Animals/AnimalProducts/Rabbit_Wool.png")));
            productTextures.put("Truffle", new Texture(Gdx.files.internal("assets/Animals/AnimalProducts/Truffle.png")));
            productTextures.put("Dinosaur_Egg", new Texture(Gdx.files.internal("assets/Animals/AnimalProducts/Dinosaur_Egg.png")));

            hayHopperTexture = new Texture(Gdx.files.internal("assets/Animals/Hay_Hopper.png"));
            hayHopperFullTexture = new Texture(Gdx.files.internal("assets/Animals/Hay_Hopper_Full.png"));
        } catch (Exception e) {
            Gdx.app.error("AnimalManager", "Error loading textures", e);
        }
    }

    public void update(float delta) {
        for (Animal animal : animals) {
            animal.update(delta);
        }

        if (feedingAnimal != null) {
            feedingTime += delta;
            if (feedingTime >= FEEDING_DURATION) {
                feedingAnimal.feed();
                feedingAnimal = null;
                feedingTime = 0;
            }
        }

        if (pettingAnimal != null) {
            pettingTime += delta;
            if (pettingTime >= PETTING_DURATION) {
                pettingAnimal.pet();
                pettingAnimal = null;
                pettingTime = 0;
            }
        }

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            float mouseX = Gdx.input.getX();
            float mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();

            if (showingAnimalMenu && selectedAnimal != null) {
                handleAnimalMenuClick(mouseX, mouseY);
            } else if (showingBuyMenu) {
                handleBuyMenuClick(mouseX, mouseY);
            } else {
                for (Animal animal : animals) {
                    if (isClickOnAnimal(animal, mouseX, mouseY)) {
                        selectedAnimal = animal;
                        showingAnimalMenu = true;
                        break;
                    }
                }
            }
        }

        if (selectedAnimal != null) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
                startPetting(selectedAnimal);
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.F)) {
                startFeeding(selectedAnimal);
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.O)) {
                selectedAnimal.setOutside(!selectedAnimal.isOutside());
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.C)) {
                selectedAnimal.collectProduct();
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
                sellAnimal(selectedAnimal);
                selectedAnimal = null;
                showingAnimalMenu = false;
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                selectedAnimal = null;
                showingAnimalMenu = false;
            }
            if (feedingController != null) feedingController.update(delta);

        }

        if (showingBuyMenu && Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            showingBuyMenu = false;
        }
    }

    private void handleAnimalMenuClick(float mouseX, float mouseY) {
        float menuX = selectedAnimal.getX() + 50;
        float menuY = selectedAnimal.getY() + 50;

        if (isClickOnButton(mouseX, mouseY, menuX + 10, menuY + 10, 80, 20)) {
            startPetting(selectedAnimal);
        } else if (isClickOnButton(mouseX, mouseY, menuX + 110, menuY + 10, 80, 20)) {
            startFeeding(selectedAnimal);
        } else if (isClickOnButton(mouseX, mouseY, menuX + 10, menuY + 35, 180, 20)) {
            selectedAnimal.setOutside(!selectedAnimal.isOutside());
        } else if (selectedAnimal.hasProduct() && isClickOnButton(mouseX, mouseY, menuX + 10, menuY + 60, 80, 20)) {
            selectedAnimal.collectProduct();
        } else if (isClickOnButton(mouseX, mouseY, menuX + 110, menuY + 60, 80, 20)) {
            sellAnimal(selectedAnimal);
            selectedAnimal = null;
            showingAnimalMenu = false;
        }
    }

    private void handleBuyMenuClick(float mouseX, float mouseY) {
        float menuX = 100;
        float menuY = 100;
        float buttonWidth = 180;
        float buttonHeight = 30;
        float buttonX = menuX + 10;
        float y = menuY + 300 - 40;

        if (isClickOnButton(mouseX, mouseY, menuX + 400 - 90, menuY + 10, 80, 30)) {
            showingBuyMenu = false;
            return;
        }

        if (isClickOnButton(mouseX, mouseY, buttonX, y, buttonWidth, buttonHeight)) {
            buyAnimal("chicken", "Chicken" + animals.size());
        }
        y -= buttonHeight + 5;

        if (isClickOnButton(mouseX, mouseY, buttonX, y, buttonWidth, buttonHeight)) {
            buyAnimal("cow", "Cow" + animals.size());
        }
        y -= buttonHeight + 5;

        if (isClickOnButton(mouseX, mouseY, buttonX, y, buttonWidth, buttonHeight)) {
            buyAnimal("duck", "Duck" + animals.size());
        }
        y -= buttonHeight + 5;

        if (isClickOnButton(mouseX, mouseY, buttonX, y, buttonWidth, buttonHeight)) {
            buyAnimal("goat", "Goat" + animals.size());
        }
        y -= buttonHeight + 5;

        if (isClickOnButton(mouseX, mouseY, buttonX, y, buttonWidth, buttonHeight)) {
            buyAnimal("pig", "Pig" + animals.size());
        }
        y -= buttonHeight + 5;

        if (isClickOnButton(mouseX, mouseY, buttonX, y, buttonWidth, buttonHeight)) {
            buyAnimal("rabbit", "Rabbit" + animals.size());
        }
        y -= buttonHeight + 5;

        if (isClickOnButton(mouseX, mouseY, buttonX, y, buttonWidth, buttonHeight)) {
            buyAnimal("sheep", "Sheep" + animals.size());
        }
    }

    public void render(SpriteBatch batch) {
        for (Animal animal : animals) {
            Texture animalTexture = animalTextures.get(animal.getType().toLowerCase());
            if (animalTexture != null) {
                batch.draw(animalTexture, animal.getX(), animal.getY());

                if (animal.hasProduct()) {
                    Texture productTexture = productTextures.get(animal.getProduct());
                    if (productTexture != null) {
                        batch.draw(productTexture,
                            animal.getX() + animalTexture.getWidth()/2 - productTexture.getWidth()/2,
                            animal.getY() + animalTexture.getHeight() + 5);
                    }
                }

                if (font != null) {
                    font.setColor(1, 1, 1, 1);
                    layout.setText(font, animal.getName());
                    font.draw(batch, animal.getName(),
                        animal.getX() + animalTexture.getWidth()/2 - layout.width/2,
                        animal.getY() - 5);
                }
            }
        }

        if (feedingAnimal != null) {
            renderFeedingAnimation(batch);
        }

        if (pettingAnimal != null) {
            renderPettingAnimation(batch);
        }
        if (feedingController != null) feedingController.render(batch);


        if (showingAnimalMenu && selectedAnimal != null) {
            renderAnimalMenu(batch);
        }

        if (showingBuyMenu) {
            renderBuyMenu(batch);
        }


    }

    private void renderFeedingAnimation(SpriteBatch batch) {
        if (feedingAnimal != null) {
            Texture animalTexture = animalTextures.get(feedingAnimal.getType().toLowerCase());
            if (animalTexture != null) {
                batch.draw(animalTexture, feedingAnimal.getX(), feedingAnimal.getY());

                Texture hopperTexture = feedingTime < FEEDING_DURATION/2 ? hayHopperFullTexture : hayHopperTexture;
                batch.draw(hopperTexture,
                    feedingAnimal.getX() + animalTexture.getWidth()/2 - hopperTexture.getWidth()/2,
                    feedingAnimal.getY() - hopperTexture.getHeight());
            }
        }
    }

    private void renderPettingAnimation(SpriteBatch batch) {
        if (pettingAnimal != null) {
            Texture animalTexture = animalTextures.get(pettingAnimal.getType().toLowerCase());
            if (animalTexture != null) {
                batch.setColor(1, 1, 1, 0.8f + 0.2f * (float)Math.sin(pettingTime * 10));
                batch.draw(animalTexture, pettingAnimal.getX(), pettingAnimal.getY());
                batch.setColor(1, 1, 1, 1);
            }
        }
    }

    private void renderAnimalMenu(SpriteBatch batch) {
        if (selectedAnimal == null) return;

        float menuWidth = 200;
        float menuHeight = 150;
        float menuX = selectedAnimal.getX() + 50;
        float menuY = selectedAnimal.getY() + 50;

        if (menuX + menuWidth > Gdx.graphics.getWidth()) {
            menuX = Gdx.graphics.getWidth() - menuWidth - 10;
        }
        if (menuY + menuHeight > Gdx.graphics.getHeight()) {
            menuY = Gdx.graphics.getHeight() - menuHeight - 10;
        }

        batch.setColor(0.2f, 0.2f, 0.2f, 0.8f);
        batch.draw(whitePixelTexture, menuX, menuY, menuWidth, menuHeight);
        batch.setColor(1, 1, 1, 1);

        if (font != null) {
            font.setColor(1, 1, 1, 1);

            layout.setText(font, selectedAnimal.getName() + " (" + selectedAnimal.getType() + ")");
            font.draw(batch, selectedAnimal.getName() + " (" + selectedAnimal.getType() + ")",
                menuX + 10, menuY + menuHeight - 10);

            layout.setText(font, "Friendship: " + selectedAnimal.getFriendship());
            font.draw(batch, "Friendship: " + selectedAnimal.getFriendship(),
                menuX + 10, menuY + menuHeight - 30);

            layout.setText(font, "Fed today: " + (selectedAnimal.isFedToday() ? "Yes" : "No"));
            font.draw(batch, "Fed today: " + (selectedAnimal.isFedToday() ? "Yes" : "No"),
                menuX + 10, menuY + menuHeight - 50);

            layout.setText(font, "Petted today: " + (selectedAnimal.isPetToday() ? "Yes" : "No"));
            font.draw(batch, "Petted today: " + (selectedAnimal.isPetToday() ? "Yes" : "No"),
                menuX + 10, menuY + menuHeight - 70);

            layout.setText(font, "Outside: " + (selectedAnimal.isOutside() ? "Yes" : "No"));
            font.draw(batch, "Outside: " + (selectedAnimal.isOutside() ? "Yes" : "No"),
                menuX + 10, menuY + menuHeight - 90);

            layout.setText(font, "Has product: " + (selectedAnimal.hasProduct() ? "Yes" : "No"));
            font.draw(batch, "Has product: " + (selectedAnimal.hasProduct() ? "Yes" : "No"),
                menuX + 10, menuY + menuHeight - 110);

            renderButton(batch, "Pet (P)", menuX + 10, menuY + 10, 80, 20);
            renderButton(batch, "Feed (F)", menuX + 110, menuY + 10, 80, 20);
            renderButton(batch, selectedAnimal.isOutside() ? "Bring Inside (O)" : "Take Outside (O)",
                menuX + 10, menuY + 35, 180, 20);

            if (selectedAnimal.hasProduct()) {
                renderButton(batch, "Collect (C)", menuX + 10, menuY + 60, 80, 20);
            }

            renderButton(batch, "Sell (S)", menuX + 110, menuY + 60, 80, 20);
        }
    }

    private void renderBuyMenu(SpriteBatch batch) {
        float menuX = 100;
        float menuY = 100;
        float menuWidth = 400;
        float menuHeight = 300;

        batch.setColor(0.2f, 0.2f, 0.2f, 0.8f);
        batch.draw(whitePixelTexture, menuX, menuY, menuWidth, menuHeight);
        batch.setColor(1, 1, 1, 1);

        if (font != null) {
            font.setColor(1, 1, 1, 1);

            layout.setText(font, "Buy Animals");
            font.draw(batch, "Buy Animals", menuX + 10, menuY + menuHeight - 10);

            float y = menuY + menuHeight - 40;
            float buttonWidth = 180;
            float buttonHeight = 30;
            float buttonX = menuX + 10;

            renderBuyButton(batch, "Chicken - 800g", buttonX, y, buttonWidth, buttonHeight, "chicken");
            y -= buttonHeight + 5;
            renderBuyButton(batch, "Cow - 1500g", buttonX, y, buttonWidth, buttonHeight, "cow");
            y -= buttonHeight + 5;
            renderBuyButton(batch, "Duck - 1100g", buttonX, y, buttonWidth, buttonHeight, "duck");
            y -= buttonHeight + 5;
            renderBuyButton(batch, "Goat - 1300g", buttonX, y, buttonWidth, buttonHeight, "goat");
            y -= buttonHeight + 5;
            renderBuyButton(batch, "Pig - 1600g", buttonX, y, buttonWidth, buttonHeight, "pig");
            y -= buttonHeight + 5;
            renderBuyButton(batch, "Rabbit - 1000g", buttonX, y, buttonWidth, buttonHeight, "rabbit");
            y -= buttonHeight + 5;
            renderBuyButton(batch, "Sheep - 1200g", buttonX, y, buttonWidth, buttonHeight, "sheep");

            renderButton(batch, "Close", menuX + menuWidth - 90, menuY + 10, 80, 30);
        }
    }

    private void renderButton(SpriteBatch batch, String text, float x, float y, float width, float height) {
        batch.setColor(0.4f, 0.4f, 0.4f, 1);
        batch.draw(whitePixelTexture, x, y, width, height);

        batch.setColor(0.6f, 0.6f, 0.6f, 1);
        batch.draw(whitePixelTexture, x, y, width, 1);
        batch.draw(whitePixelTexture, x, y, 1, height);
        batch.draw(whitePixelTexture, x + width - 1, y, 1, height);
        batch.draw(whitePixelTexture, x, y + height - 1, width, 1);

        if (font != null) {
            font.setColor(1, 1, 1, 1);
            layout.setText(font, text);
            float textX = x + (width - layout.width) / 2;
            float textY = y + (height + layout.height) / 2;
            font.draw(batch, text, textX, textY);
        }

        batch.setColor(1, 1, 1, 1);
    }

    private void renderBuyButton(SpriteBatch batch, String text, float x, float y, float width, float height, final String animalType) {
        renderButton(batch, text, x, y, width, height);

        Texture animalTexture = animalTextures.get(animalType);
        if (animalTexture != null) {
            float scale = height / animalTexture.getHeight();
            batch.draw(animalTexture,
                x + width + 10,
                y,
                animalTexture.getWidth() * scale,
                animalTexture.getHeight() * scale);
        }
    }

    private boolean isClickOnButton(float mouseX, float mouseY, float buttonX, float buttonY, float buttonWidth, float buttonHeight) {
        return mouseX >= buttonX && mouseX <= buttonX + buttonWidth &&
            mouseY >= buttonY && mouseY <= buttonY + buttonHeight;
    }

    private boolean isClickOnAnimal(Animal animal, float mouseX, float mouseY) {
        Texture texture = animalTextures.get(animal.getType().toLowerCase());
        if (texture == null) return false;

        float width = texture.getWidth();
        float height = texture.getHeight();

        return mouseX >= animal.getX() && mouseX <= animal.getX() + width &&
            mouseY >= animal.getY() && mouseY <= animal.getY() + height;
    }

    public void buyAnimal(String type, String name) {
        if (!isValidAnimalType(type)) {
            System.out.println("Invalid animal type: " + type);
            return;
        }

        for (Animal animal : animals) {
            if (animal.getName().equalsIgnoreCase(name)) {
                System.out.println("An animal with this name already exists: " + name);
                return;
            }
        }

        Animal newAnimal = new Animal(name, type);

        float x = 100 + (float)(Math.random() * 300);
        float y = 100 + (float)(Math.random() * 300);
        newAnimal.setPosition(x, y);

        animals.add(newAnimal);
        System.out.println("Bought a new " + type + " named " + name);

        showingBuyMenu = false;
    }

    public void sellAnimal(Animal animal) {
        if (animal != null && animals.contains(animal)) {
            int price = animal.getSellPrice();
            animals.remove(animal);
            System.out.println("Sold " + animal.getName() + " for " + price + " gold");
        }
    }

    public void moveAnimalTo(String name, float x, float y) {
        Animal animal = findAnimalByName(name);
        if (animal != null) {
            animal.moveTo(x, y);
        } else {
            System.out.println("Animal not found: " + name);
        }
    }

    public void feedAnimal(String name) {
        Animal animal = findAnimalByName(name);
        if (animal != null) {
            startFeeding(animal);
        } else {
            System.out.println("Animal not found: " + name);
        }
    }

    public void petAnimal(String name) {
        Animal animal = findAnimalByName(name);
        if (animal != null) {
            startPetting(animal);
        } else {
            System.out.println("Animal not found: " + name);
        }
    }

    public void setAnimalOutside(String name, boolean outside) {
        Animal animal = findAnimalByName(name);
        if (animal != null) {
            animal.setOutside(outside);
        } else {
            System.out.println("Animal not found: " + name);
        }
    }

    public String collectProductFromAnimal(String name) {
        Animal animal = findAnimalByName(name);
        if (animal != null) {
            return animal.collectProduct();
        } else {
            System.out.println("Animal not found: " + name);
            return null;
        }
    }

    public void setAnimalFriendship(String name, int friendship) {
        Animal animal = findAnimalByName(name);
        if (animal != null) {
            animal.setFriendship(friendship);
            System.out.println("Set " + name + "'s friendship to " + friendship);
        } else {
            System.out.println("Animal not found: " + name);
        }
    }

    public void printAnimalsStatus() {
        System.out.println("=== Animals Status ===");
        for (Animal animal : animals) {
            System.out.println(animal.getName() + " (" + animal.getType() + ") - Friendship: " + animal.getFriendship() +
                ", Fed today: " + animal.isFedToday() + ", Petted today: " + animal.isPetToday() +
                ", Outside: " + animal.isOutside() + ", Has product: " + animal.hasProduct());
        }
    }

    public void newDay() {
        for (Animal animal : animals) {
            animal.resetDailyStatus();
        }
        System.out.println("A new day has started for all animals.");
    }

    private Animal findAnimalByName(String name) {
        for (Animal animal : animals) {
            if (animal.getName().equalsIgnoreCase(name)) {
                return animal;
            }
        }
        return null;
    }

    private boolean isValidAnimalType(String type) {
        String lowerType = type.toLowerCase();
        return lowerType.equals("cow") || lowerType.equals("chicken") ||
            lowerType.equals("sheep") || lowerType.equals("goat") ||
            lowerType.equals("pig") || lowerType.equals("duck") ||
            lowerType.equals("rabbit") || lowerType.equals("dinosaur");
    }

    public void showBuyMenu() {
        showingBuyMenu = true;
    }

    public void startFeeding(Animal animal) {
        feedingAnimal = animal;
        feedingTime = 0;
        if (feedingController != null) {
    }
    }


    public void startPetting(Animal animal) {
        pettingAnimal = animal;
        pettingTime = 0;
    }

    public Animal getSelectedAnimal() {
        return selectedAnimal;
    }

    public void dispose() {
        for (Texture texture : animalTextures.values()) {
            texture.dispose();
        }
        for (Texture texture : productTextures.values()) {
            texture.dispose();
        }
        if (font != null) {
            font.dispose();
        }
        if (whitePixelTexture != null) {
            whitePixelTexture.dispose();
        }
        if (hayHopperTexture != null) {
            hayHopperTexture.dispose();
        }
        if (hayHopperFullTexture != null) {
            hayHopperFullTexture.dispose();
        }


        if (feedingController != null) feedingController.dispose();

    }


}
