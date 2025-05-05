package managers;

import com.google.common.eventbus.Subscribe;
import models.Events.GameEventBus;
import models.Events.TurnChangedEvent;
import models.Events.TurnEndedEvent;
import models.User;
import managers.TimeManager;

import java.util.List;

public class PlayerTurnManager {
    private List<User> players;
    private int currentTurn = 0;

    public PlayerTurnManager(List<User> players) {
        if (players != null) {
            this.players = players;
        }
        GameEventBus.INSTANCE.register(this);
    }

    public void endTurn() {
        User player = players.get(currentTurn);
        player.onTurnEnd();
        GameEventBus.INSTANCE.post(new TurnEndedEvent(player));
        advanceToNextPlayer();
    }

    private void advanceToNextPlayer() {
        currentTurn = (currentTurn + 1) % players.size();
        TimeManager.getInstance().nextTurn();
    }

    @Subscribe
    private void onTimeAdvanced(TurnChangedEvent event) {
        players.get(currentTurn).onNewTurn(event);
    }
}
