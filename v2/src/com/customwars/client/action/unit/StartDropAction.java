package com.customwars.client.action.unit;

import com.customwars.client.action.AbstractCWAction;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Tile;
import com.customwars.client.model.map.TileMap;
import com.customwars.client.ui.renderer.MapRenderer;
import com.customwars.client.ui.state.InGameSession;

import java.util.ArrayList;
import java.util.List;

/**
 * Allow a unit in a transport to be dropped on a free adjacent tile
 *
 * @author stefan
 */
public class StartDropAction extends AbstractCWAction {
  private InGameSession inGameSession;
  private MapRenderer mapRenderer;
  private TileMap<Tile> map;

  public StartDropAction(TileMap<Tile> map, MapRenderer mapRenderer, InGameSession inGameSession) {
    super("Start drop");
    this.map = map;
    this.mapRenderer = mapRenderer;
    this.inGameSession = inGameSession;
  }

  protected void doActionImpl() {
    Tile selected = inGameSession.getClick(2);
    List<Location> adjacentTiles = getEmptyAjacentTiles(selected);

    mapRenderer.removeZones();
    mapRenderer.showArrows(false);

    // Only allow the cursor to move within the adjacent empty tiles,
    // show the tiles as a movezone
    mapRenderer.startCursorTraversal(adjacentTiles);
    mapRenderer.setMoveZone(adjacentTiles);
    inGameSession.setMode(InGameSession.MODE.UNIT_DROP);
  }

  private List<Location> getEmptyAjacentTiles(Tile clicked) {
    List<Location> surroundingTiles = new ArrayList<Location>();
    for (Tile tile : map.getSurroundingTiles(clicked, 1, 1)) {
      if (tile.isFogged()) {
        surroundingTiles.add(tile);
      } else if (tile.getLocatableCount() == 0) {
        surroundingTiles.add(tile);
      }
    }
    return surroundingTiles;
  }

  public void undoAction() {
    mapRenderer.removeMoveZone();
    mapRenderer.showArrows(true);
    mapRenderer.stopCursorTraversal();
  }
}
