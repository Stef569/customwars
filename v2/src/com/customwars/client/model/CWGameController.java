package com.customwars.client.model;

import com.customwars.client.App;
import com.customwars.client.controller.ControllerManager;
import com.customwars.client.model.co.CO;
import com.customwars.client.model.fight.Fight;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.game.Player;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.CityFactory;
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
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collection;

/**
 * CW implementation of a GameController
 */
public class CWGameController implements GameController {
  private Logger logger = Logger.getLogger(CWGameController.class);
  private final Game game;
  private final Map map;
  private final ControllerManager controllerManager;

  public CWGameController(Game game, ControllerManager controllerManager) {
    this.game = game;
    this.map = game.getMap();
    this.controllerManager = controllerManager;
  }

  public Unit select(Location location) {
    Unit unit = map.getUnitOn(location);
    logger.debug("Selecting " + unit);

    game.setActiveUnit(unit);
    map.buildMovementZone(unit);
    return unit;
  }

  @Override
  public void drop(Unit transport, Unit unit, Location dropLocation) {
    transport.remove(unit);
    dropLocation.add(unit);
  }

  @Override
  public void teleport(Location from, Location to) {
    Unit unit = map.getUnitOn(from);
    map.teleport(from, to, unit);
  }

  @Override
  public boolean capture(Unit unit, City city) {
    unit.setUnitState(UnitState.CAPTURING);
    city.capture(unit);

    boolean captured;
    if (city.isCapturedBy(unit)) {
      unit.setUnitState(UnitState.IDLE);
      city.resetCapturing();

      if (!unit.getOwner().isAi()) {
        controllerManager.setHumanCityController(city);
      }
      captured = true;
    } else {
      captured = false;
    }

    logger.debug(String.format("%s(hp=%s) is capturing %s captured:%s",
      unit.getName(), unit.getHp(), city.getName(), captured ? "100%" : city.getCapCountPercentage() + "%"));
    return captured;
  }

  @Override
  public void load(Unit unit, Unit transport) {
    logger.debug("Loading " + unit.getName() + " into " + transport);

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

    logger.debug(String.format(
        "%s supplied %s nearby units",
        apc.getName(), supplyCount)
    );
    return supplyCount;
  }

  @Override
  public void join(Unit unit, Unit target) {
    logger.debug("Join unit " + unit + " with " + target);

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

    unit.destroy(false);
    controllerManager.removeUnitController(unit);
  }

  @Override
  public int attack(Unit attacker, Unit defender) {
    int attackerHPPreFight = attacker.getInternalHp();
    int defenderHPPreFight = defender.getInternalHp();

    Fight unitFight = new UnitFight(map, attacker, defender);
    int damagePercentage = unitFight.getAttackDamagePercentage();
    unitFight.startFight();

    logUnitFightStatistics(attacker, defender, attackerHPPreFight, defenderHPPreFight, damagePercentage);

    if (attacker.isDestroyed()) {
      controllerManager.removeUnitController(attacker);
    }
    if (defender.isDestroyed()) {
      controllerManager.removeUnitController(defender);
    }

    return damagePercentage;
  }

  private void logUnitFightStatistics(Unit attacker, Unit defender, int attackerHPPreFight, int defenderHPPreFight, int damagePercentage) {
    logger.debug(String.format("%s(%s/%s) is attacking %s(%s/%s) dmg:%s%% Outcome: attacker(%s) defender(%s)",
      attacker.getName(), attackerHPPreFight, attacker.getInternalMaxHp(),
      defender.getName(), defenderHPPreFight, defender.getInternalMaxHp(),
      damagePercentage, attacker.getInternalHp(), defender.getInternalHp()));
  }

