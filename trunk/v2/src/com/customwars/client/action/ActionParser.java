package com.customwars.client.action;

import com.customwars.client.model.drop.DropLocationsQueue;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.game.Player;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Terrain;
import com.customwars.client.model.gameobject.TerrainFactory;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.gameobject.UnitFactory;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import org.apache.log4j.Logger;

import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Parse a string into a CWAction
 *
 * @author stefan
 */
public class ActionParser {
  private static final Logger logger = Logger.getLogger(ActionParser.class);
  private final Game game;
  private final Map<Tile> map;

  public ActionParser(Game game) {
    this.game = game;
    this.map = game.getMap();
  }

  public CWAction parse(String str) {
    try {
      return parseAction(str);
    } catch (InputMismatchException ex) {
      logger.warn("Could not parse action out of " + str, ex);
      throw new InputMismatchException("Could not parse action out of " + str);
    }
  }

  private CWAction parseAction(String str) {
    String cmd = str.trim().toLowerCase();
    Scanner scanner = new Scanner(cmd);
    String actionName = scanner.next();

    if (actionName.equals("drop")) {
      return parseDrop(scanner);
    } else if (actionName.equals("move")) {
      return parseMove(scanner);
    } else if (actionName.equals("capture")) {
      return parseCapture(scanner);
    } else if (actionName.equals("load")) {
      return parseLoad(scanner);
    } else if (actionName.equals("supply")) {
      return parseSupply(scanner);
    } else if (actionName.equals("join")) {
      return parseJoin(scanner);
    } else if (actionName.equals("attack_unit")) {
      return parseAttackUnit(scanner);
    } else if (actionName.equals("attack_city")) {
      return parseAttackCity(scanner);
    } else if (actionName.equals("build_unit")) {
      return parseBuildUnit(scanner);
    } else if (actionName.equals("flare")) {
      return parseFlare(scanner);
    } else if (actionName.equals("transform_terrain")) {
      return parseTransformTerrain(scanner);
    } else if (actionName.equals("launch_rocket")) {
      return parseLaunchRocketAction(scanner);
    } else if (actionName.equals("build_city")) {
      return parseConstructCity(scanner);
    } else if (actionName.equals("dive")) {
      return parseDive(scanner);
    } else if (actionName.equals("surface")) {
      return parseSurface(scanner);
    } else if (actionName.equals("produce")) {
      return parseProduce(scanner);
    } else if (actionName.equals("end_turn")) {
      return ActionFactory.buildEndTurnAction();
    } else {
      logger.warn("Unknown action " + actionName);
      throw new IllegalArgumentException("unknown action " + actionName);
    }
  }

  private CWAction parseDrop(Scanner scanner) {
    int fromCol = scanner.nextInt();
    int fromRow = scanner.nextInt();
    int toCol = scanner.nextInt();
    int toRow = scanner.nextInt();
    Location from = map.getTile(fromCol, fromRow);
    Location to = map.getTile(toCol, toRow);
    Unit transport = map.getUnitOn(from);

    DropLocationsQueue dropQueue = new DropLocationsQueue();
    while (scanner.hasNextInt()) {
      int transportIndex = scanner.nextInt();
      int dropCol = scanner.nextInt();
      int dropRow = scanner.nextInt();
      Location dropLocation = map.getTile(dropCol, dropRow);
      Unit unit = (Unit) transport.getLocatable(transportIndex);
      dropQueue.addDropLocation(dropLocation, unit);
    }

    return ActionFactory.buildDropAction(transport, from, to, dropQueue);
  }

  private CWAction parseMove(Scanner scanner) {
    int fromCol = scanner.nextInt();
    int fromRow = scanner.nextInt();
    int toCol = scanner.nextInt();
    int toRow = scanner.nextInt();
    Unit unit = map.getUnitOn(fromCol, fromRow);
    Location to = map.getTile(toCol, toRow);
    return ActionFactory.buildMoveAction(unit, to);
  }

  private CWAction parseCapture(Scanner scanner) {
    int fromCol = scanner.nextInt();
    int fromRow = scanner.nextInt();
    int toCol = scanner.nextInt();
    int toRow = scanner.nextInt();
    Unit unit = map.getUnitOn(fromCol, fromRow);
    City city = map.getCityOn(toCol, toRow);
    return ActionFactory.buildCaptureAction(unit, city);
  }

  private CWAction parseLoad(Scanner scanner) {
    int fromCol = scanner.nextInt();
    int fromRow = scanner.nextInt();
    int toCol = scanner.nextInt();
    int toRow = scanner.nextInt();
    Unit unit = map.getUnitOn(fromCol, fromRow);
    Unit transport = map.getUnitOn(toCol, toRow);
    return ActionFactory.buildLoadAction(unit, transport);
  }

  private CWAction parseSupply(Scanner scanner) {
    int fromCol = scanner.nextInt();
    int fromRow = scanner.nextInt();
    int toCol = scanner.nextInt();
    int toRow = scanner.nextInt();
    Unit supplier = map.getUnitOn(fromCol, fromRow);
    Location to = map.getTile(toCol, toRow);
    return ActionFactory.buildSupplyAction(supplier, to);
  }

  private CWAction parseJoin(Scanner scanner) {
    int fromCol = scanner.nextInt();
    int fromRow = scanner.nextInt();
    int toCol = scanner.nextInt();
    int toRow = scanner.nextInt();
    Unit unit = map.getUnitOn(fromCol, fromRow);
    Unit target = map.getUnitOn(toCol, toRow);
    return ActionFactory.buildJoinAction(unit, target);
  }

