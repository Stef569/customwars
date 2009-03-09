package com.customwars.client.action.unit;

import com.customwars.client.action.AbstractCWAction;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.ui.state.InGameSession;
import org.apache.log4j.Logger;

/**
 * Heal and supply the units in supply range
 *
 * @author stefan
 */
public class SupplyAndHealAction extends AbstractCWAction {
  private Logger logger = Logger.getLogger(SupplyAndHealAction.class);
  private Game game;
  private InGameSession inGameSession;

  public SupplyAndHealAction(Game game, InGameSession inGameSession) {
    super("Supply & Heal", false);
    this.inGameSession = inGameSession;
    this.game = game;
  }

  protected void doActionImpl() {
    if (inGameSession.isTrapped()) return;

    Unit activeUnit = game.getActiveUnit();
    for (Unit unit : game.getMap().getSuppliablesInRange(activeUnit)) {
      unit.supply(activeUnit);
      unit.heal(activeUnit);
      logger.debug("supplied " + unit);
    }
  }
}
