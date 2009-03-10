package com.customwars.client.action.unit;

import com.customwars.client.action.AbstractCWAction;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.gameobject.GameObjectState;
import com.customwars.client.model.gameobject.Unit;

/**
 * The unit is made Inactive(can no longer be controlled)
 *
 * @author stefan
 */
public class WaitAction extends AbstractCWAction {
  private Game game;

  public WaitAction(Game game) {
    super("Wait", false);
    this.game = game;
  }

  public void doActionImpl() {
    wait(game.getActiveUnit());
  }

  private void wait(Unit unit) {
    game.getMap().resetFogMap(unit.getOwner());
    game.initZones();

    // The unit performed an action disable further actions->IDLE
    // when the unit didn't die from an attack.
    if (!unit.isDestroyed()) {
      unit.setState(GameObjectState.IDLE);
    }
  }
}
