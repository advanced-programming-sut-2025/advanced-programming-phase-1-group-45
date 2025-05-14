package models.MapElements.Tile;

import models.MapElements.Tile.TileFeatures.*;

public class TileCreate {
    public static Tile create(TileType type) {
        switch (type) {
            case LAKE -> {
                return makeLakeTile();
            }
            case COTTAGE ->
            {
                return makeCottageTile();
            }
            case QUARRY -> {
                return makeQuarryTile();
            }
            case GREENHOUSE -> {
                return makeGreenHouseTile();
            }
            case PLAIN -> {
                return makePlainTile();
            }
            case SHIPPINGBIN -> {
                return makeShippingBinTile();
            }
            case TREE -> {

            }
        }
        return null;
    }

    private static Tile makeLakeTile() {
        Tile tile = new Tile(TileType.LAKE);
        tile.addFeature(lakeTile.class, new lakeTile());
        return tile;
    }

    private static Tile makePlainTile() {
        Tile tile = new Tile(TileType.PLAIN);
        tile.addFeature(PlowSituation.class, new PlowSituation());
        tile.addFeature(canPlant.class, new canPlant());
        return tile;
    }

    private static Tile makeCottageTile() {
        Tile tile = new Tile(TileType.COTTAGE);
        tile.addFeature(buildingTile.class, new buildingTile());
        return tile;
    }

    private static Tile makeQuarryTile() {
        Tile tile = new Tile(TileType.QUARRY);
        tile.addFeature(buildingTile.class, new buildingTile());
        return tile;
    }

    private static Tile makeGreenHouseTile() {
        Tile tile = new Tile(TileType.GREENHOUSE);
        tile.addFeature(buildingTile.class, new buildingTile());
        return tile;
    }

    private static Tile makeShippingBinTile() {
        Tile tile = new Tile(TileType.SHIPPINGBIN);
        tile.addFeature(buildingTile.class, new buildingTile());
        return tile;
    }
    private static Tile makeTreeTile() {
        Tile tile = new Tile(TileType.TREE);
        tile.addFeature(TreeTile.class, new TreeTile());
        return tile;
    }
    private static Tile makeStoneTile() {
        Tile tile = new Tile(TileType.STONE);
        tile.addFeature(StoneTile.class, new StoneTile());
    }
}
