package com.proj.Model.Cooking;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.Timer;
import com.proj.Main;
import com.proj.Model.Inventory.Inventory;
import com.proj.Model.Inventory.InventoryItem;
import com.proj.Model.Inventory.InventoryManager;
import com.proj.Player;

import java.util.List;
import java.util.Map;

public class CookingScreen implements Screen {
    private Main game;
    private Stage stage;
    private Skin skin;
    private CookingManager cookingManager;
    private InventoryManager inventoryManager;
    private Player player;
    private Table root;
    private Label statusLabel;

    public CookingScreen(Main game, Skin skin, InventoryManager inventoryManager, CookingManager cookingManager, Player player) {
        this.game = game;
        this.skin = skin != null ? skin : new Skin(Gdx.files.internal("uiskin.json"));
        this.inventoryManager = inventoryManager;
        this.cookingManager = cookingManager;
        this.player = player;
        stage = new Stage(new ScreenViewport());
        createUI();
    }

    private void createUI() {
        stage.clear();
        root = new Table(skin);
        root.setFillParent(true);
        root.pad(12);
        stage.addActor(root);

        Label title = new Label("Cooking", skin);
        title.setFontScale(1.2f);
        title.setColor(Color.WHITE);
        root.add(title).colspan(3).left().row();

        statusLabel = new Label("", skin);
        statusLabel.setColor(Color.WHITE);
        root.add(statusLabel).colspan(3).left().padTop(6).row();

        Table listTable = new Table(skin);
        ScrollPane scroll = new ScrollPane(listTable, skin);
        scroll.setFadeScrollBars(false);
        root.add(scroll).colspan(3).expand().fill().height(360).row();

        List<CookingRecipe> recipes = cookingManager != null ? cookingManager.getAvailableRecipes() : null;
        if (recipes == null || recipes.isEmpty()) {
            listTable.add(new Label("No recipes available.", skin)).row();
        } else {
            for (CookingRecipe recipe : recipes) {
                Table entry = new Table(skin);
                entry.setBackground(skin.newDrawable("white", new Color(0,0,0,0.15f)));
                entry.left().pad(8).padBottom(10);

                TextureRegion resTex = null;
                try { resTex = recipe.getResultItem() != null ? recipe.getResultItem().getTexture() : null; } catch (Throwable ignored){}
                Image resImg = resTex != null ? new Image(resTex) : new Image();
                resImg.setSize(48,48);
                entry.add(resImg).size(48,48).left().padRight(8);

                Table mid = new Table(skin);
                Label name = new Label(recipe.getRecipeName(), skin);
                name.setColor(recipe.isLearned() ? Color.WHITE : Color.LIGHT_GRAY);
                mid.add(name).left().row();

                Table ingrRow = new Table(skin);
                for (Map.Entry<String,Integer> ing : recipe.getIngredients().entrySet()) {
                    String id = ing.getKey();
                    int qty = ing.getValue();

                    TextureRegion ingTex = null;
                    try { ingTex = com.proj.Model.GameAssetManager.getGameAssetManager().getIngredientTexture(id); } catch (Throwable ignored){}
                    Image ingImg = ingTex != null ? new Image(ingTex) : new Image();
                    ingImg.setSize(20,20);

                    int available = countAvailable(id);
                    Label q = new Label(available + "/" + qty, skin);
                    q.setColor(available >= qty ? Color.GREEN : Color.RED);

                    Table single = new Table(skin);
                    single.add(ingImg).size(20,20).padRight(4);
                    single.add(new Label(id, skin)).padRight(6);
                    single.add(q);
                    ingrRow.add(single).padRight(8);
                }
                mid.add(ingrRow).left().row();

                entry.add(mid).expandX().fillX().left().padRight(8);

                Table actions = new Table(skin);
                final TextButton actionBtn = new TextButton(recipe.isLearned() ? "Cook" : "Learn", skin);
                actions.add(actionBtn).row();

                final ProgressBar progress = new ProgressBar(0f, 1f, 0.01f, false, skin);
                progress.setValue(0f);
                progress.setVisible(false);
                actions.add(progress).width(120).padTop(6).row();

                if (!recipe.isLearned()) {
                    Label lock = new Label("Locked", skin);
                    lock.setColor(Color.GRAY);
                    actions.add(lock).row();
                }

                actionBtn.addListener(new ClickListener(){
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        if (!recipe.isLearned()) {
                            doLearn(recipe);
                        } else {
                            doCookWithAnimation(recipe, actionBtn, progress);
                        }
                    }
                });

                entry.add(actions).right();
                listTable.add(entry).expandX().fillX().padBottom(8).row();
            }
        }

