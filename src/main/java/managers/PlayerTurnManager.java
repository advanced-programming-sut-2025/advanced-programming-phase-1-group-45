package managers;

import com.google.common.eventbus.Subscribe;
import models.Events.GameEventBus;
import models.Player;

import java.util.List;

public class PlayerTurnManager {
    private List<Player> players;
    private int currentTurn = 0;
    public PlayerTurnManager(List<Player> players) {
        this.players = players;
        GameEventBus.INSTANCE.register(this);
    }
    public void addCurrentTurn() {
        Player player = players.get(currentTurn);
        player.onTurnEnd();
        advanceToNextPlayer();
        GameEventBus.INSTANCE.post(new TurnEndedEvent(player));
    }
    private void advanceToNextPlayer() {
        currentTurn = (currentTurn + 1) % players.size();
    }
    @Subscribe
    private void onTimeAdvanced(TurnAdvancedEvent event){
        players.get(currentTurn).onNewTurn(event);
    }
}
