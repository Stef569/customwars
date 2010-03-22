package com.customwars.client.ui;

import com.customwars.client.ui.slick.BasicComponent;
import com.customwars.client.ui.state.input.CWCommand;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.Sound;
import org.newdawn.slick.gui.AbstractComponent;
import org.newdawn.slick.gui.GUIContext;

import java.util.ArrayList;
import java.util.List;

/**
 * A popup menu contains MenuItems
 * When the mouse moves over a menuItem the menu item is selected
 *
 * Init should be called after all menu items are added
 * If init was not called it is invoked once on the first render attempt.
 *
 * the location and dimensions of the popUpMenu are not correct until init is invoked.
 *
 * @author stefan
 */
public class PopupMenu extends BasicComponent {
  private static final int MENU_BACKGROUND_MARGIN = 8;
  // Default White hover on black background
  private Color backGroundColor = new Color(0, 0, 0, 0.8f);
  private Color hoverColor = new Color(255, 255, 255, 0.1f);

  private final String title;
  private final List<MenuItem> menuItems;
  private final boolean renderBackground;

  private int selectedItem;
  private boolean inited;
  private int spacingY;             // The space between menu Items
  private Sound menuTickSound;      // Sound to be played when the current menu item changes

  public PopupMenu(GUIContext container) {
    this(container, "Unnamed popup");
  }

  public PopupMenu(GUIContext container, String title) {
    super(container);
    this.title = title;
    menuItems = new ArrayList<MenuItem>();
    renderBackground = true;
    setVisible(true);
  }

  public void addItems(MenuItem... menuItems) {
    for (MenuItem item : menuItems) {
      addItem(item);
    }
  }

  public void addItem(MenuItem menuItem) {
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
      menuItem.setMouseOverColor(hoverColor);
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
    g.setColor(backGroundColor);
    g.fillRoundRect(getX() - MENU_BACKGROUND_MARGIN, getY() - MENU_BACKGROUND_MARGIN,
      getWidth() + MENU_BACKGROUND_MARGIN * 2, getHeight() + MENU_BACKGROUND_MARGIN * 2, 8);
  }

  public void controlPressed(CWCommand command) {
    if (isVisible()) {
      switch (command.getEnum()) {
        case DOWN:
          moveDown();
          break;
        case UP:
          moveUp();
          break;
        case SELECT:
          select();
          break;
      }
      consumeEvent();
    }
  }

  @Override
  public void mousePressed(int button, int x, int y) {
    if (isVisible() && isWithinComponent(x, y) && button == Input.MOUSE_LEFT_BUTTON) {
      select();
      consumeEvent();
    }
  }

  public void moveUp() {
    int previousItem = selectedItem - 1;
    if (isWithinBounds(previousItem))
      setCurrentMenuItem(previousItem);
    else
      setCurrentMenuItem(menuItems.size() - 1);
  }

  public void moveDown() {
    int nextItem = selectedItem + 1;
    if (isWithinBounds(nextItem))
      setCurrentMenuItem(selectedItem + 1);
    else
      setCurrentMenuItem(0);
  }

  private void setCurrentMenuItem(int item) {
    if (item != selectedItem && isWithinBounds(item)) {
      selectedItem = item;
      selectMenuItem(selectedItem);
    }
  }

  private boolean isWithinBounds(int item) {
    return item >= 0 && item < menuItems.size();
  }

  private void select() {
    selectMenuItem(selectedItem);
    componentActivated(getSelectedMenuItem());
  }

  private void selectMenuItem(int item) {
    selectedItem = item;
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

  public void setMenuTickSound(Sound sound) {
    this.menuTickSound = sound;
  }

  public void setVerticalSpacing(int margin) {
    this.spacingY = margin;
  }

  public void setHoverColor(Color hoverColor) {
    this.hoverColor = hoverColor;
  }

  public void setBackGroundColor(Color backGroundColor) {
    this.backGroundColor = backGroundColor;
  }

  public int getCurrentItem() {
    return selectedItem;
  }

  private MenuItem getSelectedMenuItem() {
    for (MenuItem menuItem : menuItems) {
      if (menuItem.isSelected()) {
        return menuItem;
      }
    }
    return null;
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

  public String getTitle() {
    return title;
  }

  /**
   * This method is invoked when a click  has been made within a menu item
   *
   * @param source the menu item clicked on
   */
  public void componentActivated(AbstractComponent source) {
    MenuItem menuItem = (MenuItem) source;
    setCurrentMenuItem(menuItems.indexOf(menuItem));
    menuItem.setFocus(false);
    notifyListeners();
    menuItem.activate();
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

  @Override
  public void setAcceptingInput(boolean acceptingInput) {
    super.setAcceptingInput(acceptingInput);
    for (MenuItem menuItem : menuItems) {
      menuItem.setAcceptingInput(acceptingInput);
    }
  }

  public boolean atLeastHasOneItem() {
    return !menuItems.isEmpty();
  }
}
