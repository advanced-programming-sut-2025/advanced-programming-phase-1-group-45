package managers;

import com.google.common.eventbus.Subscribe;
import controllers.WeatherController;
import models.Enums.Weather;
import models.Events.GameEventBus;
import models.Events.TurnChangedEvent;
import models.Events.TurnEndedEvent;
import models.UserInfo;

import java.util.List;

public class PlayerTurnManager {
    private List<UserInfo> players;
    private int currentTurn = 0;

    public PlayerTurnManager(List<UserInfo> players) {
        if (players != null) {
            this.players = players;
        }
        GameEventBus.INSTANCE.register(this);
    }

    public void endTurn() {
        UserInfo player = players.get(currentTurn);
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
