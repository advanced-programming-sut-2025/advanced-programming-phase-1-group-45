package models.Events;

import models.Enums.Season;

public record SeasonChangedEvent(int hour, int day, Season previous, Season newSeason) {
}
