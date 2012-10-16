package com.customwars.client.model;

import com.customwars.client.App;
import com.customwars.client.controller.ControllerManager;
import com.customwars.client.model.fight.Fight;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.game.Player;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.CityFactory;
import com.customwars.client.model.gameobject.Locatable;
import com.customwars.client.model.gameobject.Terrain;
import com.customwars.client.model.gameobject.TerrainFactory;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.gameobject.UnitFactory;
import com.customwars.client.model.gameobject.UnitFight;
import com.customwars.client.model.gameobject.UnitState;
import com.customwars.client.model.gameobject.UnitVsCityFight;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;

import java.util.ArrayList;
import java.util.Collection;

/**
 * CW implementation of a GameController
 */
public class CWGameController implements GameController {
  private final Game game;
  private final Map map;
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
    Fight unitFight = new UnitFight(map, attacker, defender);
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
    Fight unitVsCityFight = new UnitVsCityFight(attacker, city);
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

  private void inflictDamageToArea(Collection<Location> area, int damage) {
    for (Location location : area) {
      Locatable locatable = location.getLastLocatable();

      if (locatable instanceof Unit) {
        Unit unitInRange = (Unit) locatable;
        if (unitInRange.willBeDestroyedAfterTakingDamage(damage)) {
          unitInRange.setHp(1);
        } else {
          unitInRange.addHp(-damage);
        }
      }
    }
  }

  @Override
  public void transformTerrain(Unit unit, Location location, Terrain transformToTerrain) {
    map.getTile(location).setTerrain(transformToTerrain);
  }

  @Override
  public void flare(Unit unit, Location flareCenter, int numOfTilesToReveal) {
    map.getTile(flareCenter).setFogged(false);
    for (Tile t : map.getSurroundingTiles(flareCenter, 1, numOfTilesToReveal)) {
      t.setFogged(false);
    }
    unit.getPrimaryWeapon().fire(1);
  }

  @Override
  public int constructCity(Unit unit, String cityID, Location location) {
    City city;
    if (map.isConstructingCityAt(location)) {
      city = map.getCityUnderConstructionAt(location);
    } else {
      city = CityFactory.getCity(cityID);
      map.addCityUnderConstruction(location, city);
    }

    // When the city is constructed it is added to the player cities collection.
    unit.construct(city);

    if (unit.isConstructionComplete()) {
      stopConstructingCity(unit, location);
      addCityToTile(location, city, unit.getOwner());
      return 100;
    } else {
      return city.getCapCountPercentage();
    }
  }

  private void addCityToTile(Location to, City city, Player cityOwner) {
    Tile t = map.getTile(to);
    city.setLocation(t);
    t.setTerrain(city);

    if (cityOwner.isAi()) {
      controllerManager.addAICityController(city);
    } else {
      controllerManager.addHumanCityController(city);
    }
  }

  public void stopConstructingCity(Unit unit, Location location) {
    unit.stopConstructing();
    map.stopConstructingCity(location);
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
      unit.setDefaultOrientation();

      if (!unit.isInTransport()) {
        // Deactivate the unit
        // so that it cannot be controlled anymore.
        unit.setActive(false);
      }

      // The unit moved
      // Reveal the los and check if we detected any hidden units.
      Player activePlayer = game.getActivePlayer();
      map.showLosFor(activePlayer);
      map.resetAllHiddenUnits(activePlayer);
      map.validateConstructingCities();

      if (unit.isCoOnBoard()) {
        updateCOZone(unit);
      }
    }
  }

  public void produceUnit(Unit producer, String unitToProduce) {
    Unit unit = UnitFactory.getUnit(unitToProduce);
    Player player = producer.getOwner();
    player.addToBudget(-unit.getPrice());
    player.addUnit(unit);
    producer.add(unit);
    producer.deCreaseConstructionMaterials();

    if (player.isAi()) {
      controllerManager.addAIUnitController(unit);
    } else {
      controllerManager.addHumanUnitController(unit);
    }
  }

  @Override
  public void buildUnit(Unit unit, Location location, Player player) {
    player.addToBudget(-unit.getPrice());
    player.addUnit(unit);
    location.add(unit);

    if (player.isAi()) {
      controllerManager.addAIUnitController(unit);
    } else {
      controllerManager.addHumanUnitController(unit);
    }
  }

  @Override
  public void loadCO(Unit unit) {
    unit.loadCO();
    unit.setExperience(unit.getStats().getMaxExperience());
    unit.getOwner().addToBudget(-unit.getPrice() / 2);
    unit.setDefaultOrientation();
    updateCOZone(unit);
  }

  private void updateCOZone(Unit unit) {
    Player unitOwner = unit.getOwner();
    int zoneRange = unitOwner.getCO().getZoneRange();
    Collection<Location> coZone = map.buildCOZone(unit, zoneRange);
    unitOwner.setCoZone(coZone);
  }

  @Override
  public void coPower() {
    game.getActivePlayer().getCO().power(game);
  }

  @Override
  public void coSuperPower() {
    game.getActivePlayer().getCO().superPower(game);
  }
}
