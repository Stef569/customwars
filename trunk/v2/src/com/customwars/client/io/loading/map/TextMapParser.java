package com.customwars.client.io.loading.map;

import com.customwars.client.model.game.Player;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.CityFactory;
import com.customwars.client.model.gameobject.Terrain;
import com.customwars.client.model.gameobject.TerrainFactory;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.gameobject.UnitFactory;
import com.customwars.client.model.map.Map;
import com.customwars.client.tools.MapUtil;

import java.awt.Color;
import java.util.HashMap;

/**
 * Parses a String[][] array into a Map.
 *
 * For example:
 * <pre>
 * private static final String[][] testMapArray = new String[][]{
 * new String[]{"", "", "", "", "", "", "", "", "", "", ""},
 * new String[]{"", "", "", "", "", "", "", "", "", "", ""},
 * new String[]{"", "HQTR-P2", "", "", "", "", "", "", "", "", ""},
 * new String[]{"", "BASE-P*", "", "INFT-P1", "", "", "", "", "", "", ""},
 * new String[]{"", "CITY-P*", "", "", "", "", "", "", "", "", ""},
 * new String[]{"", "TAPT-P*", "", "", "", "", "", "", "", "", ""},
 * new String[]{"", "", "", "", "", "", "", "", "", "", ""},
 * new String[]{"CITY-P*", "", "", "", "", "", "", "", "HQTR-P1", "", ""},
 * new String[]{"MECH-P1,BASE-P*", "CITY-P*", "", "", "", "", "", "", "", "", ""}
 * };
 * </pre>
 * P* is the neutral player.
 * MECH-P1,BASE-P* Adds a mech unit owned by player 1 on a factory owned by the neutral player.
 * See the bottom of this class for valid tokens.
 *
 * The Terrain, Unit and City are created from the TerrainFactory, UnitFactory and CityFactory.
 * The factories need to be filled with objects BEFORE using this TextMapParser.
 */
public class TextMapParser {
  private static final String NEUTRAL_PLAYER_TOKEN = "*";
  private final HashMap<Integer, Player> players;
  private final Player neutralPlayer;
  private final String[][] source;
  private Map map;

  public TextMapParser(String[][] source) {
    players = new HashMap<Integer, Player>();
    neutralPlayer = Player.createNeutralPlayer(Color.GRAY);
    players.put(-1, neutralPlayer);
    this.source = source;
  }

  /**
   * Read each token in the 2D string array.
   * Convert each token to a game object.
   * See the header of this class for an example.
   *
   * @return The map parsed from the 2D string array
   */
  public Map parseMap() {
    int rows = source.length;
    int cols = source[0].length;

    checkRowSizes(rows, cols);
    createMap(rows, cols);

    // Example:
    // tokenText = MECH-P1,BASE-P*
    // Token group = MECH-P1
    // Token group = BASE-P*
    // token 1 = MECH
    // token 2 = P1
    // token 3 = BASE
    // token 4 = P*
    for (int row = 0; row < rows; row++) {
      for (int col = 0; col < cols; col++) {
        String tokenText = source[row][col];

        if (!tokenText.equals("")) {
          String[] tokenGroups = tokenText.split(",");

          for (String tokenGroup : tokenGroups) {
            parseTokenGroup(row, col, tokenText, tokenGroup);
          }
        }
      }
    }

    return map;
  }

  private void checkRowSizes(int rows, int cols) {
    for (int row = 0; row < rows; row++) {
      if (source[row].length != cols) {
        throw new IllegalArgumentException(String.format(
          "All rows in the map must be of equal width. Row %s Actual:%s Expected:%s", row, source[row].length, cols)
        );
      }
    }
  }

  private void createMap(int rows, int cols) {
    Terrain plain = TerrainFactory.getTerrain("plain");
    map = new Map(cols, rows, 32, plain);
  }

  private void parseTokenGroup(int row, int col, String tokenText, String tokenGroup) {
    if (tokenText.contains("-")) {
      String[] tokens = tokenGroup.split("-");
      checkTokensLength(col, row, tokenText, tokens.length);
      parseTokens(col, row, tokens);
    } else {
      // Terrain
      parseToken(col, row, tokenText, null);
    }
  }

  private void checkTokensLength(int col, int row, String tokenText, int tokensLength) {
    if (tokensLength < 2) {
      throw new IllegalArgumentException(
        String.format("A Token requires 2 parameters for %s at %s,%s", tokenText, col, row)
      );
    } else if (tokensLength > 3) {
      throw new IllegalArgumentException(
        String.format("A Token has max 3 parameters for %s at %s,%s", tokenText, col, row)
      );
    }
  }

  private void parseTokens(int col, int row, String[] tokens) {
    String objectName = tokens[0];
    String playerIDToken = tokens[1].substring(1);

    Player player;
    if (playerIDToken.equals(NEUTRAL_PLAYER_TOKEN)) {
      player = neutralPlayer;
    } else {
      int playerID = Integer.parseInt(playerIDToken);

      // Player ID in the token starts at 1, we need to start with 0 in the map.
      player = createPlayer(playerID - 1);
    }

    parseToken(col, row, objectName, player);
  }

