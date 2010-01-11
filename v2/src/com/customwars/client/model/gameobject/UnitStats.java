package com.customwars.client.model.gameobject;

import com.customwars.client.model.ArmyBranch;
import com.customwars.client.model.map.Range;
import com.customwars.client.model.map.path.DefaultMoveStrategy;
import com.customwars.client.model.map.path.MoveStrategy;
import com.customwars.client.tools.Args;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Immutable statistics of a Unit
 */
public class UnitStats implements Serializable {
  final int unitID;       // The unit Type ie(1->INF, 2->APC,...)
  final int imgRowID;     // The id, used to retrieve the images for this unit
  final String name;            // Full name ie Infantry, Tank, ...
  final String description;     // Information about this Unit
  final int price;        // The price for buying this Unit
  final int movement;     // The move points
  final int vision;       // The amount of tiles this unit can see in all directions aka line of sight
  MoveStrategy moveStrategy;  // Determines how a unit moves

  final int maxExperience;      // The maximum experience this unit can have
  final int maxHp;              // The value when this unit is 100% Healthy
  Range supplyRange;            // Range in which the unit can supply
  final int maxSupplies;        // The value when this unit has 100% supplies
  final int maxTransportCount;  // Amount of units that can be transported
  final int suppliesPerTurn;    // Amount of supplies that are subtracted each turn from supplies
  final int healRate;           // Amount of healing that a transport can give to units that can be build by that transport

  final boolean canCapture;     // Abilities
  final boolean canDive;
  final boolean canSupply;
  final boolean canTransport;
  final boolean canJoin;
  final boolean canFlare;
  final boolean canHide;

  private Map<Integer, Integer> transformTerrains;  // Terrain Ids this unit can transform to for a given TerrainId
  private Map<Integer, Integer> buildCities;        // City Ids this unit can build on given terrains
  private List<Integer> buildUnits;           // Units that can be build/healed
  private List<Integer> transports;           // Units that can be transported (empty when this unit can't transport)
  final ArmyBranch armyBranch;                // Naval, Ground, Air
  final int movementType;                     // Inf, Mech, Tires, Tread, Air, Naval ...

  private final String primaryWeaponName;     // Weapons, "" means no weapon
  private final String secondaryWeaponName;
  private final int suppliesPerTurnWhenHidden;

  public UnitStats(int unitID, int imgRowID, String name, String description,
                   int price, int movement, int vision, int maxExperience,
                   int maxHp, int maxSupplies, int maxTransportCount, int suppliesPerTurn,
                   boolean canCapture, boolean canDive, boolean canSupply, boolean canTransport, boolean canJoin,
                   boolean canFlare, boolean canHide, List<Integer> transports,
                   ArmyBranch armyBranch, int movementType, Range supplyRange, String primaryWeaponName, String secondaryWeaponName, int healRate, int suppliesPerTurnWhenHidden) {
    this.unitID = unitID;
    this.imgRowID = imgRowID;
    this.name = name;
    this.description = description;
    this.price = price;
    this.movement = movement;
    this.vision = vision;
    this.maxExperience = maxExperience;

    this.maxHp = maxHp;
    this.maxSupplies = maxSupplies;
    this.maxTransportCount = maxTransportCount;
    this.suppliesPerTurn = suppliesPerTurn;

    this.canCapture = canCapture;
    this.canDive = canDive;
    this.canSupply = canSupply;
    this.canTransport = canTransport;
    this.canJoin = canJoin;
    this.canFlare = canFlare;
    this.canHide = canHide;
    this.transports = transports;

    this.armyBranch = armyBranch;
    this.movementType = movementType;
    this.supplyRange = supplyRange;
    this.primaryWeaponName = primaryWeaponName;
    this.secondaryWeaponName = secondaryWeaponName;
    this.healRate = healRate;
    this.suppliesPerTurnWhenHidden = suppliesPerTurnWhenHidden;
    validate();
  }

  public void validate() {
    transports = Args.createEmptyListIfNull(transports);
    transformTerrains = transformTerrains == null ? new HashMap<Integer, Integer>() : transformTerrains;
    buildCities = buildCities == null ? new HashMap<Integer, Integer>() : buildCities;
    buildUnits = Args.createEmptyListIfNull(buildUnits);
    supplyRange = supplyRange == null ? Range.ZERO_RANGE : supplyRange;
    Args.checkForNull(name, "please provide a name for unitID " + unitID);
    Args.checkForNull(description, "please provide a description for unitID " + unitID);
    Args.validate(suppliesPerTurn < 0, "supplies per turn should be positive");
    Args.validate(suppliesPerTurnWhenHidden < 0, "supplies per turn when hidden should be positive");
    if (moveStrategy == null) moveStrategy = new DefaultMoveStrategy();
  }

