package models.Enums;

public enum Season {
    SPRING,
    SUMMER,
    AUTUMN,
    WINTER;
    private static final Season[] values = values();

    public Season next() {
        if(values.length == 0) {
            throw new IllegalStateException("No season available");
        }
        return values[(this.ordinal() + 1) % values.length];
    }
}
