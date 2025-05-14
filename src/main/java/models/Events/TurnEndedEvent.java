package models.Events;

import models.UserInfo;

public record TurnEndedEvent(UserInfo player) {
}
