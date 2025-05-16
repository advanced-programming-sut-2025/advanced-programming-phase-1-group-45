package models.MapElements.Tile.TileFeatures;


public class PlowSituation implements TileFeature {
    private boolean isPlowed = false;

    public void plow() {
        this.isPlowed = true;
    }

    public void unPlow() {
        this.isPlowed = false;
    }

    public boolean isPlowed() {
        return this.isPlowed;
    }
}
