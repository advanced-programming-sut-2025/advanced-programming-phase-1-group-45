package models.Enums;

public enum Tile {
    PLAIN (".", "floor"),
    LAKE ("L", "lake"),
    GREENHOUSE ("G", "greenhouse"),
    COTTAGE ("C", "cottage"),
    QUARRY ("Q", "quarry"),
    TREE ("T", "tree"),
    STONE ("S", "stone"),
    FORAGING ("f", "FORAGING"),
    SHIPPINGBIN("s", "shippingbin");
    private char symbol;
    private String description;
    Tile(String symbol, String description) {
        this.symbol = symbol.charAt(0);
        this.description = description;
    }

    public char getSymbol() {return symbol;}
    public String getDescription() {return description;}
    public static void printMapLegend() {
        System.out.println("\nMap Symbols Legend:");
        for(Tile tile : Tile.values()) {
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
