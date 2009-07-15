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
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.command.Command;
import org.newdawn.slick.gui.GUIContext;
import org.newdawn.slick.state.StateBasedGame;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class MapEditorState extends CWState {
  private MapEditorController mapEditorController;
  private CursorController cursorController;
  private List<SelectPanel> panels;
  private int activePanelID;

  private Map<Tile> map;
  private MapRenderer mapRenderer;

  public void init(GameContainer gameContainer, StateBasedGame stateBasedGame) throws SlickException {
    buildPanels(gameContainer);
    for (SelectPanel panel : panels) {
      panel.loadResources(resources);
    }

    mapEditorController = new MapEditorController(this, resources, panels.size());
    mapRenderer.loadResources(resources);
  }

  private void buildPanels(GUIContext guiContex) {
    panels = new ArrayList<SelectPanel>();
    panels.add(new TerrainSelectPanel(guiContex));
    panels.add(new CitySelectPanel(guiContex));
    panels.add(new UnitSelectPanel(guiContex));
  }

  public void render(GameContainer container, Graphics g) throws SlickException {
    mapRenderer.render(g);
    getActivePanel().render(container, g);
  }

  public void update(GameContainer container, int delta) throws SlickException {
    mapRenderer.update(delta);
    getActivePanel().update(delta);
  }

  public void setMap(Map<Tile> map) {
    this.map = map;
    SpriteManager spriteManager = new SpriteManager(map);
    this.mapRenderer = new MapRenderer(map, spriteManager);
    initCursors();
    this.cursorController = new CursorController(map, spriteManager);
  }

  private void initCursors() {
    ImageStrip selectCursorImgs = resources.getSlickImgStrip("selectCursor");
    ImageStrip aimCursorImgs = resources.getSlickImgStrip("aimCursor");
    Image siloCursorImg = resources.getSlickImg("siloCursor");

    Tile randomTile = map.getRandomTile();
    TileSprite selectCursor = new TileSprite(selectCursorImgs, 250, randomTile, map);
    TileSprite aimCursor = new TileSprite(aimCursorImgs, randomTile, map);
    TileSprite siloCursor = new TileSprite(siloCursorImg, randomTile, map);

    // Use the silo cursor Image height to calculate the effect Range ie
    // If the image has a height of 160/32 is 5 tiles high/2 rounded to int becomes 2.
    int effectRange = siloCursorImg.getHeight() / map.getTileSize() / 2;
    siloCursor.setEffectRange(effectRange);

    mapRenderer.addCursor("SELECT", selectCursor);
    mapRenderer.addCursor("ATTACK", aimCursor);
    mapRenderer.addCursor("SILO", siloCursor);
    mapRenderer.activateCursor("SELECT");
  }

  @Override
  public void controlPressed(Command command, CWInput cwInput) {
    Tile t = mapRenderer.getCursorLocation();
    SelectPanel activePanel = getActivePanel();
    int selectedIndex = activePanel.getSelectedIndex();

    if (cwInput.isSelect(command)) {
      int mouseX = cwInput.getMouseX();
      int mouseY = cwInput.getMouseY();
      boolean clickedOnMap = !activePanel.isWithinComponent(mouseX, mouseY);

      if (clickedOnMap) {
        mapEditorController.add(t, selectedIndex);
      }
    } else if (cwInput.isFillMap(command)) {
      mapEditorController.fill(selectedIndex);
    } else if (cwInput.isNextPage(command)) {
      this.activePanelID = mapEditorController.nextPanel();
      mapEditorController.nextColor();
    } else if (cwInput.isRecolor(command)) {
      mapEditorController.nextColor();
    } else if (cwInput.isDelete(command)) {
      mapEditorController.delete(t);
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
