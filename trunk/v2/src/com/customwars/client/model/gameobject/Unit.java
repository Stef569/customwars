package com.customwars.client.model.gameobject;

import com.customwars.client.App;
import com.customwars.client.model.ArmyBranch;
import com.customwars.client.model.TurnHandler;
import com.customwars.client.model.fight.Attacker;
import com.customwars.client.model.fight.Defender;
import com.customwars.client.model.fight.Fight;
import com.customwars.client.model.game.Player;
import com.customwars.client.model.map.Direction;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Range;
import com.customwars.client.model.map.path.MoveStrategy;
import com.customwars.client.model.map.path.Mover;
import com.customwars.client.tools.Args;
import com.customwars.client.tools.NumberUtil;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Is a Mover meaning it has the ability to be put on and removed from a Location.
 * Is a Location, meaning it can transport Locatables
 * It has a Facing direction, this is one of the compass Direction(N,S,E,W)
 * Units have 2 weapons(both are optional): 1 Primary and 1 Secondary
 * <p/>
 * The internal hp of a unit has a range of 0-maxHp instead of 0-10. This approach allows to take a small damage.
 * and is easier then using a double. getHP and getMaxHP convert the internal hp back to the 0-10 range ie:
 * hp=100, maxhp=100 getHP() = 10 -5% damage
 * hp=95, maxhp=100 getHP() = 10 -5% damage
 * hp=90, maxhp=100 getHP() = 9
 *
 * @author Stefan
 */
public class Unit extends GameObject implements Mover, Location, TurnHandler, Attacker, Defender {
  public static final Direction DEFAULT_ORIENTATION = Direction.EAST;
  private static final int LOW_AMMO_PERCENTAGE = 50;
  private static final int LOW_SUPPLIES = 50;

  private final UnitStats stats;
  private City constructingCity; // A City that is being build by this unit
  private int hp;               // Health Points, if 0 the unit is destroyed
  private int supplies;         // Supplies, this can be in the form of rations(troops) or fuel(motorized vehicles)
  private int experience;       // Each time a unit wins a fight his Experience rises(starting from 0)
  private UnitState unitState;  // Current Unitstate (submerged, capturing a city,...)
  private Player owner;         // Player owning this unit
  private Location location;    // Current Location
  private boolean coOnBoard;        // Is a CO loaded within this unit
  private List<Location> moveZone;  // A zone where this unit can move in
  private List<Location> attZone;   // A zone where this unit can attack in
  private Direction orientation;    // The direction this unit is looking at
  private boolean hidden;           // In fog of war, is this unit hidden within enemy los

  private Weapon primaryWeapon, secondaryWeapon;
  private List<Locatable> transport;  // Locatables that are within this Transport
  private boolean hideAbilityEnabled; // Allows to hide for a longer time
  private MoveStrategy moveStrategy;  // A MoveStrategy instance for this unit

  public Unit(UnitStats unitStats) {
    super(GameObjectState.ACTIVE);
    this.stats = unitStats;
    init();
    reset();
  }

  void init() {
    stats.validate();
    transport = new LinkedList<Locatable>();
    if (stats.canDive) dive();
    unitState = UnitState.IDLE;
    moveStrategy = stats.moveStrategy.newInstance(this);
  }

  /**
   * Copy Constructor, primitive values are just copied
   * objects need to be wrapped into a new Object. Unless 1 object should be shared between all units
   *
   * @param otherUnit unit to copy
   */
  public Unit(Unit otherUnit) {
    super(otherUnit);
    stats = otherUnit.stats;
    hp = otherUnit.hp;
    supplies = otherUnit.supplies;
    experience = otherUnit.experience;
    unitState = otherUnit.unitState;

    primaryWeapon = otherUnit.primaryWeapon != null ? new Weapon(otherUnit.primaryWeapon) : null;
    secondaryWeapon = otherUnit.secondaryWeapon != null ? new Weapon(otherUnit.secondaryWeapon) : null;

    location = otherUnit.location;
    coOnBoard = otherUnit.coOnBoard;
    owner = otherUnit.owner;
    moveStrategy = otherUnit.stats.moveStrategy.newInstance(this);
    copyUnitsInTheTransport(otherUnit.transport);
  }

