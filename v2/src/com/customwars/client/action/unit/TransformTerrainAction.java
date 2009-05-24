package com.customwars.client.action.unit;

import com.customwars.client.action.DirectAction;
import com.customwars.client.model.gameobject.Terrain;
import com.customwars.client.model.gameobject.TerrainFactory;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Tile;
import com.customwars.client.ui.state.InGameContext;

public class TransformTerrainAction extends DirectAction {
  private Unit unit;
  private Tile tile;

  public TransformTerrainAction(Unit unit, Tile tile) {
    super("Transform terrain", false);
    this.unit = unit;
    this.tile = tile;
  }

  @Override
  protected void init(InGameContext context) {
  }

  @Override
  protected void invokeAction() {
    Terrain tranformTo = getTransformToTerrain();
    tile.setTerrain(tranformTo);
  }

  private Terrain getTransformToTerrain() {
    int tranformID = unit.getTransformTerrainFor(tile.getTerrain());
    return TerrainFactory.getTerrain(tranformID);
  }
}