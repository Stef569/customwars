package com.customwars.client.model.ai.build;

import com.customwars.client.model.ai.fuzzy.Fuz;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.game.Player;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.gameobject.UnitFactory;
import com.customwars.client.model.gameobject.UnitFight;
import com.customwars.client.model.map.Map;
import com.customwars.client.tools.MapUtil;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * The Default Build AI will evaluate the map and will find the best units to build.
 * It does this by cheating. The AI can see all units in the map and tries to counter this.
 * By creating units that have good attack values against these units in the map.
 * The closer the unit is the higher the multiplier.
 *
 * There are 2 advisors a Build Advisor and a Financial Advisor. They are the AI 'brains'
 * This class will use the information of those advisors. Most logic is located in
 * the advisors.
 *
 * The AI will build only on Factories or other cities that can build see city.canBuild()
 */
public class DefaultBuildAI implements BuildAI {
  private static final Logger logger = Logger.getLogger(DefaultBuildAI.class);
  private final Game gameCopy;
  private final Map map;
  private City[] factories;
  private BuildStrategy buildStrategy;
  private int availableFunds;

  public DefaultBuildAI(Game gameCopy) {
    this.gameCopy = gameCopy;
    this.map = gameCopy.getMap();
  }

  /**
   * Create a list of units to be build on factories.
   * A factory can be any city that can build.
   *
   * @return A map of city -> unit pairs
   */
  public java.util.Map<City, Unit> findUnitsToBuild() {
    factories = getCitiesThatCanBuild(gameCopy.getActivePlayer());

    if (factories.length == 0) {
      return Collections.emptyMap();
    }

    listenToAdvisors();
    return build();
  }

  protected City[] getCitiesThatCanBuild(Player player) {
    List<City> factories = new ArrayList<City>();

    for (City c : player.getAllCities()) {
      boolean isEmpty = !map.hasUnitOn(c.getLocation());

      if (c.canBuild() && isEmpty) {
        factories.add(c);
      }
    }
    return factories.toArray(new City[factories.size()]);
  }

  private void listenToAdvisors() {
    BuildAdvisor advisor = new DefaultBuildAdvisor(gameCopy);
    buildStrategy = advisor.think();

    FinancialAdvisor financialStrategy = new DefaultFinancialAdvisor(gameCopy);
    availableFunds = financialStrategy.getAvailableFunds();
  }

  /**
   * Find a unit to build for each factory. The map can be empty if no unit is to be build.
   *
   * get the global build priorities, a list of Unit->Priority
   *
   * for each Factory
   * for each Global Unit Priority
   * rate the factory for the unit priority
   *
   * if the factory has a high rating and we didn't build on this factory yet
   */
  protected HashMap<City, Unit> build() {
    HashMap<City, Unit> unitsToBuildByFactory = new HashMap<City, Unit>();

    if (availableFunds != 0) {
      List<BuildPriority> buildPriorities = buildStrategy.getBuildPriority();
      logBuildStrategy(buildPriorities, 10);

      int unitsBuild = 0;
      for (City factory : factories) {
        for (BuildPriority buildPriority : buildPriorities) {
          if (unitsBuild == factories.length) break;

          if (availableFunds > 0) {
            String unitName = buildPriority.unitName;
            Unit unit = UnitFactory.getUnit(unitName);

            Fuz.BUILD_PRIORITY priority = buildPriority.priority;
            int factoryRating = rateFactory(factory, unit, priority);

            if (factoryRating >= 6 && !unitsToBuildByFactory.containsKey(factory)) {
              if (canBuild(factory, unit)) {
                availableFunds -= unit.getPrice();
                unitsToBuildByFactory.put(factory, unit);
                unitsBuild++;
              }
            }
          }
        }
      }
    }
    return unitsToBuildByFactory;
  }

  protected void logBuildStrategy(List<BuildPriority> buildPriorities, int amount) {
    logger.debug("::: AI Build Priority top 10 :::");

    for (int i = 0; i < amount && i < buildPriorities.size(); i++) {
      BuildPriority priority = buildPriorities.get(i);
      logger.debug(priority.toString());
    }
  }

