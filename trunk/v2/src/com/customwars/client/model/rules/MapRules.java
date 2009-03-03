package com.customwars.client.model.rules;

import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;

public class MapRules {
  private int mountainVision = 3;
  private Map map;

  public MapRules(Map map) {
    this.map = map;
  }

  public int calcExtraVision(Tile t) {
    int additionalVision;
    int terrainHeight = t.getTerrain().getHeight();

    // When on higher terrain, Can see further...
    if (terrainHeight > 2) {
      additionalVision = mountainVision;
    } else {
      additionalVision = 0;
    }
    return additionalVision;
  }

  /**
   * if a tile is within the unit los
   * then there are some terrains and properties that remain fogged.
   * <br/>
   * They can only be made clear if the unit is directly next to it
   * The sameTile and adjacent tile are always visible.
   *
   * @param tileToBeFogged The tile to check relative to the baseTile
   * @param baseTile       The tile the unit is on
   * @return If the tile can be cleared of fog.
   */
  public boolean canClearFog(Tile baseTile, Tile tileToBeFogged) {
    boolean adjacent = map.isAdjacent(tileToBeFogged, baseTile);
    City city = map.getCityOn(tileToBeFogged);
    Unit unit = map.getUnitOn(tileToBeFogged);

    // Not directly next to the baseTile
    // If unit/City is hidden(remain fogged until directly next to it)
    // we cannot clear the fog.
    boolean hiddenUnit = unit != null && unit.isHidden();
    boolean hiddenProperty = city != null && city.isHidden();

    // If directly next to the tile we can see everything
    return adjacent && !hiddenUnit && !hiddenProperty;
  }
}
