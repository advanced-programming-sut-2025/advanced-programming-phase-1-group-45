package models.Events;

import models.Enums.Season;
import models.User;

public record ToolChangedEvent(String toolName, String timeAndSeason, User user) {
}
