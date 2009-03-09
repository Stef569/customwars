package com.customwars.client.action.unit;

import com.customwars.client.action.CWAction;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.gameobject.GameObjectState;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.ui.state.InGameSession;

/**
 * The unit is made Inactive(can no longer be controlled)
 *
 * @author stefan
 */
public class WaitAction extends CWAction {
  private Game game;
  private InGameSession inGameSession;

  public WaitAction(Game game, InGameSession inGameSession) {
    super("Wait", false);
    this.game = game;
    this.inGameSession = inGameSession;
  }

  public void doActionImpl() {
    wait(game.getActiveUnit());
  }

  private void wait(Unit unit) {
    game.getMap().resetFogMap(unit.getOwner());
    game.initZones();

    // The unit performed an action disable further actions ->IDLE 
    // when the unit didn't die from an attack.
    if (!unit.isDestroyed()) {
      unit.setState(GameObjectState.IDLE);
    }
  }
}
