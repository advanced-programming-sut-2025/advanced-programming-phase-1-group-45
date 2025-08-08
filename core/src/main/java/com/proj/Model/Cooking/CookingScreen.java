// CookingScreen.java
package com.proj.Model.Cooking;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.proj.Main;
import com.proj.Model.Cooking.CookingManager;
import com.proj.Model.Inventory.InventoryManager;
import com.proj.Model.Cooking.CookingRecipe;
import com.proj.Model.GameAssetManager;
import com.proj.Model.Inventory.InventoryItem;
import com.proj.Player;

import java.util.Map;

public class CookingScreen implements Screen {
    private Main game;
    private Stage stage;
    private Skin skin;
    private CookingManager cookingManager;
    private InventoryManager inventoryManager;
    private Player player;
    private Table mainTable;
    private Label statusLabel;

    public CookingScreen(Main game, Skin skin, InventoryManager inventoryManager, CookingManager cookingManager, Player player) {
        this.game = game;
        this.skin = skin;
        this.inventoryManager = inventoryManager;
        this.cookingManager = cookingManager;
        this.player = player;

        stage = new Stage(new ScreenViewport());

        createUI();
    }

    private void createUI() {
        mainTable = new Table(skin);
        mainTable.setFillParent(true);
        mainTable.pad(10);

        Label titleLabel = new Label("Cooking Recipes", skin, "title");
        mainTable.add(titleLabel).padBottom(20).colspan(2).row();

        Table recipesTable = new Table(skin);
        recipesTable.top().left().pad(10);
        recipesTable.defaults().pad(5);

        ScrollPane scrollPane = new ScrollPane(recipesTable, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);

        mainTable.add(scrollPane).expand().fill().colspan(2).row();

        statusLabel = new Label("", skin);
        statusLabel.setAlignment(Align.center);
        mainTable.add(statusLabel).padTop(10).colspan(2).row();

        TextButton closeButton = new TextButton("Close", skin);
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(game.getGameScreen());
                dispose();
            }
        });
        mainTable.add(closeButton).expandX().right().padTop(20).colspan(2);

        stage.addActor(mainTable);
        updateRecipeDisplay(recipesTable);
    }

    private void updateRecipeDisplay(Table recipesTable) {
        recipesTable.clear();
        Array<CookingRecipe> recipes = new Array<>((Array) cookingManager.getAvailableRecipes());

        if (recipes.size == 0) {
            recipesTable.add(new Label("No recipes available.", skin)).
// CookingScreen.java (continued)
    row();
            return;
        }

        float iconSize = 64;

        for (CookingRecipe recipe : recipes) {
            Table recipeEntry = new Table(skin);
            recipeEntry.setBackground("default-round");
            recipeEntry.pad(10);

            Label recipeNameLabel = new Label(recipe.getRecipeName(), skin, "default-font", "white");
            if (!recipe.isLearned()) {
                recipeNameLabel.setColor(Color.GRAY);
                recipeNameLabel.setText(recipe.getRecipeName() + " (Locked)");
            }
            recipeEntry.add(recipeNameLabel).left().row();

            Table ingredientsTable = new Table(skin);
            ingredientsTable.defaults().pad(2);
            for (Map.Entry<String, Integer> entry : recipe.getIngredients().entrySet()) {
                String ingredientId = entry.getKey();
                int requiredQuantity = entry.getValue();

                Table ingredientCell = new Table();
                Image ingredientIcon = new Image(GameAssetManager.getGameAssetManager().getIngredientTexture(ingredientId));
                Label ingredientLabel = new Label(requiredQuantity + "x " + ingredientId, skin);

                ingredientCell.add(ingredientIcon).size(iconSize / 2).padRight(5);
                ingredientCell.add(ingredientLabel).left();

                ingredientsTable.add(ingredientCell).left();

                int currentQuantity = 0;
                for (InventoryItem item : inventoryManager.getPlayerInventory().getItems().values()) {
                    if (item != null && item.getId().equals(ingredientId)) {
                        currentQuantity += item.getQuantity();
                    }
                }
                for (InventoryItem item : game.getRefrigerator().getInventory().getItems().values()) {
                    if (item != null && item.getId().equals(ingredientId)) {
                        currentQuantity += item.getQuantity();
                    }
                }

                Label quantityLabel = new Label("(" + currentQuantity + "/" + requiredQuantity + ")", skin);
                if (currentQuantity < requiredQuantity) {
                    quantityLabel.setColor(Color.RED);
                } else {
                    quantityLabel.setColor(Color.GREEN);
                }
                ingredientsTable.add(quantityLabel).left();
            }
            recipeEntry.add(ingredientsTable).left().padLeft(10).row();

            Table resultTable = new Table(skin);
            resultTable.defaults().pad(2);
            Image resultIcon = new Image(recipe.getResultItem().getTexture());
            Label resultLabel = new Label("Result: " + recipe.getResultItem().getName(), skin);
            resultTable.add(resultIcon).size(iconSize).padRight(10);
            resultTable.add(resultLabel).left();
            recipeEntry.add(resultTable).left().padTop(10).row();

            TextButton actionButton;
            if (recipe.isLearned()) {
                actionButton = new TextButton("Cook", skin);
                actionButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        if (cookingManager.cook(recipe.getRecipeName(), player)) {
                            statusLabel.setText(recipe.getRecipeName() + " cooked!");
                            statusLabel.setColor(Color.GREEN);
                        } else {
                            statusLabel.setText("Failed to cook " + recipe.getRecipeName() + ".");
                            statusLabel.setColor(Color.RED);
                        }
                        updateRecipeDisplay(recipesTable);
                    }
                });
                boolean canCook = cookingManager.isRecipeLearned(recipe.getRecipeName()) && cookingManager.canCook(recipe);
                actionButton.setDisabled(!canCook || player.getCurrentEnergy() < CookingManager.ENERGY_COST_PER_COOK);
                if (actionButton.isDisabled()) actionButton.setColor(Color.GRAY); else actionButton.setColor(Color.WHITE);

            } else {
                actionButton = new TextButton("Learn", skin);
                actionButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        cookingManager.learnRecipe(recipe.getRecipeName());
                        statusLabel.setText(recipe.getRecipeName() + " learned!");
                        statusLabel.setColor(Color.BLUE);
                        updateRecipeDisplay(recipesTable);
                    }
                });
            }
            recipeEntry.add(actionButton).expandX().right().padTop(10).row();

            recipesTable.add(recipeEntry).expandX().fillX().padBottom(15).row();
        }
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        updateRecipeDisplay((Table) ((ScrollPane) mainTable.getChildren().get(1)).getActor());
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(game.getGameScreen());
            dispose();
        }
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
    }
}
