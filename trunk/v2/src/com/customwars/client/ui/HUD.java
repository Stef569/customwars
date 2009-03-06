package com.customwars.client.ui;

import com.customwars.client.io.ResourceManager;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.map.Location;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.gui.ComponentListener;
import org.newdawn.slick.gui.GUIContext;

import java.util.List;

/**
 * Render on top of everything in the inGameState this includes:
 * Popups, tile info, unitInfo,...
 *
 * @author stefan
 */
public class HUD {
  private Game game;
  private GUIContext guiContext;
  private PopupMenu popupMenu;
  private Location selectedTile;

  public HUD(Game game, GUIContext guiContext) {
    this.game = game;
    this.guiContext = guiContext;
    popupMenu = new PopupMenu(guiContext);
  }

  public void loadResources(ResourceManager resources) {

  }

  public void moveOverTile(Location tile, boolean leftSide) {
    selectedTile = tile;
  }

  public void showPopUp(Location popUpLocation, String popUpName, List<String> items, ComponentListener componentListener) {
    int tileSize = game.getMap().getTileSize();
    int x = popUpLocation.getCol() * tileSize + tileSize / 2;
    int y = popUpLocation.getRow() * tileSize + tileSize / 2;
    showPopUp(x, y, popUpName, items, componentListener);
  }

  public void showPopUp(int x, int y, String popUpName, List<String> items, ComponentListener componentListener) {
    popupMenu.setVisible(true);
    popupMenu.clear();

    for (String item : items) {
      popupMenu.addOption(item);
    }
    popupMenu.init();
    popupMenu.setLocation(x, y);
    popupMenu.addListener(componentListener);
  }

  public void render(Graphics g) {
    g.drawString("Day:" + game.getDay(), 100, 10);
    g.drawString("Player:" + game.getActivePlayer().getName(), 100, 20);

    popupMenu.render(guiContext, g);

    if (selectedTile != null)
      renderTileInfo(selectedTile.toString(), g);
  }

  private void renderTileInfo(String tileInfo, Graphics g) {
    String line1 = tileInfo, line2 = "";

    int endIndex = tileInfo.length();
    while (g.getFont().getWidth(line1) > guiContext.getWidth() - 20) {
      line1 = tileInfo.substring(0, endIndex--);
    }

    if (endIndex > 0)
      line2 = tileInfo.substring(endIndex);
    g.drawString(line1, 10, guiContext.getHeight() - 40);
    g.drawString(line2, 10, guiContext.getHeight() - 20);
  }

  public void hidePopup() {
    popupMenu.setVisible(false);
    popupMenu.removeAllListener();
  }

  public void setGame(Game game) {
    this.game = game;
  }

  public boolean isPopupVisible() {
    return popupMenu != null && popupMenu.isVisible();
  }
}
