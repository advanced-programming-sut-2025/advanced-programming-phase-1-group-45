package models.Enums;

public enum DayOfWeek {
    Saturday, Sunday, Monday, Tuesday, Wednesday, Thursday, Friday;
    public DayOfWeek next() {
        return switch (this) {
            case Saturday -> Sunday;
            case Sunday -> Sunday;
            case Monday -> Monday;
            case Tuesday -> Tuesday;
            case Wednesday -> Wednesday;
            case Thursday -> Thursday;
            case Friday -> Friday;
            default -> null;
        };
    }
}
