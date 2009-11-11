package com.customwars.client.action;

import com.customwars.client.controller.CursorController;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Tile;
import com.customwars.client.ui.HUD;
import com.customwars.client.ui.MenuItem;
import com.customwars.client.ui.PopupMenu;
import com.customwars.client.ui.state.InGameContext;
import org.apache.log4j.Logger;
import org.newdawn.slick.gui.AbstractComponent;
import org.newdawn.slick.gui.ComponentListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Show a popup to te screen
 * The popup is filled with CWAction objects
 * When a menu item is pressed then the CWAction behind that item is executed and the popup is hidden.
 * If the CWAction behind a MenuItem is null then the menu is hidden.
 *
 * @author stefan
 */
public class ShowPopupMenuAction extends DirectAction implements ComponentListener {
  private static final Logger logger = Logger.getLogger(ShowPopupMenuAction.class);
  private InGameContext context;
  private HUD hud;
  private CursorController cursorControl;

  private String popupName;
  private List<CWAction> actions;
  private List<MenuItem> menuItems;
  private int currentOption;
  private Location popupLocation;

  public ShowPopupMenuAction(String name) {
    this(name, null);
  }

  public ShowPopupMenuAction(String popupName, Location popupLocation) {
    super(popupName);
    this.popupName = popupName;
    this.popupLocation = popupLocation;
    this.actions = new ArrayList<CWAction>();
    this.menuItems = new ArrayList<MenuItem>();
  }

  protected void init(InGameContext context) {
    this.context = context;
    hud = context.getHud();
    cursorControl = context.getCursorController();
  }

  protected void invokeAction() {
    if (actions.size() == 0) {
      logger.warn("No actions set");
    }
    if (popupLocation == null) {
      throw new IllegalArgumentException("Location is null");
    }

    hud.showPopUp(popupLocation, popupName, menuItems, this);
    context.setInputMode(InGameContext.INPUT_MODE.GUI);

    cursorControl.moveCursor(popupLocation);
    cursorControl.setCursorLocked(true);
  }

  public void undo() {
    hud.hidePopup();
    context.setInputMode(InGameContext.INPUT_MODE.DEFAULT);
    cursorControl.setCursorLocked(false);
  }

  public void addAction(CWAction action, MenuItem menuItemName) {
    actions.add(action);
    menuItems.add(menuItemName);
  }

  public void clear() {
    actions.clear();
    menuItems.clear();
  }

  public void componentActivated(AbstractComponent abstractComponent) {
    PopupMenu popupMenu = (PopupMenu) abstractComponent;
    currentOption = popupMenu.getCurrentItem();
    CWAction action = actions.get(popupMenu.getCurrentItem());
    this.undo();    // Hide the popup when clicked on a item
    context.doAction(action);
  }

  public boolean atLeastHasOneItem() {
    return menuItems.size() > 0;
  }

  public int getCurrentOption() {
    return currentOption;
  }

  public void setLocation(Tile popupLocation) {
    this.popupLocation = popupLocation;
  }
}
