package com.customwars.client.action.unit;

import com.customwars.client.App;
import com.customwars.client.action.DirectAction;
import com.customwars.client.model.GameController;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.network.MessageSender;
import com.customwars.client.network.NetworkException;
import com.customwars.client.ui.GUI;
import com.customwars.client.ui.state.InGameContext;
import org.apache.log4j.Logger;

/**
 * Supply the units in supply range
 *
 * @author stefan
 */
public class SupplyAction extends DirectAction {
  private static final Logger logger = Logger.getLogger(SupplyAction.class);
  private InGameContext inGameContext;
  private final Unit supplier;
  private GameController gameController;
  private MessageSender messageSender;

  public SupplyAction(Unit supplier) {
    super("Supply", false);
    this.supplier = supplier;
  }

  protected void init(InGameContext inGameContext) {
    this.inGameContext = inGameContext;
    gameController = inGameContext.getObj(GameController.class);
    messageSender = inGameContext.getObj(MessageSender.class);
  }

  protected void invokeAction() {
    if (!inGameContext.isTrapped()) {
      supply();
    }
  }

  private void supply() {
    int supplyCount = gameController.supply(supplier);
    logger.debug(supplier.getStats().getName() + " supplied " + supplyCount + " nearby units");
    if (App.isMultiplayer()) sendSupply();
  }

  private void sendSupply() {
    try {
      messageSender.supply(supplier);
    } catch (NetworkException ex) {
      logger.warn("Could not supply unit", ex);
      if (GUI.askToResend(ex) == GUI.YES_OPTION) {
        sendSupply();
      }
    }
  }
}
