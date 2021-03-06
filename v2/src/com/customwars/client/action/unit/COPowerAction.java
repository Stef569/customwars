package com.customwars.client.action.unit;

import com.customwars.client.App;
import com.customwars.client.action.DirectAction;
import com.customwars.client.model.GameController;
import com.customwars.client.model.co.CO;
import com.customwars.client.model.game.Game;
import com.customwars.client.network.MessageSender;
import com.customwars.client.network.NetworkException;
import com.customwars.client.ui.GUI;
import com.customwars.client.ui.state.InGameContext;
import com.customwars.client.ui.thingle.DialogListener;
import com.customwars.client.ui.thingle.DialogResult;
import org.apache.log4j.Logger;

/**
 * This action performs a CO Power.
 */
public class COPowerAction extends DirectAction {
  private static final Logger logger = Logger.getLogger(COPowerAction.class);
  private GameController gameController;
  private MessageSender messageSender;

  public COPowerAction() {
    super("CO power", false);
  }

  @Override
  protected void init(InGameContext inGameContext) {
    gameController = inGameContext.getObj(GameController.class);
    messageSender = inGameContext.getObj(MessageSender.class);
  }

  @Override
  protected void invokeAction() {
    gameController.coPower();
    if (App.isMultiplayer()) sendCOPower();
  }

  private void sendCOPower() {
    try {
      messageSender.coPower();
    } catch (NetworkException ex) {
      logger.warn("Could not send CO power", ex);
      GUI.askToResend(ex, new DialogListener() {
        public void buttonClicked(DialogResult button) {
          if (button == DialogResult.YES) {
            sendCOPower();
          }
        }
      });
    }
  }
}
