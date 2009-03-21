package com.customwars.client.action.unit;

import com.customwars.client.action.DirectAction;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.gameobject.GameObjectState;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.ui.state.InGameContext;

/**
 * The unit is made Inactive(can no longer be controlled)
 *
 * @author stefan
 */
public class WaitAction extends DirectAction {
  private Game game;
  private Unit unit;

  public WaitAction(Unit unit) {
    super("Wait");
    this.unit = unit;
  }

  protected void init(InGameContext context) {
    game = context.getGame();
  }

  protected void invokeAction() {
    game.initZones();

    if (!unit.isDestroyed()) {
      unit.setOrientation(Unit.DEFAULT_ORIENTATION);

      // Make sure that the change to idle is picked up by the event listeners
      unit.setState(GameObjectState.ACTIVE);
      unit.setState(GameObjectState.IDLE);
    }
  }
}
