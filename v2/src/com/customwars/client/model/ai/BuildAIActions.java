package com.customwars.client.model.ai;

import com.customwars.client.action.ActionFactory;
import com.customwars.client.action.CWAction;
import com.customwars.client.model.game.Player;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Unit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Creates the Build Unit CWActions.
 *
 * The CWAction can be executed in the game.
 */
public class BuildAIActions {
  private final Map<City, Unit> unitsToBuild;
  private final List<CWAction> actions;

  public BuildAIActions(Map<City, Unit> unitsToBuild) {
    this.unitsToBuild = unitsToBuild;
    actions = new ArrayList<CWAction>();
  }

  public List<CWAction> createActions() {
    for (City factory : unitsToBuild.keySet()) {
      Unit unit = unitsToBuild.get(factory);
      buildUnit(factory, unit);
    }

    return actions;
  }

  /**
   * Builds the unit and add it to the map
   */
  private void buildUnit(City city, Unit unit) {
    Player p = city.getOwner();
    CWAction buildUnitAction = ActionFactory.buildAddUnitToTileAction(unit, p, city.getLocation());
    actions.add(buildUnitAction);
  }
}
