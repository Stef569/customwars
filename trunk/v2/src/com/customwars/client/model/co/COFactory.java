package com.customwars.client.model.co;

import com.customwars.client.script.ScriptManager;
import com.customwars.client.script.ScriptedCO;
import com.customwars.client.tools.UCaseMap;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * Keeps a reference to all the CO's by name.
 */
public class COFactory {
  private static final Map<String, CO> cos = new UCaseMap<CO>();
  private static ScriptManager scriptManager;

  public static void setScriptManager(ScriptManager scriptManager) {
    COFactory.scriptManager = scriptManager;
  }

  public static void addAll(Collection<CO> cos) {
    for (CO co : cos) {
      add(co);
    }
  }

  public static void add(CO co) {
    cos.put(co.getName(), co);
  }

  public static CO getCO(String coName) {
    if (cos.containsKey(coName)) {
      return new ScriptedCO(cos.get(coName), scriptManager);
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

  public static int count() {
    return cos.size();
  }

  public static void clear() {
    cos.clear();
  }
}
