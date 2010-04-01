package com.customwars.client.action.city;

import com.customwars.client.App;
import com.customwars.client.action.DirectAction;
import com.customwars.client.model.GameController;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Location;
import com.customwars.client.network.MessageSender;
import com.customwars.client.network.NetworkException;
import com.customwars.client.ui.GUI;
import com.customwars.client.ui.renderer.MapRenderer;
import com.customwars.client.ui.state.InGameContext;
import org.apache.log4j.Logger;

import java.util.Collection;

public class LaunchRocketAction extends DirectAction {
  private static final Logger logger = Logger.getLogger(LaunchRocketAction.class);
  private MapRenderer mapRenderer;
  private final City rocketSilo;
  private final Unit rocketLauncher;
  private final Location rocketDestination;
  private InGameContext context;
  private GameController gameController;
  private MessageSender messageSender;

  public LaunchRocketAction(City rocketSilo, Unit rocketLauncher, Location rocketDestination) {
    super("Launch Rocket", false);
    this.rocketSilo = rocketSilo;
    this.rocketLauncher = rocketLauncher;
    this.rocketDestination = rocketDestination;
  }

  @Override
  protected void init(InGameContext inGameContext) {
    this.context = inGameContext;
    mapRenderer = inGameContext.getObj(MapRenderer.class);
    gameController = inGameContext.getObj(GameController.class);
    messageSender = inGameContext.getObj(MessageSender.class);
  }

  @Override
  public void invokeAction() {
    if (!context.isTrapped()) {
      launchRocket();
    }
  }

  private void launchRocket() {
    int effectRange = mapRenderer.getCursorEffectRange();
    Collection<Location> explosionArea =
      gameController.launchRocket(rocketLauncher, rocketSilo, rocketDestination, effectRange);
    mapRenderer.setExplosionArea(explosionArea);
    if (App.isMultiplayer()) sendLaunchRocket();
  }

  private void sendLaunchRocket() {
    try {
      int effectRange = mapRenderer.getCursorEffectRange();
      messageSender.launchRocket(rocketLauncher, rocketSilo, rocketDestination, effectRange);
    } catch (NetworkException ex) {
      logger.warn("Could not send launch rocket", ex);
      if (GUI.askToResend(ex) == GUI.YES_OPTION) {
        sendLaunchRocket();
      }
    }
  }
}