  private Player createPlayer(int playerID) {
    if (players.containsKey(playerID)) {
      return players.get(playerID);
    } else {
      Player player = new Player(playerID, Color.BLACK);
      players.put(playerID, player);
      return player;
    }
  }

  private void parseToken(int col, int row, String objectToken, Player owner) {
    if (objectToken != null) {
      String terrainName = getTerrainName(objectToken);
      String cityName = getCityName(objectToken);
      String unitName = getUnitName(objectToken);

      if (terrainName != null) {
        Terrain terrain = TerrainFactory.getTerrain(terrainName);
        MapUtil.addTerrainToMap(map, col, row, terrain.getID());
      } else if (cityName != null) {
        if (owner == null) throw new IllegalArgumentException("No owner for city token " + cityName);
        City city = CityFactory.getCity(cityName);
        MapUtil.addCityToMap(map, col, row, city, owner);
      } else if (unitName != null) {
        if (owner == null) throw new IllegalArgumentException("No owner for unit token " + unitName);
        Unit unit = UnitFactory.getUnit(unitName);
        MapUtil.addUnitToMap(map, col, row, unit, owner);
      } else {
        throw new IllegalArgumentException("No object for " + objectToken);
      }
    }
  }

  private String getTerrainName(String token) {
    if (token.equals("PLIN") || token.equals("") || token.equals("-")) {
      return "PLAIN";
    } else if (token.equals("FRST")) {
      return "FOREST";
    } else if (token.equals("MNTN")) {
      return "MOUNTAIN";
    } else if (token.equals("RIVR")) {
      return "RIVER";
    } else if (token.equals("ROAD")) {
      return "ROAD";
    } else if (token.equals("REEF")) {
      return "REEF";
    } else if (token.equals("SHOA")) {
      return "SHOAL";
    } else if (token.equals("SEAS")) {
      return "OCEAN";
    } else if (token.equals("METR")) {
      return "METEOR";
    } else if (token.equals("WALL")) {
      return "WALL";
    } else if (token.equals("PIPE")) {
      return "PIPE";
    } else if (token.equals("FIRE")) {
      return "FIRE";
    } else {
      return null;
    }
  }

  private String getCityName(String token) {
    if (token.equals("HQTR")) {
      return "HQ";
    } else if (token.equals("CITY")) {
      return "CITY";
    } else if (token.equals("BASE")) {
      return "FACTORY";
    } else if (token.equals("APRT")) {
      return "AIRPORT";
    } else if (token.equals("PORT")) {
      return "PORT";
    } else if (token.equals("CMTR")) {
      return "COMM_TOWER";
    } else if (token.equals("SILO")) {
      return "MISSLE_SILO";
    } else if (token.equals("RDAR")) {
      return "RADAR_TOWER";
    } else if (token.equals("TAPT")) {
      return "TEMP_AIRPORT";
    } else if (token.equals("TSPT")) {
      return "TEMP_PORT";
    } else if (token.equals("WALS")) {
      return "HORIZONTAL_WALL_SEAM";
    } else if (token.equals("PIPS")) {
      return "HORIZONTAL_PIPE_SEAM";
    } else {
      return null;
    }
  }

  private String getUnitName(String token) {
    if (token.equals("INFT")) {
      return "INFANTRY";
    } else if (token.equals("MECH")) {
      return "MECH";
    } else if (token.equals("BIKE")) {
      return "BIKES";
    } else if (token.equals("RECN")) {
      return "RECON";
    } else if (token.equals("AAIR")) {
      return "ANTI_AIR";
    } else if (token.equals("APCR")) {
      return "APC";
    } else if (token.equals("TANK")) {
      return "LIGHT_TANK";
    } else if (token.equals("MDTK")) {
      return "MEDIUM_TANK";
    } else if (token.equals("WRTK")) {
      return "HEAVY_TANK";
    } else if (token.equals("FLRE")) {
      return "FLARE";
    } else if (token.equals("ARTY")) {
      return "ARTILLERY";
    } else if (token.equals("RCKT")) {
      return "ROCKETS";
    } else if (token.equals("MISS")) {
      return "MISSILES";
    } else if (token.equals("ATNK")) {
      return "ANTI_TANK";
    } else if (token.equals("TCTR")) {
      return "TCOPTER";
    } else if (token.equals("BCTR")) {
      return "BCOPTER";
    } else if (token.equals("FGTR")) {
      return "JET";
    } else if (token.equals("BMBR")) {
      return "BOMBER";
    } else if (token.equals("SEAP")) {
      return "SEA_PLANE";
    } else if (token.equals("DUST")) {
      return "FIGHTER";
    } else if(token.equals("CRUS")) {
      return "CRUISER";
    } else if (token.equals("BSHP")) {
      return "BATTLESHIP";
    } else if (token.equals("SUBM")) {
      return "SUB";
    } else if (token.equals("LNDR")) {
      return "LANDER";
    } else if (token.equals("ACAR")) {
      return "CARRIER";
    } else {
      return null;
    }
  }
}
