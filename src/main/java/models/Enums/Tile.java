package models.Enums;

public enum Tile {
    PLAIN (".", "floor"),
    LAKE ("L", "lake"),
    GREENHOUSE ("G", "greenhouse"),
    COTTAGE ("C", "cottage"),
    QUARRY ("Q", "quarry"),
    TREE ("T", "tree"),
    STONE ("S", "stone"),
    FORAGING ("f", "FORAGING");
    private char symbol;
    private String description;
    Tile(String symbol, String description) {
        this.symbol = symbol.charAt(0);
        this.description = description;
    }

    public char getSymbol() {return symbol;}
    public String getDescription() {return description;}
}
