package com.customwars.client.action;

import com.customwars.client.controller.CursorController;
import com.customwars.client.model.map.Location;
import com.customwars.client.ui.HUD;
import com.customwars.client.ui.PopupMenu;
import com.customwars.client.ui.state.InGameContext;
import org.apache.log4j.Logger;
import org.newdawn.slick.gui.AbstractComponent;
import org.newdawn.slick.gui.ComponentListener;

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
  private HUD hud;
  private CursorController cursorControl;

  private final PopupMenu popup;
  private final Location popupLocation;

  /**
   * Show the popup centered on the screen
   */
  public ShowPopupMenuAction(PopupMenu popupMenu) {
    this(popupMenu, null);
  }

  /**
   * Show the popup in the map
   */
  public ShowPopupMenuAction(PopupMenu popupMenu, Location popupLocation) {
    super(popupMenu.getTitle());
    this.popup = popupMenu;
    this.popupLocation = popupLocation;
    popup.addListener(this);
  }

  protected void init(InGameContext inGameContext) {
    this.inGameContext = inGameContext;
    this.hud = inGameContext.getHud();
    this.cursorControl = inGameContext.getCursorController();
  }

  protected void invokeAction() {
    showPopup(popup);
    inGameContext.setInputMode(InGameContext.INPUT_MODE.GUI);
    cursorControl.moveCursor(popupLocation);
    cursorControl.setCursorLocked(true);
  }

  private void showPopup(PopupMenu popup) {
    if (popupLocation == null) {
      hud.showPopup(popup);
    } else {
      hud.showPopUpInMap(popupLocation, popup);
    }
  }

  public void undo() {
    hud.hidePopup();
    inGameContext.setInputMode(InGameContext.INPUT_MODE.DEFAULT);
    cursorControl.setCursorLocked(false);
  }

  public void componentActivated(AbstractComponent abstractComponent) {
    undo(); // Hide the popup when clicked on an item
  }
}
