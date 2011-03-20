package com.customwars.client.action.unit;

import com.customwars.client.App;
import com.customwars.client.action.ActionCommandEncoder;
import com.customwars.client.action.DirectAction;
import com.customwars.client.model.GameController;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.CityFactory;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Location;
import com.customwars.client.network.MessageSender;
import com.customwars.client.network.NetworkException;
import com.customwars.client.ui.GUI;
import com.customwars.client.ui.state.InGameContext;
import org.apache.log4j.Logger;

public class ConstructCityAction extends DirectAction {
  private static final Logger logger = Logger.getLogger(ConstructCityAction.class);
  private InGameContext inGameContext;
  private GameController gameController;
  private MessageSender messageSender;
  private final Unit unit;
  private final Location to;
  private final String cityID;

  public ConstructCityAction(Unit unit, String cityID, Location moveTo) {
    super("Construct City", false);
    this.unit = unit;
    this.to = moveTo;
    this.cityID = cityID;
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
      constructCity();
    }
  }

  private void constructCity() {
    boolean constructionComplete = gameController.constructCity(unit, cityID, to);
    logConstructingProgress(constructionComplete);
    if (App.isMultiplayer()) sendConstructCity();
  }

  private void logConstructingProgress(boolean constructionComplete) {
    City city = CityFactory.getCity(cityID);

    if (constructionComplete) {
      logger.debug(
        String.format("%s constructed a %s",
          unit.getStats().getName(), city.getName())
      );
    } else {
      logger.debug(
        String.format("%s is constructing a %s",
          unit.getStats().getName(), city.getName())
      );
    }
  }

  private void sendConstructCity() {
    try {
      messageSender.constructCity(unit, cityID, to);
    } catch (NetworkException ex) {
      logger.warn("Could not send construct city", ex);
      if (GUI.askToResend(ex) == GUI.YES_OPTION) {
        sendConstructCity();
      }
    }
  }

  @Override
  public String getActionCommand() {
    return new ActionCommandEncoder().add(cityID).build();
  }
}