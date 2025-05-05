package models.Events;

import models.Enums.Season;

public record HourAdvancedEvent(int hour, int day, Season season) {
}
