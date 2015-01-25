package com.customwars.client.model.ai.fuzzy;

import com.customwars.client.model.game.Game;
import com.customwars.client.model.game.Player;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.CityFactory;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.gameobject.UnitFactory;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Range;
import com.customwars.client.model.map.Tile;
import com.customwars.client.model.map.TileMap;
import com.customwars.client.tools.NumberUtil;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * Translates game information to fuzzy data.
 * For example the size of map is 5x5 tiles.
 * in fuzzy data this becomes the map is tiny.
 */
public class BuildAIDataGenerator {
  private static final Logger logger = Logger.getLogger(BuildAIDataGenerator.class);
  private final Game game;
  private final Map map;
  private final Player activePlayer;
  private BuildAIInformation data;

  public BuildAIDataGenerator(Game game) {
    this.game = game;
    this.map = game.getMap();
    this.activePlayer = game.getActivePlayer();
  }

  public BuildAIInformation generate() {
    data = new BuildAIInformation();
    checkFinances();
    calculateVisibleLandTilesPercentage();
    countAllCitiesInTheGame();
    countAllUnitsInTheGame();
    fuzzyMapSize();
    fuzzyMapType();
    fuzzyBattleConditions();
    fuzzyConstructionPossibilities();
    fuzzyGameProgress();
    calculateDistanceFromFactoriesToAllCitiesThatCanBeCaptured();
    citiesThatCanBuild();
    findMostExpensiveUnits(5);
    findHighThreats();
    logGeneratedData();
    return data;
  }

  private void checkFinances() {
    int budget = activePlayer.getBudget();

    if (budget == 0) {
      data.finance = Fuz.PLAYER_FINANCE.BANKRUPT;
    } else if (budget < 8000) {
      data.finance = Fuz.PLAYER_FINANCE.LOW;
    } else if (budget < 12000) {
      data.finance = Fuz.PLAYER_FINANCE.FAIR;
    } else if (budget < 18000) {
      data.finance = Fuz.PLAYER_FINANCE.MUCH;
    } else {
      data.finance = Fuz.PLAYER_FINANCE.RICH;
    }
  }

  private void calculateVisibleLandTilesPercentage() {
    int visibleLandTiles = 0;

    for (Tile t : map.getAllTiles()) {
      if (!t.isFogged() && t.getTerrain().isLand()) {
        visibleLandTiles++;
      }
    }

    data.visibleLandTiles = NumberUtil.calcPercentage(visibleLandTiles, map.countTiles());
  }

  private void countAllCitiesInTheGame() {
    java.util.Map<City, Integer> neutralCityCountByCityName = getNeutralCityCount();
    Player neutralPlayer = game.getMap().getNeutralPlayer();
    List<City> baseCities = CityFactory.getBaseCities();

    for (City city : neutralCityCountByCityName.keySet()) {
      if (isBaseCity(city.getName(), baseCities)) {
        int cityCount = neutralCityCountByCityName.get(city);
        data.setCityCount(neutralPlayer, city.getName(), cityCount);
      }
    }

    for (Player player : game.getActivePlayers()) {
      for (String cityName : CityFactory.getAllCityNames()) {
        if (isBaseCity(cityName, baseCities)) {
          int cityCount = countCities(player, cityName);
          data.setCityCount(player, cityName, cityCount);
        }
      }
    }
  }

