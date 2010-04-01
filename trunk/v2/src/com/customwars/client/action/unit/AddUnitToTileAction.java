package com.customwars.client.action.unit;

import com.customwars.client.App;
import com.customwars.client.action.DirectAction;
import com.customwars.client.model.GameController;
import com.customwars.client.model.game.Player;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Tile;
import com.customwars.client.network.MessageSender;
import com.customwars.client.network.NetworkException;
import com.customwars.client.ui.GUI;
import com.customwars.client.ui.state.InGameContext;
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
  private final Tile tile;                    // Tile to add unit to
  private final Player unitOwner;

  public AddUnitToTileAction(Unit unit, Tile tile, boolean canUndo) {
    super("Add unit to tile", canUndo);
    this.unit = unit;
    this.tile = tile;
    this.unitOwner = unit.getOwner();
  }

  protected void init(InGameContext inGameContext) {
    gameController = inGameContext.getObj(GameController.class);
    messageSender = inGameContext.getObj(MessageSender.class);
  }

  protected void invokeAction() {
    gameController.buildUnit(unit, tile, unitOwner);
    if (App.isMultiplayer()) sendBuildUnit();
  }

  private void sendBuildUnit() {
    try {
      messageSender.buildUnit(unit, tile, unitOwner);
    } catch (NetworkException ex) {
      logger.warn("Could not send build unit", ex);
      if (GUI.askToResend(ex) == GUI.YES_OPTION) {
        sendBuildUnit();
      }
    }
  }
}