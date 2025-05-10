package models.Tools;

public interface UpgradeAble {

    public abstract void upgrade();

    public abstract ToolLevel getLevel();
}
