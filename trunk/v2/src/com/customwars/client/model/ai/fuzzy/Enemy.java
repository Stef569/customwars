package com.customwars.client.model.ai.fuzzy;

import com.customwars.client.model.gameobject.Unit;

/**
 * Represents an enemy Unit at a given distance
 */
public class Enemy {
  public Unit unit;
  public int distance;

  public Enemy(Unit unit, int distance) {
    this.unit = unit;
    this.distance = distance;
  }

  @Override
  public String toString() {
    return "Enemy " + unit.getName() + " Distance=" + distance + " tiles";
  }
}