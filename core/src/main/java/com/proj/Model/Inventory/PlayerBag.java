package com.proj.Model.Inventory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.proj.Player;

public class PlayerBag {
    private Inventory inventory;
    private Player player;
    private boolean isOpen = false;
    private boolean isVisible = true;

    private Texture bagTexture;

    private float scale = 0.7f;
    private float offsetX = -150;
    private float offsetY = -150; // تنظیم شده به -150

    private float toolbarX;
    private float toolbarY;



    public PlayerBag(Player player, Inventory inventory) {
        this.player = player;
        this.inventory = inventory;

        toolbarX = 10;
        toolbarY = Gdx.graphics.getHeight() - 42;

        try {
            bagTexture = new Texture(Gdx.files.internal("items/Inventory_Parts.png"));

            if (bagTexture == null) {
                Gdx.app.error("PlayerBag", "Failed to load bag texture");
            } else {
                Gdx.app.log("PlayerBag", "Textures loaded successfully");
            }
        } catch (Exception e) {
            Gdx.app.error("PlayerBag", "Failed to load textures", e);
            e.printStackTrace();
        }
    }

    public void update(float delta) {
    }

    public void render(SpriteBatch batch, OrthographicCamera camera) {
        try {
            if (!isVisible) return;

            if (!isOpen) {
                InventoryItem selectedItem = inventory.getSelectedItem();
                if (selectedItem != null && selectedItem.getTexture() != null) {
                    renderToolInHand(batch, selectedItem);
                }
                return;
            }

            if (bagTexture == null) {
                Gdx.app.error("PlayerBag", "Bag texture is null");
                return;
            }

            float bagWidth = bagTexture.getWidth() * scale;
            float bagHeight = bagTexture.getHeight() * scale;

            float bagX = player.getPosition().x + offsetX;
            float bagY = player.getPosition().y + offsetY;

            batch.draw(
                bagTexture,
                bagX,
                bagY,
                bagWidth,
                bagHeight
            );

            checkToolbarClick(camera);
        } catch (Exception e) {
            Gdx.app.error("PlayerBag", "Error in render", e);
            e.printStackTrace();
        }
    }

    private void renderToolInHand(SpriteBatch batch, InventoryItem item) {
        try {
            if (item == null || item.getTexture() == null) return;

            float x = player.getPosition().x;
            float y = player.getPosition().y;

            switch (player.getDirection()) {
                case UP:
                    x += 16;
                    y += 20;
                    break;
                case DOWN:
                    x -= 16;
                    y -= 16;
                    break;
                case LEFT:
                    x -= 24;
                    break;
                case RIGHT:
                    x += 24;
                    break;
            }

            batch.draw(item.getTexture(), x, y, 32, 32);

        } catch (Exception e) {
            Gdx.app.error("PlayerBag", "Error in renderToolInHand", e);
            e.printStackTrace();
        }
    }

    private void checkToolbarClick(OrthographicCamera camera) {
        if (Gdx.input.justTouched()) {
            try {
                Vector3 touchPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
                camera.unproject(touchPos);

                Gdx.app.log("PlayerBag", "Mouse clicked at: " + touchPos.x + ", " + touchPos.y);

                float bagX = player.getPosition().x + offsetX;
                float bagY = player.getPosition().y + offsetY;


            } catch (Exception e) {
                Gdx.app.error("PlayerBag", "Error in checkToolbarClick", e);
                e.printStackTrace();
            }
        }
    }

    public void selectNoTool() {
        for (int i = 0; i < inventory.getCapacity(); i++) {
            if (inventory.getItem(i) == null) {
                inventory.selectSlot(i);
                Gdx.app.log("PlayerBag", "Selected empty slot as 'no tool'");
                return;
            }
        }

        inventory.selectSlot(0);
        Gdx.app.log("PlayerBag", "No empty slot found, selected slot 0");
    }

    public void setVisible(boolean visible) {
        this.isVisible = visible;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void toggleOpen() {
        isOpen = !isOpen;
        Gdx.app.log("PlayerBag", "Toggled inventory: " + (isOpen ? "open" : "closed"));
    }

    public void dispose() {
        try {
            if (bagTexture != null) {
                bagTexture.dispose();
            }
        } catch (Exception e) {
            Gdx.app.error("PlayerBag", "Error in dispose", e);
        }
    }

    public void setScale(float scale) {
        this.scale = scale;
    }
}
