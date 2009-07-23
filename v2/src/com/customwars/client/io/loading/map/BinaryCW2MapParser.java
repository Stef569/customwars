package com.customwars.client.io.loading.map;

import com.customwars.client.model.game.Player;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.CityFactory;
import com.customwars.client.model.gameobject.Locatable;
import com.customwars.client.model.gameobject.Terrain;
import com.customwars.client.model.gameobject.TerrainFactory;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.gameobject.UnitFactory;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import org.apache.log4j.Logger;
import tools.IOUtil;

import java.awt.Color;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Converts a CW2 map objects to and from a binary file.
 * Each Terrain, City and Unit object has an unique ID: eg. Tank=1 Inf=4 Bomber=403
 *
 * When a data block is optional or when the data block varies in size, a start byte is prepended
 * eg. Terrain=0 City=1 Unit=2 no_unit=3
 *
 * The bin format has 2 chunks the Header and the map data.
 * The header has 3 chunks:
 * Static, Player and Map properties
 *
 * Formatting used:
 * One binary field
 * <field name> data type (possible values)
 *
 * A group of related fields
 * CHUNK:
 *
 * Reads/writes the field list under the for each multiple times
 * for each entity
 *
 * Reads/writes fieldList1 or 2 depending on the start byte
 * fieldList1
 * or
 * fieldList2
 *
 * STATIC HEADER:
 * <CW2_HEADER_START> txt
 * <COLS> int
 * <ROWS> int
 * <TILE_SIZE> byte
 * <FOG_ON> boolean
 * <MAX_PLAYERS> byte
 *
 * PLAYER HEADER:
 * for each player (excluding the neutral player)
 * <PlAYER_ID> byte
 * <PLAYER_RGB> int
 * <PLAYER_HQ_COL> int
 * <PLAYER_HQ_ROW> int
 *
 * MAP PROPERTIES HEADER:
 * <DYNAMIC_HEADER_SIZE> int
 * for each map property:
 * <MAP_PROPERTY_NAME> txt
 * <MAP_PROPERTY_VALUE> txt
 *
 * MAP DATA:
 * <TILE COL> int
 * <TILE ROW> int
 *
 * <START_TERRAIN> byte (TERRAIN_START)
 * <TERRAIN_ID> byte
 * or
 * <START_CITY> byte (CITY_START)
 * <CITY_ID> byte
 * <OWNER_ID> byte
 *
 * <START_UNIT> byte (UNIT_START or NO_UNIT)
 * <UNIT_ID> byte
 * <OWNER_ID> byte
 * <TRANSPORT_COUNT> byte
 * for each unit in transport
 * <UNIT_ID>
 * <OWNER_ID>
 *
 * @author stefan
 */
public class BinaryCW2MapParser implements MapParser {
  private static final Logger logger = Logger.getLogger(BinaryCW2MapParser.class);
  private static final String CW2_HEADER_START = "CW2.map";
  private static final byte TERRAIN_START = 0;
  private static final byte CITY_START = 1;
  private static final byte UNIT_START = 2;
  private static final byte NO_UNIT = 3;

  public Map<Tile> readMap(InputStream in) throws IOException {
    BinaryMapReader reader = new BinaryMapReader(new DataInputStream(in));
    return reader.read();
  }

  public void writeMap(Map<Tile> map, OutputStream out) throws IOException {
    BinaryMapWriter binMapParser = new BinaryMapWriter(map, new DataOutputStream(out));
    binMapParser.write();
  }

  private class BinaryMapReader {
    private DataInputStream in;
    private java.util.Map<Integer, Player> players = new HashMap<Integer, Player>();
    private java.util.Map<Player, Location2D> hqLocations = new HashMap<Player, Location2D>();

    public BinaryMapReader(DataInputStream in) {
      this.in = in;
    }

    public Map<Tile> read() throws IOException {
      Map<Tile> map = null;

      try {
        validateStream(in);
        map = readMap();
      } catch (IOException ex) {
        throw new MapFormatException(ex);
      } finally {
        IOUtil.closeStream(in);
      }
      return map;
    }

    private void validateStream(DataInputStream in) throws IOException {
      String headerStart = in.readUTF();

      if (!headerStart.equals(CW2_HEADER_START)) {
        throw new MapFormatException("Header doesn't match " + CW2_HEADER_START +
          " not a CW2 binary map file");
      }
    }

    private Map<Tile> readMap() throws IOException {
      Map<Tile> map = readHeader();
      return readMapData(map);
    }

    private Map<Tile> readHeader() throws IOException {
      Map<Tile> map = readStaticHeader();
      readMapPlayers(map);
      readMapProperties(map);
      return map;
    }

    private Map<Tile> readStaticHeader() throws IOException {
      int cols = in.readInt();
      int rows = in.readInt();
      int tileSize = in.readByte();
      boolean fogOn = in.readBoolean();
      int maxPlayers = in.readByte();

      Terrain plain = TerrainFactory.getTerrain(0);
      return new Map<Tile>(cols, rows, tileSize, maxPlayers, fogOn, plain);
    }

    private void readMapPlayers(Map<Tile> map) throws IOException {
      for (int i = 0; i < map.getNumPlayers(); i++) {
        int id = in.readByte();
        int rgb = in.readInt();
        Color color = new Color(rgb);
        Player mapPlayer = new Player(id, color, false, null, "Map player " + id, 0, 0, false);
        Location2D hqLocation = readHQLocation();

        addPlayer(mapPlayer);
        hqLocations.put(mapPlayer, hqLocation);
      }

      // Always add the neutral player so neutral cities can be looked up
      Player neutral = new Player(Player.NEUTRAL_PLAYER_ID, Color.GRAY, true, null, "neutral map player", 0, -1, false);
      addPlayer(neutral);
    }

