package com.customwars.client.action.unit;

import com.customwars.client.App;
import com.customwars.client.action.ActionCommandEncoder;
import com.customwars.client.action.DirectAction;
import com.customwars.client.model.GameController;
import com.customwars.client.model.game.Player;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Location;
import com.customwars.client.network.MessageSender;
import com.customwars.client.network.NetworkException;
import com.customwars.client.ui.GUI;
import com.customwars.client.ui.state.InGameContext;
import com.customwars.client.ui.thingle.DialogListener;
import com.customwars.client.ui.thingle.DialogResult;
import org.apache.log4j.Logger;

/**
 * Add unit to tile
 * subtract the unit cost from the unit owner
 */
public class AddUnitToTileAction extends DirectAction {
  private static final Logger logger = Logger.getLogger(AddUnitToTileAction.class);
  private GameController gameController;
  private MessageSender messageSender;
  private final Unit unit;                    // Unit to be put on tile
  private final Location location;            // Tile to add unit to
  private final Player newUnitOwner;

  public AddUnitToTileAction(Unit unit, Player newUnitOwner, Location location) {
    super("Add unit to tile", false);
    this.unit = unit;
    this.location = location;
    this.newUnitOwner = newUnitOwner;
  }

  protected void init(InGameContext inGameContext) {
    gameController = inGameContext.getObj(GameController.class);
    messageSender = inGameContext.getObj(MessageSender.class);
  }

  protected void invokeAction() {
    gameController.buildUnit(unit, location, newUnitOwner);
    if (App.isMultiplayer()) sendBuildUnit();
  }

  private void sendBuildUnit() {
    try {
      messageSender.buildUnit(unit, location, newUnitOwner);
    } catch (NetworkException ex) {
      logger.warn("Could not send build unit", ex);
      GUI.askToResend(ex, new DialogListener() {
        public void buttonClicked(DialogResult button) {
          if (button == DialogResult.YES) {
            sendBuildUnit();
          }
        }
      });
    }
  }

  @Override
  public String getActionCommand() {
    return new ActionCommandEncoder()
      .add(location)
      .add(unit)
      .add(newUnitOwner).build();
  }
}