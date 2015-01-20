package com.customwars.client.model.ai.build;

import com.customwars.client.model.ai.fuzzy.BuildAIDataGenerator;
import com.customwars.client.model.ai.fuzzy.BuildAIInformation;
import com.customwars.client.model.ai.fuzzy.Enemy;
import com.customwars.client.model.ai.fuzzy.Fuz;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.game.Player;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.gameobject.UnitFightStats;
import com.customwars.client.model.map.Map;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * The Build Advisor evaluates the game and creates a BuildStrategy.
 *
 * A BuildStrategy contains:
 * <li>
 * <ol>A global build priority list</ol>
 * <ol>Build hints for each individual city that can build units.</ol>
 * </li>
 *
 * <li>
 * <ol>for a unit that can load the co</ol>
 * <ol>for units that can produce units</ol>
 * </li>
 *
 * The game is seen from the point of view of the active player.
 *
 * The hints are an indication what the best 'unit' is for each individual factory.
 * For example we might have found a neutral city near a factory.
 * The hint will indicate that we need a capturing unit[inf,mech,bikes] on THAT factory.
 * The priority list will just contain a build infantry with priority VERY_HIGH but not where to build it.
 *
 * Note that more then 1 unit will be returned in the build Priority List and build hints List.
 * It's up to others to use this information and actually build the units.
 * todo Carrier produce, load CO
 */
public class DefaultBuildAdvisor implements BuildAdvisor {
  public static final Comparator<BuildPriority> PRIORITY_COMPARATOR = new Comparator<BuildPriority>() {
    @Override
    public int compare(BuildPriority o1, BuildPriority o2) {
      return o2.priority.compareTo(o1.priority);
    }
  };

  private static final String TRANSPORT_HELI = "TCOPTER";
  private static final String GROUND_TRANSPORT = "APC";

  public static final String INF = "INFANTRY";
  public static final String MECH = "MECH";
  public static final String BIKES = "BIKES";
  public static final String RECON = "RECON";
  public static final String TANK = "LIGHT_TANK";
  public static final String ANTI_AIR = "ANTI_AIR";
  private List<String> LIMITED_TO_4_UNITS = Arrays.asList(RECON, TANK, ANTI_AIR);

  private Game game;
  private Map map;
  private BuildAIInformation data;
  private UnitFightStats unitFightStats;

  private List<BuildPriority> unitBuildPriority;
  private HashMap<City, List<Fuz.UNIT_TYPE>> buildHintsByCity;

  public DefaultBuildAdvisor(Game game) {
    this.game = game;
    this.map = game.getMap();
    unitBuildPriority = new ArrayList<BuildPriority>();
    buildHintsByCity = new HashMap<City, List<Fuz.UNIT_TYPE>>();
  }

  public BuildStrategy think() {
    init();

    if (map.isFogOfWarOn()) {
      explorer();
    }

    offensive();
    expand();

    Collections.sort(unitBuildPriority, PRIORITY_COMPARATOR);
    return new BuildStrategy(unitBuildPriority, buildHintsByCity);
  }

  private void init() {
    BuildAIDataGenerator mapInfoGenerator = new BuildAIDataGenerator(game);
    data = mapInfoGenerator.generate();

    unitFightStats = new UnitFightStats();
    unitFightStats.generate();

    for (City city : data.factories) {
      buildHintsByCity.put(city, new ArrayList<Fuz.UNIT_TYPE>());
    }
  }

  /**
   * In fog of war it's vital to explorer the map
   */
  private void explorer() {
    int visibleTilesPercentage = data.getVisibleTilesPercentage();
    boolean earlyGame = data.gameProgress == Fuz.GAME_PROGRESS.EARLY_GAME;
    boolean smallMap = data.mapSize == Fuz.MAP_SIZE.SMALL || data.mapSize == Fuz.MAP_SIZE.TINY;
    boolean canBuildGroundUnits = data.canConstruct(Fuz.CONSTRUCTION_POSSIBILITY.LAND);
    boolean mustExplorerMore = visibleTilesPercentage < 20;
    int tankCount = countUnits(game.getActivePlayer(), TANK);
    int reconCount = countUnits(game.getActivePlayer(), RECON);
    int bikesCount = countUnits(game.getActivePlayer(), BIKES);
    int infCount = countUnits(game.getActivePlayer(), INF);

    if (canBuildGroundUnits) {
      if (earlyGame) {
        if (infCount < 10) {
          addBuildPriority(INF, Fuz.BUILD_PRIORITY.HIGH);
        }
        if (bikesCount < 2) {
          addBuildPriority(BIKES, Fuz.BUILD_PRIORITY.VERY_HIGH);
        }
        if (reconCount == 0) {
          addBuildPriority(RECON, Fuz.BUILD_PRIORITY.HIGH);
        }
      } else if (mustExplorerMore) {
        if (smallMap) {
          if (tankCount == 0) {
            addBuildPriority(TANK, Fuz.BUILD_PRIORITY.HIGH);
          }
          if (bikesCount < 4) {
            addBuildPriority(BIKES, Fuz.BUILD_PRIORITY.VERY_HIGH);
          }
        } else {
          if (tankCount < 3) {
            addBuildPriority(TANK, Fuz.BUILD_PRIORITY.HIGH);
          }
          if (bikesCount < 6) {
            addBuildPriority(BIKES, Fuz.BUILD_PRIORITY.HIGH);
          }
        }
      }
    }
  }