  private void copyUnitsInTheTransport(List<Locatable> unitsInTransport) {
    this.transport = new LinkedList<Locatable>();
    for (Locatable locatable : unitsInTransport) {
      Unit unit = (Unit) locatable;
      this.transport.add(new Unit(unit));
    }
  }

  /**
   * Resets the unit state to default:
   * 100% Supplies, health
   * 100% ammo in both weapons(or 1 or none)
   * Facing east, Active state, no exp
   */
  void reset() {
    resupply();
    hp = stats.maxHp;
    setExperience(0);
    setOrientation(DEFAULT_ORIENTATION);
    setState(GameObjectState.ACTIVE);
  }

  /**
   * Destroy this unit
   *
   * @param fireEvent if true 1 event is fired GameObjectState -> destroyed
   *                  This allows listeners to take an action when this unit is destroyed.
   */
  public void destroy(boolean fireEvent) {
    if (coOnBoard) {
      setCoOnBoard(false);
      owner.getCO().resetPowerGauge();
    }

    clearTransport();
    owner.removeUnit(this);
    owner = null;
    location.remove(this);
    location = null;

    if (fireEvent) {
      setState(GameObjectState.DESTROYED);
    }
  }

  /**
   * Destroy each unit in the transport
   */
  private void clearTransport() {
    while (!transport.isEmpty()) {
      Locatable locatable = transport.get(transport.size() - 1);

      if (locatable instanceof Unit) {
        Unit unit = (Unit) locatable;
        unit.destroy(false);
      }
    }
  }

  public void startTurn(Player currentPlayer) {
    supplyUnitsInTransport();
  }

  /**
   * If this unit is transporting units and it can build the unit in the transport
   * then supply and heal the units in the transport
   */
  private void supplyUnitsInTransport() {
    if (stats.canTransport && !transport.isEmpty()) {
      for (Locatable locatable : transport) {
        Unit unit = (Unit) locatable;
        if (stats.canBuildUnit(unit)) {
          supply(unit);
          unit.heal(stats.healRate);
        }
      }
    }
  }

  public void endTurn(Player currentPlayer) {
    if (unitState == UnitState.SUBMERGED || hidden) {
      addSupplies(-stats.getSuppliesPerTurnWhenHidden());
    } else {
      addSupplies(-stats.suppliesPerTurn);
    }
  }

  public void loadCO() {
    setCoOnBoard(true);
  }

  private void setCoOnBoard(boolean coOnBoard) {
    boolean oldVal = this.coOnBoard;
    this.coOnBoard = coOnBoard;
    firePropertyChange("cooboard", oldVal, this.coOnBoard);
  }

  // ----------------------------------------------------------------------------
  // Actions :: Submarine
  // ----------------------------------------------------------------------------

  public void dive() {
    setUnitState(UnitState.SUBMERGED);
    hideAbilityEnabled = true;
  }

  public void surface() {
    setUnitState(UnitState.IDLE);
    hideAbilityEnabled = false;
  }

  // ----------------------------------------------------------------------------
  // Actions :: Attack/Defend
  // ----------------------------------------------------------------------------

  /**
   * This unit is the attacker
   * and attacking the defender
   */
  public void attack(Defender defender, Fight fight) {
    defender.defend(this, fight);
    tryToFireWeapon(fight);

    if (defender.isDestroyed()) {
      if (App.getBoolean("plugin.unit_can_gain_experience")) {
        setExperience(experience + 1);
      }
    }

    owner.getCO().unitAttackedHook(this, defender);
  }

