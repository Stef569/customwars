package com.customwars.client.ui;

import com.customwars.client.io.ResourceManager;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Direction;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import com.customwars.client.ui.hud.PlayerInfoBox;
import com.customwars.client.ui.hud.TerrainInfoPanel;
import com.customwars.client.ui.hud.TransportInfoPanel;
import com.customwars.client.ui.hud.UnitInfoPanel;
import com.customwars.client.ui.slick.BasicComponent;
import com.customwars.client.ui.sprite.SpriteManager;
import com.customwars.client.ui.state.input.CWCommand;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.gui.ComponentListener;
import org.newdawn.slick.gui.GUIContext;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * Render following components:
 * Popups, tile info, unit info and units in a transport
 * These components are always rendered on top.
 *
 * @author stefan
 */
public class HUD {
  private static final int INFO_BOX_HEIGH = 110;
  private static final int TOP_MARGIN = 10;

  // Control
  private boolean renderInfoPanels = true;

  // Model
  private Game game;
  private Map map;
  private int tileSize;

  // GUI
  private final GUIContext guiContext;
  private final List<BasicComponent> topComponents;
  private final List<BasicComponent> bottomComponents;
  private SpriteManager spriteManager;
  private Camera2D camera;
  private PopupMenu popupMenu;
  private TerrainInfoPanel terrainInfoPanel;
  private UnitInfoPanel unitInfoPanel;
  private TransportInfoPanel transportInfoPanel;

  public HUD(GUIContext guiContext) {
    this.guiContext = guiContext;
    topComponents = new ArrayList<BasicComponent>();
    bottomComponents = new ArrayList<BasicComponent>();
  }

  public void loadResources(ResourceManager resources) {
    initComponents();
    for (BasicComponent comp : topComponents) {
      comp.loadResources(resources);
    }
    for (BasicComponent comp : bottomComponents) {
      comp.loadResources(resources);
    }
  }

  private void initComponents() {
    terrainInfoPanel = new TerrainInfoPanel(guiContext, spriteManager);
    terrainInfoPanel.setWidth(56);
    terrainInfoPanel.setHeight(INFO_BOX_HEIGH);
    bottomComponents.add(terrainInfoPanel);

    unitInfoPanel = new UnitInfoPanel(guiContext);
    unitInfoPanel.setWidth(65);
    unitInfoPanel.setHeight(INFO_BOX_HEIGH);
    bottomComponents.add(unitInfoPanel);

    transportInfoPanel = new TransportInfoPanel(guiContext);
    transportInfoPanel.setWidth(tileSize);
    transportInfoPanel.setHeight(INFO_BOX_HEIGH);
    bottomComponents.add(transportInfoPanel);

    PlayerInfoBox playerInfoBox = new PlayerInfoBox(guiContext, game);
    playerInfoBox.setWidth(240);
    playerInfoBox.setHeight(20);
    topComponents.add(playerInfoBox);
  }

  /**
   * Updates the info boxes with information from the tile
   *
   * @param tile the tile to show information for
   */
  public void moveOverTile(Tile tile) {
    if (terrainInfoPanel != null && camera != null) {
      terrainInfoPanel.setTile(tile);
      Unit unit = map.getUnitOn(tile);

      if (isUnitVisible(tile, unit)) {
        unitInfoPanel.setVisible(true);
        unitInfoPanel.setUnit(unit);
        transportInfoPanel.setVisible(true);
        transportInfoPanel.setUnit(unit);
      } else {
        unitInfoPanel.setVisible(false);
        transportInfoPanel.setVisible(false);
      }
      Direction quadrant = map.getQuadrantFor(tile);
      locateInfoBoxes(quadrant);
    }
  }

  private boolean isUnitVisible(Tile tile, Unit unit) {
    return unit != null && !tile.isFogged() && !unit.isHidden();
  }

