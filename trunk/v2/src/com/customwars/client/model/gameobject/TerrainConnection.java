package com.customwars.client.model.gameobject;

import com.customwars.client.model.map.Direction;
import com.customwars.client.tools.Args;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Defines how a terrain connects to other terrains
 * eg. The terrain 'horizontal pipe seam' would contain following information:
 * Connects to: other 'pipe' terrains
 * Directions: East and West
 */
public class TerrainConnection implements Serializable {
  private final String connectsTo;
  private final List<Direction> connectedDirections;

  public TerrainConnection(String connectsTo, List<Direction> connectedDirections) {
    this.connectsTo = connectsTo;
    this.connectedDirections = Args.createEmptyListIfNull(connectedDirections);
  }

  public TerrainConnection(TerrainConnection otherTerrainConnection) {
    if (otherTerrainConnection == null) {
      this.connectsTo = "";
      this.connectedDirections = Collections.emptyList();
    } else {
      this.connectsTo = otherTerrainConnection.connectsTo;
      connectedDirections = new ArrayList<Direction>(otherTerrainConnection.connectedDirections);
    }
  }

  public boolean canConnectToOneOf(List<Direction> directions) {
    for (Direction direction : directions) {
      if (connectedDirections.contains(direction)) {
        return true;
      }
    }
    return false;
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

  public boolean canConnectTo(TerrainConnection otherTerrainConnection) {
    return canConnectTo(otherTerrainConnection.connectsTo);
  }

  public boolean canConnectTo(String otherTerrainType) {
    return connectsTo.equalsIgnoreCase(otherTerrainType);
  }

  @Override
  public String toString() {
    return "connectsTo '" + connectsTo + "' Dirs " + connectedDirections;
  }
}
