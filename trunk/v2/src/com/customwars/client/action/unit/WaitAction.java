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
 * The unit is made Inactive(can no longer be controlled)
 *
 * @author stefan
 */
public class WaitAction extends DirectAction {
  private static final Logger logger = Logger.getLogger(WaitAction.class);
  private final Unit unit;
  private GameController gameController;
  private MessageSender messageSender;

  public WaitAction(Unit unit) {
    super("Wait");
    this.unit = unit;
  }

  protected void init(InGameContext inGameContext) {
    gameController = inGameContext.getObj(GameController.class);
    messageSender = inGameContext.getObj(MessageSender.class);
  }

  protected void invokeAction() {
    gameController.makeUnitWait(unit);
    if (App.isMultiplayer()) sendWait();
  }

  private void sendWait() {
    try {
      messageSender.sendWait(unit);
    } catch (NetworkException ex) {
      logger.warn("Could not send wait", ex);
      if (GUI.askToResend(ex) == GUI.YES_OPTION) {
        sendWait();
      }
    }
  }
}