  public ArmyBranch getArmyBranch() {
    return armyBranch;
  }

  public int getID() {
    return unitID;
  }

  public String getName() {
    return name;
  }

  public int getImgRowID() {
    return imgRowID;
  }

  public Range getSupplyRange() {
    return supplyRange;
  }

  public int getVision() {
    return vision;
  }

  public boolean canJoin() {
    return canJoin;
  }

  public boolean canFlare() {
    return canFlare;
  }

  public int getPrice() {
    return price;
  }

  public int getMaxSupplies() {
    return maxSupplies;
  }

  public String getDescription() {
    return description;
  }

  public int getMovement() {
    return movement;
  }

  public int getMaxExperience() {
    return maxExperience;
  }

  public int getMaxHp() {
    return maxHp;
  }

  public int getMaxTransportCount() {
    return maxTransportCount;
  }

  public int getSuppliesPerTurn() {
    return suppliesPerTurn;
  }

  public boolean canDive() {
    return canDive;
  }

  public int getMovementType() {
    return movementType;
  }

  public boolean canSupply() {
    return canSupply;
  }

  public boolean canHide() {
    return canHide;
  }

  public boolean canCapture() {
    return canCapture;
  }

  public boolean canTransformTerrain(Terrain terrain) {
    return transformTerrains.containsKey(terrain.getID());
  }

  public int getTransformTerrainFor(Terrain terrain) {
    return transformTerrains.get(terrain.getID());
  }

  public Map<Integer, Integer> getTransformTerrains() {
    return Collections.unmodifiableMap(transformTerrains);
  }

  public boolean canTransport() {
    return canTransport;
  }

  public boolean canTransport(int id) {
    return transports.contains(id);
  }

  public List<Integer> getTransports() {
    return Collections.unmodifiableList(transports);
  }

  public boolean canBuildCity(int id) {
    return buildCities.containsKey(id);
  }

  public boolean canBuildCityOn(Terrain terrain) {
    return canBuildCity(terrain.getID());
  }

  public int getCityToBuildOnTerrain(Terrain terrain) {
    return buildCities.get(terrain.getID());
  }

  public boolean canBuildUnit(Unit unit) {
    return canBuildUnit(unit.getStats().unitID);
  }

  public boolean canBuildUnit(int unitID) {
    return buildUnits.contains(unitID);
  }

  public Iterable<Integer> getUnitsThatCanBeBuild() {
    return Collections.unmodifiableList(buildUnits);
  }

  public String getSecondaryWeaponName() {
    return secondaryWeaponName;
  }

  public String getPrimaryWeaponName() {
    return primaryWeaponName;
  }

  public int getSuppliesPerTurnWhenHidden() {
    return suppliesPerTurnWhenHidden;
  }

  @Override
  public String toString() {
    return "UnitStats{" +
      "unitID=" + unitID +
      ", imgRowID=" + imgRowID +
      ", name='" + name + '\'' +
      ", description='" + description + '\'' +
      ", price=" + price +
      ", movement=" + movement +
      ", vision=" + vision +
      ", maxExperience=" + maxExperience +
      ", maxHp=" + maxHp +
      ", supplyRange=" + supplyRange +
      ", maxSupplies=" + maxSupplies +
      ", maxTransportCount=" + maxTransportCount +
      ", suppliesPerTurn=" + suppliesPerTurn +
      ", suppliesPerTurnWhenHidden=" + suppliesPerTurnWhenHidden +
      ", canCapture=" + canCapture +
      ", canDive=" + canDive +
      ", canSupply=" + canSupply +
      ", canTransport=" + canTransport +
      ", canJoin=" + canJoin +
      ", canFlare=" + canFlare +
      ", canHide=" + canHide +
      ", transformTerrains=" + transformTerrains +
      ", buildCities=" + buildCities +
      ", buildUnits=" + buildUnits +
      ", transports=" + transports +
      ", armyBranch=" + armyBranch +
      ", movementType=" + movementType +
      ", primaryWeaponName='" + primaryWeaponName + '\'' +
      ", secondaryWeaponName='" + secondaryWeaponName + '\'' +
      '}';
  }
}
