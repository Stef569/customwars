package com.customwars.client.model.map.gameobject;

import java.util.Arrays;
import java.util.List;

/**
 * An immutable terrain datastore(no setters)
 * Traversing a terrain costs 'move points', this ranges from 1 to IMPASSIBLE
 * Each moveType ID which is just the index in the array has a movecost value.
 * See {@link #canBeTraverseBy(int)} and {@link #getMoveCost(int)}
 *
 * @author stefan
 */
public class Terrain extends GameObject {
  public static final byte IMPASSIBLE = Byte.MAX_VALUE;
  private int id;
  private String name;
  private String description;
  private byte defenseBonus;
  private byte height;
  private List<Byte> moveCosts;
  private boolean hidden;

  /**
   * @param id           Unique identifier
   * @param name         Short name ie 'plain', 'grass', 'woods'
   * @param description  long description ie 'provides good cover for ground units'
   * @param defenseBonus The defense bonus used in attack calculations, forrest offer better protection then plain.
   * @param height       Height of this terrain ie ocean is lower then a Mountain
   * @param moveCosts    The cost to move over this terrain for each movementType, starts at 1(no cost) to 127(Impassible)
   *                     The movementType is used as index to retrieve the moveCost from the moveCosts array
   * @param hidden       is this terrain hidden within a moveZone
   */
  public Terrain(int id, String name, String description, byte defenseBonus, byte height, boolean hidden, Byte[] moveCosts) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.defenseBonus = defenseBonus;
    this.height = height;
    this.hidden = hidden;
    this.moveCosts = Arrays.asList(moveCosts);
    validateMoveCosts(moveCosts);
  }

  private void validateMoveCosts(Byte[] moveCosts) {
    String usage = " use Terrain.IMPASSIBLE for max movecost and 1 for min movecost.";
    for (Byte byteVal : moveCosts) {
      if (byteVal == null) {
        throw new IllegalArgumentException("movecosts cannot contain null in terrain " + this + usage);
      }
      if (byteVal <= 0) {
        throw new IllegalArgumentException("movecosts cannot contain <=0 values in terrain " + this + usage);
      }
    }
  }

  /**
   * Copy constructor
   *
   * @param otherTerrain The Terrain to copy
   */
  Terrain(Terrain otherTerrain) {
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

  public byte getDefenseBonus() {
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
  public byte getMoveCost(int movementType) {
    return moveCosts.get(movementType);
  }

  public String toString() {
    return String.format("[id=%s name=%s height=%s defense=%s]", id, name, height, defenseBonus);
  }
}
