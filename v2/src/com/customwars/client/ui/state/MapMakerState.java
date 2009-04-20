package com.customwars.client.ui.state;

import com.customwars.client.App;
import com.customwars.client.MapMaker.MapEditorControl;
import com.customwars.client.MapMaker.SelectPanel;
import com.customwars.client.MapMaker.TerrainMapEditorControl;
import com.customwars.client.model.gameobject.Terrain;
import com.customwars.client.model.gameobject.TerrainFactory;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import com.customwars.client.ui.renderer.GameRenderer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.command.Command;
import org.newdawn.slick.gui.GUIContext;
import org.newdawn.slick.state.StateBasedGame;
import tools.MapUtil;

import java.util.List;

/**
 * Allows to place units/cities/terrain on a map
 *
 * @author stefan
 */
public class MapMakerState extends CWState {
  private static final int PANEL_SCROLL_DELAY = 150;
  private SelectPanel activePanel, terrainSelectPanel;
  private MapEditorControl activeEditorControl, terrainMapEditorControl;
  private GameRenderer gameRenderer;
  private Map<Tile> map;
  private List<Terrain> baseTerrains;
  private int timeTaken;

  public void init(GameContainer container, StateBasedGame game) throws SlickException {
    buildMap();
    buildGameRenderer(container);
    terrainMapEditorControl = new TerrainMapEditorControl(map);
  }

  private void buildMap() {
    int tileSize = App.getInt("plugin.tilesize");
    map = new Map<Tile>(10, 10, tileSize, 4);
    MapUtil.fillWithTiles(map, TerrainFactory.getTerrain(0));
  }

  private void buildGameRenderer(GameContainer container) {
    gameRenderer = new GameRenderer(container);
    gameRenderer.loadResources(resources);
    gameRenderer.setMap(map);
    gameRenderer.setRenderHUD(false);
    gameRenderer.setRenderEvents(false);
  }

  @Override
  public void enter(GameContainer container, StateBasedGame game) throws SlickException {
    super.enter(container, game);
    baseTerrains = TerrainFactory.getBaseTerrains();
    buildTerrainSelectRenderer(container);
    activePanel = terrainSelectPanel;
    activeEditorControl = terrainMapEditorControl;
  }

  private void buildTerrainSelectRenderer(GUIContext guiContext) {
    terrainSelectPanel = new SelectPanel(guiContext);
    for (Terrain terrain : baseTerrains) {
      Image img = resources.getSlickImgStrip("terrains").getSubImage(terrain.getID());
      terrainSelectPanel.add(img);
    }

    // Init makes sure that terrainSelectPanel.getHeight() returns the correct height.
    terrainSelectPanel.init();
    terrainSelectPanel.setLocation(0, guiContext.getHeight() - terrainSelectPanel.getHeight());
  }

  public void update(GameContainer container, int delta) throws SlickException {
    if (isMouseInSelectPanel()) {
      timeTaken += delta;
      if (timeTaken >= PANEL_SCROLL_DELAY) {
        timeTaken = 0;

        // Move the panel when the mouse is near the left or right edge
        int tileSize = map.getTileSize();
        if (cwInput.getMouseX() < tileSize) {
          activePanel.moveRight();
        }

        if (cwInput.getMouseX() > container.getWidth() - tileSize) {
          activePanel.moveLeft();
        }
      }
    }
  }

  public void render(GameContainer container, Graphics g) throws SlickException {
    gameRenderer.render(g);
    if (activePanel != null)
      activePanel.render(container, g);
  }

  @Override
  public void controlPressed(Command command, CWInput cwInput) {
    gameRenderer.moveCursor(command, cwInput);

    if (cwInput.isSelect(command)) {
      if (!isMouseInSelectPanel()) {
        Tile t = gameRenderer.getCursorLocation();
        int id = activePanel.getSelectedIndex();
        activeEditorControl.addToTile(t, id);
      }
    } else if (cwInput.isFillMap(command)) {
      int id = activePanel.getSelectedIndex();
      activeEditorControl.fillMap(map, id);
    }
  }

  private boolean isMouseInSelectPanel() {
    return activePanel.isWithinComponent(cwInput.getMouseX(), cwInput.getMouseY());
  }

  @Override
  public void mouseMoved(int oldx, int oldy, int newx, int newy) {
    super.mouseMoved(oldx, oldy, newx, newy);
    gameRenderer.mouseMoved(newx, newy);
  }

  public int getID() {
    return 50;
  }
}