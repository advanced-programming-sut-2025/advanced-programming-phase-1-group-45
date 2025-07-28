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
    private int slotSize = 36; // Based on the Stardew Valley slot size
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
        // Only render if inventory is visible
        if (!inventory.isVisible()) return;

        // Draw the inventory background
        batch.draw(backgroundTexture, x, y - backgroundTexture.getRegionHeight());

        // Draw inventory slots
        for (int i = 0; i < inventory.getCapacity(); i++) {
            int slotX = x + 16 + (i % 12) * (slotSize + padding);
            int slotY = y - 16 - (i / 12) * (slotSize + padding);

            // Draw the slot background
            batch.draw(slotTextures[i % slotTextures.length], slotX, slotY);

            // Draw the selected slot highlight
            if (i == inventory.getSelectedSlot()) {
                batch.draw(selectedSlotTexture, slotX, slotY);
            }

            // Draw the item in the slot
            InventoryItem item = inventory.getItem(i);
            if (item != null) {
                TextureRegion texture = item.getTexture();

                // Calculate position for item
                float itemX = slotX + 2;
                float itemY = slotY + 2;

                // Apply animation offset if it's a tool in use
                if (item instanceof Tool && ((Tool) item).isInUse() && i == inventory.getSelectedSlot()) {
                    float progress = ((Tool) item).getUseAnimationProgress();
                    // Simple swing animation
                    float swingAngle = (float) Math.sin(progress * Math.PI) * 15f;
                    batch.draw(texture,
                        itemX, itemY,
                        (slotSize - 4) / 2, (slotSize - 4) / 2, // Origin
                        slotSize - 4, slotSize - 4, // Size
                        1, 1, // Scale
                        swingAngle); // Rotation
                } else {
                    // Normal rendering
                    batch.draw(texture, itemX, itemY, slotSize - 4, slotSize - 4);
                }

                // Draw quantity for stackable items
                if (item.isStackable() && item.getQuantity() > 1) {
                    font.draw(batch, String.valueOf(item.getQuantity()),
                        slotX + slotSize - 10, slotY + 10);
                }
            }
        }
    }

    @Override
    public void handleInput() {
        // Toggle inventory visibility with T key
        if (Gdx.input.isKeyJustPressed(Input.Keys.T)) {
            inventory.toggleVisibility();
        }

        // Only handle other inputs if inventory is visible
        if (!inventory.isVisible()) return;

        // Handle mouse clicks to select slots
        if (Gdx.input.justTouched()) {
            int slot = getSlotAt(Gdx.input.getX(), Gdx.input.getY());
            if (slot != -1) {
                inventory.selectSlot(slot);
            }
        }
    }

    @Override
    public int getSlotAt(float screenX, float screenY) {
        // Convert screen coordinates to UI coordinates
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
