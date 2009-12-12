package com.customwars.client.action;

import com.customwars.client.controller.CursorController;
import com.customwars.client.model.map.Location;
import com.customwars.client.tools.Args;
import com.customwars.client.ui.HUD;
import com.customwars.client.ui.MenuItem;
import com.customwars.client.ui.PopupMenu;
import com.customwars.client.ui.state.InGameContext;
import org.apache.log4j.Logger;
import org.newdawn.slick.Color;
import org.newdawn.slick.gui.AbstractComponent;
import org.newdawn.slick.gui.ComponentListener;
import org.newdawn.slick.gui.GUIContext;

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
  private InGameContext inGameContext;
  private GUIContext guiContext;
  private HUD hud;
  private CursorController cursorControl;

  private final String popupName;
  private List<CWAction> actions;
  private List<MenuItem> menuItems;
  private int currentOption;
  private Location popupLocation;

  private ShowPopupMenuAction(String popupName) {
    super(popupName);
    this.popupName = popupName;
  }

  public static ShowPopupMenuAction createCenteredPopup(String popupName) {
    return createPopupInMap(popupName, null);
  }

  public static ShowPopupMenuAction createPopupInMap(String popupName, Location popupLocation) {
    ShowPopupMenuAction menu = new ShowPopupMenuAction(popupName);
    menu.popupLocation = popupLocation;
    menu.actions = new ArrayList<CWAction>();
    menu.menuItems = new ArrayList<MenuItem>();
    return menu;
  }

  protected void init(InGameContext inGameContext) {
    this.inGameContext = inGameContext;
    this.guiContext = inGameContext.getContainer();
    this.hud = inGameContext.getHud();
    this.cursorControl = inGameContext.getCursorController();
  }

  protected void invokeAction() {
    Args.validate(actions.isEmpty(), "No actions set");

    PopupMenu popup = buildPopupMenu();
    showPopup(popup);
    inGameContext.setInputMode(InGameContext.INPUT_MODE.GUI);

    cursorControl.moveCursor(popupLocation);
    cursorControl.setCursorLocked(true);
  }

  private PopupMenu buildPopupMenu() {
    PopupMenu popupMenu = new PopupMenu(guiContext);
    popupMenu.setBackGroundColor(new Color(0, 0, 0, 0.4f));
    popupMenu.setHoverColor(new Color(0, 0, 0, 0.20f));

    for (MenuItem item : menuItems) {
      popupMenu.addItem(item);
    }
    popupMenu.init();
    return popupMenu;
  }

  private void showPopup(PopupMenu popup) {
    if (popupLocation == null) {
      hud.showPopup(popupName, popup, this);
    } else {
      hud.showPopUpInMap(popupLocation, popupName, popup, this);
    }
  }

  public void undo() {
    hud.hidePopup();
    inGameContext.setInputMode(InGameContext.INPUT_MODE.DEFAULT);
    cursorControl.setCursorLocked(false);
  }

  public void addAction(CWAction action, MenuItem menuItemName) {
    actions.add(action);
    menuItems.add(menuItemName);
  }

  public void componentActivated(AbstractComponent abstractComponent) {
    PopupMenu popupMenu = (PopupMenu) abstractComponent;
    currentOption = popupMenu.getCurrentItem();
    CWAction action = actions.get(popupMenu.getCurrentItem());
    this.undo();    // Hide the popup when clicked on a item
    inGameContext.doAction(action);
  }

  public boolean atLeastHasOneItem() {
    return !menuItems.isEmpty();
  }

  public int getCurrentOption() {
    return currentOption;
  }
}
