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
  private static final Logger logger = Logger.getLogger(SupplyAction.class);
  private InGameContext context;
  private Game game;
  private Unit supplier;

  public SupplyAction(Unit supplier) {
    super("Supply", false);
    this.supplier = supplier;
  }

  protected void init(InGameContext context) {
    this.context = context;
    this.game = context.getGame();
  }

  protected void invokeAction() {
    if (!context.isTrapped()) {
      supply();
    }
  }

  private void supply() {
    for (Unit unit : game.getMap().getSuppliablesInRange(supplier)) {
      logger.debug(unit.getName() + " is supplied by " + supplier.getName());
      supplier.supply(unit);
    }
  }
}