  public boolean canAttack(Defender defender) {
    boolean validDefender = defender != null && !defender.isDestroyed() && !defender.isAlliedWith(owner);
    return validDefender && canFireOn(defender) && !isDestroyed();
  }

  private void tryToFireWeapon(Fight fight) {
    Fight.WeaponType weaponType = fight.getBestAttackWeaponType();
    fireWeapon(weaponType, 1);
  }

  /**
   * When a unit fires the shots are subtracted from the ammoCount in weapon.
   * Some units cannot fire, they should have primaryWeapon and secondaryWeapon set to null.
   *
   * @param weaponType The weapon type that was used to attack(Primary or Secondary)
   * @param shots      amount of bullets, missiles or shells the weapon will fire
   * @see Weapon
   */
  private void fireWeapon(Fight.WeaponType weaponType, int shots) {
    switch (weaponType) {
      case PRIMARY:
        primaryWeapon.fire(shots);
        break;
      case SECONDARY:
        secondaryWeapon.fire(shots);
        break;
    }
  }

  public void defend(Attacker attacker, Fight fight) {
    int attackPercentage = fight.getAttackDamagePercentage();
    int attackValue = (int) (((double) attackPercentage / stats.maxHp) * 100);
    setHp(hp - attackValue);
  }

  /**
   * This unit can counter Attack when:
   * #1 it is not destroyed
   * #2 it is direct
   * #3 the Attacker is also a defender
   * #4 it has a weapon that can return fire to the attacker
   */
  public boolean canCounterAttack(Attacker attacker) {
    boolean attackerIsDefender = attacker instanceof Defender;
    boolean canReturnFire = attackerIsDefender && !isDestroyed() && canFireOn((Defender) attacker);
    return canReturnFire && isDirect();
  }

  public List<Location> getAttackZone() {
    return Collections.unmodifiableList(attZone);
  }

  // ---------------------------------------------------------------------------
  // Actions :: Transport
  // ---------------------------------------------------------------------------

  public int getCol() {
    return location.getCol();
  }

  public int getRow() {
    return location.getRow();
  }

  public String getLocationString() {
    return location.getLocationString();
  }

  public Locatable getLastLocatable() {
    return getLocatable(transport.size() - 1);
  }

  public Locatable getLocatable(int index) {
    return !transport.isEmpty() ? transport.get(index) : null;
  }

  public boolean remove(Locatable locatable) {
    if (!contains(locatable) || locatable == null) {
      return false;
    }

    locatable.setLocation(null);    // Keep locatable and tile in sync
    transport.remove(locatable);
    firePropertyChange("transport", locatable, null);
    return true;
  }

  public boolean contains(Locatable locatable) {
    return transport.contains(locatable);
  }

  public void add(Locatable locatable) {
    if (canAdd(locatable)) {
      transport.add(locatable);
      locatable.setLocation(this);    // Keep locatable and tile in sync
    }
    firePropertyChange("transport", null, locatable);
  }

  public boolean canAdd(Locatable locatable) {
    if (locatable instanceof Unit) {
      Unit unit = (Unit) locatable;
      return canTransport(unit.getMovementType());
    } else {
      return false;
    }
  }

  public boolean canTransport(int id) {
    return stats.canTransport && stats.canTransport(id) && !isTransportFull();
  }

  private boolean isTransportFull() {
    return transport.size() >= stats.maxTransportCount;
  }

  /**
   * @return Amount of units in the transport
   */
  public int getLocatableCount() {
    return transport.size();
  }

  /**
   * Get the index of the unit in the transport. starting from 0
   *
   * @param unit The unit in this transport where we want to retrieve the index for
   * @return the index or -1 when the unit is not in this transport
   */
  public int indexOf(Unit unit) {
    for (int i = 0; i < transport.size(); i++) {
      Unit unitInTransport = (Unit) getLocatable(i);

      if (unitInTransport == unit) {
        return i;
      }
    }
    return -1;
  }

