package com.proj.View;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.proj.Enums.Shop;
import com.proj.Main;
import com.proj.Model.GameAssetManager;
import com.proj.Model.Inventory.InventoryItem;
import com.proj.Model.Inventory.InventoryManager;
import com.proj.Player;
import com.proj.managers.PriceManager;
import com.proj.managers.ShopManager;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.proj.GameScreen;
import com.proj.map.farmName;

import java.util.Collections;

public class ShopScreen implements Screen {
    private Stage stage;
    private final Player player;
    private final InventoryManager inventoryManager;
    private final ShopManager shopManager;
    private  Shop currentShop;
    private final Screen previousScreen;
    private final Main game;
    private final Skin skin;
    private Label shopTitleLabel; // Add for dynamic title
    private SelectBox<Shop> shopSelectBox; //
    private TextButton backButton;

    // UI Components
    private Table mainTable;
    private Table shopItemsTable;
    private Table playerItemsTable;
    private Label moneyLabel;
    private TextButton buySellToggle;
    private boolean isBuyMode = true;
    private final Texture backgroundTexture;
    private final SpriteBatch batch;

    public ShopScreen(Main game, Player player, InventoryManager inventoryManager,
                      ShopManager shopManager, Shop initialShop, Screen previousScreen) {
        Gdx.app.log("ShopScreen", "Constructor started");
        this.game = game;
        this.player = player;
        this.inventoryManager = inventoryManager;
        this.shopManager = shopManager;
        this.currentShop = currentShop;
        this.previousScreen = previousScreen;
        this.currentShop = initialShop;

        // Initialize graphics components FIRST
        batch = new SpriteBatch();
        backgroundTexture = new Texture(Gdx.files.internal("shopBackground.jpg"));
        Gdx.app.log("ShopScreen", "Background texture loaded");

        // Then initialize UI components
        stage = new Stage(new ScreenViewport());
        skin = GameAssetManager.getGameAssetManager().getSkin();

        setupUI();
        Gdx.app.log("ShopScreen", "Constructor completed");
    }

