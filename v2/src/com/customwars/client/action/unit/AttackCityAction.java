package com.customwars.client.action.unit;

import com.customwars.client.SFX;
import com.customwars.client.action.DirectAction;
import com.customwars.client.controller.ControllerManager;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Terrain;
import com.customwars.client.model.gameobject.TerrainFactory;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.gameobject.UnitVsCityFight;
import com.customwars.client.model.map.Direction;
import com.customwars.client.model.map.Tile;
import com.customwars.client.ui.state.InGameContext;

import java.util.Arrays;
import java.util.List;

/**
 * The unit attacks a City
 */
public class AttackCityAction extends DirectAction {
  private final static List<Direction> VERTICAL_DIRECTIONS = Arrays.asList(Direction.NORTH, Direction.SOUTH);
  private final Unit attacker;
  private final City city;
  private final Tile cityLocation;
  private final UnitVsCityFight unitVsCityFight;
  private ControllerManager controllerManager;
  private InGameContext context;

  /**
   * @param attacker The unit that is attacking
   * @param city     The city that is under attack
   */
  public AttackCityAction(Unit attacker, City city) {
    super("Attack City", false);
    this.attacker = attacker;
    this.city = city;
    this.cityLocation = (Tile) city.getLocation();
    this.unitVsCityFight = new UnitVsCityFight();
  }

  @Override
  protected void init(InGameContext context) {
    this.context = context;
    controllerManager = context.getControllerManager();
  }

  @Override
  protected void invokeAction() {
    if (!context.isTrapped()) {
      attackCity();
    }
  }

  public void attackCity() {
    unitVsCityFight.initFight(attacker, city);
    unitVsCityFight.startFight();

    if (city.isDestroyed()) {
      SFX.playSound("explode");
      controllerManager.removeCityController(city);

      Terrain terrain = getDestroyedTerrain(city);
      cityLocation.setTerrain(terrain);
    }
  }

  /**
   * Get the destroyed terrain that replaces the city when it is destroyed
   *
   * By using the base type and prepending destroyed_ to it.
   *
   * The search is performed in the following order:
   * 1. Search for the destroyed_type terrain
   * 2. Get the directions this city connects to
   * If it connects horizontal. Search for the horizontal_destroyed_type terrain
   * If it connects vertical. Search for the vertical_destroyed_type terrain
   *
   * If at this point no terrain can be found just return a plain.
   */
  private Terrain getDestroyedTerrain(City city) {
    String type = city.getType();

    if (TerrainFactory.hasTerrainForName("destroyed_" + type)) {
      return TerrainFactory.getTerrain("destroyed_" + type);
    }

    if (city.canConnectToAll(VERTICAL_DIRECTIONS)) {
      if (TerrainFactory.hasTerrainForName("vertical_destroyed_" + type)) {
        return TerrainFactory.getTerrain("vertical_destroyed_" + type);
      }
    } else {
      if (TerrainFactory.hasTerrainForName("horizontal_destroyed_" + type)) {
        return TerrainFactory.getTerrain("horizontal_destroyed_" + type);
      }
    }
    return TerrainFactory.getTerrain("plain");
  }
}
