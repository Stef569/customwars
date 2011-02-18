package com.customwars.client.ui.renderer;

import com.customwars.client.App;
import com.customwars.client.io.ResourceManager;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import com.customwars.client.model.map.TileMap;
import com.customwars.client.ui.Camera2D;
import com.customwars.client.ui.GUI;
import com.customwars.client.ui.Scroller;
import com.customwars.client.ui.mapeditor.CitySelectPanel;
import com.customwars.client.ui.mapeditor.SelectPanel;
import com.customwars.client.ui.mapeditor.TerrainSelectPanel;
import com.customwars.client.ui.mapeditor.UnitSelectPanel;
import com.customwars.client.ui.sprite.SpriteManager;
import com.customwars.client.ui.state.input.CWInput;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.gui.GUIContext;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * Renders the view in map editor mode
 * There is 1 centered map and 3 select panels.
 * one for adding terrains
 * one for adding cities
 * one for adding units
 * At all times there is only 1 active panel
 * <p/>
 * To add a gameobject to the map
 * Select a gameobject by clicking on it within the panel
 * click on the map to add it
 */
public class MapEditorRenderer implements Renderable {
  private MapRenderer mapRenderer;
  private Camera2D camera;
  private Scroller scroller;
  private Point center;
  private Point inputOffset;
  private Image helpImage;
  private Point helpImageLocation;
  private LineRenderer lineRenderer;
  private List<SelectPanel> panels;
  private int activePanelID;
  private boolean showSelectPanel;

  private final GUIContext guiContext;
  private final CWInput cwInput;
  private ResourceManager resources;

  public MapEditorRenderer(GUIContext guiContext, CWInput cwInput) {
    this.guiContext = guiContext;
    this.cwInput = cwInput;
    this.inputOffset = new Point();

    buildHelpPanel(guiContext);
    buildSelectPanels(guiContext);
  }

  private void buildHelpPanel(GUIContext guiContext) {
    lineRenderer = new LineRenderer(guiContext.getDefaultFont());
    lineRenderer.addText("Controls:");
    lineRenderer.addText("Fill:" + cwInput.getControlsAsText(CWInput.FILL_MAP));
    lineRenderer.addText("Add:" + cwInput.getControlsAsText(CWInput.SELECT));
    lineRenderer.addText("Delete:" + cwInput.getControlsAsText(CWInput.DELETE));
    lineRenderer.addText("Change panel:" + cwInput.getControlsAsText(CWInput.NEXT_PAGE));
    lineRenderer.addText("Lock cursor:" + cwInput.getControlsAsText(CWInput.CANCEL));
    lineRenderer.addText("Recolor:" + cwInput.getControlsAsText(CWInput.RECOLOR));
    lineRenderer.addText("Save map:" + cwInput.getControlsAsText(CWInput.SAVE));
    lineRenderer.addText("Open map:" + cwInput.getControlsAsText(CWInput.OPEN));
    lineRenderer.addText("New map:" + cwInput.getControlsAsText(CWInput.NEW));
    lineRenderer.addText("Move the mouse near the bottom");
    lineRenderer.addText("to show the active panel");
    lineRenderer.setLocation(350, 40);
  }

  private void buildSelectPanels(GUIContext guiContex) {
    panels = new ArrayList<SelectPanel>();
    panels.add(new TerrainSelectPanel(guiContex));
    panels.add(new CitySelectPanel(guiContex));
    panels.add(new UnitSelectPanel(guiContex));
  }

  public void loadResources(ResourceManager resources) {
    this.resources = resources;
    this.helpImage = resources.getSlickImg("questionMark");
    loadPanelResources();
  }

  private void loadPanelResources() {
    for (SelectPanel panel : panels) {
      panel.loadResources(resources);
    }
  }

  public void update(int elapsedTime) {
    mapRenderer.update(elapsedTime);
    camera.update(elapsedTime);

    // Don't scroll when showing the select panel
    if (!showSelectPanel) {
      scroller.setCursorLocation(mapRenderer.getCursorLocation());
      scroller.update(elapsedTime);
    }

    getActivePanel().update(elapsedTime);
  }

  public void recolor(Color color) {
    for (SelectPanel panel : panels) {
      panel.recolor(color);
    }
  }

