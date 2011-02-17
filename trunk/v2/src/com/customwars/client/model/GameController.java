package com.customwars.client.model;

import com.customwars.client.model.game.Player;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Terrain;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Location;

import java.util.Collection;

/**
 * Defines methods that can change the game. The context of these methods are not validated. This means that
 * before calling a method the caller should check if that action is actually possible. ie.:
 * if(canTeleport(from,to)) gameController.teleport(from,to);
 * <p/>
 * When the action is not possible the behaviour of these methods is not determined. Most probably they will throw a NPE.
 */
public interface GameController {
  /**
   * Drops a unit from within the transport to a drop location
   */
  void drop(Unit transport, Unit unit, Location dropLocation);

  void teleport(Location from, Location to);

  /**
   * Capture the city with unit
   *
   * @param unit unit that wants to perform the capturing
   * @param city The city to capture
   * @return if the city has been captured
   */
  boolean capture(Unit unit, City city);

  /**
   * Remove the unit from the map and add it to the transport
   */
  void load(Unit unit, Unit transport);

  /**
   * Supplies units around the apc
   *
   * @param apc The unit that performs the supply action
   * @return The amount of units that have been supplied
   */
  int supply(Unit apc);

  /**
   * The unit joins with target. Only the target unit remains on the map.
   * If joining causes the target to go over the max HP of a unit then
   * translate that excess hp to money and add it to the budget of the unit owner.
   */
  void join(Unit unit, Unit target);

  /**
   * The attacking unit attacks the city
   *
   * @return the damage percentage done to the city
   */
  int attack(Unit attacker, City city);

  /**
   * The attacking unit attacks the defending unit
   *
   * @return the damage percentage done to the defender
   */
  int attack(Unit attacker, Unit defender);

  /**
   * Launch a rocket from a silo and inflict damage to the effect range.
   * Note that units cannot die of a rocket attack. Instead they will keep 1HP.
   *
   * @param unit              unit that caused the rocket to launch
   * @param silo              The silo containing the rocket
   * @param rocketDestination Where the rocket should land
   * @param effectRange       The range around the destination that should receive damage
   * @return The area that will be damaged by the rocket
   */
  Collection<Location> launchRocket(Unit unit, City silo, Location rocketDestination, int effectRange);

  /**
   * Transform the terrain on the location to the transformToTerrain terrain.
   * Transforming terrains costs construction points.
   */
  void transformTerrain(Unit unit, Location location, Terrain transformToTerrain);

  /**
   * The unit fires a flare revealing flareRange tiles around the flareCenter.
   * The primary weapon ammo is decreased by 1.
   */
  void flare(Unit unit, Location flareCenter, int numOfTilesToReveal);

  /**
   * The unit constructs a new city on the location.
   * Constructing might take several turns before completion.
   * Constructing a city costs construction materials.
   */
  boolean constructCity(Unit unit, int cityID, Location location);

  void dive(Unit unit);

  void surface(Unit unit);

  /**
   * Make the unit IDLE, meaning that it can no longer be controlled.
   */
  void makeUnitWait(Unit unit);

  /**
   * The player creates a new unit. The unit is placed on the location in the map.
   * Note that the location can be a Tile or a unit!
   * Creating a unit costs the player the price of the unit.
   */
  void buildUnit(Unit unit, Location location, Player player);
}
