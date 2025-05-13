package models.MapElements.Tile.TileFeatures;

public abstract class canGrow {
    public abstract void advanceDayInStage();

    public abstract void advanceStage();

    public abstract int getDaysInCurrentStage();
}
