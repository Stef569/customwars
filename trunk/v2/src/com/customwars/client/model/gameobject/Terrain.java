package com.customwars.client.model.gameobject;

import com.customwars.client.model.map.Direction;
import com.customwars.client.tools.Args;
import com.customwars.client.tools.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An immutable terrain
 * Traversing a terrain costs 'move points', this ranges from MIN_MOVE_COST to IMPASSIBLE
 * A moveType ID which is just the index in the movecost array has a cost to move over this terrain.
 * See {@link #canBeTraverseBy(int)} and {@link #getMoveCost(int)}
 * <p/>
 * Some terrains remain hidden even within the los of other objects(ie forests).
 * Some terrain have extra vision(ie mountains, cities)
 * <p/>
 * type is the terrain type that this terrain is inherited from(ie a horizontal road inherits from road)
 * spansOverType is used for bridges (ie a bridge is a road terrain that spans over a river)
 *
 * @author stefan
 */
public class Terrain extends GameObject {
  public static final int IMPASSIBLE = Byte.MAX_VALUE;
  public static final int MIN_MOVE_COST = 1;
  private final int id;
  private final String type, spansOverType;
  private final String name;
  private final String description;
  private final int defenseBonus;
  private final int height;
  private final boolean hidden;
  private final int vision;
  private final List<Integer> moveCosts;
  private TerrainConnection connection;

  public Terrain(int id, String type, String name, String description, int defenseBonus, int height, boolean hidden, int vision, List<Integer> moveCosts) {
    this(id, type, type, name, description, defenseBonus, height, hidden, vision, moveCosts, null, "");
  }

  public Terrain(int id, String type, String connectType, String name, String description, int defenseBonus, int height, boolean hidden, int vision, List<Integer> moveCosts) {
    this(id, type, connectType, name, description, defenseBonus, height, hidden, vision, moveCosts, null, "");
  }

  /**
   * @param id                  Unique identifier
   * @param type                The base Terrain this terrain is inherited from eg (Road, River, Ocean,...)
   * @param connectType         The base Terrain this terrain connects to
   * @param name                Short name eg 'plain', 'grass', 'woods'
   * @param description         Long description eg 'provides good cover for ground units'
   * @param defenseBonus        The defense bonus used in attack calculations, forest offer better protection then plain.
   * @param height              Height of this terrain eg ocean is lower then a Mountain
   * @param hidden              Is this terrain hidden within a moveZone
   * @param vision              The additional vision this terrain gives in fog of war
   * @param moveCosts           The cost to move over this terrain for each movementType, starts at 1(no cost) to 127(Impassible)
   * @param connectedDirections The Directions another terrain of the same type can be connected to
   */
  public Terrain(int id, String type, String connectType, String name, String description, int defenseBonus, int height, boolean hidden, int vision,
                 List<Integer> moveCosts, List<Direction> connectedDirections, String spansOverType) {
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
    this.connection = new TerrainConnection(connectType, connectedDirections);
    this.spansOverType = spansOverType;
    init();
  }

  public void init() {
    Args.checkForContent(name, "Name is required");
    Args.checkForNull(moveCosts, "move costs are required for " + name);
    Collections.replaceAll(moveCosts, -1, IMPASSIBLE);

    for (int moveCost : moveCosts) {
      validateMoveCost(moveCost);
    }

    Args.checkForNull(description, "Description is required for " + name);
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
    this.spansOverType = otherTerrain.spansOverType;
    this.name = otherTerrain.name;
    this.description = otherTerrain.description;
    this.defenseBonus = otherTerrain.defenseBonus;
    this.height = otherTerrain.height;
    this.hidden = otherTerrain.hidden;
    this.vision = otherTerrain.vision;
    this.moveCosts = new ArrayList<Integer>(otherTerrain.moveCosts);
    this.connection = new TerrainConnection(otherTerrain.connection);
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

  int getHeight() {
    return height;
  }

  public boolean isHidden() {
    return hidden;
  }

  public int getVision() {
    return vision;
  }

  public boolean isMountain() {
    return height >= 2;
  }

  public boolean isLand() {
    return height >= 0;
  }

  public boolean isRiver() {
    return height == -1;
  }

  public boolean isOcean() {
    return height == -2;
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

  public boolean inheritsFrom(String otherTerrainType) {
    return type.equalsIgnoreCase(otherTerrainType);
  }

  public boolean isSameType(Terrain otherTerrain) {
    return connection.canConnectTo(otherTerrain.connection);
  }

  public boolean isSameType(String otherTerrainType) {
    return connection.canConnectTo(otherTerrainType);
  }

  public boolean spansOver(Terrain terrain) {
    return spansOver(terrain.type);
  }

  public boolean spansOver(String spansOver) {
    return StringUtil.hasContent(spansOver) && StringUtil.hasContent(spansOverType) &&
      spansOverType.equalsIgnoreCase(spansOver);
  }

  public boolean canConnectToOneOf(List<Direction> directions) {
    return connection.canConnectToOneOf(directions);
  }

  /**
   * Return true when each direction in the directions list
   * is within this terrain connectedDirections
   */
  public boolean canConnectToAll(List<Direction> directions) {
    return connection.canConnectToAll(directions);
  }

  public String toString() {
    return String.format("[id=%s name='%s' type=%s height=%s defense=%s vision=%s connects=%s]", id, name, type, height, defenseBonus, vision, connection);
  }
}
