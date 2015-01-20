package com.customwars.client.model.ai.build;

import com.customwars.client.model.ai.fuzzy.Fuz;

/**
 * The build priority for a unit
 */
public class BuildPriority {
  public String unitName;
  public Fuz.BUILD_PRIORITY priority;

  public BuildPriority(String unitName, Fuz.BUILD_PRIORITY priority) {
    this.unitName = unitName;
    this.priority = priority;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    BuildPriority otherBuildPriority = (BuildPriority) o;
    return unitName.equals(otherBuildPriority.unitName);
  }

  @Override
  public int hashCode() {
    return 31 * unitName.hashCode();
  }

  @Override
  public String toString() {
    return unitName + " " + priority;
  }


}
