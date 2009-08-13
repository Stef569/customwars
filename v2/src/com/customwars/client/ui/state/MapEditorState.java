package com.customwars.client.ui.state;

import com.customwars.client.controller.CursorController;
import com.customwars.client.controller.MapEditorController;
import com.customwars.client.io.img.slick.ImageStrip;
import com.customwars.client.model.map.Direction;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
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

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * In this state the user can create and save maps
 * There are 3 panels
 * one for adding terrains
 * one for adding cities
 * one for adding units
 */
public class MapEditorState extends CWState {
  private MapEditorController mapEditorController;
  private CursorController cursorController;
  private List<SelectPanel> panels;
  private int activePanelID;

  private Map<Tile> map;
  private MapRenderer mapRenderer;

  public void init(GameContainer gameContainer, StateBasedGame stateBasedGame) throws SlickException {
    buildPanels(gameContainer);
    loadPanelResources();

    mapEditorController = new MapEditorController(this, resources, panels.size());
    mapRenderer.loadResources(resources);
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

  public void render(GameContainer container, Graphics g) throws SlickException {
    mapRenderer.render(g);
    getActivePanel().render(container, g);
    renderControls(g);
  }

  private void renderControls(Graphics g) {
    int LEFT_MARGIN = 350;
    g.drawString("The Controls:", LEFT_MARGIN, 10);
    g.drawString("Fill: " + cwInput.getControlsAsText(CWInput.fillMap), LEFT_MARGIN, 20);
    g.drawString("Add: " + cwInput.getControlsAsText(CWInput.select), LEFT_MARGIN, 30);
    g.drawString("Delete object: " + cwInput.getControlsAsText(CWInput.delete), LEFT_MARGIN, 40);
    g.drawString("Change panel: " + cwInput.getControlsAsText(CWInput.nextPage), LEFT_MARGIN, 50);
    g.drawString("Recolor: " + cwInput.getControlsAsText(CWInput.recolor), LEFT_MARGIN, 60);
  }

  public void update(GameContainer container, int delta) throws SlickException {
    mapRenderer.update(delta);
    getActivePanel().update(delta);
  }

  public void setMap(Map<Tile> map) {
    SpriteManager spriteManager = new SpriteManager(map);
    this.map = map;
    this.mapRenderer = new MapRenderer(map, spriteManager);
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
      mapEditorController.nextColor();
    } else if (cwInput.isRecolor(command)) {
      mapEditorController.nextColor();
    } else if (cwInput.isDelete(command)) {
      mapEditorController.delete(cursorLocation);
    } else {
      moveCursor(command, cwInput);
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
