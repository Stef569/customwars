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
 * The unit is made Inactive(can no longer be controlled)
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
      GUI.askToResend(ex, new DialogListener() {
        public void buttonClicked(DialogResult button) {
          if (button == DialogResult.YES) {
            sendWait();
          }
        }
      });
    }
  }
}
