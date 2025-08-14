package com.proj.Model.Inventory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class InventoryUI {
    private Inventory inventory;
    private BitmapFont font;
    private int x, y;
    private int slotSize = 40;
    private int padding = 4;
    private ShapeRenderer shapeRenderer;

    private Color slotBackgroundColor = new Color(0.878f, 0.788f, 0.651f, 1f);
    private Color slotBorderColor = new Color(0.651f, 0.486f, 0.322f, 1f);
    private Color selectedSlotBorderColor = new Color(1f, 0.843f, 0f, 1f);

    private Vector2 toolAnimationOffset = new Vector2();

    public InventoryUI(Inventory inventory, int x, int y) {
        this.inventory = inventory;
        this.x = x;
        this.y = y;

        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
    }

    public void render(SpriteBatch batch) {
        if (!inventory.isVisible()) return;

        batch.end();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (int i = 0; i < inventory.getCapacity(); i++) {
            int slotX = x + (i % 12) * (slotSize + padding);
            int slotY = y - (i / 12) * (slotSize + padding);

            shapeRenderer.setColor(slotBackgroundColor);
            shapeRenderer.rect(slotX, slotY, slotSize, slotSize);

            if (i == inventory.getSelectedSlot()) {
                shapeRenderer.setColor(selectedSlotBorderColor);
            } else {
                shapeRenderer.setColor(slotBorderColor);
            }

            int borderWidth = 2;
            shapeRenderer.rect(slotX, slotY + slotSize - borderWidth, slotSize, borderWidth);
            shapeRenderer.rect(slotX, slotY, slotSize, borderWidth);
            shapeRenderer.rect(slotX, slotY, borderWidth, slotSize);
            shapeRenderer.rect(slotX + slotSize - borderWidth, slotY, borderWidth, slotSize);
        }

        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        batch.begin();

        for (int i = 0; i < inventory.getCapacity(); i++) {
            int slotX = x + (i % 12) * (slotSize + padding);
            int slotY = y - (i / 12) * (slotSize + padding);

            InventoryItem item = inventory.getItem(i);
            if (item != null) {
                TextureRegion texture = item.getTexture();

                float itemX = slotX + 4;
                float itemY = slotY + 4;

                if (item instanceof Tool && ((Tool) item).isInUse() && i == inventory.getSelectedSlot()) {
                    float progress = ((Tool) item).getUseAnimationProgress();
                    float swingAngle = (float) Math.sin(progress * Math.PI) * 15f;
                    batch.draw(texture,
                        itemX, itemY,
                        (slotSize - 8) / 2, (slotSize - 8) / 2,
                        slotSize - 8, slotSize - 8,
                        1, 1,
                        swingAngle);
                } else {
                    batch.draw(texture, itemX, itemY, slotSize - 8, slotSize - 8);
                }

                if (item.isStackable() && item.getQuantity() > 1) {
                    font.draw(batch, String.valueOf(item.getQuantity()),
                        slotX + slotSize - 12, slotY + 12);
                }
            }
        }
    }

    public void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.T)) {
            inventory.toggleVisibility();
        }

        if (!inventory.isVisible()) return;

        if (Gdx.input.justTouched()) {
            int slot = getSlotAt(Gdx.input.getX(), Gdx.input.getY());
            if (slot != -1) {
                inventory.selectSlot(slot);
            }
        }
    }

    public int getSlotAt(float screenX, float screenY) {
        float worldY = Gdx.graphics.getHeight() - screenY;

        for (int i = 0; i < inventory.getCapacity(); i++) {
            int slotX = x + (i % 12) * (slotSize + padding);
            int slotY = y - (i / 12) * (slotSize + padding);

            if (screenX >= slotX && screenX < slotX + slotSize &&
                worldY >= slotY && worldY < slotY + slotSize) {
                return i;
            }
        }
        return -1;
    }

    public void dispose() {
        font.dispose();
        shapeRenderer.dispose();
    }
}
