package models.Events;

import models.Enums.Season;

public record DayChangedEvent(int day, Season season) {
}