  /**
   * Find the 5 highest unit threats in the game.
   * Find the Best units against these threats.
   *
   * The priority increases if the threat is nearer.
   */
  private void offensive() {
    List<Enemy> highThreats = data.getHighThreatsInMap();
    int highThreatCount = highThreats.size();

    if (highThreatCount > 0) {
      List<Enemy> topUnitThreats;

      if (highThreatCount >= 5) {
        topUnitThreats = highThreats.subList(0, 5);
      } else {
        topUnitThreats = highThreats.subList(0, highThreatCount);
      }

      for (Enemy enemy : topUnitThreats) {
        String enemyName = enemy.unit.getName();
        List<Unit> bestAttackers = unitFightStats.getTopEnemies(enemyName, 10);

        for (int bestAttackIndex = 0; bestAttackIndex < bestAttackers.size(); bestAttackIndex++) {
          Unit unit = bestAttackers.get(bestAttackIndex);
          String unitName = unit.getName().toUpperCase();

          if (canBuild(unit)) {
            if (LIMITED_TO_4_UNITS.contains(unitName.toUpperCase())) {
              int unitCount = data.getUnitCount(game.getActivePlayer(), unitName);
              if (unitCount >= 4) {
                return;
              }
            }


            if (enemy.distance < 5) {
              if (bestAttackIndex < 5) {
                addBuildPriority(unitName, Fuz.BUILD_PRIORITY.CRITICAL);
              } else {
                addBuildPriority(unitName, Fuz.BUILD_PRIORITY.VERY_HIGH);
              }
            } else if (enemy.distance < 9) {
              if (bestAttackIndex < 5) {
                addBuildPriority(unitName, Fuz.BUILD_PRIORITY.VERY_HIGH);
              } else {
                addBuildPriority(unitName, Fuz.BUILD_PRIORITY.HIGH);
              }
            } else if (enemy.distance < 15) {
              addBuildPriority(unitName, Fuz.BUILD_PRIORITY.NORMAL);
            } else {
              addBuildPriority(unitName, Fuz.BUILD_PRIORITY.LOW);
            }
          }
        }
      }
    }
  }

  private boolean canBuild(Unit unit) {
    for (City city : data.factories) {
      if (city.canBuild(unit.getName())) {
        return true;
      }
    }

    return false;
  }

  private void expand() {
    for (City factory : data.factories) {
      Fuz.DISTANCE distanceToNearestCity = data.getDistanceToNearestCity(factory);

      if (distanceToNearestCity != null) {
        switch (distanceToNearestCity) {
          case VERY_CLOSE:
            cityIsVeryClose(factory);
            break;
          case CLOSE:
            cityIsClose(factory);
            break;
          case FAR:
            cityIsFarAwayFrom(factory);
            break;
          case VERY_FAR:
            cityIsVeryFarAway(factory);
            break;
          case UNREACHABLE:
            break;
        }
      }
    }
  }

  /**
   * When an uncaptured city is very close near a factory.
   * Make sure that an infantry unit is available.
   */
  private void cityIsVeryClose(City factory) {
    boolean canBuildGroundUnits = factory.canBuild(INF.toLowerCase());
    addBuildHint(factory, Fuz.UNIT_TYPE.CAPTURE);

    if (canBuildGroundUnits) {
      addBuildPriority(INF, Fuz.BUILD_PRIORITY.VERY_HIGH);
      addBuildPriority(MECH, Fuz.BUILD_PRIORITY.HIGH);
      addBuildPriority(BIKES, Fuz.BUILD_PRIORITY.HIGH);
    }
  }

  private void cityIsClose(City factory) {
    boolean canBuildGroundUnits = factory.canBuild(INF.toLowerCase());
    addBuildHint(factory, Fuz.UNIT_TYPE.CAPTURE);

    if (canBuildGroundUnits) {
      if (data.gameProgress == Fuz.GAME_PROGRESS.EARLY_GAME) {
        addBuildPriority(BIKES, Fuz.BUILD_PRIORITY.VERY_HIGH);
        addBuildPriority(INF, Fuz.BUILD_PRIORITY.HIGH);
      } else if (data.finance == Fuz.PLAYER_FINANCE.LOW) {
        addBuildPriority(INF, Fuz.BUILD_PRIORITY.HIGH);
        addBuildPriority(BIKES, Fuz.BUILD_PRIORITY.NORMAL);
      } else {
        addBuildPriority(MECH, Fuz.BUILD_PRIORITY.HIGH);
        addBuildPriority(INF, Fuz.BUILD_PRIORITY.NORMAL);
        addBuildPriority(BIKES, Fuz.BUILD_PRIORITY.LOW);
      }
    }
  }

