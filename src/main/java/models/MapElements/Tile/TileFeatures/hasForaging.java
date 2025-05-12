package models.MapElements.Tile;

import models.crops.ForagingCrop;
import models.crops.ForagingSeed;

public interface hasForaging extends TileFeature, UnWalkAble {
    public void collectForagingElement();
    //TODO
    //add to inventory
    //increase foraging ability
}
