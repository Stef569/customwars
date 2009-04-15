package com.customwars.client.model.gameobject;

import com.customwars.client.model.map.Direction;
import tools.Args;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An immutable terrain datastore(no setters)
 * Traversing a terrain costs 'move points', this ranges from MIN_MOVE_COST to IMPASSIBLE
 * A moveType ID which is just the index in the movecost array has a cost to move over this terrain.
 * See {@link #canBeTraverseBy(int)} and {@link #getMoveCost(int)}
 *
 * Some terrains remain hidden even within the los of other objects(ie forrests).
 * Some terrain have extra vision(ie mountains, cities)
 *
 * @author stefan
 */
public class Terrain extends GameObject {
  public static final int IMPASSIBLE = Byte.MAX_VALUE;
  public static final int MIN_MOVE_COST = 1;
  private int id;
  private String type;
  private String name;
  private String description;
  private int defenseBonus;
  private int height;
  private boolean hidden;
  private int vision;
  private List<Integer> moveCosts;
  private List<Direction> connectedDirections;

  public Terrain(int id, String type, String name, String description, int defenseBonus, int height, boolean hidden, int vision, List<Integer> moveCosts) {
    this(id, type, name, description, defenseBonus, height, hidden, vision, moveCosts, null);
  }

  /**
   * @param id                  Unique identifier
   * @param type                The base Terrain this terrain is based on ie: Road, River, Ocean
   * @param name                Short name ie 'plain', 'grass', 'woods'
   * @param description         long description ie 'provides good cover for ground units'
   * @param defenseBonus        The defense bonus used in attack calculations, forrest offer better protection then plain.
   * @param height              Height of this terrain ie ocean is lower then a Mountain
   * @param hidden              is this terrain hidden within a moveZone
   * @param vision              The additional vision this terrain gives in fog of war
   * @param moveCosts           The cost to move over this terrain for each movementType, starts at 1(no cost) to 127(Impassible)
   * @param connectedDirections The Directions another terrain of the same type can be connected to
   */
  public Terrain(int id, String type, String name, String description, int defenseBonus, int height, boolean hidden, int vision,
                 List<Integer> moveCosts, List<Direction> connectedDirections) {
    super(GameObjectState.IDLE);
    this.id = id;
    this.type = type;
    this.name = name;
    this.description = description;
    this.defenseBonus = defenseBonus;
    this.height = height;
    this.hidden = hidden;
    this.vision = vision;
    this.moveCosts = moveCosts;
    this.connectedDirections = connectedDirections;
    init();
  }

  public void init() {
    Args.checkForContent(name, "Name is required");
    Args.checkForNull(moveCosts, "move costs are required for " + name);
    Collections.replaceAll(moveCosts, -1, IMPASSIBLE);

    for (int moveCost : moveCosts) {
      validateMoveCost(moveCost);
    }

    if (description == null) description = "";
    connectedDirections = Args.createEmptyListIfNull(connectedDirections);
  }

  private void validateMoveCost(int moveCosts) {
    String usage = " use " + IMPASSIBLE + " for max movecost and " + MIN_MOVE_COST + " for min movecost.";
    if (moveCosts < MIN_MOVE_COST) {
      throw new IllegalArgumentException("movecost " + moveCosts + " is <" + MIN_MOVE_COST + " in terrain " + this + usage);
    } else if (moveCosts > IMPASSIBLE) {
      throw new IllegalArgumentException("movecost " + moveCosts + " is >" + IMPASSIBLE + " in terrain " + this + usage);
    }
  }

  /**
   * Copy constructor
   *
   * @param otherTerrain The Terrain to copy
   */
  public Terrain(Terrain otherTerrain) {
    super(otherTerrain);
    this.id = otherTerrain.id;
    this.type = otherTerrain.type;
    this.name = otherTerrain.name;
    this.description = otherTerrain.description;
    this.defenseBonus = otherTerrain.defenseBonus;
    this.height = otherTerrain.height;
    this.hidden = otherTerrain.hidden;
    this.vision = otherTerrain.vision;
    this.moveCosts = new ArrayList<Integer>(otherTerrain.moveCosts);
    this.connectedDirections = new ArrayList<Direction>(otherTerrain.connectedDirections);
    init();
  }

  public int getID() {
    return id;
  }

  public String getType() {
    return type;
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

  public int getVision() {
    return vision;
  }

  /**
   * @return True if the movementType ID can move over this terrain
   */
  public boolean canBeTraverseBy(int movementType) {
    return getMoveCost(movementType) != IMPASSIBLE;
  }

  /**
   * The value IMPASSIBLE is returned when this terrain
   * cannot be traversed by the movementType ID
   *
   * @return The cost for moving over this terrain for a movementType
   */
  public int getMoveCost(int movementType) {
    if (movementType >= moveCosts.size()) {
      throw new IllegalArgumentException(this + " does not contain movecosts for movetype: " + movementType + " " + moveCosts);
    }
    return moveCosts.get(movementType);
  }

  public boolean isSameType(Terrain otherTerrain) {
    return type.equalsIgnoreCase(otherTerrain.type);
  }

  public boolean canConnectTo(Terrain otherTerrain) {
    return otherTerrain != null && connectedDirections != null && connectedDirections.size() > 0;
  }

  public boolean canConnectToOneOf(List<Direction> directions) {
    for (Direction direction : directions) {
      if (connectedDirections.contains(direction)) {
        return true;
      }
    }
    return false;
  }

  public boolean canConnectsToAll(Terrain otherTerrain) {
    return canConnectToAll(otherTerrain.connectedDirections);
  }

  /**
   * Return true when each direction in the directions list
   * is within this terrain connectedDirections
   */
  public boolean canConnectToAll(List<Direction> directions) {
    int numFound = 0;
    for (Direction direction : connectedDirections) {
      if (!directions.contains(direction)) {
        return false;
      } else {
        numFound++;
      }
    }
    return numFound == directions.size();
  }

  public String toString() {
    return String.format("[id=%s name='%s' type=%s height=%s defense=%s vision=%s connects=%s]", id, name, type, height, defenseBonus, vision, connectedDirections);
  }
}
