package models.Events;

import com.google.common.eventbus.EventBus;

public enum GameEventBus {
    INSTANCE;
    private final EventBus eventBus = new EventBus();
    public void post(Object event) {
        eventBus.post(event);
    }

    public void register(Object object) {
        eventBus.register(object);
    }

    public void unregister(Object object) {
        eventBus.unregister(object);
    }
}
