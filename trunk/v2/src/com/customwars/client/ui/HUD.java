package com.customwars.client.ui;

import com.customwars.client.App;
import com.customwars.client.io.ResourceManager;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Direction;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import com.customwars.client.tools.NumberUtil;
import com.customwars.client.ui.hud.panel.HorizontalTerrainInfoPanel;
import com.customwars.client.ui.hud.panel.HorizontalTransportInfoPanel;
import com.customwars.client.ui.hud.panel.HorizontalUnitInfoPanel;
import com.customwars.client.ui.hud.panel.InfoPanel;
import com.customwars.client.ui.hud.panel.PlayerInfoPanel;
import com.customwars.client.ui.hud.panel.VerticalTerrainInfoPanel;
import com.customwars.client.ui.hud.panel.VerticalTransportInfoPanel;
import com.customwars.client.ui.hud.panel.VerticalUnitInfoPanel;
import com.customwars.client.ui.layout.Layout;
import com.customwars.client.ui.sprite.SpriteManager;
import com.customwars.client.ui.state.input.CWCommand;
import org.apache.log4j.Logger;
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
 * A Popup can either be shown in the center or within the map at a given tile location
 *
 * @author stefan
 */
public class HUD {
  private static final Logger logger = Logger.getLogger(HUD.class);
  private static final int INFO_PANEL_HEIGHT = 110;
  private static final int TOP_MARGIN = 30;

  // Control
  private boolean renderInfoPanels = true;

  // Model
  private Game game;
  private Map map;
  private int tileSize;

  // GUI
  private final boolean showHorizontalInfoPanels = App.getBoolean("plugin.horizontal_panels");
  private final GUIContext guiContext;
  private final List<Component> topComponents;
  private final List<Component> bottomComponents;
  private SpriteManager spriteManager;
  private Camera2D camera;
  private PopupMenu popupMenu;
  private InfoPanel terrainInfoPanel;
  private InfoPanel unitInfoPanel;
  private InfoPanel transportInfoPanel;
  private boolean renderPopupInMap;

  public HUD(GUIContext guiContext) {
    this.guiContext = guiContext;
    topComponents = new ArrayList<Component>();
    bottomComponents = new ArrayList<Component>();
  }

  public void loadResources(ResourceManager resources) {
    initComponents();
    for (Component panel : topComponents) {
      panel.loadResources(resources);
    }
    for (Component panel : bottomComponents) {
      panel.loadResources(resources);
    }
  }

  private void initComponents() {
    if (showHorizontalInfoPanels) {
      createHorizontalPanels();
    } else {
      createVerticalPanels();
    }

    PlayerInfoPanel playerInfoPanel = new PlayerInfoPanel(game);
    playerInfoPanel.setWidth(240);
    playerInfoPanel.setHeight(20);
    topComponents.add(playerInfoPanel);
  }

  private void createHorizontalPanels() {
    terrainInfoPanel = new HorizontalTerrainInfoPanel(guiContext, spriteManager);
    bottomComponents.add(terrainInfoPanel);

    unitInfoPanel = new HorizontalUnitInfoPanel();
    bottomComponents.add(unitInfoPanel);

    transportInfoPanel = new HorizontalTransportInfoPanel();
    transportInfoPanel.setHeight(tileSize);
    bottomComponents.add(transportInfoPanel);

    int widest = NumberUtil.findHighest(terrainInfoPanel.getWidth(), unitInfoPanel.getWidth());
    terrainInfoPanel.setWidth(widest);
    unitInfoPanel.setWidth(widest);
  }

  private void createVerticalPanels() {
    terrainInfoPanel = new VerticalTerrainInfoPanel(spriteManager);
    terrainInfoPanel.setWidth(56);
    terrainInfoPanel.setHeight(INFO_PANEL_HEIGHT);
    bottomComponents.add(terrainInfoPanel);

    unitInfoPanel = new VerticalUnitInfoPanel();
    unitInfoPanel.setWidth(65);
    unitInfoPanel.setHeight(INFO_PANEL_HEIGHT);
    bottomComponents.add(unitInfoPanel);

    transportInfoPanel = new VerticalTransportInfoPanel();
    transportInfoPanel.setWidth(tileSize);
    transportInfoPanel.setHeight(INFO_PANEL_HEIGHT);
    bottomComponents.add(transportInfoPanel);
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
        unitInfoPanel.setTile(tile);
        transportInfoPanel.setVisible(true);
        transportInfoPanel.setTile(tile);
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
    if (showHorizontalInfoPanels) {
      locateHorizontalPanels(quadrant);
    } else {
      locateVerticalPanels(quadrant);
    }
  }

