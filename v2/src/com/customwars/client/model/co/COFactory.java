package com.customwars.client.model.co;

import com.customwars.client.script.ScriptManager;
import com.customwars.client.script.ScriptedCO;
import com.customwars.client.tools.UCaseMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Keeps a reference to:
 * All the CO's by name.
 * All the CO styles by name.
 */
public class COFactory {
  private static final Map<String, CO> cos = new UCaseMap<CO>();
  private static final Map<String, COStyle> coStyles = new UCaseMap<COStyle>();
  private static ScriptManager scriptManager;

  public static void setScriptManager(ScriptManager scriptManager) {
    COFactory.scriptManager = scriptManager;
  }

  public static void addCOs(Collection<CO> cos) {
    for (CO co : cos) {
      addCO(co);
    }
  }

  public static void addCO(CO co) {
    cos.put(co.getName(), co);
  }

  public static void addCOStyles(Collection<COStyle> styles) {
    for (COStyle coStyle : styles) {
      addCOStyle(coStyle);
    }
  }

  public static void addCOStyle(COStyle coStyle) {
    coStyles.put(coStyle.getName(), coStyle);
  }

  public static CO getCO(String coName) {
    if (cos.containsKey(coName)) {
      AbstractCO co = (AbstractCO) cos.get(coName);
      co.init();
      return new ScriptedCO(co, scriptManager);
    } else {
      throw new IllegalArgumentException("No Co for " + coName + " cos:" + cos.keySet());
    }
  }

  public static Collection<CO> getAllCOS() {
    return Collections.unmodifiableCollection(cos.values());
  }

  public static boolean hasCOForName(String coName) {
    return cos.containsKey(coName);
  }

  public static int getCOCount() {
    return cos.size();
  }

  public static COStyle getCOStyle(String coStyleName) {
    return coStyles.get(coStyleName);
  }

  public static COStyle getCOStyle(int coStyleID) {
    for (COStyle coStyle : coStyles.values()) {
      if (coStyle.getID() == coStyleID) {
        return coStyle;
      }
    }
    throw new IllegalArgumentException("No coStyle for ID " + coStyleID);
  }

  public static Collection<COStyle> getAllCOStyles() {
    return coStyles.values();
  }

  public static boolean hasCOStyleFor(String coStyle) {
    return coStyles.containsKey(coStyle);
  }

  public static int getCOStyleCount() {
    return coStyles.size();
  }

  public static void clear() {
    cos.clear();
    coStyles.clear();
  }

  /**
   * @return a random CO
   */
  public static CO getRandomCO() {
    int rand = (int) (Math.random() * cos.size());
    List<CO> coList = new ArrayList<CO>(cos.values());
    return getCO(coList.get(rand).getName());
  }
}