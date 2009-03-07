package com.customwars.client.action.unit;

import com.customwars.client.action.CWAction;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Tile;
import com.customwars.client.model.map.TileMap;
import com.customwars.client.ui.renderer.MapRenderer;
import com.customwars.client.ui.state.InGameSession;

import java.util.ArrayList;
import java.util.List;

/**
 * Allow a unit in a transport to be dropped
 *
 * @author stefan
 */
public class StartDropAction extends CWAction {
  private InGameSession inGameSession;
  private MapRenderer mapRenderer;
  private TileMap<Tile> map;

  public StartDropAction(TileMap<Tile> map, MapRenderer mapRenderer, InGameSession inGameSession) {
    super("Start drop", false);
    this.map = map;
    this.mapRenderer = mapRenderer;
    this.inGameSession = inGameSession;
  }

  protected void doActionImpl() {
    Tile selected = inGameSession.getClick(2);
    List<Location> emptyAdjacentTiles = getEmptyAjacentTiles(selected);

    mapRenderer.removeZones();

    // Only allow the cursor to move within the adjacent empty tiles,
    // show the tiles as a attackZone
    mapRenderer.startCursorTraversal(emptyAdjacentTiles);
    mapRenderer.setAttackZone(emptyAdjacentTiles);
    inGameSession.setMode(InGameSession.MODE.UNIT_DROP);
  }

  private List<Location> getEmptyAjacentTiles(Tile clicked) {
    List<Location> emptyTiles = new ArrayList<Location>();
    for (Location location : map.getSurroundingTiles(clicked, 1, 1)) {
      if (location.getLocatableCount() == 0) {
        emptyTiles.add(location);
      }
    }
    return emptyTiles;
  }
}
