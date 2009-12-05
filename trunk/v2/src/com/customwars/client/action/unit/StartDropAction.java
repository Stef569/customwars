package com.customwars.client.action.unit;

import com.customwars.client.action.ClearInGameStateAction;
import com.customwars.client.action.DirectAction;
import com.customwars.client.controller.CursorController;
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
 * Allow unit(s) in a transport to be dropped on a free adjacent tile around the center location
 *
 * @author stefan
 */
public class StartDropAction extends DirectAction {
  private static final Logger logger = Logger.getLogger(StartDropAction.class);
  private InGameContext context;
  private CursorController cursorControl;
  private MapRenderer mapRenderer;
  private Map<Tile> map;
  private final Location center;
  private final Unit transport;
  private final Unit unitToBeDropped;

  public StartDropAction(Location center, Unit transport, Unit unitToBeDropped) {
    super("Start drop", true);
    this.center = center;
    this.transport = transport;
    this.unitToBeDropped = unitToBeDropped;
  }

  protected void init(InGameContext context) {
    this.context = context;
    this.map = context.getGame().getMap();
    this.cursorControl = context.getCursorController();
    this.mapRenderer = context.getMapRenderer();
  }

  protected void invokeAction() {
    List<Location> adjacentTiles = getEmptyAjacentTiles(center);

    logger.debug("Preparing to drop around tile " + center.getLocationString() + " empty tiles count " + adjacentTiles.size());
    mapRenderer.removeZones();
    mapRenderer.showArrowPath(false);
    mapRenderer.showArrowHead(true);
    mapRenderer.setDropLocations(context.getDropLocations(), center);

    // Only allow the cursor to move within the empty adjacent tiles
    // show the tiles as a movezone
    cursorControl.startCursorTraversal(adjacentTiles);
    mapRenderer.setMoveZone(adjacentTiles);
    context.setInputMode(InGameContext.INPUT_MODE.UNIT_DROP);
  }

  /**
   * @param transportLocation the location of the transporting unit
   * @return a list of locations where the unit to be dropped can be dropped on
   */
  private List<Location> getEmptyAjacentTiles(Location transportLocation) {
    List<Location> surroundingTiles = new ArrayList<Location>();
    for (Tile adjacentTile : map.getSurroundingTiles(transportLocation, 1, 1)) {
      if (!context.isDropLocationTaken(adjacentTile)) {
        if (map.isFreeDropLocation(adjacentTile, transport)) {
          if (canUnitMoveOverTerrain(adjacentTile)) {
            surroundingTiles.add(adjacentTile);
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
    new ClearInGameStateAction().invoke(context);
  }
}
