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
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.proj.GameScreen;
import com.proj.Main;
import com.proj.Model.Inventory.Inventory;
import com.proj.Model.Inventory.InventoryItem;
import com.proj.Model.Cooking.FoodItem;
import com.proj.Player;

import java.util.Map;

public class StorageScreen implements Screen {
    private Main game;
    private Stage stage;
    private Skin skin;
    private Player player;
    private Inventory playerInventory;
    private Inventory refrigeratorInventory;
    private Label statusLabel;
    private Table inventoryTable;
    private Table refrigeratorTable;
    private ScrollPane inventoryScrollPane;
    private ScrollPane refrigeratorScrollPane;

    // تعداد ستون‌ها در هر جدول
    private static final int COLUMNS = 6;
    // اندازه هر سلول
    private static final float CELL_SIZE = 80;
    // اندازه آیکون
    private static final float ICON_SIZE = 64;

    public StorageScreen(Main game, Skin skin, Player player) {
        this.game = game;
        this.skin = skin;
        this.player = player;
        this.playerInventory = ((GameScreen)game.getScreen()).inventoryManager.getPlayerInventory();
        this.refrigeratorInventory = ((GameScreen)game.getScreen()).refrigerator.getInventory();

        stage = new Stage(new ScreenViewport());
        createUI();
    }

    private void createUI() {
        Table mainTable = new Table(skin);
        mainTable.setFillParent(true);
        mainTable.pad(20);

        // عنوان
        Label titleLabel = new Label("Storage System", skin, "title");
        mainTable.add(titleLabel).colspan(2).pad(10).row();

        // توضیحات
        Label infoLabel = new Label("Click on items to transfer between inventories", skin);
        mainTable.add(infoLabel).colspan(2).pad(10).row();

        // جدول اصلی برای نمایش هر دو موجودی
        Table contentTable = new Table(skin);

        // بخش کوله‌پشتی
        Table playerSection = new Table(skin);
        playerSection.setBackground(skin.getDrawable("default-pane"));
        playerSection.pad(10);

        Label playerLabel = new Label("Your Inventory", skin, "default-font", "white");
        playerLabel.setAlignment(Align.center);
        playerSection.add(playerLabel).expandX().fillX().padBottom(10).row();

        inventoryTable = new Table(skin);
        updateInventoryTable();

        inventoryScrollPane = new ScrollPane(inventoryTable, skin);
        inventoryScrollPane.setFadeScrollBars(false);
        playerSection.add(inventoryScrollPane).expand().fill().row();

        // بخش یخچال
        Table fridgeSection = new Table(skin);
        fridgeSection.setBackground(skin.getDrawable("default-pane"));
        fridgeSection.pad(10);

        Label fridgeLabel = new Label("Refrigerator", skin, "default-font", "white");
        fridgeLabel.setAlignment(Align.center);
        fridgeSection.add(fridgeLabel).expandX().fillX().padBottom(10).row();

        refrigeratorTable = new Table(skin);
        updateRefrigeratorTable();

        refrigeratorScrollPane = new ScrollPane(refrigeratorTable, skin);
        refrigeratorScrollPane.setFadeScrollBars(false);
        fridgeSection.add(refrigeratorScrollPane).expand().fill().row();

        // اضافه کردن هر دو بخش به جدول اصلی
        contentTable.add(playerSection).expand().fill().pad(10);
        contentTable.add(fridgeSection).expand().fill().pad(10);

        mainTable.add(contentTable).expand().fill().colspan(2).row();

        // نمایش وضعیت
        statusLabel = new Label("", skin);
        statusLabel.setAlignment(Align.center);
        mainTable.add(statusLabel).colspan(2).pad(10).row();

        // دکمه بستن
        TextButton closeButton = new TextButton("Close", skin);
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(game.getGameScreen());
                dispose();
            }
        });
        mainTable.add(closeButton).colspan(2).pad(10).right();

        stage.addActor(mainTable);
    }

    private void updateInventoryTable() {
        inventoryTable.clear();
        createItemGrid(inventoryTable, playerInventory, true);
    }

    private void updateRefrigeratorTable() {
        refrigeratorTable.clear();
        createItemGrid(refrigeratorTable, refrigeratorInventory, false);
    }

    private void createItemGrid(Table table, Inventory inventory, boolean isPlayerInventory) {
        int index = 0;

        for (int i = 0; i < inventory.getCapacity(); i++) {
            final int slotIndex = i;
            InventoryItem item = inventory.getItem(slotIndex);

            Table cell = new Table(skin);
            cell.setBackground(skin.getDrawable("default-round"));
            cell.pad(5);

            if (item != null) {
                // نمایش آیتم
                Image itemImage = new Image(item.getTexture());
                itemImage.setScaling(Scaling.fit);
                cell.add(itemImage).size(ICON_SIZE).row();

                // نمایش نام آیتم
                Label nameLabel = new Label(item.getName(), skin, "small");
                nameLabel.setWrap(true);
                nameLabel.setAlignment(Align.center);
                cell.add(nameLabel).width(CELL_SIZE - 10).padTop(3).row();

                // نمایش تعداد
                Label quantityLabel = new Label("x" + item.getQuantity(), skin);
                cell.add(quantityLabel).padTop(3);

                // اضافه کردن رویداد کلیک
                cell.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        if (isPlayerInventory) {
                            transferItem(playerInventory, refrigeratorInventory, slotIndex);
                        } else {
                            transferItem(refrigeratorInventory, playerInventory, slotIndex);
                        }
                    }
                });

                // رنگ متفاوت برای آیتم‌های غذایی
                if (item instanceof FoodItem) {
                    cell.setColor(new Color(1, 0.9f, 0.9f, 1));
                }
            } else {
                // سلول خالی
                cell.add().size(ICON_SIZE);
            }

            table.add(cell).size(CELL_SIZE).pad(5);

            // ایجاد سطر جدید بعد از هر COLUMNS ستون
            index++;
            if (index % COLUMNS == 0) {
                table.row();
            }
        }
    }

    private void transferItem(Inventory source, Inventory destination, int slotIndex) {
        InventoryItem itemToTransfer = source.getItem(slotIndex);

        if (itemToTransfer == null) {
            statusLabel.setText("No item in this slot!");
            statusLabel.setColor(Color.RED);
            return;
        }

        InventoryItem transferCopy = new InventoryItem(itemToTransfer.getId(), itemToTransfer.getName(),
            itemToTransfer.getTexture(), itemToTransfer.isStackable(), itemToTransfer.getMaxStackSize()) {
            @Override
            public void use() {
                // متد خالی
            }
        };
        transferCopy.setQuantity(1);

        // انتقال آیتم
        if (destination.addItem(transferCopy)) {
            // کاهش تعداد از مبدا
            itemToTransfer.decreaseQuantity(1);

            // اگر تعداد به صفر رسید، آیتم را حذف کن
            if (itemToTransfer.getQuantity() <= 0) {
                source.removeItem(slotIndex);
            }

            // نمایش پیام موفقیت
            statusLabel.setText(transferCopy.getName() + " transferred successfully!");
            statusLabel.setColor(Color.GREEN);

            // به‌روزرسانی جداول
            updateInventoryTable();
            updateRefrigeratorTable();
        } else {
            // نمایش پیام خطا
            statusLabel.setText("Destination inventory is full!");
            statusLabel.setColor(Color.RED);
        }
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();

        // بستن صفحه با کلید Escape
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
