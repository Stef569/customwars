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
   *
   * @param transport The transporter that wants to drop the given unit
   * @param unit The unit to be dropped on the given drop location
   * @param dropLocation The location where the unit in the transport should be dropped on
   */
  void drop(Unit transport, Unit unit, Location dropLocation);

  /**
   * Teleports the unit on the from location in the map to the to destination in the map.
   * The location of the unit is changed to the destination.
   *
   * @param from The location in the map to teleport the unit from
   * @param to   The location in the map to teleport the unit to
   */
  void teleport(Location from, Location to);

  /**
   * Capture the city with the given unit
   *
   * @param unit unit that wants to perform the capturing
   * @param city The city to capture
   * @return if the city has been captured
   */
  boolean capture(Unit unit, City city);

  /**
   * Remove the unit from the map and add it to the transport
   *
   * @param unit The unit that wants to be loaded into the transport
   * @param transport The transporter that will load the given unit
   */
  void load(Unit unit, Unit transport);

  /**
   * Supplies units around the given unit
   *
   * @param unit The unit that performs the supply action
   * @return The amount of units that have been supplied
   */
  int supply(Unit unit);

  /**
   * The unit joins with target. Only the target unit remains on the map.
   * If joining causes the target to go over the max HP of a unit then
   * translate that excess hp to money and add it to the budget of the unit owner.
   *
   * @param unit   The unit that wants to join with the target
   * @param target The unit that remains in the map after the join
   */
  void join(Unit unit, Unit target);

  /**
   * The attacking unit attacks the city
   *
   * @param attacker The unit that wants to attack the city
   * @param city     The city that will be attacked
   * @return the damage percentage done to the city
   */
  int attack(Unit attacker, City city);

  /**
   * The attacking unit attacks the defending unit.
   * The defender can perform a counter attack.
   *
   * @param attacker The unit that wants to attack the defender
   * @param defender The unit that will receive the damage
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
   * Transforming a terrain costs 1 construction point.
   *
   * @param unit               The unit that wants to transform a terrain
   * @param location           The location of the terrain that will be transformed
   * @param transformToTerrain The new terrain that will replace the previous terrain
   */
  void transformTerrain(Unit unit, Location location, Terrain transformToTerrain);

  /**
   * The unit fires a flare revealing flareRange tiles around the flareCenter.
   * The primary weapon ammo is decreased by 1.
   *
   * @param unit               The unit that wants to fire a flare
   * @param flareCenter        The centre location, where numOfTilesToReveal will be revealed
   * @param numOfTilesToReveal The radius of the spiral around the centre location.
   */
  void flare(Unit unit, Location flareCenter, int numOfTilesToReveal);

  /**
   * The unit start constructing a new city on the location.
   * Constructing might take several turns before completion.
   * The construction materials are decreased by 1 if the city has been constructed.
   *
   * @param unit     The unit that wants to construct a new city
   * @param location The location to construct the city
   * @param cityID   The name of the city that is to be placed on the location
   * @return the current construction percentage
   */
  int constructCity(Unit unit, String cityID, Location location);

  void dive(Unit unit);

  void surface(Unit unit);

  /**
   * Make the unit IDLE, meaning that it can no longer be controlled.
   *
   * @param unit The unit that needs to be IDLE
   */
  void makeUnitWait(Unit unit);

  /**
   * Create a unit and load it into the producer unit.
   * Creating a unit costs the player the price of the produced unit.
   *
   * @param producer      The producing unit to add the new unit to
   * @param unitToProduce The name of the unit to produce.
   */
  void produceUnit(Unit producer, String unitToProduce);

  /**
   * The player creates a new unit. The unit is placed on the location in the map.
   * Creating a unit costs the player the price of the unit.
   *
   * @param unit     The unit to be build and placed in the map on the given location
   * @param location The location where the unit will be placed on
   * @param player   The player that will control this new unit
   */
  void buildUnit(Unit unit, Location location, Player player);

  /**
   * Load the CO into the given unit
   *
   * @param unit The unit that wants to have the co on board
   */
  void loadCO(Unit unit);

  /**
   * Perform the CO power of the active player.
   */
  void coPower();

  /**
   * Perform the CO super power of the active player.
   */
  void coSuperPower();
}
