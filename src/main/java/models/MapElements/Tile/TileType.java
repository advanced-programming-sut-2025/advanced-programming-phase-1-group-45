package models.MapElement.Tile;

import models.Item;

public enum TileType {
    PLAIN(".", "floor"),
    LAKE("L", "lake"),
    GREENHOUSE("G", "greenhouse"),
    COTTAGE("C", "cottage"),
    QUARRY("Q", "quarry"),
    TREE("T", "tree"),
    STONE("S", "stone"),
    SHIPPINGBIN("s", "shippingbin");
    private char symbol;
    private String description;


    TileType(String symbol, String description) {
        this.symbol = symbol.charAt(0);
        this.description = description;
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

    public static void printMapLegend() {
        System.out.println("\nMap Symbols Legend:");
        for (TileType tile : TileType.values()) {
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
