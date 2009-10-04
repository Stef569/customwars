package com.customwars.client.ui.state;

import com.customwars.client.Config;
import com.customwars.client.controller.CursorController;
import com.customwars.client.controller.MapEditorController;
import com.customwars.client.io.img.slick.ImageStrip;
import com.customwars.client.model.map.Direction;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import com.customwars.client.ui.Camera2D;
import com.customwars.client.ui.GUI;
import com.customwars.client.ui.Scroller;
import com.customwars.client.ui.hud.Dialog;
import com.customwars.client.ui.mapMaker.CitySelectPanel;
import com.customwars.client.ui.mapMaker.SelectPanel;
import com.customwars.client.ui.mapMaker.TerrainSelectPanel;
import com.customwars.client.ui.mapMaker.UnitSelectPanel;
import com.customwars.client.ui.renderer.MapRenderer;
import com.customwars.client.ui.sprite.SpriteManager;
import com.customwars.client.ui.sprite.TileSprite;
import org.apache.log4j.Logger;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.command.Command;
import org.newdawn.slick.gui.GUIContext;
import org.newdawn.slick.loading.DeferredResource;
import org.newdawn.slick.loading.LoadingList;
import org.newdawn.slick.state.StateBasedGame;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
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
  private static final Logger logger = Logger.getLogger(MapEditorState.class);
  private MapEditorController mapEditorController;
  private CursorController cursorController;
  private List<SelectPanel> panels;
  private int activePanelID;

  private Map<Tile> map;
  private MapRenderer mapRenderer;
  private Camera2D camera;
  private Scroller scroller;
  private GUIContext guiContext;

  public void init(GameContainer gameContainer, StateBasedGame stateBasedGame) throws SlickException {
    this.guiContext = gameContainer;
    createPanels(gameContainer);

    if (LoadingList.isDeferredLoading()) {
      LoadingList.get().add(
        new DeferredResource() {
          public void load() throws IOException {
            init();
          }

          public String getDescription() {
            return "init map editor";
          }
        }
      );
    } else {
      init();
    }
  }

  private void init() {
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

  public void update(GameContainer container, int delta) throws SlickException {
    mapRenderer.update(delta);
    camera.update(delta);
    scroller.setCursorLocation(mapRenderer.getCursorLocation());
    scroller.update(delta);
    getActivePanel().update(delta);
    container.getInput().setOffset(camera.getX(), camera.getY());
  }

  public void render(GameContainer container, Graphics g) throws SlickException {
    g.scale(camera.getZoomLvl(), camera.getZoomLvl());
    g.translate(-camera.getX(), -camera.getY());
    mapRenderer.render(g);
    g.translate(camera.getX(), camera.getY());

    getActivePanel().render(container, g);
    renderControls(g);
    g.resetTransform();
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

  @Override
  public void leave(GameContainer container, StateBasedGame game) throws SlickException {
    super.leave(container, game);
    cwInput.resetInputTransition();
  }

  public void setMap(Map<Tile> map) {
    this.map = map;
    initCamera(map);
    this.scroller = new Scroller(camera);
    SpriteManager spriteManager = new SpriteManager(map);
    this.mapRenderer = new MapRenderer(map, spriteManager);
    mapRenderer.loadResources(resources);
    this.cursorController = new CursorController(map, spriteManager);
    initCursors();
  }

  private void initCamera(Map<Tile> map) {
    Dimension screenSize = new Dimension(guiContext.getWidth(), guiContext.getHeight());
    Dimension worldSize = new Dimension(map.getWidth(), map.getHeight());
    this.camera = new Camera2D(screenSize, worldSize, map.getTileSize());
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
        mapEditorController.loadMap(file);
      } catch (IOException e) {
        logger.error(e);
        GUI.showExceptionDialog(
          String.format("Could not open the map '%s'", file.getPath()), e,
          "Error while Opening map"
        );
      } catch (Exception e) {
        logger.error(e);
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
        GUI.showdialog(
          String.format("%s your map '%s'\nhas been saved to %s", author, mapName, Config.MAPS_DIR),
          "Saved"
        );
      } catch (IOException e) {
        logger.error(e);
        GUI.showExceptionDialog(
          String.format("Could not save the map '%s'", mapName), e,
          "Error while saving"
        );
      } catch (Exception e) {
        logger.error(e);
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
