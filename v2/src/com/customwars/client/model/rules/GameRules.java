package com.customwars.client.model.rules;

import com.customwars.client.model.game.Player;
import com.customwars.client.model.game.TurnBasedGame;

public class GameRules {

  /**
   * The game is over when all enemies are destroyed
   */
  public boolean isGameOver(TurnBasedGame game) {
    Player activePlayer = game.getActivePlayer();
    for (Player player : game.getAllPlayers()) {
      if (!player.isAlliedWith(activePlayer) && !player.isDestroyed()) {
        return false;
      }
    }
    return true;
  }
}
