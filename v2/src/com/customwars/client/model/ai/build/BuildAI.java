package com.customwars.client.model.ai.build;

import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Unit;

import java.util.Map;

/**
 * The Build AI will evaluate the map and will find the best units to build.
 */
public interface BuildAI {

  /**
   * Find a unit to build for each factory.
   * A factory can be any city that can build. Ie: Port, Airport,...
   * This is the main contract of the BUILD AI.
   *
   * @return A map of city -> unit pairs or an empty map if no unit can be build
   */
  Map<City, Unit> findUnitsToBuild();
}