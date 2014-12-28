package com.customwars.client.ui.thingle;

/**
 * A Listener for responding to events occurring within the Menu. The
 * implementation is notified of menu item selection.
 */
public interface MenuListener {
  /**
   * Notification that a given menu item is selected
   *
   * @param selectedIndex The index of the menu item that is selected
   */
  void selected(int selectedIndex);
}
