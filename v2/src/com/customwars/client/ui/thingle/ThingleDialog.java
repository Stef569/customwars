package com.customwars.client.ui.thingle;

/**
 * Defines a Thingle Dialog
 */
public interface ThingleDialog {
  void show();

  void render();

  boolean isVisible();

  String getTitle();
}
