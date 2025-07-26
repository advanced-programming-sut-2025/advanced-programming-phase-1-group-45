package com.proj.Model.Inventory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.proj.Player;

import java.util.HashMap;
import java.util.Map;

public class InventoryManager {
    private static InventoryManager instance;
    private Map<String, TextureRegion> itemTextures;
    private Inventory playerInventory;
    private InventoryUI inventoryUI;

    // Texture atlas for inventory UI components
    private Texture inventoryPartsTexture;
    private TextureRegion[] inventorySlots;
    private TextureRegion inventoryBackground;
    private TextureRegion selectedSlotHighlight;

    private InventoryManager() {
        itemTextures = new HashMap<>();
        playerInventory = new Inventory(24); // Start with medium backpack
        loadTextures();
        loadInventoryUI();
        createInitialTools();
    }

    public static InventoryManager getInstance() {
        if (instance == null) {
            instance = new InventoryManager();
        }
        return instance;
    }

    private void loadTextures() {
        // Load tool textures from the assets provided in the image
        // Assuming you've placed these files in the correct directory structure

        itemTextures.put("hoe_basic", new TextureRegion(new Texture(Gdx.files.internal("items/Hoe/Hoe.png"))));
        itemTextures.put("pickaxe_basic", new TextureRegion(new Texture(Gdx.files.internal("items/PickAxe/starter.png")))); // Uncommented this line
        itemTextures.put("axe_basic", new TextureRegion(new Texture(Gdx.files.internal("items/Axe/Axe.png"))));
        itemTextures.put("watering_can_basic", new TextureRegion(new Texture(Gdx.files.internal("items/Watering_Can/Watering_Can.png"))));
        itemTextures.put("fishing_rod_basic", new TextureRegion(new Texture(Gdx.files.internal("items/Fishing_Pole/Training_Rod.png"))));
        itemTextures.put("scythe_basic", new TextureRegion(new Texture(Gdx.files.internal("items/Scythe/Scythe.png"))));

         itemTextures.put("hoe_copper", new TextureRegion(new Texture(Gdx.files.internal("items/Hoe/Copper_Hoe.png"))));
         itemTextures.put("pickaxe_copper", new TextureRegion(new Texture(Gdx.files.internal("items/PickAxe/copper.png"))));
         itemTextures.put("axe_copper", new TextureRegion(new Texture(Gdx.files.internal("items/Axe/Copper_Axe.png"))));
         itemTextures.put("watering_can_copper", new TextureRegion(new Texture(Gdx.files.internal("items/Watering_Can/Copper_Watering_Can.png"))));

         itemTextures.put("hoe_steel", new TextureRegion(new Texture(Gdx.files.internal("items/Hoe/Steel_Hoe.png"))));
         itemTextures.put("pickaxe_steel", new TextureRegion(new Texture(Gdx.files.internal("items/PickAxe/steel.png"))));
         itemTextures.put("axe_steel", new TextureRegion(new Texture(Gdx.files.internal("items/Axe/Steel_Axe.png"))));
         itemTextures.put("watering_can_steel", new TextureRegion(new Texture(Gdx.files.internal("items/Watering_Can/Steel_Watering_Can.png"))));

         itemTextures.put("hoe_gold", new TextureRegion(new Texture(Gdx.files.internal("items/Hoe/Gold_Hoe.png"))));
         itemTextures.put("pickaxe_gold", new TextureRegion(new Texture(Gdx.files.internal("items/PickAxe/gold.png"))));
         itemTextures.put("axe_gold", new TextureRegion(new Texture(Gdx.files.internal("items/Axe/Gold_Axe.png"))));
         itemTextures.put("watering_can_gold", new TextureRegion(new Texture(Gdx.files.internal("items/Watering_Can/Gold_Watering_Can.png"))));

         itemTextures.put("hoe_iridium", new TextureRegion(new Texture(Gdx.files.internal("items/Hoe/Iridium_Hoe.png"))));
         itemTextures.put("pickaxe_iridium", new TextureRegion(new Texture(Gdx.files.internal("items/PickAxe/iridium.png"))));
         itemTextures.put("axe_iridium", new TextureRegion(new Texture(Gdx.files.internal("items/Axe/Iridium_Axe.png"))));
         itemTextures.put("watering_can_iridium", new TextureRegion(new Texture(Gdx.files.internal("items/Watering_Can/Iridium_Watering_Can.png"))));
    }