  /**
   * @return The range in tiles around this unit
   *         where units in this transport can be dropped on
   */
  public int getMaxDropRange() {
    if (stats.canTransport && !transport.isEmpty()) {
      Unit unitInTransport = (Unit) transport.get(0);

      switch (unitInTransport.getArmyBranch()) {
        case LAND:
          return 1;
        default:
          return unitInTransport.getMovePoints();
      }
    } else {
      return 1;
    }
  }

  // ----------------------------------------------------------------------------
  // Actions :: supply, heal
  // ----------------------------------------------------------------------------

  /**
   * Restore
   * supplies to max
   * ammo to max
   */
  public void resupply() {
    setSupplies(stats.maxSupplies);
    restockWeapons();
  }

  private void restockWeapons() {
    if (hasPrimaryWeapon()) {
      primaryWeapon.restock();
    }
    if (hasSecondaryWeapon()) {
      secondaryWeapon.restock();
    }
  }

  public void supply(Unit unit) {
    if (canSupply(unit))
      unit.resupply();
  }

  /**
   * This unit is the supplier, and returns if it can supply the given unit
   * #1 Can this unit supply at all
   * #2 Can we add supplies or ammo to one of the weapons of the unit
   * #3 Is the supplier and the unit owned by the same player
   *
   * @param unit the unit that is going to be supplied by this unit
   * @return Can this unit supply the given unit
   */
  public boolean canSupply(Unit unit) {
    return unit != null && stats.canSupply && !unit.isFullySupplied() && owner == unit.owner;
  }

  /**
   * @return true when
   *         #1 the unit is 100% supplied
   *         #2 The weapons of the unit both have 100% ammo, when one of the weapons is null it has 100% ammo.
   */
  public boolean isFullySupplied() {
    boolean fullSupplies = getSuppliesPercentage() == 100;
    boolean priWeaponAmmoIsFull = !hasPrimaryWeapon() || primaryWeapon.getAmmoPercentage() == 100;
    boolean secWeaponAmmoIsFull = !hasSecondaryWeapon() || secondaryWeapon.getAmmoPercentage() == 100;
    return fullSupplies && priWeaponAmmoIsFull && secWeaponAmmoIsFull;
  }

  public void heal(int healRate) {
    int healCost = getHealCost(healRate);
    if (canHeal(healCost)) {
      addHp(healRate);
      owner.addToBudget(-healCost);
    }
  }

  public boolean canHeal(int healCost) {
    return hp != stats.maxHp && owner.isWithinBudget(healCost);
  }

  private int getHealCost(int healRate) {
    int healAmount = getMaxHp() - getHp();
    if (healAmount > healRate) {
      healAmount = healRate;
    }
    return healAmount * (getPrice() / stats.maxHp);
  }

  // ----------------------------------------------------------------------------
  // Actions :: Construction of City, Transform terrains
  // ----------------------------------------------------------------------------

  /**
   * Construct city, This might take more then one invocation
   * isConstructionComplete() will return true when the city is constructed.
   * When the city construction is completed call stopConstructing() on this unit
   *
   * @param city The city to construct
   */
  public void construct(City city) {
    Args.checkForNull(city);
    if (constructingCity == null || constructingCity.getID() != city.getID()) {
      constructingCity = city;
    }

    constructingCity.capture(this);
  }

  public boolean isConstructionComplete() {
    return constructingCity.isCapturedBy(this);
  }

  public void stopConstructing() {
    constructingCity = null;
  }

  // ---------------------------------------------------------------------------
  // Setters
  // ---------------------------------------------------------------------------

  public void setExperience(int newExperience) {
    int oldVal = this.experience;
    this.experience = Args.getBetweenZeroMax(newExperience, stats.maxExperience);
    firePropertyChange("experience", oldVal, experience);
  }

  public void setPrimaryWeapon(Weapon priWeapon) {
    Weapon oldVal = this.primaryWeapon;
    primaryWeapon = priWeapon;
    firePropertyChange("primaryWeapon", oldVal, orientation);
  }

