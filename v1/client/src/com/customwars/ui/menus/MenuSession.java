package com.customwars.ui.menus;

import com.customwars.ai.Battle;
import com.customwars.ai.BattleOptions;
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
  private int[] sideSelect;
  private boolean[] altSelect;
  private int[] coSelections;

  private BattleOptions battleOptions;

  public void setMap(Map map) {
    this.map = map;
  }

  public void setMapFileName(String fileName) {
    this.fileName = fileName;
  }

  public void setCoSelections(int[] coSelections) {
    this.coSelections = coSelections;
  }

  public void setBattleOptions(BattleOptions battleOptions) {
    this.battleOptions = battleOptions;
  }

  public void setAltSelect(boolean[] altSelect) {
    this.altSelect = altSelect;
  }

  public Map getMap() {
    return map;
  }

  public String getFileName() {
    return fileName;
  }

  public void setSideSelect(int[] sideSelect) {
    this.sideSelect = sideSelect;
  }

  // Creates new instances of critical objects for the battle
  public Battle getBattle() {
    return new Battle(fileName, coSelections, sideSelect, altSelect, battleOptions);
  }
}
