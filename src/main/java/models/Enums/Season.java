package models.Enums;

public enum Season {
    SPRING,
    SUMMER,
    AUTUMN,
    WINTER;
    private static final Season[] values = values();

    public Season next() {
        if (values.length == 0) {
            throw new IllegalStateException("No season available");
        }
        return values[(this.ordinal() + 1) % values.length];
    }

    public Weather[] getWeathers() {
        return switch (this) {
            case SPRING, SUMMER, AUTUMN -> new Weather[]{
                    Weather.SUNNY, Weather.RAINY
                    , Weather.STORMY
            };
            case WINTER -> new Weather[]{
                    Weather.SUNNY, Weather.SNOWY
            };
        };
    }

}
