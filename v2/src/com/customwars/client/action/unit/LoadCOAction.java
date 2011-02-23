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
 * Load a CO into a Unit
 */
public class LoadCOAction extends DirectAction {
  private static final Logger logger = Logger.getLogger(LoadCOAction.class);
  private final Unit unit;
  private MessageSender messageSender;
  private GameController gameController;

  public LoadCOAction(Unit unit) {
    super("Load CO", false);
    this.unit = unit;
  }

  @Override
  protected void init(InGameContext inGameContext) {
    gameController = inGameContext.getObj(GameController.class);
    messageSender = inGameContext.getObj(MessageSender.class);
  }

  @Override
  protected void invokeAction() {
    gameController.loadCO(unit);
    if (App.isMultiplayer()) sendLoadCO();
  }

  private void sendLoadCO() {
    try {
      messageSender.loadCO(unit);
    } catch (NetworkException ex) {
      logger.warn("Could not send load CO", ex);
      if (GUI.askToResend(ex) == GUI.YES_OPTION) {
        sendLoadCO();
      }
    }
  }
}
