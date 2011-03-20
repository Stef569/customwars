package com.customwars.client.action;

import com.customwars.client.model.game.Player;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Terrain;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Location;

/**
 * Uses the builder pattern to encode game objects into text.
 * Methods calls can be chained.
 * Each parameter is separated by a single space.
 * Example: 'move 0 0 1 0'
 */
public class ActionCommandEncoder {
  private final StringBuilder actionCommand;

  public ActionCommandEncoder() {
    actionCommand = new StringBuilder(12);
  }

  public ActionCommandEncoder(String str) {
    this();
    actionCommand.append(str);
  }

  public ActionCommandEncoder add(Location t) {
    add(t.getCol() + "");
    add(t.getRow() + "");
    return this;
  }

  public ActionCommandEncoder add(Unit unit) {
    return add(unit.getStats().getName());
  }

  public ActionCommandEncoder add(City city) {
    return add(city.getName());
  }

  public ActionCommandEncoder add(Terrain terrain) {
    return add(terrain.getName());
  }

  public ActionCommandEncoder add(Player player) {
    return add(player.getId() + "");
  }

  public ActionCommandEncoder add(int integer) {
    return add(integer + "");
  }

  /**
   * Add a parameter to the action command.
   * A space character is put before the given parameter if the action command is not empty.
   *
   * @param parameter The parameter to add to the action command.
   */
  public ActionCommandEncoder add(String parameter) {
    if (actionCommand.length() != 0) {
      actionCommand.append(" ");
    }
    actionCommand.append(parameter);
    return this;
  }

  public String build() {
    return actionCommand.toString();
  }
}
