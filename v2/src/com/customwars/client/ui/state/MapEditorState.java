package com.customwars.client.ui.state;

import com.customwars.client.Config;
import com.customwars.client.controller.CursorController;
import com.customwars.client.controller.MapEditorController;
import com.customwars.client.io.img.slick.ImageStrip;
import com.customwars.client.model.map.Direction;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import com.customwars.client.ui.hud.Dialog;
import com.customwars.client.ui.mapMaker.CitySelectPanel;
import com.customwars.client.ui.mapMaker.SelectPanel;
import com.customwars.client.ui.mapMaker.TerrainSelectPanel;
import com.customwars.client.ui.mapMaker.UnitSelectPanel;
import com.customwars.client.ui.renderer.MapRenderer;
import com.customwars.client.ui.sprite.SpriteManager;
import com.customwars.client.ui.sprite.TileSprite;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.command.Command;
import org.newdawn.slick.gui.GUIContext;
import org.newdawn.slick.state.StateBasedGame;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * In this state the user can create open and save maps in the cw2 format
 * There are 3 panels that contain a row of gameobjects
 * one for adding terrains
 * one for adding cities
 * one for adding units
 *
 * To add a gameobject:
 * Select 1 gameobject by clicking on it
 * click on the map to add it
 * At all times there is 1 active panel
 */
public class MapEditorState extends CWState {
  private MapEditorController mapEditorController;
  private CursorController cursorController;
  private List<SelectPanel> panels;
  private int activePanelID;

  private Map<Tile> map;
  private MapRenderer mapRenderer;

  public void init(GameContainer gameContainer, StateBasedGame stateBasedGame) throws SlickException {
    createPanels(gameContainer);

    mapEditorController = new MapEditorController(this, resources, panels.size());
    recolorPanels();
  }

  private void createPanels(GameContainer gameContainer) {
    buildPanels(gameContainer);
    loadPanelResources();
  }

  private void buildPanels(GUIContext guiContex) {
    panels = new ArrayList<SelectPanel>();
    panels.add(new TerrainSelectPanel(guiContex));
    panels.add(new CitySelectPanel(guiContex));
    panels.add(new UnitSelectPanel(guiContex));
  }

  private void loadPanelResources() {
    for (SelectPanel panel : panels) {
      panel.loadResources(resources);
    }
  }

  private void recolorPanels() {
    for (int panelIndex = 0; panelIndex < panels.size(); panelIndex++) {
      this.activePanelID = panelIndex;
      mapEditorController.recolor();
    }
    this.activePanelID = 0;
  }

  public void render(GameContainer container, Graphics g) throws SlickException {
    mapRenderer.render(g);
    getActivePanel().render(container, g);
    renderControls(g);
  }

  private void renderControls(Graphics g) {
    int LEFT_MARGIN = 350;
    g.drawString("The Controls:", LEFT_MARGIN, 10);
    g.drawString("Fill: " + cwInput.getControlsAsText(CWInput.fillMap), LEFT_MARGIN, 22);
    g.drawString("Add: " + cwInput.getControlsAsText(CWInput.select), LEFT_MARGIN, 34);
    g.drawString("Delete object: " + cwInput.getControlsAsText(CWInput.delete), LEFT_MARGIN, 46);
    g.drawString("Change panel: " + cwInput.getControlsAsText(CWInput.nextPage), LEFT_MARGIN, 58);
    g.drawString("Recolor: " + cwInput.getControlsAsText(CWInput.recolor), LEFT_MARGIN, 70);
    g.drawString("Save map: " + cwInput.getControlsAsText(CWInput.save), LEFT_MARGIN, 82);
    g.drawString("Open map: " + cwInput.getControlsAsText(CWInput.open), LEFT_MARGIN, 94);
  }

  public void update(GameContainer container, int delta) throws SlickException {
    mapRenderer.update(delta);
    getActivePanel().update(delta);
  }

  public void setMap(Map<Tile> map) {
    this.map = map;
    SpriteManager spriteManager = new SpriteManager(map);
    this.mapRenderer = new MapRenderer(map, spriteManager);
    mapRenderer.loadResources(resources);
    this.cursorController = new CursorController(map, spriteManager);
    initCursors();
  }

