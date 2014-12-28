package com.customwars.client.ui.thingle;

import com.customwars.client.App;
import com.customwars.client.io.loading.ThinglePageLoader;
import org.newdawn.slick.Font;
import org.newdawn.slick.Input;
import org.newdawn.slick.thingle.Page;
import org.newdawn.slick.thingle.Theme;
import org.newdawn.slick.thingle.Widget;
import org.newdawn.slick.thingle.internal.slick.FontWrapper;

/**
 * A menu that uses a thingle page to render it's content.
 * It requires a menu widget to be present in the xml page.
 * The children of this widget are the menu items of this menu.
 * <p/>
 * An example of the page:
 * <panel name="menu">
 * <button text="a" action="selectItem(this)" enterComponent="enterComponent(0)"/>
 * <button text="b" action="selectItem(this)" enterComponent="enterComponent(1)"/>
 * <button text="c" width="280" action="selectItem(this)" enterComponent="enterComponent(2)"/>
 * </panel>
 * <p/>
 * The action and enterComponent parameters are required.
 * action fires an event to the listener when the key SPACE or ENTER is pressed or on a LEFT CLICK on an menu item.
 * enterComponent controls the internal selected index. The parameter sets the internal selectedIndex.
 */
public class ThingleMenu {
  private final Page page;
  private final int menuItemCount;
  private final MenuListener listener;
  private int selectedIndex;
  private boolean visible;

  /**
   * @param dir      The directory where the thingle xml page is stored in
   * @param source   The name of the thingle page
   * @param listener The listener to be notified when a menu item is pressed
   */
  public ThingleMenu(String dir, String source, MenuListener listener) {
    this.listener = listener;
    ThinglePageLoader thingleLoader = new ThinglePageLoader(dir);
    page = thingleLoader.loadPage(source, this);
    page.setDrawDesktop(false);

    selectedIndex = -1;
    Widget root = page.getWidget("menu");
    menuItemCount = root.getChildrenCount();

    page.layout();
  }

  public void translateItems() {
    Widget root = page.getWidget("menu");
    for (Widget widget : root.getChildren()) {
      translateWidget(widget);
    }
  }

  private void translateWidget(Widget widget) {
    String btnName = widget.getText();
    String translatedBtnName = App.translate(btnName);
    widget.setText(translatedBtnName);
  }

  public void layout() {
    page.layout();
  }

  public void render() {
    if (visible) {
      page.render();
    }
  }

  public void moveUp() {
    int previousItem = selectedIndex - 1;
    if (isWithinBounds(previousItem)) {
      setSelectedIndex(previousItem);
    } else {
      setSelectedIndex(menuItemCount - 1);
    }
  }

  public void moveDown() {
    int nextItem = selectedIndex + 1;
    if (isWithinBounds(nextItem)) {
      setSelectedIndex(selectedIndex + 1);
    } else {
      setSelectedIndex(0);
    }
  }

  public void setSelectedIndex(int index) {
    if (index != selectedIndex && isWithinBounds(index)) {
      selectedIndex = index;
    }
  }

  public void setFont(Font font) {
    page.setFont(new FontWrapper(font));
  }

  public void setTheme(Theme theme) {
    page.setTheme(theme);
  }

  private boolean isWithinBounds(int index) {
    return index >= 0 && index < menuItemCount;
  }

  /**
   * Show this menu and enables input
   */
  public void enable() {
    page.enable();
    visible = true;
  }

  /**
   * Hide this menu and disable input
   */
  public void disable() {
    page.disable();
    visible = false;
  }

  public int getSelectedIndex() {
    return selectedIndex;
  }

  public void keyPressed(int key) {
    if (key == Input.KEY_UP) {
      moveUp();
    } else if (key == Input.KEY_DOWN) {
      moveDown();
    } else if (key == Input.KEY_ENTER || key == Input.KEY_SPACE) {
      setSelectedIndex(selectedIndex);
    }
  }

  /**
   * thingle internal method. Don't use.
   */
  public void selectItem(Widget selectedWidget) {
    listener.selected(selectedIndex);
  }

  /**
   * thingle internal method. Don't use.
   */
  public void enterComponent(int newSelectedIndex) {
    setSelectedIndex(newSelectedIndex);
  }
}
