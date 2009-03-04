package com.customwars.client.model.gameobject;

import tools.Args;

import java.util.List;

/**
 * An immutable terrain datastore(no setters)
 * Traversing a terrain costs 'move points', this ranges from 1 to IMPASSIBLE
 * Each moveType ID which is just the index in the array has a movecost value.
 * See {@link #canBeTraverseBy(int)} and {@link #getMoveCost(int)}
 *
 * Some terrains remain hidden even within the los of other objects.
 *
 * @author stefan
 */
public class Terrain extends GameObject {
  public static final int IMPASSIBLE = Byte.MAX_VALUE;
  public static final int MIN_MOVE_COST = 1;
  private int id;
  private String name;
  private String description;
  private int defenseBonus;
  private int height;
  private List<Integer> moveCosts;
  private boolean hidden;

  /**
   * @param id           Unique identifier
   * @param name         Short name ie 'plain', 'grass', 'woods'
   * @param description  long description ie 'provides good cover for ground units'
   * @param defenseBonus The defense bonus used in attack calculations, forrest offer better protection then plain.
   * @param height       Height of this terrain ie ocean is lower then a Mountain
   * @param hidden       is this terrain hidden within a moveZone
   * @param moveCosts    The cost to move over this terrain for each movementType, starts at 1(no cost) to 127(Impassible)
   *                     The movementType is used as index to retrieve the moveCost from the moveCosts array
   */
  public Terrain(int id, String name, String description, int defenseBonus, int height, boolean hidden, List<Integer> moveCosts) {
    super(GameObjectState.IDLE);
    this.id = id;
    this.name = name;
    this.description = description;
    this.defenseBonus = defenseBonus;
    this.height = height;
    this.hidden = hidden;
    this.moveCosts = moveCosts;
    init();
  }

  void init() {
    Args.checkForContent(name, "Name is required");
    Args.checkForNull(moveCosts, "move costs are required for " + name);
    for (int moveCost : moveCosts)
      validateMoveCost(moveCost);

    if (description == null) description = "";
  }

  private void validateMoveCost(int moveCosts) {
    String usage = " use " + IMPASSIBLE + " for max movecost and " + MIN_MOVE_COST + " for min movecost.";
    if (moveCosts < MIN_MOVE_COST) {
      throw new IllegalArgumentException("movecost " + moveCosts + " is <" + MIN_MOVE_COST + " in terrain " + this + usage);
    }
    if (moveCosts > IMPASSIBLE) {
      throw new IllegalArgumentException("movecost " + moveCosts + " is >" + IMPASSIBLE + " in terrain " + this + usage);
    }
  }

  /**
   * Copy constructor
   *
   * @param otherTerrain The Terrain to copy
   */
  Terrain(Terrain otherTerrain) {
    super(otherTerrain);
    this.id = otherTerrain.id;
    this.name = otherTerrain.name;
    this.description = otherTerrain.description;
    this.defenseBonus = otherTerrain.defenseBonus;
    this.height = otherTerrain.height;
    this.hidden = otherTerrain.hidden;
    this.moveCosts = otherTerrain.moveCosts;
  }

  public int getID() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public int getDefenseBonus() {
    return defenseBonus;
  }

  public int getHeight() {
    return height;
  }

  public boolean isHidden() {
    return hidden;
  }

  /**
   * @return True if we can move over this terrain with the movementType ID
   */
  public boolean canBeTraverseBy(int movementType) {
    return getMoveCost(movementType) != IMPASSIBLE;
  }

  /**
   * The value IMPASSIBLE is returned
   * when this terrain cannot be traversed by the movementType ID
   *
   * @return The cost for moving over this terrain for a movementType
   */
  public int getMoveCost(int movementType) {
    if (movementType >= moveCosts.size()) {
      throw new IllegalArgumentException(this + " does not contain movecosts for movetype: " + movementType + " " + moveCosts);
    }
    return moveCosts.get(movementType);
  }

  public String toString() {
    return String.format("[id=%s name=%s height=%s defense=%s]", id, name, height, defenseBonus);
  }
}