  /**
   * Sets the (x, y) coordinates of the info boxes, depending on where the cursor is
   * currently on the screen. If the cursor is half-way or more toward the right side
   * of the screen, the info boxes will be set to display on the left side. Otherwise,
   * the info boxes will be set to display on the right side.
   *
   * @param quadrant 1 of the 4 quadrants in the map
   */
  public final void locateInfoBoxes(Direction quadrant) {
    if (Direction.isWestQuadrant(quadrant)) {
      locateRightToLeft(topComponents, TOP_MARGIN);
      locateRightToLeft(bottomComponents, camera.getHeight() - INFO_BOX_HEIGH);
    } else {
      locateLeftToRight(topComponents, TOP_MARGIN);
      locateLeftToRight(bottomComponents, camera.getHeight() - INFO_BOX_HEIGH);
    }
  }

  private void locateRightToLeft(List<BasicComponent> components, int topMargin) {
    Point startPoint = new Point(camera.getWidth(), topMargin);
    int currentXPos = startPoint.x;

    for (int i = 0; i < components.size(); i++) {
      BasicComponent comp = components.get(i);
      BasicComponent nextComp;

      if (i == 0) {
        currentXPos = startPoint.x - comp.getWidth();
      } else {
        nextComp = components.get(i);
        currentXPos -= nextComp.getWidth();
      }

      comp.setLocation(currentXPos, startPoint.y);
    }
  }

  private void locateLeftToRight(List<BasicComponent> components, int topMargin) {
    Point startPoint = new Point(0, topMargin);
    int currentXPos = 0;

    for (int i = 0; i < components.size(); i++) {
      BasicComponent comp = components.get(i);
      BasicComponent nextComp;

      if (i == 0) {
        currentXPos = startPoint.x;
      } else {
        nextComp = components.get(i - 1);
        currentXPos += nextComp.getWidth();
      }

      comp.setLocation(currentXPos, startPoint.y);
    }
  }

  public void showPopUp(Location popUpLocation, String popUpName, List<MenuItem> items, ComponentListener componentListener) {
    PopupMenu popup = buildPopupMenu(items);
    int x = popUpLocation.getCol() * tileSize + tileSize / 2;
    int y = popUpLocation.getRow() * tileSize + tileSize / 2;

    if (!GUI.canFitToScreen(x, y, popup.getWidth(), popup.getHeight())) {
      x = camera.getX() + tileSize / 2;
      y = camera.getY() + tileSize / 2;
    }
    showPopUp(x, y, componentListener);
  }

  private PopupMenu buildPopupMenu(List<MenuItem> items) {
    popupMenu = new PopupMenu(guiContext);
    popupMenu.setBackGroundColor(new Color(0, 0, 0, 0.4f));
    popupMenu.setHoverColor(new Color(0, 0, 0, 0.20f));

    for (MenuItem item : items) {
      popupMenu.addItem(item);
    }
    popupMenu.init();
    return popupMenu;
  }

  public void showPopUp(int x, int y, ComponentListener componentListener) {
    renderInfoPanels = false;
    popupMenu.setLocation(x, y);
    popupMenu.init();
    popupMenu.addListener(componentListener);
  }

  public void render(Graphics g) {
    if (renderInfoPanels) {
      renderInfoPanels(g);
    }
  }

  private void renderInfoPanels(Graphics g) {
    for (BasicComponent comp : topComponents) {
      comp.render(guiContext, g);
    }
    for (BasicComponent comp : bottomComponents) {
      comp.render(guiContext, g);
    }
  }

  public void renderPopup(Graphics g) {
    if (isPopupVisible()) popupMenu.render(guiContext, g);
  }

  public void hidePopup() {
    renderInfoPanels = true;
    popupMenu = null;
  }

  public void setGame(Game game) {
    this.game = game;
    this.map = game.getMap();
    this.tileSize = map.getTileSize();
  }

  public void setCamera(Camera2D camera) {
    this.camera = camera;
  }

  public void setSpriteManager(SpriteManager spriteManager) {
    this.spriteManager = spriteManager;
  }

  public boolean isPopupVisible() {
    return popupMenu != null && popupMenu.isVisible();
  }

  public void controlPressed(CWCommand command) {
    if (isPopupVisible()) {
      popupMenu.controlPressed(command);
    }
  }
}