  private void initCursors() {
    ImageStrip selectCursorImgs = resources.getSlickImgStrip("selectCursor");
    Tile randomTile = map.getRandomTile();
    TileSprite selectCursor = new TileSprite(selectCursorImgs, 250, randomTile, map);

    mapRenderer.addCursor("SELECT", selectCursor);
    mapRenderer.activateCursor("SELECT");
  }

  @Override
  public void controlPressed(Command command, CWInput cwInput) {
    Tile cursorLocation = mapRenderer.getCursorLocation();
    SelectPanel activePanel = getActivePanel();
    int selectedIndex = activePanel.getSelectedIndex();

    if (cwInput.isSelect(command)) {
      int mouseX = cwInput.getMouseX();
      int mouseY = cwInput.getMouseY();
      boolean clickedOnMap = !activePanel.isWithinComponent(mouseX, mouseY);

      if (clickedOnMap) {
        mapEditorController.add(cursorLocation, selectedIndex);
      }
    } else if (cwInput.isFillMap(command)) {
      mapEditorController.fill(selectedIndex);
    } else if (cwInput.isNextPage(command)) {
      this.activePanelID = mapEditorController.nextPanel();
    } else if (cwInput.isRecolor(command)) {
      mapEditorController.nextColor();
    } else if (cwInput.isDelete(command)) {
      mapEditorController.delete(cursorLocation);
    } else if (cwInput.isSave(command)) {
      saveMap();
    } else if (cwInput.isOpen(command)) {
      openMap();
    } else {
      moveCursor(command, cwInput);
    }
  }

  private void saveMap() {
    cwInput.setActive(false);
    Dialog dialog = new Dialog("Save Map");
    dialog.addTextField("Map name");
    dialog.addTextField("Map description");
    dialog.addTextField("Author");

    int eventType = dialog.show();
    handleSaveMapDialogInput(eventType, dialog);
    cwInput.setActive(true);
  }

  private void openMap() {
    cwInput.setActive(false);
    JFileChooser fileChooser = new JFileChooser(Config.MAPS_DIR);
    fileChooser.showOpenDialog(null);

    File file = fileChooser.getSelectedFile();
    handleOpenMapDialogInput(file);
    cwInput.setActive(true);
  }

  private void handleOpenMapDialogInput(File file) {
    if (file != null) {
      try {
        mapEditorController.loadMap(file.getName());
      } catch (FileNotFoundException e) {
        JOptionPane.showMessageDialog(null,
          "Could not open the map '" + file.getPath() + "'\n " +
            e.getMessage(),
          "Error while Opening map",
          JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  public void handleSaveMapDialogInput(int eventType, Dialog dialog) {
    if (eventType == JOptionPane.OK_OPTION) {
      String mapName = dialog.getFieldValue("map name");
      String mapDescription = dialog.getFieldValue("map description");
      String author = dialog.getFieldValue("author");

      try {
        mapEditorController.saveMap(mapName, mapDescription, author);
        JOptionPane.showMessageDialog(null,
          author + ", your map '" + mapName + "'\n" +
            " has been saved to " + Config.MAPS_DIR,
          "Saved",
          JOptionPane.PLAIN_MESSAGE);
      } catch (IOException e) {
        JOptionPane.showMessageDialog(null,
          "Could not save the map '" + mapName + "'\n " +
            e.getMessage(),
          "Error while saving",
          JOptionPane.PLAIN_MESSAGE);
      }
    }
  }

  public void moveCursor(Command command, CWInput cwInput) {
    if (cwInput.isUp(command)) {
      cursorController.moveCursor(Direction.NORTH);
    }

    if (cwInput.isDown(command)) {
      cursorController.moveCursor(Direction.SOUTH);
    }

    if (cwInput.isLeft(command)) {
      cursorController.moveCursor(Direction.WEST);
    }

    if (cwInput.isRight(command)) {
      cursorController.moveCursor(Direction.EAST);
    }
  }

  @Override
  public void mouseMoved(int oldx, int oldy, int newx, int newy) {
    cursorController.moveCursor(newx, newy);
  }

  public void recolor(Color color) {
    getActivePanel().recolor(color);
  }

  public SelectPanel getActivePanel() {
    return panels.get(activePanelID);
  }

  public int getID() {
    return 50;
  }
}
