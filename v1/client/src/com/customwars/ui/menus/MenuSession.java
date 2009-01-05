package com.customwars.ui.menus;

import com.customwars.map.Map;

/**
 * Holds data that is shared between menus
 *
 * @author stefan
 * @since 2.0
 */
public class MenuSession {
  private Map map;
  private String fileName;

  // SETTERS
  public void setMap(Map map) {
    this.map = map;
  }

  public void setMapFileName(String fileName) {
    this.fileName = fileName;
  }

  // GETTERS
  public Map getMap() {
    return map;
  }

  public String getFileName() {
    return fileName;
  }
}
