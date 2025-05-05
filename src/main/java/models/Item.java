package models;

public abstract class Item {
    private final String itemName;
    private final Class<?> itemSuperClass;
    public Item(String itemName, Class<?> itemSuperClass) {
        this.itemName = itemName;
        this.itemSuperClass = this.getClass();
    }
    public String getItemName() {
        return itemName;
    }
    public Class<?> getItemSuperClass() {
        return itemSuperClass;
    }
}
