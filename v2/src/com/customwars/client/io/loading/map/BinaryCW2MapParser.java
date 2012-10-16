package com.customwars.client.io.loading.map;

import com.customwars.client.App;
import com.customwars.client.model.game.GameRules;
import com.customwars.client.model.game.Player;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.CityFactory;
import com.customwars.client.model.gameobject.Terrain;
import com.customwars.client.model.gameobject.TerrainFactory;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.gameobject.UnitFactory;
import com.customwars.client.model.map.Location2D;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import com.customwars.client.tools.IOUtil;

import java.awt.Color;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;

/**
 * Converts a CW2 map objects to and from a binary file.
 * Each Terrain, City and Unit object has an unique ID: eg. Tank=1 Inf=4 Bomber=403
 * <p/>
 * When a data block is optional or when the data block varies in size, a start byte is prepended
 * eg. Terrain=0 City=1 Unit=2 no_unit=3
 * <p/>
 * The bin format has 2 chunks the Header and the map data.
 * The header has 3 chunks:
 * Static, Player and Map properties
 * <p/>
 * Formatting used:
 * One binary field
 * <field name> data type (possible values)
 * <p/>
 * A group of related fields
 * CHUNK:
 * <p/>
 * Reads/writes the field list under the for each multiple times
 * for each entity
 * <p/>
 * Reads/writes fieldList1 or 2 depending on the start byte
 * fieldList1
 * or
 * fieldList2
 * <p/>
 * STATIC HEADER:
 * <CW2_HEADER_START> txt
 * <COLS> int
 * <ROWS> int
 * <MAP_NAME>
 * <AUTHOR>
 * <DESCRIPTION>
 * <p/>
 * PLAYER HEADER:
 * <NUM_PLAYERS> byte
 * for each player (excluding the neutral player)
 * <PlAYER_ID> byte
 * <PLAYER_RGB> int
 * <PLAYER_HQ_COL> int
 * <PLAYER_HQ_ROW> int
 * <p/>
 * RULES:
 * <startWeather> int
 * <cityFunds> int
 * <dayLimit> int
 * <playerBudgetStart> int
 * <fogOfWar> boolean
 * <p/>
 * MAP DATA:
 * <TILE COL> int
 * <TILE ROW> int
 * <p/>
 * <START_TERRAIN> byte (TERRAIN_START)
 * <TERRAIN_ID> byte
 * or
 * <START_CITY> byte (CITY_START)
 * <CITY_ID> byte
 * <OWNER_ID> byte
 * <p/>
 * <START_UNIT> byte (UNIT_START or NO_UNIT)
 * <UNIT_ID> byte
 * <OWNER_ID> byte
 * <TRANSPORT_COUNT> byte
 * for each unit in transport
 * <UNIT_ID>
 * <OWNER_ID>
 */
public class BinaryCW2MapParser implements MapParser {
  private static final String CW2_HEADER_START = "CW2.map";
  private static final byte TERRAIN_START = 0;
  private static final byte CITY_START = 1;
  private static final byte UNIT_START = 2;
  private static final byte NO_UNIT = 3;

  public Map readMap(InputStream in) throws IOException {
    BinaryMapReader reader = new BinaryMapReader(new DataInputStream(in));
    return reader.read();
  }

  public void writeMap(Map map, OutputStream out) throws IOException {
    BinaryMapWriter binMapParser = new BinaryMapWriter(map, new DataOutputStream(out));
    binMapParser.write();
  }

  private static class BinaryMapReader {
    private final DataInputStream in;
    private final java.util.Map<Integer, Player> players = new HashMap<Integer, Player>();
    private final java.util.Map<Player, Location2D> hqLocations = new HashMap<Player, Location2D>();

    public BinaryMapReader(DataInputStream in) {
      this.in = in;
    }

    public Map read() throws IOException {
      Map map = null;

      try {
        validateStream(in);
        map = readMap();
      } finally {
        IOUtil.closeStream(in);
      }
      return map;
    }

    private static void validateStream(DataInputStream in) throws IOException {
      String headerStart = in.readUTF();

      if (!headerStart.equals(CW2_HEADER_START)) {
        throw new MapFormatException("Header doesn't match " + CW2_HEADER_START +
          " not a CW2 binary map file");
      }
    }

    private Map readMap() throws IOException {
      Map map = readHeader();
      map.setDefaultRules(readGameRules());
      return readMapData(map);
    }

    private Map readHeader() throws IOException {
      Map map = readStaticHeader();
      readMapPlayers();

      // Always add the neutral player so neutral cities can be looked up
      addPlayer(map.getNeutralPlayer());
      return map;
    }

