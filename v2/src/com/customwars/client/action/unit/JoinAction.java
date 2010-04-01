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
 * The unit joins the target
 *
 * @author stefan
 */
public class JoinAction extends DirectAction {
  private static final Logger logger = Logger.getLogger(JoinAction.class);
  private final Unit target, unit;
  private InGameContext inGameContext;
  private GameController gameController;
  private MessageSender messageSender;

  public JoinAction(Unit unit, Unit target) {
    super("Join", false);
    this.unit = unit;
    this.target = target;
  }

  protected void init(InGameContext inGameContext) {
    this.inGameContext = inGameContext;
    gameController = inGameContext.getObj(GameController.class);
    messageSender = inGameContext.getObj(MessageSender.class);
  }

  protected void invokeAction() {
    if (!inGameContext.isTrapped()) {
      join();
    }
  }

  private void join() {
    logger.debug("Join unit " + unit + " with " + target);
    gameController.join(unit, target);
    if (App.isMultiplayer()) sendJoin();
  }

  private void sendJoin() {
    try {
      messageSender.join(unit, target);
    } catch (NetworkException ex) {
      logger.warn("Could not send join unit", ex);
      if (GUI.askToResend(ex) == GUI.YES_OPTION) {
        sendJoin();
      }
    }
  }
}
