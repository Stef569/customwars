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
import com.customwars.client.ui.thingle.DialogListener;
import com.customwars.client.ui.thingle.DialogResult;
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
    int constructionPercentage = gameController.constructCity(unit, cityID, to);
    logConstructingProgress(constructionPercentage);
    if (App.isMultiplayer()) sendConstructCity();
  }

  private void logConstructingProgress(int constructionPercentage) {
    if (constructionPercentage == 100) {
      logger.debug(
        String.format("%s constructed a %s",
          unit.getStats().getName(), cityID)
      );
    } else {
      logger.debug(
        String.format("%s is constructing a %s (%s/100)",
          unit.getStats().getName(), cityID, constructionPercentage)
      );
    }
  }

  private void sendConstructCity() {
    try {
      messageSender.constructCity(unit, cityID, to);
    } catch (NetworkException ex) {
      logger.warn("Could not send construct city", ex);
      GUI.askToResend(ex, new DialogListener() {
        public void buttonClicked(DialogResult button) {
          if (button == DialogResult.YES) {
            sendConstructCity();
          }
        }
      });
    }
  }

  @Override
  public String getActionCommand() {
    return new ActionCommandEncoder().add(cityID).build();
  }
}
