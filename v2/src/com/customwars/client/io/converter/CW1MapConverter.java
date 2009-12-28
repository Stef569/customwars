package com.customwars.client.io.converter;

import com.customwars.client.io.loading.CWResourceLoader;
import com.customwars.client.io.loading.ModelLoader;
import com.customwars.client.io.loading.map.BinaryCW2MapParser;
import com.customwars.client.model.ArmyBranch;
import com.customwars.client.model.game.Player;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.CityFactory;
import com.customwars.client.model.gameobject.Terrain;
import com.customwars.client.model.gameobject.TerrainFactory;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.gameobject.UnitFactory;
import com.customwars.client.model.gameobject.UnitStats;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Range;
import com.customwars.client.model.map.Tile;
import com.customwars.client.model.map.connector.TerrainConnector;
import com.customwars.client.tools.FileUtil;
import com.customwars.client.tools.MapUtil;
import org.apache.log4j.BasicConfigurator;

import java.awt.Color;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Read all cw1 maps from CW1_INPUT_DIR
 * Convert to CW2 map object
 * Save to CW2_OUTPUT_DIR as CW2 map file.
 */
public class CW1MapConverter {
  private static final String CW1_INPUT_DIR = "/home/stefan/projects/cw1/versus";
  private static final String CW2_OUTPUT_DIR = "/home/stefan/projects/cw2/maps/versus/";

  // CW1 has max 10 players
  private List<Player> players = new ArrayList<Player>(10);

  public CW1MapConverter() {
    BasicConfigurator.configure();
    loadCW2Resources();
    convertMaps();
  }

