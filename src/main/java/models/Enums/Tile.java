package models.Enums;

import com.google.common.eventbus.Subscribe;
import models.Item;

public enum Tile {
    PLAIN(".", "floor"),
    LAKE("L", "lake"),
    GREENHOUSE("G", "greenhouse"),
    COTTAGE("C", "cottage"),
    QUARRY("Q", "quarry"),
    TREE("T", "tree"),
    STONE("S", "stone"),
    FORAGING("f", "FORAGING"),
    SHIPPINGBIN("s", "shippingbin");
    private Item item = null;
    private char symbol;
    private String description;
    private boolean isTilled = false;


    Tile(String symbol, String description) {
        this.symbol = symbol.charAt(0);
        this.description = description;
    }

    public void addItem(Item item) {
        this.item = item;
    }

    public Item getItem() {
        return item;
    }

    public void removeItem() {
        item = null;
    }

    public char getSymbol() {
        return symbol;
    }

    public String getDescription() {
        return description;
    }

    public void changeSymbol(char symbol) {
        this.symbol = symbol;
    }

    public void changeDescription(String description) {
        this.description = description;
    }

    public void tillThisTileWithHoe() {
        isTilled = true;
    }

    public void untilThisTileWithPickaxe() {
        isTilled = false;
    }

    public boolean isTilled() {
        return isTilled;
    }

    public static void printMapLegend() {
        System.out.println("\nMap Symbols Legend:");
        for (Tile tile : Tile.values()) {
            System.out.printf("%-2s : %-15s (%s)%n",
                    tile.symbol,
                    tile.name(),
                    tile.description);
        }
        System.out.println("\nExample Map Key:");
        System.out.println("T . L C Q");
        System.out.println("Tree, Plain, Lake, Cottage, Quarry\n");
    }
}