    private Location2D readHQLocation() throws IOException {
      int hqCol = in.readInt();
      int hqRow = in.readInt();

      return new Location2D(hqCol, hqRow);
    }

    private void addPlayer(Player player) {
      if (players.containsKey(player.getId())) {
        throw new MapFormatException("Duplicate player ID(" + player.getId() + ")");
      } else {
        players.put(player.getId(), player);
      }
    }

    private void readMapProperties(Map<Tile> map) throws IOException {
      int dynamicHeaderSize = in.readInt();
      int bytesRead = 0;

      while (bytesRead < dynamicHeaderSize) {
        String property = in.readUTF();
        String value = in.readUTF();
        map.putProperty(property, value);
        bytesRead += (property.getBytes().length + value.getBytes().length);
      }
    }

    private Map<Tile> readMapData(Map<Tile> map) throws IOException {
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

      Unit unit;
      if (nextBytesIsUnit()) {
        unit = readUnit();
        tile.add(unit);
        addUnitsToTransport(unit);
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
      return TerrainFactory.getTerrain(terrainID);
    }

    private City readCity() throws IOException {
      int cityID = in.readByte();
      int ownerID = in.readByte();
      Player owner = players.get(ownerID);
      City city = CityFactory.getCity(cityID);
      city.setOwner(owner);
      return city;
    }

    private Unit readUnit() throws IOException {
      int unitID = in.readByte();
      int unitOwnerID = in.readByte();
      Player owner = players.get(unitOwnerID);

      Unit unit = UnitFactory.getUnit(unitID);
      unit.setOwner(owner);
      return unit;
    }

    private void addUnitsToTransport(Unit transport) throws IOException {
      int unitsInTransportCount = in.readByte();

      for (int i = 0; i < unitsInTransportCount; i++) {
        if (nextBytesIsUnit()) {
          Unit unit = readUnit();
          addUnitsToTransport(unit);  // handle transports in transports in transports...
          transport.add(unit);
        }
      }
    }
  }

  private class BinaryMapWriter {
    private Map<Tile> map;
    private DataOutputStream out;

    public BinaryMapWriter(Map<Tile> map, DataOutputStream out) {
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
      writeMapData();
    }

    private void writeHeader() throws IOException {
      writeStaticHeader();
      writePlayers();
      writeMapProperties();
    }

    private void writeStaticHeader() throws IOException {
      writeTxt(out, CW2_HEADER_START);
      out.writeInt(map.getCols());
      out.writeInt(map.getRows());
      out.writeByte(map.getTileSize());
      out.writeBoolean(map.isFogOfWarOn());
      out.writeByte(map.getNumPlayers());
    }

    /**
     * Write the players to the outputstream
     * playerID, color and hq location
     */
    private void writePlayers() throws IOException {
      Set<Player> players = getUniquePlayers();

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

    /**
     * Get each unique player in the map
     * excluding the neutral player
     */
    private Set<Player> getUniquePlayers() {
      Set<Player> players = new HashSet<Player>();
      for (Tile t : map.getAllTiles()) {
        Unit unit = map.getUnitOn(t);
        City city = map.getCityOn(t);

        if (unit != null && !unit.getOwner().isNeutral()) {
          players.add(unit.getOwner());
        }

        if (city != null && !city.getOwner().isNeutral()) {
          players.add(city.getOwner());
        }
      }
      return players;
    }

    private void writeMapProperties() throws IOException {
      out.writeInt(getPropertiesByteSize());

      for (String property : map.getPropertyKeys()) {
        String value = map.getProperty(property);
        writeTxt(out, property);
        writeTxt(out, value);
      }
    }

    private int getPropertiesByteSize() {
      int headerSize = 0;
      for (String property : map.getPropertyKeys()) {
        String value = map.getProperty(property);
        headerSize += property.getBytes().length;
        headerSize += value.getBytes().length;
      }
      return headerSize;
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
        out.writeByte(unit.getID());
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
     *
     * If this unit is not a transport or the transport is empty
     * write 0 as transport count
     */
    private void writeUnitsInTransport(Unit unit) throws IOException {
      int unitsInTransport = unit.getLocatableCount();

      if (unit.canTransport() && unitsInTransport > 0) {
        out.writeByte(unitsInTransport);

        for (int i = 0; i < unitsInTransport; i++) {
          Unit unitInTransport = (Unit) unit.getLocatable(i);
          writeUnit(unitInTransport);
        }
      } else {
        out.writeByte(0);
      }
    }

    private void writeTxt(DataOutputStream out, String txt) throws IOException {
      if (txt == null) {
        txt = "";
        logger.warn("Writing empty txt");
      }
      out.writeUTF(txt);
    }
  }

  /**
   * A Location that has a Col and Row
   */
  private class Location2D implements Location {
    private int col, row;

    public Location2D(int col, int row) {
      this.col = col;
      this.row = row;
    }

    public boolean canAdd(Locatable locatable) {
      return false;
    }

    public void add(Locatable locatable) {
    }

    public boolean remove(Locatable locatable) {
      return false;
    }

    public boolean contains(Locatable locatable) {
      return false;
    }

    public Locatable getLastLocatable() {
      return null;
    }

    public Locatable getLocatable(int index) {
      return null;
    }

    public int getLocatableCount() {
      return 0;
    }

    public int getCol() {
      return col;
    }

    public int getRow() {
      return row;
    }

    public String getLocationString() {
      return col + "," + row;
    }
  }
}
