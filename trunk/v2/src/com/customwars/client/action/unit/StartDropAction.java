package com.customwars.client.action.unit;

import com.customwars.client.action.ClearInGameStateAction;
import com.customwars.client.action.DirectAction;
import com.customwars.client.controller.CursorController;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import com.customwars.client.ui.renderer.MapRenderer;
import com.customwars.client.ui.state.InGameContext;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Allow unit(s) in a transport to be dropped on a free drop tile around the center location within dropRange
 *
 * @author stefan
 */
public class StartDropAction extends DirectAction {
  private static final Logger logger = Logger.getLogger(StartDropAction.class);
  private InGameContext inGameContext;
  private CursorController cursorControl;
  private MapRenderer mapRenderer;
  private Map map;
  private final Location center;
  private final Unit transport;
  private final Unit unitToBeDropped;

  public StartDropAction(Location center, Unit transport, Unit unitToBeDropped) {
    super("Start drop", true);
    this.center = center;
    this.transport = transport;
    this.unitToBeDropped = unitToBeDropped;
  }

  protected void init(InGameContext inGameContext) {
    this.inGameContext = inGameContext;
    this.map = inGameContext.getObj(Game.class).getMap();
    this.cursorControl = inGameContext.getObj(CursorController.class);
    this.mapRenderer = inGameContext.getObj(MapRenderer.class);
  }

  protected void invokeAction() {
    List<Location> emptyDropTiles = getEmptyDropTiles(center);

    if (!emptyDropTiles.isEmpty()) {
      logger.debug("Preparing to drop around tile " + center.getLocationString() + " empty tiles count " + emptyDropTiles.size());
      mapRenderer.removeZones();
      mapRenderer.showArrowPath(false);
      mapRenderer.showArrowHead(true);
      mapRenderer.setDropLocations(inGameContext.getDropQueue().getDropTiles(), center);

      // Only allow the cursor to move within the empty adjacent tiles
      // show the tiles as a movezone
      cursorControl.startCursorTraversal(emptyDropTiles);
      mapRenderer.setMoveZone(emptyDropTiles);
      inGameContext.setInputMode(InGameContext.INPUT_MODE.UNIT_DROP);
    }
  }

  /**
   * @param transportLocation the location of the transporting unit
   * @return a list of locations where the unit in the transport can be dropped on
   */
  private List<Location> getEmptyDropTiles(Location transportLocation) {
    List<Location> surroundingTiles = new ArrayList<Location>();
    for (Tile dropTile : map.getSurroundingTiles(transportLocation, 1, transport.getMaxDropRange())) {
      if (!inGameContext.isDropLocationTaken(dropTile)) {
        if (map.isFreeDropLocation(dropTile, transport)) {
          if (canUnitMoveOverTerrain(dropTile)) {
            surroundingTiles.add(dropTile);
          }
        }
      }
    }
    return surroundingTiles;
  }

  private boolean canUnitMoveOverTerrain(Tile tile) {
    return tile.getTerrain().canBeTraverseBy(unitToBeDropped.getMovementType());
  }

  /**
   * Undo will reset the in game state
   * undoing start drop is too hard
   */
  public void undo() {
    cursorControl.stopCursorTraversal();
    cursorControl.moveCursor(transport.getLocation());
    new ClearInGameStateAction().invoke(inGameContext);
  }
}
