package models.MapElement.Tile.TileFeatures;

public abstract class canGrow {
    public abstract void advanceDayInStage();

    public abstract void advanceStage();

    public abstract int getDaysInCurrentStage();
}