  private CWAction parseAttackUnit(Scanner scanner) {
    int fromCol = scanner.nextInt();
    int fromRow = scanner.nextInt();
    int toCol = scanner.nextInt();
    int toRow = scanner.nextInt();
    int defenderCol = scanner.nextInt();
    int defenderRow = scanner.nextInt();
    Unit attacker = map.getUnitOn(fromCol, fromRow);
    Unit defender = map.getUnitOn(defenderCol, defenderRow);
    Location moveToLocation = map.getTile(toCol, toRow);
    return ActionFactory.buildUnitVsUnitAttackAction(attacker, defender, moveToLocation);
  }

  private CWAction parseAttackCity(Scanner scanner) {
    int fromCol = scanner.nextInt();
    int fromRow = scanner.nextInt();
    int toCol = scanner.nextInt();
    int toRow = scanner.nextInt();
    int cityCol = scanner.nextInt();
    int cityRow = scanner.nextInt();
    Unit unit = map.getUnitOn(fromCol, fromRow);
    Location to = map.getTile(toCol, toRow);
    City city = map.getCityOn(cityCol, cityRow);
    return ActionFactory.buildUnitVsCityAttackAction(unit, city, to);
  }

  private CWAction parseBuildUnit(Scanner scanner) {
    int col = scanner.nextInt();
    int row = scanner.nextInt();
    int unitID = scanner.nextInt();
    int playerID = scanner.nextInt();
    Tile selectTile = map.getTile(col, row);
    Unit unit = UnitFactory.getUnit(unitID);
    Player player = game.getPlayerByID(playerID);
    unit.setOwner(player);
    return ActionFactory.buildAddUnitToTileAction(unit, selectTile, false);
  }

  private CWAction parseLaunchRocketAction(Scanner scanner) {
    int fromCol = scanner.nextInt();
    int fromRow = scanner.nextInt();
    int toCol = scanner.nextInt();
    int toRow = scanner.nextInt();
    int rocketDestinationCol = scanner.nextInt();
    int rocketDestinationRow = scanner.nextInt();
    Unit unit = map.getUnitOn(fromCol, fromRow);
    City city = map.getCityOn(toCol, toRow);
    Location rocketDestination = map.getTile(rocketDestinationCol, rocketDestinationRow);
    return ActionFactory.buildLaunchRocketAction(unit, city, rocketDestination);
  }

  private CWAction parseTransformTerrain(Scanner scanner) {
    int fromCol = scanner.nextInt();
    int fromRow = scanner.nextInt();
    int toCol = scanner.nextInt();
    int toRow = scanner.nextInt();
    int terrainID = scanner.nextInt();
    Location from = map.getTile(fromCol, fromRow);
    Location to = map.getTile(toCol, toRow);
    Unit unit = map.getUnitOn(from);
    Terrain transformToTerrain = TerrainFactory.getTerrain(terrainID);
    return ActionFactory.buildTransformTerrainAction(unit, to, transformToTerrain);
  }

  private CWAction parseFlare(Scanner scanner) {
    int fromCol = scanner.nextInt();
    int fromRow = scanner.nextInt();
    int toCol = scanner.nextInt();
    int toRow = scanner.nextInt();
    int flareCol = scanner.nextInt();
    int flareRow = scanner.nextInt();
    Unit unit = map.getUnitOn(fromCol, fromRow);
    Location to = map.getTile(toCol, toRow);
    Tile flareCenter = map.getTile(flareCol, flareRow);
    return ActionFactory.buildFireFlareAction(unit, to, flareCenter);
  }

  private CWAction parseConstructCity(Scanner scanner) {
    int fromCol = scanner.nextInt();
    int fromRow = scanner.nextInt();
    int toCol = scanner.nextInt();
    int toRow = scanner.nextInt();
    int cityID = scanner.nextInt();
    Location from = map.getTile(fromCol, fromRow);
    Location to = map.getTile(toCol, toRow);
    Unit unit = map.getUnitOn(from);
    return ActionFactory.buildConstructCityAction(unit, cityID, to);
  }

  private CWAction parseDive(Scanner scanner) {
    int fromCol = scanner.nextInt();
    int fromRow = scanner.nextInt();
    int toCol = scanner.nextInt();
    int toRow = scanner.nextInt();
    Unit unit = map.getUnitOn(fromCol, fromRow);
    Location to = map.getTile(toCol, toRow);
    return ActionFactory.buildDiveAction(unit, to);
  }

  private CWAction parseSurface(Scanner scanner) {
    int fromCol = scanner.nextInt();
    int fromRow = scanner.nextInt();
    int toCol = scanner.nextInt();
    int toRow = scanner.nextInt();
    Unit unit = map.getUnitOn(fromCol, fromRow);
    Location to = map.getTile(toCol, toRow);
    return ActionFactory.buildSurfaceAction(unit, to);
  }

  private CWAction parseProduce(Scanner scanner) {
    int fromCol = scanner.nextInt();
    int fromRow = scanner.nextInt();
    int toCol = scanner.nextInt();
    int toRow = scanner.nextInt();
    int unitID = scanner.nextInt();
    Unit producer = map.getUnitOn(fromCol, fromRow);
    Location to = map.getTile(toCol, toRow);
    Unit unitToBuild = UnitFactory.getUnit(unitID);
    return ActionFactory.buildProduceUnitAction(producer, unitToBuild, to);
  }
}