    private void setupUI() {
        mainTable = new Table();
        mainTable.setFillParent(true);
        stage.addActor(mainTable);

        // Title
        mainTable.add(new Label(currentShop.getManager() + "'s Shop", skin)).colspan(3).padBottom(20).row();

        // Money display
        moneyLabel = new Label("Money: " + player.getMoney() + "g", skin);
        mainTable.add(moneyLabel).colspan(3).padBottom(10).row();

        // Toggle button
        buySellToggle = new TextButton("Buy Mode", skin);
        buySellToggle.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                isBuyMode = !isBuyMode;
                buySellToggle.setText(isBuyMode ? "Buy Mode" : "Sell Mode");
                refreshItemsDisplay();
            }
        });
        mainTable.add(buySellToggle).colspan(3).padBottom(15).row();

        // Create tables for shop and player items
        Table contentTable = new Table();
        shopItemsTable = new Table();
        playerItemsTable = new Table();

        // Set up shop items section
        Label shopLabel = new Label("Shop Items", skin);
        ScrollPane shopScroll = new ScrollPane(shopItemsTable, skin);
        shopScroll.setFadeScrollBars(false);

        // Set up player inventory section
        Label playerLabel = new Label("Your Inventory", skin);
        ScrollPane playerScroll = new ScrollPane(playerItemsTable, skin);
        playerScroll.setFadeScrollBars(false);
        shopSelectBox = new SelectBox<>(skin);
        Array<Shop> availableShops = getAvailableShops(); // Implement this method
        shopSelectBox.setItems(availableShops);
        shopSelectBox.setSelected(currentShop);

        shopSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Shop selectedShop = shopSelectBox.getSelected();
                if (selectedShop != null) {
                    currentShop = selectedShop;
                    shopTitleLabel.setText(currentShop.getManager() + "'s Shop");
                    refreshItemsDisplay();
                }
            }
        });

        // Add shop selection to UI
        Table headerTable = new Table();
        headerTable.add(new Label("Select Shop: ", skin)).padRight(10);
        headerTable.add(shopSelectBox).width(250);
        mainTable.add(headerTable).colspan(3).padBottom(15).row();

        // Dynamic shop title
        shopTitleLabel = new Label(currentShop.getManager() + "'s Shop", skin);
        mainTable.add(shopTitleLabel).colspan(3).padBottom(20).row();

        // Add to content table
        contentTable.add(shopLabel).padBottom(5);
        contentTable.add(playerLabel).padBottom(5).row();
        contentTable.add(shopScroll).width(400).height(300).pad(5);
        contentTable.add(playerScroll).width(400).height(300).pad(5).row();

        mainTable.add(contentTable).colspan(3).padBottom(15).row();
        backButton = new TextButton("Back", skin);
        headerTable.add(backButton);

         backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Use the Main's gameScreen reference
                if (game.getGameScreen() != null) {
                    game.setScreen(game.getGameScreen());
                } else {
                    // Fallback to creating new GameScreen if reference is null
                    game.setScreen(new GameScreen(game, farmName.STANDARD));
                }
            }
        });
        // Initial display
        refreshItemsDisplay();
    }

    private Array<Shop> getAvailableShops() {
        Array<Shop> shops = new Array<>();
        // Add all shops to the array using libGDX's addAll method
        shops.addAll(Shop.values());
        return shops;
    }

    private void refreshItemsDisplay() {
        shopItemsTable.clear();
        playerItemsTable.clear();

        if (isBuyMode) {
            // Display shop items for buying
            for (Shop.ShopItem item : currentShop.getItems()) {
                addShopItemRow(item);
            }
        } else {
            // Display player items for selling
            Array<InventoryItem> items = new Array<>();
            for (InventoryItem item : inventoryManager.getPlayerInventory().getItems().values()) {
                if (item != null) {
                    items.add(item);
                }
            }

            for (InventoryItem item : items) {
                String itemName = item.getName();
                int quantity = item.getQuantity();
                Double price = PriceManager.getPrice(itemName);

                if (price != null) {
                    addPlayerItemRow(itemName, quantity, price.intValue());
                }
            }
        }
    }

    private void addShopItemRow(Shop.ShopItem item) {
        Table row = new Table();

        // Item icon (placeholder)
        Image icon = new Image(new TextureRegionDrawable(new TextureRegion(
            item.getIcon() != null ? item.getIcon() : new Texture(Gdx.files.internal("NPCTable/dialogue_icon.png"))
        )));
        row.add(icon).size(32, 32).padRight(10);

        // Item info
        Label nameLabel = new Label(item.getName(), skin);
        Label priceLabel = new Label(item.getPrice() + "g", skin);
        Label stockLabel = new Label(item.getStockLimit() > 0 ? "Stock: " + item.getStockLimit() : "Unlimited", skin);

        Table infoTable = new Table();
        infoTable.add(nameLabel).left().row();
        infoTable.add(priceLabel).left().row();
        infoTable.add(stockLabel).left();

        row.add(infoTable).width(200);

        // Buy button
        TextButton buyButton = new TextButton("Buy", skin);
        buyButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Pass currentShop to buyItem
                if (shopManager.buyItem(currentShop, item.getName(), 1)) {
                    moneyLabel.setText("Money: " + player.getMoney() + "g");
                    refreshItemsDisplay();
                } else {
                    // ... error handling ...
                }
            }
        });
        row.add(buyButton).padLeft(10);

        shopItemsTable.add(row).pad(5).left().fillX();
        shopItemsTable.row();
    }

    private void addPlayerItemRow(String itemName, int quantity, int price) {
        Table row = new Table();

        // Item icon (placeholder)
        Image icon = new Image(new TextureRegionDrawable(new TextureRegion(
            new Texture(Gdx.files.internal("NPCTable/dialogue_icon.png"))
        )));
        row.add(icon).size(32, 32).padRight(10);

        // Item info
        Label nameLabel = new Label(itemName, skin);
        Label quantityLabel = new Label("Qty: " + quantity, skin);
        Label priceLabel = new Label(price + "g", skin);

        Table infoTable = new Table();
        infoTable.add(nameLabel).left().row();
        infoTable.add(quantityLabel).left().row();
        infoTable.add(priceLabel).left();

        row.add(infoTable).width(200);

        // Sell button
        TextButton sellButton = new TextButton("Sell", skin);
        sellButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (inventoryManager.removeItem(itemName, 1)) {
                    player.addMoney(price);
                    moneyLabel.setText("Money: " + player.getMoney() + "g");
                    refreshItemsDisplay();
                }
            }
        });
        row.add(sellButton).padLeft(10);

        playerItemsTable.add(row).pad(5).left().fillX();
        playerItemsTable.row();
    }

    private void exitShop() {
        Gdx.app.postRunnable(() -> {
            dispose();
            game.setScreen(previousScreen);

            if (previousScreen != null) {
                previousScreen.show();
            }
        });
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        // Clear screen with transparent color
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Draw background
        batch.begin();
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();

        // Draw UI on top
        stage.act(delta);
        stage.draw();
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
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        Gdx.app.log("ShopScreen", "Disposing resources");
        stage.dispose();
        backgroundTexture.dispose();
        batch.dispose();
    }
}