  public void render(Graphics g) {
    g.scale(camera.getZoomLvl(), camera.getZoomLvl());
    g.translate(-camera.getX(), -camera.getY());
    inputOffset.setLocation(camera.getX(), camera.getY());
    center(g);
    renderScrollingContent(g);
    g.resetTransform();
    renderStaticContent(g);
    guiContext.getInput().setOffset(inputOffset.x, inputOffset.y);
  }

  private void center(Graphics g) {
    if (center != null) {
      g.translate(center.x, center.y);
      inputOffset.translate(-center.x, -center.y);
    }
  }

  private void renderScrollingContent(Graphics g) {
    mapRenderer.render(g);
  }

  private void renderStaticContent(Graphics g) {
    if (showSelectPanel) {
      getActivePanel().render(guiContext, g);
    }

    if (isHoveringOverHelpImage()) {
      lineRenderer.render(g);
    }

    g.drawImage(helpImage, helpImageLocation.x, helpImageLocation.y);
    g.drawString(mapRenderer.getCursorLocation().getLocationString(), 10, 10);
  }

  private boolean isHoveringOverHelpImage() {
    int mouseX = cwInput.getAbsoluteMouseX();
    int mouseY = cwInput.getAbsoluteMouseY();

    return mouseX > helpImageLocation.x && mouseX < helpImageLocation.x + helpImage.getWidth() &&
      mouseY > helpImageLocation.y && mouseY < helpImageLocation.y + helpImage.getHeight();
  }

  public void setMap(Map map, SpriteManager spriteManager) {
    initCamera(map);
    scroller = new Scroller(camera);
    helpImageLocation = new Point(camera.getWidth() - helpImage.getWidth() - 10, 10);

    mapRenderer = new MapRenderer(map, spriteManager);
    mapRenderer.loadResources(resources);

    if (mapRenderer.canCenterMap(camera.getWidth(), camera.getHeight())) {
      center = GUI.getCenteredRenderPoint(map.getWidth(), map.getHeight(), camera.getWidth(), camera.getHeight());
    } else {
      center = null;
    }
  }

  private void initCamera(TileMap<Tile> map) {
    Dimension screenSize = new Dimension(guiContext.getWidth(), guiContext.getHeight());
    Dimension worldSize = new Dimension(map.getWidth(), map.getHeight());
    this.camera = new Camera2D(screenSize, worldSize, map.getTileSize());
    boolean zoomEnabled = App.getBoolean("display.zoom");
    camera.setZoomingEnabled(zoomEnabled);
    GUI.setCamera(camera);
  }

  public int select(Tile cursorLocation) {
    for (SelectPanel panel : panels) {
      if (panel.canSelect(cursorLocation)) {
        panel.select(cursorLocation);
        return panels.indexOf(panel);
      }
    }
    throw new AssertionError("Cannot happen, Terrain panel can always select from a Tile");
  }

  public void setActivePanelID(int activePanelID) {
    this.activePanelID = activePanelID;
  }

  /**
   * Show the panel when the mouse is near the edge of the container
   * Hide the panel when the mouse moved out of the panel
   */
  public void toggleShowSelectPanel() {
    int mouseX = cwInput.getAbsoluteMouseX();
    int mouseY = cwInput.getAbsoluteMouseY();

    if (showSelectPanel) {
      showSelectPanel = getActivePanel().isWithinComponent(mouseX, mouseY);
    } else {
      showSelectPanel = mouseY > camera.getHeight() - 10 && mouseY <= camera.getHeight();
    }
  }

  public Tile getCursorLocation() {
    return mapRenderer.getCursorLocation();
  }

  public int getSelectedIndex() {
    SelectPanel activePanel = getActivePanel();
    return activePanel.getSelectedIndex();
  }

  public boolean isMouseInMap() {
    SelectPanel activePanel = getActivePanel();
    int mouseX = cwInput.getAbsoluteMouseX();
    int mouseY = cwInput.getAbsoluteMouseY();
    return !activePanel.isWithinComponent(mouseX, mouseY);
  }

  public int getPanelCount() {
    return panels.size();
  }

  private SelectPanel getActivePanel() {
    return panels.get(activePanelID);
  }

  public boolean isShowingSelectPanel() {
    return showSelectPanel;
  }
}
