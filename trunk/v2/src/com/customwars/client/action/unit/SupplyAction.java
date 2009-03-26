package com.customwars.client.action.unit;

import com.customwars.client.action.DirectAction;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.ui.state.InGameContext;
import org.apache.log4j.Logger;

/**
 * Supply the units in supply range
 *
 * @author stefan
 */
public class SupplyAction extends DirectAction {
  private Logger logger = Logger.getLogger(SupplyAction.class);
  private InGameContext context;
  private Game game;
  private Unit supplier;

  public SupplyAction(Unit unit) {
    super("Supply & Heal", false);
    this.supplier = unit;
  }

  protected void init(InGameContext context) {
    this.context = context;
    this.game = context.getGame();
  }

  protected void invokeAction() {
    if (context.isTrapped()) return;

    for (Unit unit : game.getMap().getSuppliablesInRange(supplier)) {
      supplier.supply(unit);
      logger.debug("supplied " + unit);
    }
  }
}
