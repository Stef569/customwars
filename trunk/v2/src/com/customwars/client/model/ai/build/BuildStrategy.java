package com.customwars.client.model.ai.build;

import com.customwars.client.model.ai.fuzzy.Fuz;
import com.customwars.client.model.gameobject.City;

import java.util.List;
import java.util.Map;

/**
 * The build strategy contains the result of the build AI:
 * <li>
 * <ol>A global build priority list</ol>
 * <ol>Build hints for each individual city that can build units.</ol>
 * </li>
 */
public class BuildStrategy {
  private final List<BuildPriority> buildPriority;
  private final Map<City, List<Fuz.UNIT_TYPE>> buildHintsByCity;

  public BuildStrategy(List<BuildPriority> buildPriority, Map<City, List<Fuz.UNIT_TYPE>> buildHintsByCity) {
    this.buildPriority = buildPriority;
    this.buildHintsByCity = buildHintsByCity;
  }

  /**
   * Get a list of the Build priorities. Sorted by priority.
   * The highest priority starts at index 0.
   */
  public List<BuildPriority> getBuildPriority() {
    return buildPriority;
  }

  /**
   * The build hints for a city.
   * Indicating a good choice for a unit type to be build here.
   * Note we use Fuzzy data here, instead of specifying a Unit the ai would say:
   * It's a good idea to build capturing/offensive or defensive units in the city.
   *
   * @param city The city to get the unit type hint for
   * @return A List of unit types that appears to be a good choice to build in this city
   */
  public List<Fuz.UNIT_TYPE> getCityBuildHints(City city) {
    return buildHintsByCity.get(city);
  }

  /**
   * return true if there is any build hint for this city
   *
   * @see #getCityBuildHints(com.customwars.client.model.gameobject.City)
   */
  public boolean hasCityBuildHintsFor(City city) {
    List buildHints = buildHintsByCity.get(city);
    return buildHints != null && !buildHints.isEmpty();
  }
}
