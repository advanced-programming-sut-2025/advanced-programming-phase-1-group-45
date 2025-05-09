package models.Tools.Backpack;

public enum BackpackType {
    BASIC("Basic", 12) {
        @Override
        public boolean canAddItem(int currentSize) {
            return currentSize < getCapacity();
        }

        @Override
        public BackpackType upgrade() {
            return LARGE;
        }
    },

    LARGE("Large", 24) {
        @Override
        public boolean canAddItem(int currentSize) {
            return currentSize < getCapacity();
        }

        @Override
        public BackpackType upgrade() {
            return DELUX;
        }
    },
    DELUX("Delux", Integer.MAX_VALUE) {
        @Override
        public boolean canAddItem(int currentSize) {
            return true;
        }

        @Override
        public BackpackType upgrade() {
            throw new IllegalStateException("You reached to the last level");
        }
    };
    private final String name;
    private final int capacity;

    BackpackType(String name, int capacity) {
        this.name = name;
        this.capacity = capacity;
    }

    public String getName() {
        return name;
    }

    public int getCapacity() {
        return capacity;
    }

    public abstract boolean canAddItem(int currentSize);

    public abstract BackpackType upgrade();
}
