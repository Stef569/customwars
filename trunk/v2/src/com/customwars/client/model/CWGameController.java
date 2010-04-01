package com.customwars.client.model;

import com.customwars.client.App;
import com.customwars.client.controller.ControllerManager;
import com.customwars.client.model.fight.Fight;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.game.Player;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.GameObjectState;
import com.customwars.client.model.gameobject.Locatable;
import com.customwars.client.model.gameobject.Terrain;
import com.customwars.client.model.gameobject.TerrainFactory;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.gameobject.UnitFight;
import com.customwars.client.model.gameobject.UnitState;
import com.customwars.client.model.gameobject.UnitVsCityFight;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import com.customwars.client.tools.MapUtil;

import java.util.ArrayList;
import java.util.Collection;

/**
 * CW implementation of a GameController
 */
public class CWGameController implements GameController {
  private final Game game;
  private final Map<Tile> map;
  private final ControllerManager controllerManager;

  public CWGameController(Game game, ControllerManager controllerManager) {
    this.game = game;
    this.map = game.getMap();
    this.controllerManager = controllerManager;
  }

  @Override
  public void drop(Unit transport, Unit unit, Location dropLocation) {
    transport.remove(unit);
    dropLocation.add(unit);
  }

  @Override
  public void teleport(Location from, Location to) {
    game.getMap().teleport(from, to, from.getLastLocatable());
  }

  @Override
  public boolean capture(Unit unit, City city) {
    unit.setUnitState(UnitState.CAPTURING);
    city.capture(unit);

    if (city.isCapturedBy(unit)) {
      unit.setUnitState(UnitState.IDLE);
      controllerManager.addHumanCityController(city);
      city.resetCapturing();
      return true;
    } else {
      return false;
    }
  }

  @Override
  public void load(Unit unit, Unit transport) {
    unit.getLocation().remove(unit);
    transport.add(unit);
  }

  @Override
  public int supply(Unit apc) {
    int supplyCount = 0;
    for (Unit unit : map.getSuppliablesInRange(apc)) {
      apc.supply(unit);
      supplyCount++;
    }
    return supplyCount;
  }

  @Override
  public void join(Unit unit, Unit target) {
    //Add Money if joining cause the target to go over max HP
    int excessHP = unit.getHp() + target.getHp() - target.getMaxHp();

    if (excessHP > 0) {
      Player owner = target.getOwner();
      int excessMoney = (excessHP * unit.getStats().getPrice()) / unit.getMaxHp();
      owner.addToBudget(excessMoney);
    }

    // add HP, supplies, ammo to target
    target.addHp(unit.getHp());
    target.addSupplies(unit.getSupplies());

    if (unit.getAvailableWeapon() != null) {
      target.addAmmo(unit.getAvailableWeapon().getAmmo());
    }

    target.setUnitState(unit.getUnitState());
    unit.destroy(false);
    controllerManager.removeUnitController(unit);
  }

  @Override
  public int attack(Unit attacker, Unit defender) {
    Fight unitFight = new UnitFight();
    unitFight.initFight(attacker, defender);
    int damagePercentage = unitFight.getAttackDamagePercentage();
    unitFight.startFight();

    if (attacker.isDestroyed()) {
      controllerManager.removeUnitController(attacker);
    }
    if (defender.isDestroyed()) {
      controllerManager.removeUnitController(defender);
    }

    return damagePercentage;
  }

  @Override
  public int attack(Unit attacker, City city) {
    Tile cityLocation = (Tile) city.getLocation();
    Fight unitVsCityFight = new UnitVsCityFight();
    unitVsCityFight.initFight(attacker, city);
    int damagePercentage = unitVsCityFight.getAttackDamagePercentage();
    unitVsCityFight.startFight();

    if (city.isDestroyed()) {
      controllerManager.removeCityController(city);
      Terrain terrain = TerrainFactory.getDestroyedTerrain(city);
      cityLocation.setTerrain(terrain);
    }
    return damagePercentage;
  }

  @Override
  public Collection<Location> launchRocket(Unit unit, City silo, Location rocketDestination, int effectRange) {
    silo.launchRocket(unit);
    Collection<Location> effectArea = getArea(rocketDestination, effectRange);
    inflictDamageToArea(effectArea, App.getInt("plugin.silo_rocket_damage"));
    return effectArea;
  }

  private Collection<Location> getArea(Location center, int range) {
    Collection<Location> explosionArea = new ArrayList<Location>();

    explosionArea.add(center);
    for (Location tile : map.getSurroundingTiles(center, 1, range)) {
      explosionArea.add(tile);
    }
    return explosionArea;
  }

  private void inflictDamageToArea(Collection<Location> range, int damage) {
    for (Location location : range) {
      Locatable locatable = location.getLastLocatable();

      if (locatable instanceof Unit) {
        Unit unitInRange = (Unit) locatable;
        unitInRange.addHp(-damage);
      }
    }
  }

  @Override
  public void transformTerrain(Unit unit, Location location, Terrain transformToTerrain) {
    map.getTile(location).setTerrain(transformToTerrain);
  }

  @Override
  public void flare(Unit unit, Location flareCenter, int flareRange) {
    map.getTile(flareCenter).setFogged(false);
    for (Tile t : map.getSurroundingTiles(flareCenter, 1, flareRange)) {
      t.setFogged(false);
    }
  }

  @Override
  public boolean constructCity(Unit unit, City city, Location location) {
    unit.construct(city);

    if (unit.isConstructionComplete()) {
      unit.stopConstructing();
      addCityToTile(location, city, unit.getOwner());
      return true;
    } else {
      return false;
    }
  }

  private void addCityToTile(Location to, City city, Player cityOwner) {
    MapUtil.addCityToMap(map, to, city, cityOwner);

    if (cityOwner.isAi()) {
      controllerManager.addAICityController(city);
    } else {
      controllerManager.addHumanCityController(city);
    }
  }

  @Override
  public void dive(Unit unit) {
    unit.dive();
  }

  @Override
  public void surface(Unit unit) {
    unit.surface();
  }

  @Override
  public void makeUnitWait(Unit unit) {
    if (!unit.isDestroyed()) {
      unit.setOrientation(Unit.DEFAULT_ORIENTATION);

      // Make sure that the change to idle is picked up by the event listeners
      unit.setState(GameObjectState.ACTIVE);
      unit.setState(GameObjectState.IDLE);

      // The unit moved
      // Reveal the los and check if we detected any hidden units.
      Player activePlayer = game.getActivePlayer();
      map.showLosFor(activePlayer);
      map.resetAllHiddenUnits(activePlayer);
    }
  }

  @Override
  public void buildUnit(Unit unit, Location location, Player player) {
    player.addToBudget(-unit.getStats().getPrice());
    player.addUnit(unit);
    location.add(unit);

    if (player.isAi()) {
      controllerManager.addAIUnitController(unit);
    } else {
      controllerManager.addHumanUnitController(unit);
    }
  }
}
