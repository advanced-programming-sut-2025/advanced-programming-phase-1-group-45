package com.proj.Model.Cooking;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.proj.GameScreen;
import com.proj.Main;
import com.proj.Model.Inventory.Inventory;
import com.proj.Model.Inventory.InventoryItem;

import java.util.Map;

public class StorageScreen implements Screen {
    private Main game;
    private GameScreen gameScreen;
    private Inventory playerInventory;
    private Inventory refrigeratorInventory;
    private SpriteBatch batch;
    private BitmapFont font;

    public StorageScreen(Main game, GameScreen gameScreen, Inventory playerInventory, Inventory refrigeratorInventory) {
        this.game = game;
        this.gameScreen = gameScreen;
        this.playerInventory = playerInventory;
        this.refrigeratorInventory = refrigeratorInventory;
        this.batch = new SpriteBatch();
        this.font = new BitmapFont();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        // Draw title
        font.draw(batch, "Storage System", 20, Gdx.graphics.getHeight() - 20);
        font.draw(batch, "Press ESC to return", 20, Gdx.graphics.getHeight() - 40);

        // Draw player inventory
        font.draw(batch, "Player Inventory:", 20, Gdx.graphics.getHeight() - 80);
        int y = Gdx.graphics.getHeight() - 100;
        for (Map.Entry<Integer, InventoryItem> entry : playerInventory.getItems().entrySet()) {
            InventoryItem item = entry.getValue();
            if (item != null) {
                font.draw(batch, item.getName() + " x" + item.getQuantity(), 20, y);
                y -= 20;
            }
        }

        // Draw refrigerator inventory
        font.draw(batch, "Refrigerator:", 20, Gdx.graphics.getHeight() / 2);
        y = Gdx.graphics.getHeight() / 2 - 20;
        for (Map.Entry<Integer, InventoryItem> entry : refrigeratorInventory.getItems().entrySet()) {
            InventoryItem item = entry.getValue();
            if (item != null) {
                font.draw(batch, item.getName() + " x" + item.getQuantity(), 20, y);
                y -= 20;
            }
        }

        batch.end();

        // Handle input
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(gameScreen);
            dispose();
        }
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void show() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
    }
}