  /**
   * Rates a factory.
   *
   * The rating is based on the build priority and the build hints.
   * A higher rating means a higher probability that this unit should be build on this factory.
   *
   * @param unit     The unit to rate this factory against
   * @param priority The priority of this unit
   * @return The rating for the Factory, [0-12]
   */
  protected int rateFactory(City factory, Unit unit, Fuz.BUILD_PRIORITY priority) {
    int factoryRating = 0;

    if (factory.canBuild(unit.getName())) {
      if (unitCanMoveOffFactory(unit, factory)) {
        switch (priority) {
           case CRITICAL:
            factoryRating = 12;
            break;
          case VERY_HIGH:
            factoryRating = 10;
            break;
          case HIGH:
            factoryRating = 8;
            break;
          case NORMAL:
            factoryRating = 6;
            break;
          case LOW:
            factoryRating = 4;
            break;
          case VERY_LOW:
            factoryRating = 1;
            break;
        }
      }

      if (buildStrategy.hasCityBuildHintsFor(factory)) {
        List<Fuz.UNIT_TYPE> factoryBuildHints = buildStrategy.getCityBuildHints(factory);

        if (isUnitOfType(unit, factoryBuildHints)) {
          factoryRating = factoryRating + 2;
        }
      }
    }

    return factoryRating;
  }

  /**
   * Temporarily add the unit to the factory, check if it can move.
   *
   * @return If the unit can move at least 1 tile off the factory location.
   */
  private boolean unitCanMoveOffFactory(Unit unit, City factory) {
    MapUtil.addUnitToMap(map, factory.getLocation(), unit, gameCopy.getActivePlayer());
    map.buildMovementZone(unit);
    boolean canMove =  unit.getMoveZone().size() != 1;
    factory.getLocation().remove(unit);
    gameCopy.getActivePlayer().removeUnit(unit);
    return canMove;
  }

  /**
   * Checks if the given unit is part of the UNIT_TYPE enum.
   * [CAPTURE, OFFENSE, DEFENSE, SCOUT]
   * For example an Recon unit is a good scouting unit, but not a very good offensive unit.
   * isUnitOfType("Recon", UNIT_TYPE.SCOUT) return true;
   *
   * @return if the unit is part of any UNIT_TYPE enum
   */
  private boolean isUnitOfType(Unit unit, List<Fuz.UNIT_TYPE> unitTypes) {
    List<String> defenseUnits = Arrays.asList("ANTI_TANK", "MECH");

    boolean canCapture = unit.getStats().canCapture();
    boolean manyMovePoints = unit.getMovePoints() > 3;
    boolean canScout = manyMovePoints;
    boolean hasWeapon = unit.getAvailableAttackWeapon() != null;

    // If the unit can do 60% damage to the light tank
    // Then it's considered a good attacker
    Unit tank = UnitFactory.getUnit("LIGHT_TANK");
    boolean isGoodAttacker = manyMovePoints && UnitFight.getAttackDamagePercentage(unit, tank) > 60;
    boolean isGoodDefender = defenseUnits.contains(unit.getName());

    if (canCapture && unitTypes.contains(Fuz.UNIT_TYPE.CAPTURE)) {
      return true;
    } else if (canScout && unitTypes.contains(Fuz.UNIT_TYPE.SCOUT)) {
      return true;
    } else if (hasWeapon) {
      if (isGoodAttacker && unitTypes.contains(Fuz.UNIT_TYPE.OFFENSE)) {
        return true;
      } else if (isGoodDefender && unitTypes.contains(Fuz.UNIT_TYPE.DEFENSE)) {
        return true;
      }
    }

    return false;
  }

  /**
   * Check if the given unit can be build by this city
   * 1. The city can build this unit type
   * 2. The owner has enough money to buy the unit
   * 3. The financial situation allows to build this unit
   *
   * @return can the city build the given unit
   */
  protected boolean canBuild(City city, Unit unit) {
    Player player = gameCopy.getActivePlayer();
    int unitPrice = unit.getPrice();

    boolean canBuild = city.canBuild(unit);
    boolean canBuy = player.isWithinBudget(unitPrice);
    int fundsLeft = availableFunds - unitPrice;

    return canBuild && canBuy && fundsLeft > 0;
  }
}