package com.customwars.client.controller;

import com.customwars.client.App;
import com.customwars.client.io.ResourceManager;
import com.customwars.client.model.game.Player;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Terrain;
import com.customwars.client.model.gameobject.TerrainFactory;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Direction;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import com.customwars.client.tools.FileUtil;
import com.customwars.client.tools.StringUtil;
import com.customwars.client.ui.renderer.MapEditorRenderer;
import com.customwars.client.ui.sprite.SpriteManager;
import com.customwars.client.ui.sprite.TileSprite;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Handles input in Map Editor mode
 * Unit City and terrain each have their own control object
 *
 * @author stefan
 */
public class MapEditorController {
  private static final int STARTUP_MAP_COLS = 10;
  private static final int STARTUP_MAP_ROWS = 10;
  private final MapEditorRenderer mapEditorView;
  private final ResourceManager resources;
  private CursorController cursorController;

  private final HashMap<Color, Player> players;
  private final List<Color> colors;
  private List<MapEditorControl> controls;
  private final int panelCount;
  private int activePanelID, colorID;
  private Map<Tile> map;

  public MapEditorController(MapEditorRenderer mapEditorView, ResourceManager resources) {
    this.resources = resources;
    this.mapEditorView = mapEditorView;
    this.panelCount = mapEditorView.getPanelCount();
    this.colors = new ArrayList<Color>(resources.getSupportedColors());
    this.players = new HashMap<Color, Player>();

    init();
  }

  private void init() {
    createEmptyMap(STARTUP_MAP_COLS, STARTUP_MAP_ROWS);
    buildPlayers();
    recolorPanels();
    changeToPanel(0);
  }

  public void createEmptyMap(int cols, int rows) {
    int tileSize = App.getInt("plugin.tilesize");
    Terrain plain = TerrainFactory.getTerrain(0);

    Map<Tile> emptyMap = new Map<Tile>(cols, rows, tileSize, plain);
    setMap(emptyMap);
  }

  private void buildPlayers() {
    Color neutralColor = App.getColor("plugin.neutral_color", Color.GRAY);
    int nextPlayerID = 0;

    for (Color color : colors) {
      Player player;
      if (color.equals(neutralColor)) {
        player = new Player(Player.NEUTRAL_PLAYER_ID, color, true);
      } else {
        player = new Player(nextPlayerID++, color, false);
      }

      players.put(color, player);
    }
  }

  private void recolorPanels() {
    for (int panelIndex = 0; panelIndex < mapEditorView.getPanelCount(); panelIndex++) {
      changeToPanel(panelIndex);
      changeToColor(0);
    }
  }

  public void loadMap(File file) throws IOException {
    if (!isValidMapFile(file)) {
      throw new IllegalArgumentException(file.getName() + " is not a valid CW2 map file");
    }

    String mapName = FileUtil.StripFileExtension(file.getName());
    Map<Tile> map = resources.isMapCached(mapName) ? resources.getMap(mapName) : resources.loadMap(file);
    setMap(map);
  }

  private boolean isValidMapFile(File file) {
    String fileName = file.getName();
    String mapFileExtension = App.get("map.file.extension");
    return fileName != null && fileName.endsWith(mapFileExtension);
  }

  public void saveMap(String mapName, String mapDescription, String author) throws IOException {
    map.setMapName(mapName);
    map.setDescription(mapDescription);
    map.setAuthor(author);
    saveMap(mapName);
  }

  public void saveMap(String fileName) throws IOException {
    String mapFileExtension = App.get("map.file.extension");
    String mapName = StringUtil.appendTrailingSuffix(fileName, mapFileExtension);
    String mapDir = App.get("home.maps.dir");
    File newMapFile = new File(mapDir, mapName);

    if (newMapFile.exists()) {
      throw new IOException("The map " + fileName + " already exists");
    } else {
      resources.saveMap(map, new FileOutputStream(newMapFile));
    }
  }

  private void setMap(Map<Tile> map) {
    this.map = map;
    SpriteManager spriteManager = new SpriteManager(map);
    mapEditorView.setMap(map, spriteManager);
    this.cursorController = new CursorController(map, spriteManager);
    buildControls(map);
    initCursors(map);
  }

  private void buildControls(Map<Tile> map) {
    controls = new ArrayList<MapEditorControl>();
    controls.add(new TerrainMapEditorControl(map));
    controls.add(new CityMapEditorControl(map));
    controls.add(new UnitMapEditorControl(map));
  }

  private void initCursors(Map<Tile> map) {
    TileSprite selectCursor = resources.createCursor(map, App.get("user.selectcursor"));
    cursorController.addCursor("SELECT", selectCursor);
    cursorController.activateCursor("SELECT");
  }

  public void add(Tile t, int selectedIndex) {
    Color color = getActiveColor();
    Player player = players.get(color);
    getActiveControl().addToTile(t, selectedIndex, player);
  }

  public void delete(Tile t) {
    if (t.getLocatableCount() > 0) {
      getControl(Unit.class).removeFromTile(t);
    } else {
      if (t.getTerrain() instanceof City) {
        getControl(City.class).removeFromTile(t);
      } else {
        getControl(Terrain.class).removeFromTile(t);
      }
    }
  }

  public void fill(int selectedIndex) {
    getActiveControl().fillMap(map, selectedIndex);
  }

  public void nextColor() {
    changeToColor(colorID + 1);
  }

  public void previousColor() {
    changeToColor(colorID - 1);
  }

  private void changeToColor(int newColorID) {
    if (newColorID >= colors.size()) {
      newColorID = 0;
    } else if (newColorID <= -1) {
      newColorID = colors.size();
    }

    this.colorID = newColorID;
    recolor();
  }

  public void recolor() {
    Color color = colors.get(colorID);
    mapEditorView.recolor(color);
  }

  public void nextPanel() {
    changeToPanel(activePanelID + 1);
  }

  public void previousPanel() {
    changeToPanel(activePanelID - 1);
  }

  private int changeToPanel(int newPanelID) {
    if (newPanelID >= panelCount) {
      newPanelID = 0;
    } else if (newPanelID <= -1) {
      newPanelID = panelCount;
    }

    activePanelID = newPanelID;
    mapEditorView.setActivePanelID(activePanelID);
    return activePanelID;
  }

  private MapEditorControl getControl(Class controlClass) {
    for (MapEditorControl control : controls) {
      if (control.isTypeOf(controlClass)) {
        return control;
      }
    }
    throw new IllegalArgumentException("No control for " + controlClass);
  }

  private MapEditorControl getActiveControl() {
    return controls.get(activePanelID);
  }

  private Color getActiveColor() {
    return colors.get(colorID);
  }

  public void moveCursor(Direction direction) {
    cursorController.moveCursor(direction);
  }

  public void moveCursor(int x, int y) {
    cursorController.moveCursor(x, y);
  }
}
