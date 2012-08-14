package com.customwars.client.action.unit;

import com.customwars.client.App;
import com.customwars.client.SFX;
import com.customwars.client.action.DirectAction;
import com.customwars.client.model.GameController;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.network.MessageSender;
import com.customwars.client.network.NetworkException;
import com.customwars.client.ui.GUI;
import com.customwars.client.ui.renderer.MapRenderer;
import com.customwars.client.ui.state.InGameContext;
import org.apache.log4j.Logger;

/**
 * Load the unit in the transport
 */
public class LoadAction extends DirectAction {
  private static final Logger logger = Logger.getLogger(LoadAction.class);
  private InGameContext inGameContext;
  private MapRenderer mapRenderer;
  private GameController gameController;
  private MessageSender messageSender;
  private final Unit unit, transport;

  public LoadAction(Unit unit, Unit transport) {
    super("Load", false);
    this.unit = unit;
    this.transport = transport;
  }

  protected void init(InGameContext inGameContext) {
    this.inGameContext = inGameContext;
    this.mapRenderer = inGameContext.getObj(MapRenderer.class);
    this.gameController = inGameContext.getObj(GameController.class);
    this.messageSender = inGameContext.getObj(MessageSender.class);
  }

  protected void invokeAction() {
    if (!inGameContext.isTrapped()) {
      load();
    }
  }

  private void load() {
    logger.debug("Loading " + unit + " into " + transport);
    gameController.load(unit, transport);
    mapRenderer.removeUnit(unit);

    if (transport.getStats().canLaunchUnit()) {
      // Planes that land on a carrier have to wait 1 turn
      unit.setActive(false);
    } else {
      // Other units can be dropped in the same turn
      unit.setActive(true);
    }

    SFX.playSound("load");
    if (App.isMultiplayer()) sendLoad();
  }

  private void sendLoad() {
    try {
      messageSender.load(unit, transport);
    } catch (NetworkException ex) {
      logger.warn("Could not send load unit", ex);
      if (GUI.askToResend(ex) == GUI.YES_OPTION) {
        sendLoad();
      }
    }
  }
}
