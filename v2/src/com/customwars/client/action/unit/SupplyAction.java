package com.customwars.client.action.unit;

import com.customwars.client.App;
import com.customwars.client.action.DirectAction;
import com.customwars.client.model.GameController;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.network.MessageSender;
import com.customwars.client.network.NetworkException;
import com.customwars.client.ui.GUI;
import com.customwars.client.ui.state.InGameContext;
import com.customwars.client.ui.thingle.DialogListener;
import com.customwars.client.ui.thingle.DialogResult;
import org.apache.log4j.Logger;

/**
 * Supply the units in supply range
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
    gameController.supply(supplier);
    if (App.isMultiplayer()) sendSupply();
  }

  private void sendSupply() {
    try {
      messageSender.supply(supplier);
    } catch (NetworkException ex) {
      logger.warn("Could not supply unit", ex);
      GUI.askToResend(ex, new DialogListener() {
        public void buttonClicked(DialogResult button) {
          if (button == DialogResult.YES) {
            sendSupply();
          }
        }
      });
    }
  }
}
