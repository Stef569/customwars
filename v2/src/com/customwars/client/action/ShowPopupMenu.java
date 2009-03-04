package com.customwars.client.action;

import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Tile;
import com.customwars.client.ui.HUD;
import com.customwars.client.ui.PopupMenu;
import com.customwars.client.ui.renderer.MapRenderer;
import org.newdawn.slick.gui.AbstractComponent;
import org.newdawn.slick.gui.ComponentListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Show a popup to te screen
 * The popup is filled with CWAction objects
 * When a menu item is pressed then the CWAction behind that item is executed.
 *
 * @author stefan
 */
public class ShowPopupMenu extends CWAction implements ComponentListener {
  private String popupName;
  private HUD hud;
  private List<CWAction> unitActions;
  private List<String> unitMenuItemNames;
  private InGameSession inGameSession;
  private MapRenderer mapRenderer;

  public ShowPopupMenu(String popupName, HUD hud, InGameSession inGameSession, MapRenderer mapRenderer) {
    super(popupName);
    this.mapRenderer = mapRenderer;
    this.popupName = popupName;
    this.hud = hud;
    this.inGameSession = inGameSession;
    this.unitActions = new ArrayList<CWAction>();
    this.unitMenuItemNames = new ArrayList<String>();
  }

  public void doActionImpl() {
    Tile clicked = inGameSession.getClick(2);
    if (clicked != null) {
      showPopUp(clicked, unitMenuItemNames);
    }
    inGameSession.setMode(InGameSession.MODE.MENU);
  }

  private void showPopUp(Location popUpLocation, List<String> actions) {
    hud.showPopUp(popUpLocation, popupName, actions, this);
  }

  void undoAction() {
    inGameSession.setMode(InGameSession.MODE.DEFAULT);
    hud.hidePopup();
  }

  public void addAction(CWAction action, String menuItemName) {
    unitActions.add(action);
    unitMenuItemNames.add(menuItemName);
  }

  public void clearActions() {
    unitActions.clear();
    unitMenuItemNames.clear();
  }

  public void componentActivated(AbstractComponent abstractComponent) {
    PopupMenu popupMenu = (PopupMenu) abstractComponent;
    CWAction action = unitActions.get(popupMenu.getCurrentOption());
    action.doAction();

    // Hide the popup when clicked on a item
    this.undoAction();
  }
}
