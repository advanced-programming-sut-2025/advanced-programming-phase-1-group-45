package models.Events;

import models.User;

public record TurnEndedEvent(User player) {
}
