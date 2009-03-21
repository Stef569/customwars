package com.customwars.client.action.unit;

import com.customwars.client.action.ClearInGameStateAction;
import com.customwars.client.action.DirectAction;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Tile;
import com.customwars.client.model.map.TileMap;
import com.customwars.client.ui.renderer.MapRenderer;
import com.customwars.client.ui.state.InGameContext;

import java.util.ArrayList;
import java.util.List;

/**
 * Allow unit(s) in a transport to be dropped on a free adjacent tile around the center location
 *
 * @author stefan
 */
public class StartDropAction extends DirectAction {
  private InGameContext context;
  private MapRenderer mapRenderer;
  private TileMap<Tile> map;
  private Location center;

  public StartDropAction(Location center) {
    super("Start drop", true);
    this.center = center;
  }

  protected void init(InGameContext context) {
    this.context = context;
    this.map = context.getGame().getMap();
    this.mapRenderer = context.getMapRenderer();
  }

  protected void invokeAction() {
    List<Location> adjacentTiles = getEmptyAjacentTiles(center);

    mapRenderer.removeZones();
    mapRenderer.showArrows(false);

    // Only allow the cursor to move within the empty adjacent tiles,
    // show the tiles as a movezone
    mapRenderer.startCursorTraversal(adjacentTiles);
    mapRenderer.setMoveZone(adjacentTiles);
    context.setMode(InGameContext.MODE.UNIT_DROP);
  }

  private List<Location> getEmptyAjacentTiles(Location center) {
    List<Location> surroundingTiles = new ArrayList<Location>();
    for (Tile tile : map.getSurroundingTiles(center, 1, 1)) {
      if (!context.isDropLocationTaken(tile)) {
        if (tile.isFogged()) {
          surroundingTiles.add(tile);
        } else if (tile.getLocatableCount() == 0) {
          surroundingTiles.add(tile);
        }
      }
    }
    return surroundingTiles;
  }

  /**
   * Undo will reset everything to default,
   * undoing start drop is too hard
   */
  public void undo() {
    new ClearInGameStateAction().invoke(context);
  }
}
