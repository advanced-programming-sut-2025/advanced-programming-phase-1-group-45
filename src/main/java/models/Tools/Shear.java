package models.Tools;

public class Shear extends Tool{
    public Shear(int energy) {
        super("Shear", energy);
    }

    public void useTool() {

        //User.decreaseEnergy(4);
    }

    @Override
    public void useTool(Tile targetTile) {

    }

    @Override
    public void decreaseEnergy() {

    }
}
