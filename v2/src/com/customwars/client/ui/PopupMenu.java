package com.customwars.client.ui;

import com.customwars.client.ui.slick.BasicComponent;
import com.customwars.client.ui.state.CWInput;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Sound;
import org.newdawn.slick.command.Command;
import org.newdawn.slick.gui.AbstractComponent;
import org.newdawn.slick.gui.GUIContext;

import java.util.ArrayList;
import java.util.List;

/**
 * A popup menu contains MenuItems
 * When the mouse moves over a menuItem the menu item is selected
 *
 * @author stefan
 */
public class PopupMenu extends BasicComponent {
  private static final int MENU_BACKGROUND_MARGIN = 8;

  private static final Color BACKGOUND_COLOR = new Color(0, 0, 0, 0.4f);
  private static final Color HOVER_COLOR = new Color(0, 0, 0, 0.20f);

  private List<MenuItem> menuItems;
  private int currItem;

  private boolean visible;
  private boolean inited;
  private boolean renderBackground;
  private int spacingY = 0;                       // The space between menu Items
  private Sound menuTickSound;                    // Sound to be played when the current menu item changes
  private Animation cursorAnim;                   // Cursor animation to be shown on all menu items

  public PopupMenu(GUIContext container) {
    super(container);
    menuItems = new ArrayList<MenuItem>();
    visible = true;
    renderBackground = true;
  }

  public void addItem(MenuItem menuItem) {
    if (cursorAnim != null) menuItem.setCursorAnim(cursorAnim);
    menuItems.add(menuItem);
  }

  /**
   * On the first invocation init this component
   * Render the background
   * When the mouse moves over a menu item the current selection is updated
   * Render each menu item
   */
  public void renderimpl(GUIContext container, Graphics g) {
    Color origColor = g.getColor();
    if (!inited) {
      init();
      inited = true;
    }

    if (renderBackground)
      renderBackground(g);

    MenuItem selectedMenuItem = getSelectedMenuItem();
    setCurrentMenuItem(menuItems.indexOf(selectedMenuItem));

    for (MenuItem menuItem : menuItems) {
      menuItem.render(container, g);

      // menuItem.render can fire events!
      // These events can modify other menu item areas
      // Resulting in a ConcurrentMofication Exception
      // those events should set visible to false
      // menuItem.render sets a rectangle to graphics
      // resetting the color, removes that rectangle
      if (!isVisible()) {
        g.setColor(origColor);
        return;
      }
    }
    g.setColor(origColor);
  }

  /**
   * Init menu items once after all menu items are added
   * We need to know the widest menu item in the menu before we define the menu item bounds
   */
  public void init() {
    int widestMenuItem = getWidestMenuItem();
    setWidth(widestMenuItem);
    setHeight(getTotalMenuItemsHeight());
    initMenuItems(widestMenuItem);
    selectMenuItem(0);
  }

  private void initMenuItems(int widestMenuItem) {
    for (MenuItem menuItem : menuItems) {
      menuItem.setLocation(getX(), getVerticalLocation(menuItem));
      menuItem.setWidth(widestMenuItem);
      menuItem.initBoxes();
      menuItem.setMouseOverColor(HOVER_COLOR);
    }
  }

  private int getVerticalLocation(MenuItem menuItem) {
    int height = getY();
    for (int i = 0; i < menuItems.size(); i++) {
      if (i < menuItems.indexOf(menuItem)) {
        height += menuItems.get(i).getHeight() + spacingY;
      }
    }
    return height;
  }

  /**
   * Render the menu background which is MENU_BACKGROUND_MARGIN bigger then the menu bounds
   */
  private void renderBackground(Graphics g) {
    g.setColor(BACKGOUND_COLOR);
    g.fillRoundRect(getX() - MENU_BACKGROUND_MARGIN, getY() - MENU_BACKGROUND_MARGIN,
            getWidth() + MENU_BACKGROUND_MARGIN * 2, getHeight() + MENU_BACKGROUND_MARGIN * 2, 8);
  }

  public void controlPressed(Command command, CWInput cwInput) {
    if (cwInput.isDownPressed(command)) {
      moveDown();
    } else if (cwInput.isUpPressed(command)) {
      moveUp();
    } else if (cwInput.isSelectPressed(command)) {
      selectMenuItem(currItem);
      componentActivated(getSelectedMenuItem());
    }
    consumeEvent();
  }

  public void moveUp() {
    int previousItem = currItem - 1;
    if (isWithinBounds(previousItem))
      setCurrentMenuItem(previousItem);
    else
      setCurrentMenuItem(menuItems.size() - 1);
  }

  public void moveDown() {
    int nextItem = currItem + 1;
    if (isWithinBounds(nextItem))
      setCurrentMenuItem(currItem + 1);
    else
      setCurrentMenuItem(0);
  }

  private void setCurrentMenuItem(int item) {
    if (item != currItem && isWithinBounds(item)) {
      currItem = item;
      selectMenuItem(currItem);
    }
  }

  private boolean isWithinBounds(int item) {
    return item >= 0 && item < menuItems.size();
  }

  private void selectMenuItem(int item) {
    currItem = item;
    playMenuTick();
    updateMenuItems(item);
  }

  private void playMenuTick() {
    if (menuTickSound != null) menuTickSound.play();
  }

  private void updateMenuItems(int item) {
    if (item >= 0 && item < menuItems.size()) {
      for (MenuItem menuItem : menuItems) {
        if (menuItems.get(item) == menuItem) {
          menuItem.setSelected(true);
        } else {
          menuItem.setSelected(false);
        }
      }
    }
  }

  public void setVisible(boolean visible) {
    this.visible = visible;
  }

  public void setMenuTickSound(Sound sound) {
    this.menuTickSound = sound;
  }

  public void setCursorAnim(Animation cursorAnim) {
    this.cursorAnim = cursorAnim;
  }

  public void setVerticalSpacing(int margin) {
    this.spacingY = margin;
  }

  public int getCurrentItem() {
    return currItem;
  }

  private MenuItem getSelectedMenuItem() {
    for (MenuItem menuItem : menuItems) {
      if (menuItem.isSelected()) {
        return menuItem;
      }
    }
    return null;
  }

  public boolean isVisible() {
    return visible;
  }

  private int getWidestMenuItem() {
    int maxWidth = 0;
    for (MenuItem menuItem : menuItems) {
      int width = menuItem.getWidth();
      if (width > maxWidth) maxWidth = width;
    }
    return maxWidth;
  }

  private int getTotalMenuItemsHeight() {
    int totalHeight = 0;
    for (MenuItem menuItem : menuItems) {
      totalHeight += menuItem.getHeight() + spacingY;
    }
    return totalHeight;
  }

  /**
   * This method is invoked when a click has been made within a menu item
   *
   * @param source the menu item clicked on
   */
  public void componentActivated(AbstractComponent source) {
    MenuItem menuItem = (MenuItem) source;
    setCurrentMenuItem(menuItems.indexOf(menuItem));
    menuItem.setFocus(false);
    notifyListeners();
  }

  public void clear() {
    removeAllListener();
    menuItems.clear();
  }

  private void removeAllListener() {
    for (MenuItem menuItem : menuItems) {
      input.removeListener(menuItem);
    }

    this.listeners.clear();
  }

  public void setCursor(Image cursor) {
    Image[] singleImg = new Image[1];
    singleImg[0] = cursor;
    Animation anim = new Animation(singleImg, 99);
    anim.setAutoUpdate(false);
    setCursorAnim(anim);
  }
}
