package com.customwars.client.action.unit;

import com.customwars.client.App;
import com.customwars.client.SFX;
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

public class SurfaceAction extends DirectAction {
  private static final Logger logger = Logger.getLogger(SurfaceAction.class);
  private InGameContext inGameContext;
  private final Unit unit;
  private GameController gameController;
  private MessageSender messageSender;

  public SurfaceAction(Unit unit) {
    super("Surface", false);
    this.unit = unit;
  }

  @Override
  protected void init(InGameContext inGameContext) {
    this.inGameContext = inGameContext;
    gameController = inGameContext.getObj(GameController.class);
    messageSender = inGameContext.getObj(MessageSender.class);
  }

  @Override
  protected void invokeAction() {
    if (!inGameContext.isTrapped()) {
      surface();
    }
  }

  private void surface() {
    gameController.surface(unit);
    SFX.playSound("surface");
    if (App.isMultiplayer()) sendSurface();
  }

  private void sendSurface() {
    try {
      messageSender.surface(unit);
    } catch (NetworkException ex) {
      logger.warn("Could not send surface", ex);
      GUI.askToResend(ex, new DialogListener() {
        public void buttonClicked(DialogResult button) {
          if (button == DialogResult.YES) {
            sendSurface();
          }
        }
      });
    }
  }
}