  private void locateHorizontalPanels(Direction quadrant) {
    if (Direction.isWestQuadrant(quadrant)) {
      int leftOffset = guiContext.getWidth() - bottomComponents.get(0).getWidth();
      Layout.locateRightToLeft(topComponents, leftOffset, TOP_MARGIN);
      Layout.locateBottomToTop(bottomComponents, leftOffset, guiContext.getHeight());
    } else {
      Layout.locateLeftToRight(topComponents, 0, TOP_MARGIN);
      Layout.locateBottomToTop(bottomComponents, 0, guiContext.getHeight());
    }
  }

  private void locateVerticalPanels(Direction quadrant) {
    if (Direction.isWestQuadrant(quadrant)) {
      Layout.locateRightToLeft(topComponents, camera.getWidth(), TOP_MARGIN);
      Layout.locateRightToLeft(bottomComponents, camera.getWidth(), camera.getHeight() - INFO_PANEL_HEIGHT);
    } else {
      Layout.locateLeftToRight(topComponents, 0, TOP_MARGIN);
      Layout.locateLeftToRight(bottomComponents, 0, camera.getHeight() - INFO_PANEL_HEIGHT);
    }
  }

  public void showPopUpInMap(Location popupLocation, String popupName, PopupMenu popup, ComponentListener componentListener) {
    int x = popupLocation.getCol() * tileSize + tileSize / 2;
    int y = popupLocation.getRow() * tileSize + tileSize / 2;

    if (!GUI.canFitToScreen(x, y, popup.getWidth(), popup.getHeight())) {
      x = camera.getX() + tileSize / 2;
      y = camera.getY() + tileSize / 2;

      if (!GUI.canFitToScreen(x, y, popup.getWidth(), popup.getHeight())) {
        logger.warn("popup " + popupName + " cannot fit within the map");
      }
    }
    this.renderPopupInMap = true;
    showPopup(new Point(x, y), popupName, popup, componentListener);
  }

  /**
   * Show the popup centered within the container
   * if the popup can fit the container show it in the center
   * if the popup cannot fit split the popup up into 2 popups
   */
  public void showPopup(String popupName, PopupMenu popup, ComponentListener componentListener) {
    Point center = GUI.getCenteredRenderPoint(popup.getSize(), guiContext);

    if (!GUI.canFitToScreen(center.x, center.y, popup.getWidth(), popup.getHeight())) {
      logger.warn("popup " + popupName + " cannot fit within the screen");
    }
    this.renderPopupInMap = false;
    showPopup(center, popupName, popup, componentListener);
  }

  public void showPopup(Point popupLocation, String popupName, PopupMenu popup, ComponentListener componentListener) {
    this.popupMenu = popup;
    renderInfoPanels = false;
    popupMenu.setLocation(popupLocation.x, popupLocation.y);
    popupMenu.init();
    popupMenu.addListener(componentListener);
  }

  /**
   * Render content with the top left Point is the top left point of the container
   *
   * @param g non translated graphics
   */
  public void renderAbsolute(Graphics g) {
    if (renderInfoPanels) {
      renderInfoPanels(g);
    }

    if (!renderPopupInMap) {
      renderPopup(g);
    }
  }

  private void renderInfoPanels(Graphics g) {
    for (Component comp : topComponents) {
      comp.render(g);
    }
    for (Component comp : bottomComponents) {
      comp.render(g);
    }
  }

  /**
   * Render content that is translated
   * Various translations could be performed like: Scrolling, Centering
   *
   * @param g translated graphics
   */
  public void renderTranslated(Graphics g) {
    if (renderPopupInMap) {
      renderPopup(g);
    }
  }

  private void renderPopup(Graphics g) {
    if (isPopupVisible()) popupMenu.render(guiContext, g);
  }

  public void hidePopup() {
    renderInfoPanels = true;
    renderPopupInMap = false;
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

  public boolean isRenderingPopupInMap() {
    return isPopupVisible() && renderPopupInMap;
  }

  public boolean isRenderingAbsolutePopup() {
    return isPopupVisible() && !renderPopupInMap;
  }
}