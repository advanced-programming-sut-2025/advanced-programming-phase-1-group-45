package managers;

import models.Enums.Season;

public record TurnAdvancedEvent(int hour, int day, Season season) {
}
