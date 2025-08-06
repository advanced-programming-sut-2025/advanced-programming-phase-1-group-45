package com.proj.Model.inventoryItems.inventoryItems;//package com.proj.Model.inventoryItems;
//
//import com.badlogic.gdx.graphics.g2d.TextureRegion;
//import com.proj.Control.WorldController;
//import com.proj.Model.Inventory.InventoryItem;
//import com.proj.Player;
//import com.proj.map.GameMap;
//
//public class TreeSeedItem extends InventoryItem {
//    private final String treeId;
//
//    public TreeSeedItem(String id, String name, TextureRegion texture, int quantity, String treeId) {
//        super(id, name, texture, true, 99);
//        this.treeId = treeId;
//        setQuantity(quantity);
//    }
//
//    public String getTreeId() { return treeId; }
//
//    @Override
//    public boolean isStackable() { return true; }
//    @Override
//    public int getMaxStackSize() { return 99; }
//
//    @Override
//    public void use() {
//        GameMap currentMap = WorldController.getInstance().getGameMap();
//        Player player = WorldController.getInstance().getPlayer();
//
//        // محاسبه موقعیت کاشت بر اساس جهت بازیکن
//        int tileX = (int) (player.getPosition().x / currentMap.getTileWidth());
//        int tileY = (int) (player.getPosition().y / currentMap.getTileHeight());
//
//        switch (player.getDirection()) {
//            case UP: tileY++; break;
//            case DOWN: tileY--; break;
//            case LEFT: tileX--; break;
//            case RIGHT: tileX++; break;
//        }
//
//        // کاشت درخت
//        if (currentMap.getTreeManager().plantTree(treeId, tileX, tileY)) {
//            decreaseQuantity(1); // کاهش تعداد بذر
//            player.useEnergy(10); // مصرف انرژی برای کاشت
//        }
//    }
//}
//
