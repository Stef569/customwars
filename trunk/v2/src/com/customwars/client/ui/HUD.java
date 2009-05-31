package com.customwars.client.ui;

import com.customwars.client.io.ResourceManager;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.gameobject.Locatable;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Direction;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Tile;
import com.customwars.client.ui.hud.PlayerInfoBox;
import com.customwars.client.ui.hud.TerrainInfoBox;
import com.customwars.client.ui.hud.UnitInfoBox;
import com.customwars.client.ui.slick.BasicComponent;
import com.customwars.client.ui.sprite.SpriteManager;
import com.customwars.client.ui.state.CWInput;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.command.Command;
import org.newdawn.slick.gui.ComponentListener;
import org.newdawn.slick.gui.GUIContext;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * Render following components:
 * Popups, tile info, unitInfo,...
 *
 * @author stefan
 */
public class HUD {
  private static final int INFO_BOX_HEIGH = 100;
  private Game game;
  private SpriteManager spriteManager;
  private Camera2D camera;
  private GUIContext guiContext;
  private PopupMenu popupMenu;
  private List<BasicComponent> topComponents;
  private List<BasicComponent> bottomComponents;
  private TerrainInfoBox terrainInfoBox;
  private UnitInfoBox unitInfoBox;

  public HUD(GUIContext guiContext, SpriteManager spriteManager) {
    this.guiContext = guiContext;
    this.spriteManager = spriteManager;
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
    terrainInfoBox = new TerrainInfoBox(guiContext, spriteManager);
    terrainInfoBox.setWidth(56);
    terrainInfoBox.setHeight(INFO_BOX_HEIGH);
    bottomComponents.add(terrainInfoBox);

    unitInfoBox = new UnitInfoBox(guiContext);
    unitInfoBox.setWidth(65);
    unitInfoBox.setHeight(INFO_BOX_HEIGH);
    bottomComponents.add(unitInfoBox);

    PlayerInfoBox playerInfoBox = new PlayerInfoBox(guiContext, game);
    playerInfoBox.setWidth(150);
    playerInfoBox.setHeight(80);
    topComponents.add(playerInfoBox);
  }

  public void moveOverTile(Tile tile) {
    if (terrainInfoBox != null && camera != null) {
      terrainInfoBox.setTile(tile);
      Locatable locatable = tile.getLastLocatable();

      if (locatable instanceof Unit && !tile.isFogged()) {
        unitInfoBox.setVisible(true);
        unitInfoBox.setUnit((Unit) locatable);
      } else {
        unitInfoBox.setVisible(false);
      }
      Direction quadrant = game.getMap().getQuadrantFor(tile);
      locateInfoBoxes(quadrant);
    }
  }

  /**
   * Sets the (x, y) coordinates of the info boxes, depending on where the cursor is
   * currently on the screen. If the cursor is half-way or more toward the right side
   * of the screen, the info boxes will be set to display on the left side. Otherwise,
   * the info boxes will be set to display on the right side.
   */
  public final void locateInfoBoxes(Direction quadrant) {
    if (quadrant == Direction.NORTHWEST || quadrant == Direction.SOUTHWEST) {
      locateRightToLeft(topComponents, 10);
      locateRightToLeft(bottomComponents, camera.getHeight() - INFO_BOX_HEIGH);
    } else {
      locateLeftToRight(topComponents, 10);
      locateLeftToRight(bottomComponents, camera.getHeight() - INFO_BOX_HEIGH);
    }
  }

  private void locateRightToLeft(List<BasicComponent> components, int topLeftY) {
    Point topLeft = new Point(camera.getWidth(), topLeftY);

    for (int i = 0; i < components.size(); i++) {
      BasicComponent comp = components.get(i);
      BasicComponent nextComp;

      if (i == 0) {
        comp.setLocation(topLeft.x - comp.getWidth(), topLeft.y);
        continue;
      }

      nextComp = components.get(i - 1);
      comp.setLocation(topLeft.x - nextComp.getWidth() - comp.getWidth(), topLeft.y);
    }
  }

  private void locateLeftToRight(List<BasicComponent> components, int topLeftY) {
    Point topLeft = new Point(0, topLeftY);

    for (int i = 0; i < components.size(); i++) {
      BasicComponent comp = components.get(i);
      BasicComponent nextComp;

      if (i == 0) {
        comp.setLocation(topLeft.x, topLeft.y);
        continue;
      }

      nextComp = components.get(i - 1);
      comp.setLocation(topLeft.x + nextComp.getWidth(), topLeft.y);
    }
  }

  public void showPopUp(Location popUpLocation, String popUpName, List<MenuItem> items, ComponentListener componentListener) {
    PopupMenu popup = buildPopupMenu(items);
    int x, y;
    int tileSize = game.getMap().getTileSize();
    x = popUpLocation.getCol() * tileSize + tileSize / 2;
    y = popUpLocation.getRow() * tileSize + tileSize / 2;

    if (!canFitToScreen(popup, x, y)) {
      x = tileSize / 2;
      y = tileSize / 2;
    }
    showPopUp(x, y, componentListener);
  }

  private boolean canFitToScreen(PopupMenu popup, int x, int y) {
    int maxX = x + popup.getWidth();
    int maxY = y + popup.getHeight();
    return maxY < camera.getWidth() && maxX < camera.getHeight();
  }

  private PopupMenu buildPopupMenu(List<MenuItem> items) {
    popupMenu = new PopupMenu(guiContext);

    for (MenuItem item : items) {
      popupMenu.addItem(item);
    }
    popupMenu.init();
    return popupMenu;
  }

  public void showPopUp(int x, int y, ComponentListener componentListener) {
    popupMenu.setLocation(x, y);
    popupMenu.init();
    popupMenu.addListener(componentListener);
  }

  public void render(Graphics g) {
    if (isPopupVisible()) popupMenu.render(guiContext, g);

    for (BasicComponent comp : topComponents) {
      comp.render(guiContext, g);
    }
    for (BasicComponent comp : bottomComponents) {
      comp.render(guiContext, g);
    }
  }

  public void hidePopup() {
    popupMenu = null;
  }

  public void setGame(Game game) {
    this.game = game;
  }

  public void setCamera(Camera2D camera) {
    this.camera = camera;
  }

  public boolean isPopupVisible() {
    return popupMenu != null && popupMenu.isVisible();
  }

  public void controlPressed(Command command, CWInput cwInput) {
    if (isPopupVisible())
      popupMenu.controlPressed(command, cwInput);
  }
}