  @Override
  public int attack(Unit attacker, City city) {
    int attackerHPPreFight = attacker.getInternalHp();
    int defenderHPPreFight = city.getHp();

    Tile cityLocation = (Tile) city.getLocation();
    Fight unitVsCityFight = new UnitVsCityFight(attacker, city);
    int damagePercentage = unitVsCityFight.getAttackDamagePercentage();
    unitVsCityFight.startFight();

    logCityFightStatistics(attacker, city, attackerHPPreFight, defenderHPPreFight, damagePercentage);

    if (city.isDestroyed()) {
      controllerManager.removeCityController(city);
      Terrain terrain = TerrainFactory.getDestroyedTerrain(city);
      cityLocation.setTerrain(terrain);
    }
    return damagePercentage;
  }

  private void logCityFightStatistics(Unit attacker, City defender, int attackerHPPreFight, int defenderHPPreFight, int damagePercentage) {
    logger.debug(String.format("%s(%s/%s) is attacking %s(%s/%s) dmg:%s%% Outcome: attacker(%s) defender(%s)",
      attacker.getName(), attackerHPPreFight, attacker.getInternalMaxHp(),
      defender.getName(), defenderHPPreFight, defender.getHp(),
      damagePercentage, attacker.getInternalHp(), defender.getHp()));
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
      Unit unitInRange = map.getUnitOn(location);

      if (unitInRange != null) {
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

    logger.debug(String.format(
        "Revealing %s tiles around %s",
        numOfTilesToReveal, flareCenter.getLocationString())
    );
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

    int constructionPercentage;
    if (unit.isConstructionComplete()) {
      stopConstructingCity(unit, location);
      addCityToTile(location, city, unit.getOwner());
      constructionPercentage = 100;
    } else {
      constructionPercentage = city.getCapCountPercentage();
    }

    logConstructingProgress(unit, city.getName(), constructionPercentage);
    return constructionPercentage;
  }

  private void logConstructingProgress(Unit unit, String cityName, int constructionPercentage) {
    if (constructionPercentage == 100) {
      logger.debug(
        String.format("%s constructed a %s",
          unit.getName(), cityName)
      );
    } else {
      logger.debug(
        String.format("%s is constructing a %s (%s/100)",
          unit.getName(), cityName, constructionPercentage)
      );
    }
  }

  private void addCityToTile(Location to, City city, Player cityOwner) {
    Tile t = map.getTile(to);
    city.setLocation(t);
    t.setTerrain(city);

    if (!cityOwner.isAi()) {
      controllerManager.setHumanCityController(city);
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
    logger.debug(producer.getName() + " producing a " + unitToProduce);

    Unit unit = UnitFactory.getUnit(unitToProduce);
    Player player = producer.getOwner();
    player.addToBudget(-unit.getPrice());
    player.addUnit(unit);
    producer.add(unit);
    producer.deCreaseConstructionMaterials();

    if (!player.isAi()) {
      controllerManager.setHumanUnitController(unit);
    }
  }

  @Override
  public void buildUnit(Unit unit, Location location, Player player) {
    player.addToBudget(-unit.getPrice());
    player.addUnit(unit);
    map.getTile(location).add(unit);

    if (!player.isAi()) {
      controllerManager.setHumanUnitController(unit);
    }
  }

  @Override
  public void loadCO(Unit unit) {
    String coName = unit.getOwner().getCO().getName();
    logger.debug(String.format("CO %s loaded into %s", coName, unit.getName()));

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
    CO co = game.getActivePlayer().getCO();
    String powerName = co.getPowerName();
    String powerDescription = co.getPowerDescription();
    logger.debug(String.format("%s activates %s:%s", co.getName(), powerName, powerDescription));
    co.power(game);
  }

  @Override
  public void coSuperPower() {
    CO co = game.getActivePlayer().getCO();
    String superPowerName = co.getSuperPowerName();
    String superPowerDescription = co.getSuperPowerDescription();
    logger.debug(String.format("%s activates %s:%s", co.getName(), superPowerName, superPowerDescription));
    co.superPower(game);
  }
}