  public void setSecondaryWeapon(Weapon secWeapon) {
    Weapon oldVal = this.secondaryWeapon;
    secondaryWeapon = secWeapon;
    firePropertyChange("secondaryWeapon", oldVal, orientation);
  }

  public void setOrientation(String orientation) {
    Direction dir = Direction.getDirection(orientation);
    setOrientation(dir);
  }

  public void setOrientation(Direction orientation) {
    Direction oldVal = this.orientation;
    this.orientation = orientation;
    firePropertyChange("orientation", oldVal, orientation);
  }

  public void setOwner(Player owner) {
    Player oldVal = this.owner;
    this.owner = owner;
    firePropertyChange("owner", oldVal, owner);
  }

  public void addHp(int additionalHp) {
    setHp(hp + additionalHp * 10);
  }

  protected void setHp(int hp) {
    if (hp <= 0) {
      destroy(true);
    }

    int oldVal = this.hp;
    this.hp = Args.getBetweenZeroMax(hp, stats.maxHp);
    firePropertyChange("hp", oldVal, this.hp);
  }

  public void addSupplies(int additionalSupplies) {
    setSupplies(supplies + additionalSupplies);
  }

  protected void setSupplies(int amount) {
    int oldVal = this.supplies;
    this.supplies = Args.getBetweenZeroMax(amount, stats.maxSupplies);
    firePropertyChange("supplies", oldVal, this.supplies);
  }

  public void addAmmo(int additionalAmmo) {
    Weapon availableWeapon = getAvailableWeapon();
    if (availableWeapon != null) {
      availableWeapon.addAmmo(additionalAmmo);
    }
  }

  public void setLocation(Location newLocation) {
    Location oldLocation = this.location;
    this.location = newLocation;
    firePropertyChange("location", oldLocation, newLocation);
  }

  public void setMoveZone(List<Location> moveZone) {
    List<Location> oldVal = this.moveZone;
    this.moveZone = moveZone;
    firePropertyChange("moveZone", oldVal, moveZone);
  }

  public void setAttackZone(List<Location> attackZone) {
    List<Location> oldVal = this.attZone;
    this.attZone = attackZone;
    firePropertyChange("attackZone", oldVal, attackZone);
  }

  public void setUnitState(UnitState unitState) {
    UnitState oldVal = this.unitState;
    this.unitState = unitState;
    firePropertyChange("unitState", oldVal, unitState);
  }

  public void setHidden(boolean hidden) {
    boolean oldVal = this.hidden;
    this.hidden = hidden;
    firePropertyChange("hidden", oldVal, hidden);
  }

  public void setMoveStrategy(MoveStrategy moveStrategy) {
    MoveStrategy oldVal = moveStrategy;
    this.moveStrategy = moveStrategy;
    firePropertyChange("moveStrategy", oldVal, moveStrategy);
  }

  // ---------------------------------------------------------------------------
  // Getters :: Weapon
  // ---------------------------------------------------------------------------

  /**
   * Retrieve the first available weapon: Not null and some ammo left
   * starting with the primary weapon
   *
   * @return The first available weapon, null when no weapons are available
   */
  public Weapon getAvailableWeapon() {
    return canFirePrimaryWeapon() ? primaryWeapon : canFireSecondaryWeapon() ? secondaryWeapon : null;
  }

  public Weapon getPrimaryWeapon() {
    return primaryWeapon;
  }

  public Weapon getSecondaryWeapon() {
    return secondaryWeapon;
  }

  /**
   * @return if this unit can fire on the defender
   */
  private boolean canFireOn(Defender defender) {
    ArmyBranch defenderArmyBranch = defender.getArmyBranch();
    boolean canFirePrimaryWeapon = hasPrimaryWeapon() && primaryWeapon.canFire(defenderArmyBranch);
    boolean canFireSecondaryWeapon = hasSecondaryWeapon() && secondaryWeapon.canFire(defenderArmyBranch);

    return canFirePrimaryWeapon || canFireSecondaryWeapon;
  }