  private boolean isBaseCity(String cityName, List<City> baseCities) {
    for (City baseCity : baseCities) {
      if (baseCity.getName().equalsIgnoreCase(cityName)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Counts all the neutral cities in this map.
   *
   * @return A map of City -> City count entries
   */
  public java.util.Map<City, Integer> getNeutralCityCount() {
    HashMap<City, Integer> neutralCities = new HashMap<City, Integer>();

    for (Tile t : map.getAllTiles()) {
      City city = map.getCityOn(t);

      if (city != null) {
        Player player = city.getOwner();

        if (player.isNeutral()) {
          int currentCount;
          if (neutralCities.containsKey(city)) {
            currentCount = neutralCities.get(city);
          } else {
            currentCount = 0;
          }

          neutralCities.put(city, currentCount + 1);
        }
      }
    }

    return neutralCities;
  }

  private int countCities(Player player, String cityName) {
    int cityCount = 0;

    for (City city : player.getAllCities()) {
      if (city.getName().equalsIgnoreCase(cityName)) {
        cityCount++;
      }
    }
    return cityCount;
  }

  private void countAllUnitsInTheGame() {
    for (Player player : game.getActivePlayers()) {
      for (String unitName : UnitFactory.getAllUnitNames()) {
        int unitCount = countUnits(player, unitName);
        data.setUnitCount(player, unitName, unitCount);
      }
    }
  }

  private int countUnits(Player player, String unitName) {
    int unitCount = 0;

    for (Unit unit : player.getArmy()) {
      if (unit.getStats().getName().equalsIgnoreCase(unitName)) {
        unitCount++;
      }
    }
    return unitCount;
  }

  private void fuzzyMapSize() {
    int rows = map.getRows();
    int cols = map.getCols();

    if (rows < 10 || cols < 10) {
      data.mapSize = Fuz.MAP_SIZE.TINY;
    } else if (rows < 18 || cols < 18) {
      data.mapSize = Fuz.MAP_SIZE.SMALL;
    } else if (rows < 25 || cols < 25) {
      data.mapSize = Fuz.MAP_SIZE.NORMAL;
    } else if (rows < 35 || cols < 35) {
      data.mapSize = Fuz.MAP_SIZE.LARGE;
    } else {
      data.mapSize = Fuz.MAP_SIZE.HUGE;
    }
  }

  private void fuzzyMapType() {
    int countOceanTiles = countTerrain("ocean");
    int countLandTiles = countTerrain("plain") + countTerrain("forest") + countTerrain("road") + countTerrain("pipe") + countTerrain("wall");
    int landPercentage = NumberUtil.calcPercentage(countLandTiles, countLandTiles + countOceanTiles);

    if (landPercentage < 25) {
      data.mapType = Fuz.MAP_TYPE.ISLANDS;
    } else if (landPercentage < 60) {
      data.mapType = Fuz.MAP_TYPE.PENINSULA;
    } else {
      data.mapType = Fuz.MAP_TYPE.PANGEA;
    }
  }

  private int countTerrain(String terrain) {
    int terrainCount = 0;
    for (Tile t : map.getAllTiles()) {
      if (t.getTerrain().getType().equalsIgnoreCase(terrain)) {
        terrainCount++;
      }
    }

    return terrainCount;
  }

  private void fuzzyBattleConditions() {
    HashMap<Player, Integer> netWorth = new HashMap<Player, Integer>();

    int totalWorth = 0;
    for (Player player : game.getActivePlayers()) {
      if (player.getArmyCount() > 2) {
        int budget = player.getBudget();
        totalWorth += budget;

        for (Unit unit : player.getArmy()) {
          totalWorth += unit.getPrice();
        }
        netWorth.put(player, totalWorth);
      }
    }

    int higestWorth = 0;
    for (Player player : netWorth.keySet()) {
      int playerWorth = netWorth.get(player);
      if (playerWorth > higestWorth) {
        higestWorth = playerWorth;
      }
    }

    int lowestWorth = Integer.MAX_VALUE;
    for (Player player : netWorth.keySet()) {
      int playerWorth = netWorth.get(player);
      if (playerWorth < lowestWorth) {
        lowestWorth = playerWorth;
      }
    }

    int worth = 0;
    Player activePlayer = this.activePlayer;
    if (netWorth.containsKey(activePlayer)) {
      worth = netWorth.get(activePlayer);
    }

    if (worth > higestWorth) {
      data.battleConditions = Fuz.BATTLE_CONDITION.WINNING;
    } else if (worth < higestWorth && worth > lowestWorth) {
      data.battleConditions = Fuz.BATTLE_CONDITION.EVEN;
    } else {
      data.battleConditions = Fuz.BATTLE_CONDITION.LOSING;
    }
  }

  private void fuzzyConstructionPossibilities() {
    boolean canBuildLand = false;
    boolean canBuildAir = false;
    boolean canBuildSea = false;

    for (City city : activePlayer.getAllCities()) {
      if (city.canBuild("infantry")) {
        canBuildLand = true;
        break;
      }
    }
    for (City city : activePlayer.getAllCities()) {
      if (city.canBuild("bomber")) {
        canBuildAir = true;
        break;
      }
    }

    for (City city : activePlayer.getAllCities()) {
      if (city.canBuild("cruiser")) {
        canBuildSea = true;
        break;
      }
    }

    if (canBuildLand) data.addConstructionPossibility(Fuz.CONSTRUCTION_POSSIBILITY.LAND);
    if (canBuildAir) data.addConstructionPossibility(Fuz.CONSTRUCTION_POSSIBILITY.AIR);
    if (canBuildSea) data.addConstructionPossibility(Fuz.CONSTRUCTION_POSSIBILITY.SEA);
  }

  private void fuzzyGameProgress() {
    if (game.getDay() < 10) {
      data.gameProgress = Fuz.GAME_PROGRESS.EARLY_GAME;
    } else {
      data.gameProgress = Fuz.GAME_PROGRESS.IN_GAME;
    }
  }

  private void calculateDistanceFromFactoriesToAllCitiesThatCanBeCaptured() {
    List<City> allEnemyCities = new ArrayList<City>();

    for (City city : getNeutralCityCount().keySet()) {
      if (city.canBeCaptured()) {
        allEnemyCities.add(city);
      }
    }

    // Find every city in the map
    // That is not owned by the active player
    for (Player player : game.getAllPlayers()) {
      if (player != activePlayer) {
        for (City city : player.getAllCities()) {
          if (city.canBeCaptured()) {
            allEnemyCities.add(city);
          }
        }
      }
    }

    City[] factories = getCitiesThatCanBuild(activePlayer);

    // Find the distance between each factory and each other enemy city
    for (City factory : factories) {
      for (City city : allEnemyCities) {
        int distance = TileMap.getDistanceBetween(factory.getLocation(), city.getLocation());

        if (distance < 4) {
          data.addCityDistance(factory, city, Fuz.DISTANCE.VERY_CLOSE);
        } else if (distance < 7) {
          data.addCityDistance(factory, city, Fuz.DISTANCE.CLOSE);
        } else if (distance < 10) {
          data.addCityDistance(factory, city, Fuz.DISTANCE.FAR);
        } else if (distance < 16) {
          data.addCityDistance(factory, city, Fuz.DISTANCE.VERY_FAR);
        } else {
          data.addCityDistance(factory, city, Fuz.DISTANCE.UNREACHABLE);
        }
      }
    }
  }

  private City[] getCitiesThatCanBuild(Player p) {
    List<City> factories = new ArrayList<City>();

    for (City c : p.getAllCities()) {
      boolean isEmpty = game.getMap().getUnitOn(c.getLocation()) == null;

      if (c.canBuild() && isEmpty) {
        factories.add(c);
      }
    }
    return factories.toArray(new City[]{});
  }

  private void citiesThatCanBuild() {
    List<City> factories = new ArrayList<City>();

    for (City c : activePlayer.getAllCities()) {
      boolean hasUnit = game.getMap().hasUnitOn(c.getLocation());

      if (c.canBuild() && !hasUnit) {
        factories.add(c);
      }
    }

    data.factories = factories.toArray(new City[factories.size()]);
  }

  private void findMostExpensiveUnits(int max) {
    List<Unit> allUnits = UnitFactory.getAllUnits();
    List<Unit> unitCopies = new ArrayList<Unit>(allUnits);

    Collections.sort(unitCopies, new Comparator<Unit>() {
      public int compare(Unit o1, Unit o2) {
        return o2.getPrice() - o1.getPrice();
      }
    });

    List<String> mostExpensiveUnits = new ArrayList<String>(allUnits.size());
    for (Unit unit : unitCopies) {
      mostExpensiveUnits.add(unit.getName().toUpperCase());
    }

    List<String> topExpensiveUnits = Collections.unmodifiableList(mostExpensiveUnits.subList(0, max + 1));
    data.setMostExpensiveUnits(topExpensiveUnits);
  }

  /**
   * for each unit of the current AI player
   * find nearby enemies
   * get best unit against this enemy
   * closer == higher multiplier
   */
  private void findHighThreats() {
    List<Enemy> allEnemyUnits = new ArrayList<Enemy>();

    for (Unit unit : activePlayer.getArmy()) {
      if (!unit.isInTransport()) {
        List<Enemy> enemyUnits = findEnemiesNearby(unit);
        allEnemyUnits.addAll(enemyUnits);
      }
    }

    Collections.sort(allEnemyUnits, new Comparator<Enemy>() {
      public int compare(Enemy o1, Enemy o2) {
        return o1.distance - o2.distance;
      }
    });

    data.setHighThreatsInMap(allEnemyUnits);
  }

  private List<Enemy> findEnemiesNearby(Unit unit) {
    List<Enemy> enemies = new ArrayList<Enemy>();

    for (Tile t : map.getSurroundingTiles(unit.getLocation(), new Range(1, 20))) {
      Unit enemyUnit = map.getUnitOn(t);

      if (enemyUnit != null && !enemyUnit.isAlliedWith(activePlayer)) {
        City hq = activePlayer.getHq();

        // A game can be played without a HQ
        if (hq != null) {
          Location hqLocation = hq.getLocation();
          int distance = TileMap.getDistanceBetween(hqLocation, enemyUnit.getLocation());
          Enemy enemy = new Enemy(enemyUnit, distance);
          enemies.add(enemy);
        } else {
          Location unitLocation = unit.getLocation();
          int distance = TileMap.getDistanceBetween(unitLocation, enemyUnit.getLocation());
          Enemy enemy = new Enemy(enemyUnit, distance);
          enemies.add(enemy);
        }
      }
    }

    return enemies;
  }

  private void logGeneratedData() {
    logger.debug("Battle Conditions: " + data.battleConditions);
    logger.debug("Finances: " + data.finance);
    logger.debug("Game Progress: " + data.gameProgress);
    logger.debug("Map Size: " + data.mapSize);
    logger.debug("Map Type: " + data.mapType);
    logger.debug("Visible land tiles: " + data.visibleLandTiles + "%");

    logger.debug("Units: ");
    for (Player player : data.unitsCountByPlayer.keySet()) {
      for (String unitName : UnitFactory.getAllUnitNames()) {
        int count = data.getUnitCount(player, unitName);

        if (count != 0) {
          logger.debug(player.getName() + " " + unitName + " " + count);
        }
      }
    }

    logger.debug("Cities: ");
    for (Player player : data.cityCountByPlayer.keySet()) {
      for (City city : CityFactory.getBaseCities()) {
        int count = data.getCityCount(player, city.getName());

        if (count != 0) {
          logger.debug(player.getName() + " " + city.getName() + " " + count);
        }
      }
    }

    logger.debug("City Distances: ");
    for (Player player : game.getAllPlayers()) {
      for (City city : getCitiesThatCanBuild(player)) {
        Fuz.DISTANCE distance = data.getDistanceToNearestCity(city);
        logger.debug(city.getName() + " " + distance);
      }
    }

    for (City city : getCitiesThatCanBuild(map.getNeutralPlayer())) {
      Fuz.DISTANCE distance = data.getDistanceToNearestCity(city);
      logger.debug(city.getName() + " " + distance);
    }

    logger.debug("Expensive units: ");
    printUnits(data.mostExpensiveUnits);

    logger.debug("Construction: " + data.constructionPossibilities);
    logger.debug("Factories: " + data.factories.length);
  }

  private void printUnits(List<String> unitNames) {
    for (String unitName : unitNames) {
      logger.debug(unitName);
    }
  }
}
