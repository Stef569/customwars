package com.customwars.client.controller;

import com.customwars.client.App;
import com.customwars.client.MapMaker.control.CityMapEditorControl;
import com.customwars.client.MapMaker.control.MapEditorControl;
import com.customwars.client.MapMaker.control.TerrainMapEditorControl;
import com.customwars.client.MapMaker.control.UnitMapEditorControl;
import com.customwars.client.io.ResourceManager;
import com.customwars.client.model.game.Player;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Terrain;
import com.customwars.client.model.gameobject.TerrainFactory;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import com.customwars.client.ui.state.MapEditorState;

import java.awt.Color;
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
  private HashMap<Color, Player> players;
  private List<MapEditorControl> controls;
  private List<Color> colors;
  private Map<Tile> map;

  private int activeID, colorID;

  private MapEditorState mapEditorView;
  private final int panelCount;

  public MapEditorController(MapEditorState mapEditorState, ResourceManager resources, int panelCount) {
    this.mapEditorView = mapEditorState;
    this.panelCount = panelCount;
    loadResources(resources);
    init();
  }

  private void loadResources(ResourceManager resources) {
    colors = new ArrayList<Color>(resources.getSupportedColors());
    resources.recolor(colors.toArray(new Color[colors.size()]));
  }

  private void init() {
    createEmptyMap(STARTUP_MAP_COLS, STARTUP_MAP_ROWS);
    buildPlayers();
    changeToPanel(0);
    nextColor();
  }

  private void buildPlayers() {
    players = new HashMap<Color, Player>();
    Color neutralColor = App.getColor("plugin.neutral_color", Color.GRAY);
    int nextPlayerID = 0;

    for (Color color : colors) {
      boolean neutral = false;
      if (color.equals(neutralColor)) {
        neutral = true;
      }

      Player player = new Player(nextPlayerID++, color, neutral, null);
      players.put(color, player);
    }
  }

  public void createEmptyMap(int cols, int rows) {
    int tileSize = App.getInt("plugin.tilesize");
    String version = App.get("game.version");

    Terrain plain = TerrainFactory.getTerrain(0);
    Map<Tile> map = new Map<Tile>(cols, rows, tileSize, 4, false, plain);
    map.putProperty("VERSION", version);
    setMap(map);
  }

  public void loadMap(String fileName) {

  }

  public void saveMap(String fileName) {
  }

  public void setMapName(String mapName) {
    map.putProperty("NAME", mapName);
  }

  public void setAuthor(String author) {
    map.putProperty("AUTHOR", author);
  }

  public void setDescription(String description) {
    map.putProperty("DESCRIPTION", description);
  }

  private void setMap(Map<Tile> map) {
    this.map = map;
    mapEditorView.setMap(map);
    buildControls();
  }

  private void buildControls() {
    controls = new ArrayList<MapEditorControl>();
    controls.add(new TerrainMapEditorControl(map));
    controls.add(new CityMapEditorControl(map));
    controls.add(new UnitMapEditorControl(map));
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

  public int nextPanel() {
    return changeToPanel(activeID + 1);
  }

  public int previousPanel() {
    return changeToPanel(activeID - 1);
  }

  private int changeToPanel(int newPanelID) {
    if (newPanelID >= panelCount) {
      newPanelID = 0;
    } else if (newPanelID <= -1) {
      newPanelID = panelCount;
    }

    activeID = newPanelID;
    return activeID;
  }

  private MapEditorControl getControl(Class c) {
    for (MapEditorControl control : controls) {
      if (control.isTypeOf(c)) {
        return control;
      }
    }
    throw new IllegalArgumentException("No control for " + c);
  }

  private MapEditorControl getActiveControl() {
    return controls.get(activeID);
  }

  private Color getActiveColor() {
    return colors.get(colorID);
  }
}