  /**
   * @return if one of the two weapons has enough ammo to make 1 more shot
   */
  public boolean canFire() {
    return canFirePrimaryWeapon() || canFireSecondaryWeapon();
  }

  public boolean canFirePrimaryWeapon() {
    return hasPrimaryWeapon() && primaryWeapon.hasAmmoLeft();
  }

  public boolean canFireSecondaryWeapon() {
    return hasSecondaryWeapon() && secondaryWeapon.hasAmmoLeft();
  }

  public boolean hasPrimaryWeapon() {
    return primaryWeapon != null;
  }

  public boolean hasSecondaryWeapon() {
    return secondaryWeapon != null;
  }

  public Range getAttackRange() {
    Weapon weapon = getAvailableWeapon();

    if (weapon != null) {
      int minRange = weapon.getRange().getMinRange();
      int maxRange = weapon.getRange().getMaxRange();
      int coMaxRange = owner.getCO().fireRangeHook(maxRange);
      return new Range(minRange, coMaxRange);
    } else {
      return Range.ZERO_RANGE;
    }
  }

  /**
   * @return True when one of the weapons has less then 50% ammo.
   */
  public boolean hasLowAmmo() {
    boolean lowAmmo = false;
    if (hasPrimaryWeapon()) {
      if (primaryWeapon.getAmmoPercentage() <= LOW_AMMO_PERCENTAGE) {
        lowAmmo = true;
      }
    } else if (hasSecondaryWeapon()) {
      if (secondaryWeapon.getAmmoPercentage() <= LOW_AMMO_PERCENTAGE) {
        lowAmmo = true;
      }
    }
    return lowAmmo;
  }

  // ---------------------------------------------------------------------------
  // Getters :: Supplies, hp
  // ---------------------------------------------------------------------------

  public UnitStats getStats() {
    return stats;
  }

  public int getSupplies() {
    return supplies;
  }

  public int getSuppliesPercentage() {
    return NumberUtil.calcPercentage(supplies, stats.maxSupplies);
  }

  public boolean hasLowSupplies() {
    return getSuppliesPercentage() <= LOW_SUPPLIES;
  }

  /**
   * Converts the internal hp to a number between 0 - 10 rounding up to the nearest value.
   * hp=25
   * getHp() return 3
   * hp=19
   * getHp() return 2
   *
   * @return The rounded Hp between 0 - 10
   */
  public int getHp() {
    return (int) Math.ceil((double) hp / 10);
  }

  /**
   * Converts the internal maxhp to a number between 0 - 10 rounding up to the nearest value.
   * maxhp=99
   * getMaxHp() return 10
   * maxhp=79
   * getMaxHp() return 8
   *
   * @return The max Hp between 0 - 10
   */
  public int getMaxHp() {
    return (int) Math.ceil((double) stats.maxHp / 10);
  }

  public int getInternalHp() {
    return hp;
  }

  public int getInternalMaxHp() {
    return stats.maxHp;
  }

  public int getHpPercentage() {
    return NumberUtil.calcPercentage(hp, stats.maxHp);
  }

  /**
   * @return if the hp dropped at least 10% under the max hp
   */
  public boolean hasLowHp() {
    // Don't use the internal hp because 99/100 is not low hp, 9/10 is
    return getHp() < getMaxHp();
  }

  // ---------------------------------------------------------------------------
  // Getters :: Moving
  // ---------------------------------------------------------------------------

  public Location getLocation() {
    return location;
  }

  public int getMovementType() {
    return stats.movementType;
  }

  public MoveStrategy getMoveStrategy() {
    return moveStrategy;
  }

  public int getMovePoints() {
    if (owner == null) {
      return stats.movement;
    } else {
      return owner.getCO().unitMovementHook(this, stats.movement);
    }
  }

