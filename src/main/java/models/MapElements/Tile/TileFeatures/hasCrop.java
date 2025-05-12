package models.MapElements.Tile;

import models.crops.CropGrowStrategy;
import models.crops.CropInfo;

public class hasCrop extends canGrow implements TileFeature, UnWalkAble {
    private Tile tile;
    private CropInfo cropInfo;
    private boolean harvestAble;
    private CropGrowStrategy cropGrowStrategy;


    @Override
    public void grow() {

    }
}
