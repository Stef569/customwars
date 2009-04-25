package com.customwars.client.controller;

import com.customwars.client.App;
import com.customwars.client.MapMaker.control.CityMapEditorControl;
import com.customwars.client.MapMaker.control.MapEditorControl;
import com.customwars.client.MapMaker.control.TerrainMapEditorControl;
import com.customwars.client.MapMaker.control.UnitMapEditorControl;
import com.customwars.client.io.ResourceManager;
import com.customwars.client.model.game.Player;
import com.customwars.client.model.gameobject.TerrainFactory;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import com.customwars.client.ui.mapMaker.CitySelectPanel;
import com.customwars.client.ui.mapMaker.SelectPanel;
import com.customwars.client.ui.mapMaker.TerrainSelectPanel;
import com.customwars.client.ui.mapMaker.UnitSelectPanel;
import com.customwars.client.ui.renderer.GameRenderer;
import org.newdawn.slick.gui.GUIContext;
import tools.MapUtil;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Handles input for the Map Editor, the current selected Panel/Control
 *
 * @author stefan
 */
public class MapEditorController {
  private HashMap<Color, Player> players;
  private List<SelectPanel> panels;
  private List<MapEditorControl> controls;

  private List<Color> colors;
  private Map<Tile> map;

  private int activeID, colorID;

  private GameRenderer gameRenderer;

  public MapEditorController(GameRenderer gameRenderer, GUIContext guiContext) {
    this.gameRenderer = gameRenderer;
    createEmptyMap(10, 10);
    buildControls();
    buildPanels(guiContext);
  }

  private void buildControls() {
    controls = new ArrayList<MapEditorControl>();
    controls.add(new TerrainMapEditorControl(map));
    controls.add(new CityMapEditorControl());
    controls.add(new UnitMapEditorControl());
  }

  private void buildPanels(GUIContext guiContex) {
    panels = new ArrayList<SelectPanel>();
    panels.add(new TerrainSelectPanel(guiContex));
    panels.add(new CitySelectPanel(guiContex));
    panels.add(new UnitSelectPanel(guiContex));
  }

  public void loadResources(ResourceManager resources) {
    colors = new ArrayList<Color>(resources.getSupportedColors());
    resources.recolor(colors.toArray(new Color[colors.size()]));

    for (SelectPanel panel : panels) {
      panel.loadResources(resources);
    }
  }

  public void init() {
    buildPlayers();
    nextPanel();
    nextColor();
  }

  private void buildPlayers() {
    players = new HashMap<Color, Player>();
    Color neutralColor = App.getColor("plugin.neutral_color", Color.GRAY);
    int nextID = 0;

    for (Color color : colors) {
      boolean neutral = false;
      if (color.equals(neutralColor)) {
        neutral = true;
      }

      Player player = new Player(nextID++, color, neutral, null);
      players.put(color, player);
    }
  }

  public void createEmptyMap(int cols, int rows) {
    int tileSize = App.getInt("plugin.tilesize");
    String version = App.get("game.version");

    Map<Tile> map = new Map<Tile>(cols, rows, tileSize, 4);
    MapUtil.fillWithTiles(map, TerrainFactory.getTerrain(0));
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
    gameRenderer.setMap(map);
    buildControls();
  }

  public void add() {
    Color color = getActiveColor();
    Player player = players.get(color);
    Tile t = gameRenderer.getCursorLocation();
    int selectedIndex = getActivePanel().getSelectedIndex();
    getActiveControl().addToTile(t, selectedIndex, player);
  }

  public void delete() {
    Tile t = gameRenderer.getCursorLocation();
    getActiveControl().removeFromTile(t);
  }

  public void fill() {
    int selectedIndex = getActivePanel().getSelectedIndex();
    getActiveControl().fillMap(map, selectedIndex);
  }

  public void nextColor() {
    changeToColor(colorID + 1);
  }

  public void previousColor() {
    changeToColor(colorID - 1);
  }

  private void changeToColor(int newColorID) {
    if (newColorID >= panels.size()) {
      newColorID = 0;
    } else if (newColorID <= -1) {
      newColorID = panels.size();
    }

    this.colorID = newColorID;
    recolor(colorID);
  }

  private void recolor(int colorID) {
    Color color = colors.get(colorID);
    getActivePanel().recolor(color);
  }

  public void nextPanel() {
    changeToPanel(activeID + 1);
  }

  public void previousPanel() {
    changeToPanel(activeID - 1);
  }

  private void changeToPanel(int newPanelID) {
    if (newPanelID >= panels.size()) {
      newPanelID = 0;
    } else if (newPanelID <= -1) {
      newPanelID = panels.size();
    }

    activeID = newPanelID;
    changeToColor(colorID);
  }

  public SelectPanel getActivePanel() {
    return panels.get(activeID);
  }

  private MapEditorControl getActiveControl() {
    return controls.get(activeID);
  }

  private Color getActiveColor() {
    return colors.get(colorID);
  }
}
