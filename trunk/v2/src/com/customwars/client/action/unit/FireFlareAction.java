package com.customwars.client.action.unit;

import com.customwars.client.App;
import com.customwars.client.action.ActionCommandEncoder;
import com.customwars.client.action.DirectAction;
import com.customwars.client.model.GameController;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Location;
import com.customwars.client.network.MessageSender;
import com.customwars.client.network.NetworkException;
import com.customwars.client.ui.GUI;
import com.customwars.client.ui.state.InGameContext;
import org.apache.log4j.Logger;

/**
 * Fires a flare
 * all tiles within flare range, surrounding the center tile are revealed.
 */
public class FireFlareAction extends DirectAction {
  private static final Logger logger = Logger.getLogger(FireFlareAction.class);
  private InGameContext inGameContext;
  private GameController gameController;
  private MessageSender messageSender;
  private final Location flareCenter;
  private final int flareRange;
  private final Unit unit;

  public FireFlareAction(Unit unit, Location flareCenter) {
    super("Fire flare", false);
    this.unit = unit;
    this.flareCenter = flareCenter;
    this.flareRange = App.getInt("plugin.flare_range");
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
      fireFlare();
    }
  }

  private void fireFlare() {
    gameController.flare(unit, flareCenter, flareRange);
    logger.debug("Revealing " + flareRange + " tiles around " + flareCenter.getLocationString());
    if (App.isMultiplayer()) sendFlare();
  }

  private void sendFlare() {
    try {
      messageSender.flare(flareCenter, flareRange);
    } catch (NetworkException ex) {
      logger.warn("Could not send fire flare", ex);
      if (GUI.askToResend(ex) == GUI.YES_OPTION) {
        sendFlare();
      }
    }
  }

  @Override
  public String getActionCommand() {
    return new ActionCommandEncoder().add(flareCenter).build();
  }
}