  public void addPathMoveCost(int moveCost) {
    addSupplies(-moveCost);
  }

  public boolean canMove() {
    return supplies != 0;
  }

  /**
   * Units can move through allied units, they cannot move through enemy units
   *
   * @param location The location that can contain a 'trapper'
   * @return if the location contains a trapper
   */
  public boolean hasTrapperOn(Location location) {
    Locatable locatable = location.getLastLocatable();

    if (locatable instanceof Unit) {
      Unit trapper = (Unit) locatable;
      return !trapper.isAlliedWith(owner);
    } else {
      return false;
    }
  }

  public List<Location> getMoveZone() {
    return Collections.unmodifiableList(moveZone);
  }

  // ---------------------------------------------------------------------------
  // Getters :: Other
  // ---------------------------------------------------------------------------

  public ArmyBranch getArmyBranch() {
    return stats.armyBranch;
  }

  public Player getOwner() {
    return owner;
  }

  public boolean isAlliedWith(Player player) {
    return owner.isAlliedWith(player);
  }

  public int getExperience() {
    return experience;
  }

  public UnitState getUnitState() {
    return unitState;
  }

  public boolean isWithinMoveZone(Location location) {
    return moveZone.contains(location);
  }

  public boolean isWithinAttackZone(Location location) {
    return attZone.contains(location);
  }

  public boolean isHidden() {
    return hidden;
  }

  /**
   * @return Can this unit build the specific unit
   */
  public boolean canBuildUnit(Unit unit) {
    return canBuildUnit() && stats.canBuildUnit(unit);
  }

  /**
   * Can this unit build units:
   * #1 Only transporting units can build a unit
   * #2 There is 1 free place in this transport
   *
   * @return Can this unit build units
   */
  public boolean canBuildUnit() {
    return stats.canTransport && !isTransportFull();
  }

  public boolean canHide() {
    return stats.canHide || hideAbilityEnabled;
  }

  public int getCaptureRate() {
    if (owner == null) {
      return getHp();
    } else {
      return owner.getCO().captureRateHook(getHp());
    }
  }

  public boolean isInTransport() {
    return location instanceof Unit;
  }

  /**
   * @return Can this unit fire only on adjacent enemies
   */
  public boolean isDirect() {
    Weapon weapon = getAvailableWeapon();
    return weapon != null && weapon.isDirect();
  }

  /**
   * @return Can this unit fire on enemies that are 1 or more tiles away
   */
  public boolean isInDirect() {
    Weapon weapon = getAvailableWeapon();
    return weapon != null && weapon.isInDirect();
  }

  public boolean isNaval() {
    return stats.armyBranch == ArmyBranch.NAVAL;
  }

  public boolean isLand() {
    return stats.armyBranch == ArmyBranch.LAND;
  }

  public boolean isAir() {
    return stats.armyBranch == ArmyBranch.AIR;
  }

  public int getPrice() {
    if (owner == null) {
      return stats.price;
    } else {
      return owner.getCO().unitPriceHook(stats.price);
    }
  }

  public boolean isCoOnBoard() {
    return coOnBoard;
  }

  public boolean isInCOZone() {
    return owner.isInCOZone(location);
  }

  @Override
  public String toString() {
    return String.format("%s(ID=%s) location=%s %s branch=%s owner=%s unit state=%s",
      stats.getName(), stats.getID(), getLocationText(), getStatsText(), stats.getArmyBranch(), getOwnerText(), unitState);
  }

  private String getLocationText() {
    return location == null ? "Not located" : location.getLocationString();
  }

  private String getOwnerText() {
    return owner == null ? "Not owned" : owner.toString();
  }

  private String getStatsText() {
    return String.format("hp=%s/%s supplies=%s/%s exp=%s/%s transport=%s/%s",
      hp, stats.getMaxHp(), supplies, stats.getMaxSupplies(), experience, stats.getMaxExperience(), transport.size(), stats.getMaxTransportCount());
  }
}