        Table hints = new Table(skin);
        hints.add(new Label("Tips: Press Esc to close. Cook uses " + CookingManager.ENERGY_COST_PER_COOK + " energy.", skin)).left();
        root.add(hints).colspan(3).left().padTop(8).row();
    }

    private int countAvailable(String ingredientId) {
        int total = 0;
        try {
            if (inventoryManager != null && inventoryManager.getPlayerInventory() != null) {
                for (InventoryItem it : inventoryManager.getPlayerInventory().getItems().values()) {
                    if (it != null && it.getId().equals(ingredientId)) total += it.getQuantity();
                }
            }
            if (cookingManager != null && cookingManager.getRefrigeratorInventory() != null) {
                for (InventoryItem it : cookingManager.getRefrigeratorInventory().getItems().values()) {
                    if (it != null && it.getId().equals(ingredientId)) total += it.getQuantity();
                }
            }
        } catch (Throwable t) {
            Gdx.app.error("CookingScreen", "countAvailable", t);
        }
        return total;
    }

    private void doLearn(CookingRecipe recipe) {
        try {
            if (cookingManager == null) { setStatus("Cannot learn: no manager", Color.RED); return; }
            cookingManager.learnRecipe(recipe.getRecipeName());
            setStatus(recipe.getRecipeName() + " learned!", Color.CYAN);
            createUI();
        } catch (Throwable t) {
            Gdx.app.error("CookingScreen", "doLearn", t);
            setStatus("Error learning recipe", Color.RED);
        }
    }

    private void doCookWithAnimation(CookingRecipe recipe, TextButton btn, ProgressBar progress) {
        try {
            if (cookingManager == null || player == null) { setStatus("Cannot cook: missing data", Color.RED); return; }
            if (!cookingManager.canCook(recipe)) { setStatus("Not enough ingredients", Color.RED); return; }
            if (player.getCurrentEnergy() < CookingManager.ENERGY_COST_PER_COOK) { setStatus("Not enough energy", Color.RED); return; }

            btn.setDisabled(true);
            progress.setVisible(true);
            progress.setValue(0f);
            setStatus("Cooking " + recipe.getRecipeName() + "...", Color.ORANGE);

            final float totalTime = 1.5f;
            final float[] elapsed = {0f};
            Timer.schedule(new Timer.Task(){
                @Override
                public void run() {
                    elapsed[0] += 0.05f;
                    float v = Math.min(1f, elapsed[0] / totalTime);
                    progress.setValue(v);
                    if (v >= 1f) {
                        this.cancel();
                        boolean ok = cookingManager.cook(recipe.getRecipeName(), player);
                        if (ok) {
                            setStatus(recipe.getRecipeName() + " cooked!", Color.GREEN);
                            com.proj.Model.Cooking.FoodItem res = recipe.getResultItem();
                            if (res != null) {
                                TextureRegion t = res.getTexture();
                                if (inventoryManager != null && inventoryManager.getPlayerInventory() != null) {
                                    inventoryManager.getPlayerInventory().addItem(res);
                                }
                                player.startEatingAnimation(t);
                                player.restoreEnergy(res.getEnergyRestored());
                            }
                        } else {
                            setStatus("Failed to cook " + recipe.getRecipeName(), Color.RED);
                        }
                        Timer.schedule(new Timer.Task(){
                            @Override public void run() {
                                btn.setDisabled(false);
                                progress.setVisible(false);
                                progress.setValue(0f);
                                createUI();
                            }
                        }, 0.5f);
                    }
                }
            }, 0f, 0.05f);
        } catch (Throwable t) {
            Gdx.app.error("CookingScreen", "doCookWithAnimation", t);
            setStatus("Error cooking", Color.RED);
            btn.setDisabled(false);
            progress.setVisible(false);
        }
    }

    private void setStatus(String txt, Color color) {
        statusLabel.setText(txt);
        statusLabel.setColor(color);
    }

    @Override
    public void show() { Gdx.input.setInputProcessor(stage); }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.06f, 0.06f, 0.07f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Math.min(delta, 1/30f));
        stage.draw();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            if (game != null) game.setScreen(game.getGameScreen());
            dispose();
        }
    }

    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() { if (stage != null) stage.dispose(); }
}