    private void loadInventoryUI() {
        // Load the inventory UI components from the Inventory_Parts.png
        inventoryPartsTexture = new Texture(Gdx.files.internal("items/Inventory_Parts.png"));

        // Extract the relevant parts based on the image
        // These coordinates should match the regions in your Inventory_Parts.png
        // You'll need to adjust these based on the exact layout of your texture

        // Inventory background (the main panel)
        inventoryBackground = new TextureRegion(inventoryPartsTexture, 0, 0, 384, 256);

        // Selected slot highlight (the golden border)
        selectedSlotHighlight = new TextureRegion(inventoryPartsTexture, 384, 0, 36, 36);

        // Individual inventory slots
        inventorySlots = new TextureRegion[12]; // Assuming 12 slots per row
        for (int i = 0; i < 12; i++) {
            inventorySlots[i] = new TextureRegion(inventoryPartsTexture, i * 36, 256, 36, 36);
        }
    }

    private void createInitialTools() {
        playerInventory.addItem(new Pickaxe("pickaxe_basic", "Pickaxe", itemTextures.get("pickaxe_basic"), 1));
        playerInventory.addItem(new Axe("axe_basic", "Axe", itemTextures.get("axe_basic"), 1)); // Uncommented this line
        playerInventory.addItem(new Hoe("hoe_basic", "Hoe", itemTextures.get("hoe_basic"), 1));
        playerInventory.addItem(new WateringCan("watering_can_basic", "Watering Can", itemTextures.get("watering_can_basic"), 1)); // Uncommented this line
        playerInventory.addItem(new FishingRod("fishing_rod_basic", "Fishing Rod", itemTextures.get("fishing_rod_basic"), 1)); // Uncommented this line
        playerInventory.addItem(new Scythe("scythe_basic", "Scythe", itemTextures.get("scythe_basic"), 1));
    }

    public void initialize(int screenWidth, int screenHeight) {
        // Create the inventory UI using the loaded textures
        inventoryUI = new StardewInventoryUI(playerInventory, 10, screenHeight - 10,
            inventoryBackground, inventorySlots, selectedSlotHighlight);
    }

    // Added update method to handle inventory updates
    public void update(float delta, Player player) {
        // Update inventory
        playerInventory.update(delta);

        // Handle inventory UI input
        if (inventoryUI != null) {
            inventoryUI.handleInput();
        }
    }

    // Added render method to render the inventory UI
    public void render(SpriteBatch batch) {
        if (inventoryUI != null) {
            inventoryUI.render(batch);
        }
    }

    public Inventory getPlayerInventory() {
        return playerInventory;
    }

    public InventoryUI getInventoryUI() {
        return inventoryUI;
    }

    public TextureRegion getInventoryBackground() {
        return inventoryBackground;
    }

    public TextureRegion[] getInventorySlots() {
        return inventorySlots;
    }

    public TextureRegion getSelectedSlotHighlight() {
        return selectedSlotHighlight;
    }

    public TextureRegion getTexture(String textureId) {
        return itemTextures.get(textureId);
    }

    public Tool createTool(ToolType type, int level) {
        String levelName;
        switch (level) {
            case 1: levelName = "basic"; break;
            case 2: levelName = "copper"; break;
            case 3: levelName = "steel"; break;
            case 4: levelName = "gold"; break;
            case 5: levelName = "iridium"; break;
            default: levelName = "basic";
        }

        String textureId = "";
        Tool tool = null;

        switch (type) {
            case HOE:
                textureId = "hoe_" + levelName;
                tool = new Hoe("hoe_" + levelName, "Hoe", itemTextures.get(textureId), level);
                break;
            case PICKAXE:
                textureId = "pickaxe_" + levelName;
                tool = new Pickaxe("pickaxe_" + levelName, "Pickaxe", itemTextures.get(textureId), level);
                break;
//            case AXE:
//                textureId = "axe_" + levelName;
//                tool = new Axe("axe_" + levelName, "Axe", itemTextures.get(textureId), level);
//                break;
//            case WATERING_CAN:
//                textureId = "watering_can_" + levelName;
//                tool = new WateringCan("watering_can_" + levelName, "Watering Can", itemTextures.get(textureId), level);
//                break;
//            case FISHING_ROD:
//                textureId = "fishing_rod_" + levelName;
//                tool = new FishingRod("fishing_rod_" + levelName, "Fishing Rod", itemTextures.get(textureId), level);
//                break;
            case SCYTHE:
                textureId = "scythe_" + levelName;
                tool = new Scythe("scythe_" + levelName, "Scythe", itemTextures.get(textureId), level);
                break;
        }

        return tool;
    }

    public void dispose() {
        for (TextureRegion texture : itemTextures.values()) {
            if (texture != null && texture.getTexture() != null) {
                texture.getTexture().dispose();
            }
        }

        if (inventoryPartsTexture != null) {
            inventoryPartsTexture.dispose();
        }

        if (inventoryUI != null) {
            inventoryUI.dispose();
        }
    }
}