    private Map readStaticHeader() throws IOException {
      int cols = in.readInt();
      int rows = in.readInt();
      String mapName = in.readUTF();
      String author = in.readUTF();
      String description = in.readUTF();

      Terrain plain = TerrainFactory.getTerrain(0);
      int tileSize = App.getInt("plugin.tilesize");
      return new Map(mapName, author, description, cols, rows, tileSize, plain);
    }

    private void readMapPlayers() throws IOException {
      int numPlayers = in.readByte();
      for (int i = 0; i < numPlayers; i++) {
        int id = in.readByte();
        int rgb = in.readInt();
        Color color = new Color(rgb);
        Player mapPlayer = new Player(id, color, "Map player " + id, 0, 0, false);
        Location2D hqLocation = readHQLocation();

        addPlayer(mapPlayer);
        hqLocations.put(mapPlayer, hqLocation);
      }
    }

    private Location2D readHQLocation() throws IOException {
      int hqCol = in.readInt();
      int hqRow = in.readInt();

      return new Location2D(hqCol, hqRow);
    }

    private void addPlayer(Player player) throws MapFormatException {
      if (players.containsKey(player.getId())) {
        throw new MapFormatException("Duplicate player ID(" + player.getId() + ")");
      } else {
        players.put(player.getId(), player);
      }
    }

    private GameRules readGameRules() throws IOException {
      int weatherStart = in.readInt();
      int cityFunds = in.readInt();
      int dayLimit = in.readInt();
      int playerBudgetStart = in.readInt();
      boolean fogOfWar = in.readBoolean();
      return new GameRules(weatherStart, fogOfWar, dayLimit, cityFunds, playerBudgetStart);
    }

    private Map readMapData(Map map) throws IOException {
      while (true) {
        try {
          Tile t = readTile();
          map.setTile(t);
        } catch (EOFException ex) {
          break;
        }
      }

      // After all tiles have been read,
      // read the hq's from the map and add them to the players
      // if the player doesn't have a hq then the hqLocation will be off the map.
      for (Player p : players.values()) {
        Location2D hqLocation = hqLocations.get(p);
        if (hqLocation != null) {
          Tile hqLoc = map.getTile(hqLocation);

          if (hqLoc != null) {
            City hq = map.getCityOn(hqLoc);
            p.setHq(hq);
          }
        }
      }
      return map;
    }

    private Tile readTile() throws IOException {
      int col = in.readInt();
      int row = in.readInt();
      Terrain terrain = readTerrainOrCity(col, row);
      Tile tile = new Tile(col, row, terrain);

      // Locate the city
      if (terrain instanceof City) {
        City city = (City) terrain;
        city.setLocation(tile);
      }

      if (nextBytesIsUnit()) {
        Unit unit = readUnit();

        if (unit != null) {
          tile.add(unit);
          addUnitsToTransport(unit);
        }
      }

      return tile;
    }

    private Terrain readTerrainOrCity(int col, int row) throws IOException {
      Terrain terrain;
      int terrainStartByte = in.readByte();
      if (terrainStartByte == CITY_START) {
        terrain = readCity();
      } else if (terrainStartByte == TERRAIN_START) {
        terrain = readTerrain();
      } else {
        throw new MapFormatException("tile @ " + col + "," + row + " has an invalid terrain start byte " + terrainStartByte);
      }
      return terrain;
    }

    private boolean nextBytesIsUnit() throws IOException {
      int unitStartByte = in.readByte();
      if (unitStartByte == UNIT_START) {
        return true;
      } else if (unitStartByte == NO_UNIT) {
        return false;
      } else {
        throw new MapFormatException("unit has an invalid unit start byte " + unitStartByte);
      }
    }

    private Terrain readTerrain() throws IOException {
      int terrainID = in.readByte();

      if (TerrainFactory.hasTerrainForID(terrainID)) {
        return TerrainFactory.getTerrain(terrainID);
      } else {
        // If the terrain is not supported, default to the first terrain
        return TerrainFactory.getTerrain(0);
      }
    }

    private City readCity() throws IOException {
      int cityID = in.readByte();
      int ownerID = in.readByte();

      if (CityFactory.hasCityForID(cityID)) {
        return createCity(cityID, ownerID);
      } else {
        // If this city is not supported, default to the first city
        return createCity(0, ownerID);
      }
    }

    private City createCity(int cityID, int ownerID) {
      Player owner = getPlayer(ownerID);
      City city = CityFactory.getCity(cityID);
      owner.addCity(city);
      return city;
    }

    private Unit readUnit() throws IOException {
      int unitID = in.readByte();
      int unitOwnerID = in.readByte();

      if (UnitFactory.hasUnitForID(unitID)) {
        Player owner = getPlayer(unitOwnerID);
        Unit unit = UnitFactory.getUnit(unitID);
        owner.addUnit(unit);
        return unit;
      } else {
        // If this unit is not supported, default to no unit
        return null;
      }
    }

