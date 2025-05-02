package models.Enums;

public enum Season {
    SPRING, SUMMER, AUTUMN, WINTER;
    private final Season[] values = values();
    public Season next() {
        return values[(this.ordinal() + 1) % values.length];
    }
}
