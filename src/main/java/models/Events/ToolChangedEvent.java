package models.Events;

import models.UserInfo;

public record ToolChangedEvent(String toolName, String timeAndSeason, UserInfo user) {
}