    private Player getPlayer(int id) {
      if (!players.containsKey(id)) {
        throw new IllegalArgumentException("No player for " + id + " " + players.keySet());
      }
      return players.get(id);
    }

    private void addUnitsToTransport(Unit transport) throws IOException {
      int unitsInTransportCount = in.readByte();

      for (int i = 0; i < unitsInTransportCount; i++) {
        if (nextBytesIsUnit()) {
          Unit unit = readUnit();

          if (unit != null) {
            addUnitsToTransport(unit);  // handle transports in transports in transports...
            transport.add(unit);
          }
        }
      }
    }
  }

  private static class BinaryMapWriter {
    private final Map map;
    private final DataOutputStream out;

    public BinaryMapWriter(Map map, DataOutputStream out) {
      this.map = map;
      this.out = out;
    }

    public void write() throws IOException {
      try {
        writeMap();
      } finally {
        IOUtil.closeStream(out);
      }
    }

    private void writeMap() throws IOException {
      writeHeader();
      writeGameRules();
      writeMapData();
    }

    private void writeHeader() throws IOException {
      writeStaticHeader();
      writePlayers();
    }

    private void writeStaticHeader() throws IOException {
      writeTxt(out, CW2_HEADER_START);
      out.writeInt(map.getCols());
      out.writeInt(map.getRows());
      writeTxt(out, map.getMapName());
      writeTxt(out, map.getAuthor());
      writeTxt(out, map.getDescription());
    }

    private void writeGameRules() throws IOException {
      GameRules rules = map.getDefaultGameRules();
      out.writeInt(rules.getStartWeather());
      out.writeInt(rules.getCityFunds());
      out.writeInt(rules.getDayLimit());
      out.writeInt(rules.getPlayerBudgetStart());
      out.writeBoolean(rules.isFogOfWarOn());
    }

    /**
     * Write the players to the output stream
     * player size, playerID, color and hq location
     */
    private void writePlayers() throws IOException {
      Collection<Player> players = map.getUniquePlayers();

      out.writeByte(players.size());
      for (Player p : players) {
        out.writeByte(p.getId());
        out.writeInt(p.getColor().getRGB());
        writeHQ(p.getHq());
      }
    }

    private void writeHQ(City hq) throws IOException {
      if (hq != null) {
        out.writeInt(hq.getLocation().getCol());
        out.writeInt(hq.getLocation().getRow());
      } else {
        out.writeInt(-1);
        out.writeInt(-1);
      }
    }

    private void writeMapData() throws IOException {
      for (Tile t : map.getAllTiles()) {
        writeTile(t);
      }
    }

    private void writeTile(Tile t) throws IOException {
      out.writeInt(t.getCol());
      out.writeInt(t.getRow());

      Terrain terrain = t.getTerrain();
      Unit unit = map.getUnitOn(t);

      if (terrain instanceof City) {
        City city = (City) terrain;
        writeCity(city);
      } else {
        writeTerrain(terrain);
      }

      writeUnit(unit);
    }

    private void writeCity(City city) throws IOException {
      out.writeByte(CITY_START);
      out.writeByte(city.getID());
      out.writeByte(city.getOwner().getId());
    }

    private void writeTerrain(Terrain terrain) throws IOException {
      out.writeByte(TERRAIN_START);
      out.writeByte(terrain.getID());
    }

    private void writeUnit(Unit unit) throws IOException {
      if (unit != null) {
        out.writeByte(UNIT_START);
        out.writeByte(unit.getStats().getID());
        out.writeByte(unit.getOwner().getId());
        writeUnitsInTransport(unit);
      } else {
        out.writeByte(NO_UNIT);
      }
    }

    /**
     * If this unit is a transport with units inside it
     * write the transport count followed by
     * a list of the units inside
     * <p/>
     * If this unit is not a transport or the transport is empty
     * write 0 as transport count
     */
    private void writeUnitsInTransport(Unit unit) throws IOException {
      int unitsInTransport = unit.getUnitsInTransportCount();

      if (unit.getStats().canTransport() && unitsInTransport > 0) {
        out.writeByte(unitsInTransport);

        for (int i = 0; i < unitsInTransport; i++) {
          Unit unitInTransport = unit.getUnitInTransport(i);
          writeUnit(unitInTransport);
        }
      } else {
        out.writeByte(0);
      }
    }

    /**
     * This util method makes sure some text is always written
     * to the output stream, even if the given txt is null
     */
    private static void writeTxt(DataOutputStream out, String txt) throws IOException {
      if (txt == null) {
        txt = "";
      }
      out.writeUTF(txt);
    }
  }
}
