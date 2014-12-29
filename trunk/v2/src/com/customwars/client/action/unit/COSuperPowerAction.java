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
 * This action performs a CO Super Power.
 */
public class COSuperPowerAction extends DirectAction {
  private static final Logger logger = Logger.getLogger(COSuperPowerAction.class);
  private GameController gameController;
  private MessageSender messageSender;
  private Game game;

  public COSuperPowerAction() {
    super("CO Super power", false);
  }

  @Override
  protected void init(InGameContext inGameContext) {
    game = inGameContext.getObj(Game.class);
    gameController = inGameContext.getObj(GameController.class);
    messageSender = inGameContext.getObj(MessageSender.class);
  }

  @Override
  protected void invokeAction() {
    CO co = game.getActivePlayer().getCO();
    String superPowerName = co.getSuperPowerName();
    String superPowerDescription = co.getSuperPowerDescription();
    logger.debug(co.getName() + " activates " + superPowerName + ":" + superPowerDescription);
    gameController.coSuperPower();
    if (App.isMultiplayer()) sendCOSuperPower();
  }

  private void sendCOSuperPower() {
    try {
      messageSender.coSuperPower();
    } catch (NetworkException ex) {
      logger.warn("Could not send CO super power", ex);
      GUI.askToResend(ex, new DialogListener() {
        public void buttonClicked(DialogResult button) {
          if (button == DialogResult.YES) {
            sendCOSuperPower();
          }
        }
      });
    }
  }
}