  private void loadCW2Resources() {
    CWResourceLoader modelLoader = new ModelLoader("resources/res/plugin/default/data/");

    try {
      modelLoader.load();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void convertMaps() {
    for (File file : new File(CW1_INPUT_DIR).listFiles()) {
      if (file.isFile()) {
        Map<Tile> map = readCW1MAPFile(file);

        // Terrains need to be connected, since cw1 maps only store base terrains
        // not specific terrains
        connectTerrains(map);
        writeCW2Map(file, map);
      } else {
        System.out.println("Skipping dir " + file);
      }
    }
  }

  //reads a .map file (determines which .map type a file is and calls correct reading function)

  public Map<Tile> readCW1MAPFile(File file) {
    int fileType = 0;
    DataInputStream inputStream = null;

    try {
      inputStream = new DataInputStream(new FileInputStream(file));
      fileType = inputStream.readInt();
    } catch (IOException e) {
      System.err.println(e);
    }

    Map<Tile> map = null;
    try {
      if (fileType <= -1) {
        map = readNewMapFile(inputStream);
      } else {
        //map = readOldMAPFile(inputStream);
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return map;
  }

  public Map<Tile> readNewMapFile(DataInputStream in) throws IOException {
    String name = readUntil((char) 0, in);
    String author = readUntil((char) 0, in);
    String desc = readUntil((char) 0, in);
    int width = in.readInt();
    int height = in.readInt();
    int numArmies = in.readByte();

    for (int playerIndex = 0; playerIndex < numArmies; playerIndex++) {
      Color color = getColor(in.readByte());
      players.add(new Player(playerIndex, color));
    }

    // Add the neutral player
    players.add(Player.createNeutralPlayer(Color.GRAY));

    Terrain plain = new Terrain(0, "dummy", "dum", "my", 0, 0, false, 0, Arrays.asList(5));
    Map<Tile> map = new Map<Tile>(width, height, 32, plain);
    map.setMapName(name);
    map.setAuthor(author);
    map.setDescription(desc);

    for (int col = 0; col < width; col++) {
      for (int row = 0; row < height; row++) {
        byte terrainID = in.readByte();
        byte colorID = in.readByte();
        Color color = getColor(colorID);
        Player armyPtr = getArmy(color);
        Tile t = map.getTile(col, row);
        Terrain ter = getTerrain(terrainID);

        if (ter instanceof City) {
          City c = (City) ter;
          MapUtil.addCityToMap(map, t, c, armyPtr);

          if (c.getName().equalsIgnoreCase("HQ")) {
            armyPtr.setHq(c);
          }
        } else {
          t.setTerrain(ter);
        }
      }
    }

    while (true) {
      int unitID = in.readByte();
      if (unitID == -10) break;
      Color color = getColor(in.readByte());
      Player armyPtr = getArmy(color);
      int col = in.readInt();
      int row = in.readInt();
      Unit unit = getUnit(unitID);
      armyPtr.addUnit(unit);
      Tile t = map.getTile(col, row);
      t.add(unit);
    }

    players.clear();
    return map;
  }

  private String readUntil(char c, DataInputStream in) throws IOException {
    String word = "";
    char tempbyte;
    while (true) {
      tempbyte = (char) in.readByte();
      if (tempbyte == c) break;
      word += tempbyte;
    }
    return word;
  }

  private Color getColor(byte b) {
    Color c = Color.RED;
    switch (b) {
      case -1:
        c = Color.GRAY;
        break;
      case 0:
        c = Color.RED;
        break;
      case 1:
        c = Color.BLUE;
        break;
      case 2:
        c = Color.GREEN;
        break;
      case 3:
        c = Color.YELLOW;
        break;
      case 4:
        c = Color.BLACK;
        break;
      case 5:
        c = Color.ORANGE;
        break;
      case 6:
        // Out of colors
        c = Color.ORANGE;
        break;
      case 7:
        c = Color.cyan;
        break;
      case 8:
        c = Color.magenta;
        break;
      case 9:
        // I'm out of colors, using pink
        c = Color.pink;
        break;
      default:
        System.out.println("Defaulting to RED for id " + b);
        return c;
    }
    return c;
  }

  //returns a given player by color

  public Player getArmy(Color color) {
    for (Player player : players) {
      if (player.getColor().equals(color))
        return player;
    }
    throw new IllegalStateException("No player found for " + color);
  }

  private Terrain getTerrain(int type) {
    Terrain selectedTerrain = null;

    switch (type) {
      case 0:
        selectedTerrain = TerrainFactory.getTerrain(0);
        break;
      case 1:
        selectedTerrain = TerrainFactory.getTerrain(1);
        break;
      case 2:
        selectedTerrain = TerrainFactory.getTerrain(17);
        break;
      case 3:
        selectedTerrain = TerrainFactory.getTerrain(2);
        break;
      case 4:
        selectedTerrain = TerrainFactory.getTerrain(13);
        break;
      case 5:
        selectedTerrain = TerrainFactory.getTerrain(19);
        break;
      case 6:
        selectedTerrain = TerrainFactory.getTerrain(30);
        break;
      case 7:
        selectedTerrain = TerrainFactory.getTerrain(75);
        break;
      case 8:
        selectedTerrain = TerrainFactory.getTerrain(64);
        break;
      case 9:
        selectedTerrain = CityFactory.getCity(4);
        break;
      case 10:
        selectedTerrain = CityFactory.getCity(0);
        break;
      case 11:
        selectedTerrain = CityFactory.getCity(1);
        break;
      case 12:
        selectedTerrain = CityFactory.getCity(2);
        break;
      case 13:
        selectedTerrain = CityFactory.getCity(3);
        break;
      case 14:
        selectedTerrain = createCity(6);
        break;
      case 15:
        selectedTerrain = TerrainFactory.getTerrain(86);
        break;
      case 16:
        selectedTerrain = CityFactory.getCity(5);
        break;
      case 17:
        selectedTerrain = TerrainFactory.getTerrain(86);
        break;
      case 18:
        selectedTerrain = TerrainFactory.getTerrain(82);
        break;
      case 19:
        selectedTerrain = TerrainFactory.getTerrain(100);
        break;
      case 20:
        selectedTerrain = TerrainFactory.getTerrain(15);
        break;
      case 21:
        selectedTerrain = createTerrain(81);
        break;
      case 22:
        selectedTerrain = TerrainFactory.getTerrain(86);
        break;
      case 23:
        selectedTerrain = TerrainFactory.getTerrain(86);
        break;
      case 24:
        selectedTerrain = TerrainFactory.getTerrain(82);
        break;
      case 25:
        selectedTerrain = TerrainFactory.getTerrain(86);
        break;
    }
    return selectedTerrain;
  }

  public Unit getUnit(int type) {
    Unit unit = null;
    switch (type) {
      case 0:
        unit = UnitFactory.getUnit(0);
        break;
      case 1:
        unit = UnitFactory.getUnit(1);
        break;
      case 2:
        unit = UnitFactory.getUnit(2);
        break;
      case 3:
        unit = UnitFactory.getUnit(3);
        break;
      case 4:
        unit = UnitFactory.getUnit(4);
        break;
      case 5:
        unit = UnitFactory.getUnit(5);
        break;
      case 6:
        unit = UnitFactory.getUnit(6);
        break;
      case 7:
        unit = UnitFactory.getUnit(7);
        break;
      case 8:
        unit = UnitFactory.getUnit(8);
        break;
      case 9:
        unit = UnitFactory.getUnit(9);
        break;
      case 10:
        unit = UnitFactory.getUnit(10);
        break;
      case 11:
        unit = UnitFactory.getUnit(11);
        break;
      case 12:
        unit = UnitFactory.getUnit(12);
        break;
      case 13:
        unit = UnitFactory.getUnit(13);
        break;
      case 14:
        unit = UnitFactory.getUnit(14);
        break;
      case 15:
        unit = UnitFactory.getUnit(15);
        break;
      case 16:
        unit = UnitFactory.getUnit(16);
        break;
      case 17:
        unit = UnitFactory.getUnit(17);
        break;
      case 18:
        unit = UnitFactory.getUnit(18);
        break;
      case 19:
        unit = createUnit(36);
        break;
      case 20:
        unit = createUnit(27);
        break;
      case 21:
        unit = createUnit(28);
        break;
      case 22:
        unit = UnitFactory.getUnit(19);
        break;
      case 23:
        unit = createUnit(20);
        break;
      case 24:
        unit = createUnit(29);
        break;
      case 25:
        unit = createUnit(30);
        break;
      case 26:
        unit = createUnit(31);
        break;
      case 27:
        unit = createUnit(32);
        break;
      case 28:
        unit = createUnit(33);
        break;
      case 29:
        unit = UnitFactory.getUnit(20);
        break;
      case 30:
        unit = UnitFactory.getUnit(21);
        break;
      case 31:
        unit = createUnit(34);
        break;
    }
    return unit;
  }

  /**
   * Create a unit with the given id
   * All the other values are defaults, they should be ignored
   */
  private Unit createUnit(int id) {
    UnitStats unitStats = new UnitStats(id, 0, "dummy unit", "", 0, 0, 0, 0, 0, 0, 0, 0,
      false, false, false, false, false, false, false, null, ArmyBranch.LAND, 0, Range.ZERO_RANGE, "", "", 0);

    return new Unit(unitStats);
  }

  private Terrain createTerrain(int id) {
    return new Terrain(id, "", "dummy terrain", "", 0, 0, false, 0, Arrays.asList(1));
  }

  private City createCity(int id) {
    return new City(id, "", "dummy city", "", 0, 0, Arrays.asList(1), 0, false, null, null, null, null, 0, 0);
  }

  private void writeCW2Map(File cw1File, Map<Tile> map) {
    String mapName = map.getMapName();

    // Some cw1 maps don't have a name!
    // Default to the file name without the .map suffix
    if (mapName.trim().length() == 0) {
      mapName = FileUtil.getFileNameWithoutExtension(cw1File);
    }

    System.out.println("Writing " + mapName);

    File out = new File(CW2_OUTPUT_DIR, mapName + ".map");
    try {
      BinaryCW2MapParser mapParser = new BinaryCW2MapParser();
      mapParser.writeMap(map, new FileOutputStream(out));
    } catch (IOException e) {
      System.out.println("Problem writing map " + mapName + " " + map.getDescription());
      e.printStackTrace();
    }
  }

  private void connectTerrains(Map<Tile> map) {
    TerrainConnector terrainConnector = new TerrainConnector(map);
    for (Tile t : map.getAllTiles()) {
      Terrain terr = t.getTerrain();
      terrainConnector.turnSurroundingTerrains(t, terr);
    }
  }

  public static void main(String[] args) {
    new CW1MapConverter();
  }
}
