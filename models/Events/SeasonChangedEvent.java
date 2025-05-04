package models.Events;

import models.Enums.Season;

public record SeasonChangedEvent(Season previous, Season newSeason) {
}
