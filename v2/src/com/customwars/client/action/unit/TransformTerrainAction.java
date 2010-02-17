package com.customwars.client.action.unit;

import com.customwars.client.action.DirectAction;
import com.customwars.client.model.gameobject.Terrain;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import com.customwars.client.ui.state.InGameContext;

/**
 * Overwrite the terrain on transformLocation to transformToTerrain
 */
public class TransformTerrainAction extends DirectAction {
  private final Location transformLocation;
  private final Terrain transformToTerrain;
  private InGameContext context;
  private Map<Tile> map;

  public TransformTerrainAction(Location transformLocation, Terrain transformToTerrain) {
    super("Transform terrain", false);
    this.transformLocation = transformLocation;
    this.transformToTerrain = transformToTerrain;
  }

  @Override
  protected void init(InGameContext context) {
    this.context = context;
    this.map = context.getGame().getMap();
  }

  @Override
  protected void invokeAction() {
    if (!context.isTrapped()) {
      map.getTile(transformLocation).setTerrain(transformToTerrain);
    }
  }
}