  private void cityIsFarAwayFrom(City factory) {
    boolean canBuildGroundUnits = factory.canBuild(INF.toLowerCase());

    if (canBuildGroundUnits) {
      if (data.gameProgress == Fuz.GAME_PROGRESS.EARLY_GAME) {
        addBuildHint(factory, Fuz.UNIT_TYPE.CAPTURE);
        addBuildPriority(BIKES, Fuz.BUILD_PRIORITY.VERY_HIGH);
        addBuildPriority(INF, Fuz.BUILD_PRIORITY.NORMAL);
      } else if (data.finance == Fuz.PLAYER_FINANCE.LOW) {
        addBuildPriority(INF, Fuz.BUILD_PRIORITY.VERY_HIGH);
        addBuildPriority(BIKES, Fuz.BUILD_PRIORITY.HIGH);
      } else {
        addBuildPriority(MECH, Fuz.BUILD_PRIORITY.HIGH);
        addBuildPriority(INF, Fuz.BUILD_PRIORITY.NORMAL);
        addBuildPriority(BIKES, Fuz.BUILD_PRIORITY.HIGH);
      }
    }

    // We might need a transport here
    boolean canBuildAirUnits = factory.canBuild(TRANSPORT_HELI);
    int tCopterCount = data.getUnitCount(game.getActivePlayer(), TRANSPORT_HELI);

    if (canBuildAirUnits) {
      addBuildHint(factory, Fuz.UNIT_TYPE.CAPTURE);

      if (data.mapSize == Fuz.MAP_SIZE.SMALL) {
        if (tCopterCount < 2) {
          addBuildPriority(TRANSPORT_HELI, Fuz.BUILD_PRIORITY.NORMAL);
        }
      } else if (data.mapSize == Fuz.MAP_SIZE.NORMAL) {
        if (tCopterCount < 3) {
          addBuildPriority(TRANSPORT_HELI, Fuz.BUILD_PRIORITY.NORMAL);
        }
      } else if (data.mapSize == Fuz.MAP_SIZE.LARGE) {
        if (tCopterCount < 4) {
          addBuildPriority(TRANSPORT_HELI, Fuz.BUILD_PRIORITY.NORMAL);
        }
      } else if (data.mapSize == Fuz.MAP_SIZE.HUGE) {
        if (tCopterCount < 5) {
          addBuildPriority(TRANSPORT_HELI, Fuz.BUILD_PRIORITY.NORMAL);
        }
      }
    }

    int apcCount = data.getUnitCount(game.getActivePlayer(), GROUND_TRANSPORT);

    if (canBuildGroundUnits) {
      if (data.mapSize == Fuz.MAP_SIZE.SMALL) {
        if (apcCount < 2) {
          addBuildPriority(GROUND_TRANSPORT, Fuz.BUILD_PRIORITY.NORMAL);
        }
      } else if (data.mapSize == Fuz.MAP_SIZE.NORMAL) {
        if (apcCount < 3) {
          addBuildPriority(GROUND_TRANSPORT, Fuz.BUILD_PRIORITY.NORMAL);
        }
      } else if (data.mapSize == Fuz.MAP_SIZE.LARGE) {
        if (apcCount < 4) {
          addBuildPriority(GROUND_TRANSPORT, Fuz.BUILD_PRIORITY.NORMAL);
        }
      } else if (data.mapSize == Fuz.MAP_SIZE.HUGE) {
        if (apcCount < 5) {
          addBuildPriority(GROUND_TRANSPORT, Fuz.BUILD_PRIORITY.NORMAL);
        }
      }
    }
  }

  private void cityIsVeryFarAway(City factory) {
    cityIsFarAwayFrom(factory);
  }

  private void addBuildHint(City city, Fuz.UNIT_TYPE buildType) {
    buildHintsByCity.get(city).add(buildType);
  }

  private void addBuildPriority(String unitName, Fuz.BUILD_PRIORITY newPriority) {
    BuildPriority buildPriority = new BuildPriority(unitName, newPriority);
    int index = unitBuildPriority.indexOf(buildPriority);

    if (index != -1) {
      // Overwrite previous value
      // Priority can only be increased
      Fuz.BUILD_PRIORITY currentPriority = unitBuildPriority.get(index).priority;

      if (newPriority.ordinal() > currentPriority.ordinal()) {
        unitBuildPriority.set(index, new BuildPriority(unitName, newPriority));
      }
    } else {
      unitBuildPriority.add(new BuildPriority(unitName, newPriority));
    }
  }

  private int countUnits(Player p, String name) {
    return data.getUnitCount(p, name);
  }

}
