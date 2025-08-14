package com.proj.Model.Inventory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class StardewInventoryUI extends InventoryUI {
    private Inventory inventory;
    private BitmapFont font;
    private int x, y;
    private int slotSize = 36;
    private int padding = 2;

    private TextureRegion backgroundTexture;
    private TextureRegion[] slotTextures;
    private TextureRegion selectedSlotTexture;

    public StardewInventoryUI(Inventory inventory, int x, int y,
                              TextureRegion backgroundTexture,
                              TextureRegion[] slotTextures,
                              TextureRegion selectedSlotTexture) {
        super(inventory, x, y);
        this.inventory = inventory;
        this.x = x;
        this.y = y;
        this.backgroundTexture = backgroundTexture;
        this.slotTextures = slotTextures;
        this.selectedSlotTexture = selectedSlotTexture;

        font = new BitmapFont();
        font.setColor(Color.WHITE);
    }

    @Override
    public void render(SpriteBatch batch) {
        if (!inventory.isVisible()) return;

        batch.draw(backgroundTexture, x, y - backgroundTexture.getRegionHeight());

        for (int i = 0; i < inventory.getCapacity(); i++) {
            int slotX = x + 16 + (i % 12) * (slotSize + padding);
            int slotY = y - 16 - (i / 12) * (slotSize + padding);

            batch.draw(slotTextures[i % slotTextures.length], slotX, slotY);

            if (i == inventory.getSelectedSlot()) {
                batch.draw(selectedSlotTexture, slotX, slotY);
            }

            InventoryItem item = inventory.getItem(i);
            if (item != null) {
                TextureRegion texture = item.getTexture();

                float itemX = slotX + 2;
                float itemY = slotY + 2;

                if (item instanceof Tool && ((Tool) item).isInUse() && i == inventory.getSelectedSlot()) {
                    float progress = ((Tool) item).getUseAnimationProgress();
                    float swingAngle = (float) Math.sin(progress * Math.PI) * 15f;
                    batch.draw(texture,
                        itemX, itemY,
                        (slotSize - 4) / 2, (slotSize - 4) / 2,
                        slotSize - 4, slotSize - 4,
                        1, 1,
                        swingAngle);
                } else {
                    batch.draw(texture, itemX, itemY, slotSize - 4, slotSize - 4);
                }

                if (item.isStackable() && item.getQuantity() > 1) {
                    font.draw(batch, String.valueOf(item.getQuantity()),
                        slotX + slotSize - 10, slotY + 10);
                }
            }
        }
    }

    @Override
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

    @Override
    public int getSlotAt(float screenX, float screenY) {
        float worldY = Gdx.graphics.getHeight() - screenY;

        for (int i = 0; i < inventory.getCapacity(); i++) {
            int slotX = x + 16 + (i % 12) * (slotSize + padding);
            int slotY = y - 16 - (i / 12) * (slotSize + padding);

            if (screenX >= slotX && screenX < slotX + slotSize &&
                worldY >= slotY && worldY < slotY + slotSize) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void dispose() {
        font.dispose();
    }



}
