package controllers;

import models.Backpack;
import models.Enums.Shop;
import models.Tool;

public class SellController {
    // Attributes
    private Backpack playerInventory;
    private MovementController movementController;
    // Methods
    public void showSellableItems() { /* Filter inventory */ }
    public void sellItem(Tool item, int quantity) { /* Handle transaction */ }
    public void calculateTotalValue() { /* Sum selected items */ }
    Shop[] shops;
}
