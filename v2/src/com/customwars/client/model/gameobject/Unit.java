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
 * It can transport other units:
 * If a unit is within a transport the location will be null. The transport field will
 * be set to the transporting unit. A unit in a transport can be idle or active:
 * Active means that the unit can be loaded and dropped in the same turn.
 * Idle means that the unit needs to wait 1 turn in the transport.
 * <p/>
 * It has a Facing direction, this is one of the compass Direction(N,S,E,W)
 * Units have 2 weapons(both are optional): 1 Primary and 1 Secondary
 * <p/>
 * The internal hp of a unit has a range of 0-maxHp instead of 0-10. This approach allows to take a small damage.
 * and is easier then using a double. getHP and getMaxHP convert the internal hp back to the 0-10 range ie:
 * hp=100, maxhp=100 getHP() = 10 -5% damage
 * hp=95, maxhp=100 getHP() = 10 -5% damage
 * hp=90, maxhp=100 getHP() = 9
 */
public class Unit extends GameObject implements Mover, TurnHandler, Attacker, Defender {
  public static final Direction DEFAULT_ORIENTATION = Direction.EAST;
  private static final int LOW_AMMO_PERCENTAGE = 50;
  private static final int LOW_SUPPLIES = 50;

  private final UnitStats stats;
  private City constructingCity; // A City that is being build by this unit
  private int constructionMaterials;   // Amount of construction material carried
  private int hp;               // Health Points, if 0 the unit is destroyed
  private int supplies;         // Supplies, this can be in the form of rations(troops) or fuel(motorized vehicles)
  private int experience;       // Each time a unit wins a fight his Experience rises(starting from 0)
  private UnitState unitState;  // Current Unitstate (submerged, capturing a city,...)
  private Player owner;         // Player owning this unit
  private Location location;    // Current Location, null if in a transport
  private boolean coOnBoard;        // Is a CO loaded within this unit
  private List<Location> moveZone;  // A zone where this unit can move in
  private List<Location> attZone;   // A zone where this unit can attack in
  private Direction orientation;    // The direction this unit is looking at
  private boolean hidden;           // In fog of war, is this unit hidden within enemy los

  private Weapon primaryWeapon, secondaryWeapon;
  private Unit transport;             // The unit that is transporting this unit
  private List<Unit> unitsInTransport;// Units that are within this transport
  private MoveStrategy moveStrategy;  // A MoveStrategy instance for this unit

  public Unit(UnitStats unitStats) {
    super(GameObjectState.ACTIVE);
    this.stats = unitStats;
    init();
    reset();
  }

  void init() {
    stats.init();
    unitsInTransport = new LinkedList<Unit>();
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
    constructionMaterials = otherUnit.constructionMaterials;

    primaryWeapon = otherUnit.primaryWeapon != null ? new Weapon(otherUnit.primaryWeapon) : null;
    secondaryWeapon = otherUnit.secondaryWeapon != null ? new Weapon(otherUnit.secondaryWeapon) : null;

    location = otherUnit.location;
    coOnBoard = otherUnit.coOnBoard;
    owner = otherUnit.owner;
    moveStrategy = otherUnit.stats.moveStrategy.newInstance(this);
    copyUnitsInTheTransport(otherUnit.unitsInTransport);
  }

  private void copyUnitsInTheTransport(List<Unit> unitsInTransport) {
    this.unitsInTransport = new LinkedList<Unit>();
    for (Unit unit : unitsInTransport) {
      this.unitsInTransport.add(new Unit(unit));
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
    constructionMaterials = stats.maxConstructionMaterial;
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
      owner.setCoZone(Collections.<Location>emptyList());
    }

    clearTransport();
    owner.removeUnit(this);
    owner = null;

    if (isInTransport()) {
      transport.remove(this);
      transport = null;
    } else {
      location.remove(this);
      location = null;
    }

    if (fireEvent) {
      setState(GameObjectState.DESTROYED);
    }
  }

  /**
   * Destroy each unit in the transport
   */
  private void clearTransport() {
    while (hasUnitsInTransport()) {
      Unit unit = unitsInTransport.get(unitsInTransport.size() - 1);
      unit.destroy(false);
    }
  }

  public void startTurn(Player currentPlayer) {
    supplyUnitsInTransport();
    activateUnitsInTransport();
  }

  /**
   * If this unit is transporting units and it can supply the units.
   * then supply and heal the units in the transport.
   */
  private void supplyUnitsInTransport() {
    if (stats.canTransport()) {
      for (Unit unit : unitsInTransport) {
        if (stats.canSupplyUnitInTransport(unit)) {
          supplyInTransport(unit);
          unit.heal(stats.healRate);
        }
      }
    }
  }

  /**
   * Make all units in the transport active
   * so they can be dropped/launched in this turn.
   */
  private void activateUnitsInTransport() {
    for (Unit unitInTransport : unitsInTransport) {
      unitInTransport.setActive(true);
    }
  }

  public void endTurn(Player currentPlayer) {
    subtractSupplies();
  }

  private void subtractSupplies() {
    if (isSubmerged() || hidden) {
      addSupplies(-stats.getSuppliesPerTurnWhenHidden());
    } else {
      if (!isInTransport()) {
        addSupplies(-stats.suppliesPerTurn);
      }
    }
  }

  public void loadCO() {
    setCoOnBoard(true);
  }

  private void setCoOnBoard(boolean coOnBoard) {
    boolean oldVal = this.coOnBoard;
    this.coOnBoard = coOnBoard;
    firePropertyChange("coOnBoard", oldVal, this.coOnBoard);
  }

  // ----------------------------------------------------------------------------
  // Actions :: Submarine
  // ----------------------------------------------------------------------------

  public void dive() {
    setUnitState(UnitState.SUBMERGED);
  }

  public void surface() {
    setUnitState(UnitState.IDLE);
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
   * #2 it is not indirect
   * #3 the Attacker is also a defender
   * #4 it has a weapon that can return fire to the attacker
   */
  public boolean canCounterAttack(Attacker attacker) {
    boolean attackerIsDefender = attacker instanceof Defender;
    boolean canReturnFire = attackerIsDefender && !isDestroyed() && canFireOn((Defender) attacker);
    return canReturnFire && canFireOnAdjacentEnemies();
  }

  public List<Location> getAttackZone() {
    return Collections.unmodifiableList(attZone);
  }

  // ---------------------------------------------------------------------------
  // Actions :: Transport
  // ---------------------------------------------------------------------------

  public Unit getLastUnitInTransport() {
    return getUnitInTransport(unitsInTransport.size() - 1);
  }

  public Unit getUnitInTransport(int index) {
    return !unitsInTransport.isEmpty() ? unitsInTransport.get(index) : null;
  }

  public boolean remove(Unit unit) {
    if (!contains(unit) || unit == null) {
      return false;
    }

    unit.setLocation(null);
    unitsInTransport.remove(unit);
    unit.transport = null;
    firePropertyChange("transport", unit, null);
    return true;
  }

  public boolean contains(Unit unit) {
    return unitsInTransport.contains(unit);
  }

  public void add(Unit unit) {
    if (canAdd(unit)) {
      unitsInTransport.add(unit);
      unit.setLocation(null);
      unit.transport = this;
    }
    firePropertyChange("transport", null, unit);
  }

  public boolean canAdd(Unit unit) {
    return canTransport(unit.stats.name);
  }

  public boolean canTransport(String unitName) {
    return stats.canTransport(unitName) && !isTransportFull();
  }

  public boolean isTransportFull() {
    return unitsInTransport.size() >= stats.getMaxTransportCount();
  }

  public boolean isInTransport() {
    return transport != null;
  }

  /**
   * Check if this transport is transporting at least 1 active unit of the given armybranch.
   */
  public boolean isTransportingUnitType(ArmyBranch armyBranch) {
    for (Unit unitInTransport : unitsInTransport) {
      if (unitInTransport.getArmyBranch() == armyBranch && unitInTransport.isActive()) {
        return true;
      }
    }
    return false;
  }

  /**
   * @return true if there is at least 1 unit in this transport.
   */
  public boolean hasUnitsInTransport() {
    return !unitsInTransport.isEmpty();
  }

  /**
   * @return Amount of units in the transport
   */
  public int getUnitsInTransportCount() {
    return unitsInTransport.size();
  }

  /**
   * @return all the units in this transport, to be used in a for each loop.
   */
  public Iterable<Unit> getUnitsInTransport() {
    return unitsInTransport;
  }

  /**
   * Get the index of the unit in the transport. starting from 0
   *
   * @param unit The unit in this transport where we want to retrieve the index for
   * @return the index or -1 when the unit is not in this transport
   */
  public int indexOf(Unit unit) {
    return unitsInTransport.indexOf(unit);
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

  public void supplyInTransport(Unit unit) {
    if (canSupply(unit, true)) {
      unit.resupply();
    }
  }

  public void supply(Unit unit) {
    if (canSupply(unit, false)) {
      unit.resupply();
    }
  }

  /**
   * This unit is the supplier, and returns if it can supply the given unit
   * #1 Can this unit supply at all
   * #2 Can we add supplies or ammo to one of the weapons of the unit
   * #3 Is the supplier and the unit owned by the same player
   *
   * @param unit        the unit that is going to be supplied by this unit
   * @param inTransport true if only units in the transport should be resupplied..
   * @return Can this unit supply the given unit
   */
  public boolean canSupply(Unit unit, boolean inTransport) {
    if (unit == null) return false;
    boolean canSupply = inTransport ? stats.canSupplyUnitInTransport(unit) : stats.canSupplyUnitAroundTransport(unit);
    boolean needsSupplies = !unit.hasMaxSupplies();
    boolean allied = owner.equals(unit.owner);

    return canSupply && needsSupplies && allied;
  }

  /**
   * @return true when
   *         #1 the unit is 100% supplied
   *         #2 The weapons of the unit both have 100% ammo, when one of the weapons is null it has 100% ammo.
   */
  public boolean hasMaxSupplies() {
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
    return !hasMaxHP() && owner.isWithinBudget(healCost);
  }

  public boolean hasMaxHP() {
    return hp == stats.maxHp;
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
    if (constructionMaterials > 0) {
      Args.checkForNull(city);
      if (constructingCity == null || constructingCity != city) {
        constructingCity = city;
      }

      constructingCity.capture(this);

      if (constructingCity.isCaptured()) {
        deCreaseConstructionMaterials();
      }
    }
  }

  public boolean isConstructionComplete() {
    return constructingCity.isCapturedBy(this);
  }

  public void stopConstructing() {
    constructingCity = null;
  }

  public boolean hasConstructionMaterials() {
    return constructionMaterials > 0;
  }

  public boolean canConstructCityOn(Terrain terrain) {
    return hasConstructionMaterials() && stats.canBuildCityOn(terrain);
  }

  // ---------------------------------------------------------------------------
  // Setters
  // ---------------------------------------------------------------------------

  public void deCreaseConstructionMaterials() {
    setConstructionMaterials(constructionMaterials - 1);
  }

  private void setConstructionMaterials(int newVal) {
    int oldVal = this.constructionMaterials;
    this.constructionMaterials = Args.getBetweenZeroMax(newVal, stats.maxConstructionMaterial);
    firePropertyChange("constructionMaterials", oldVal, constructionMaterials);
  }

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

  public void setHp(int hp) {
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

  public void setMaxConstructionMaterials() {
    setConstructionMaterials(stats.maxConstructionMaterial);
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
    MoveStrategy oldVal = this.moveStrategy;
    this.moveStrategy = moveStrategy;
    firePropertyChange("moveStrategy", oldVal, moveStrategy);
  }

  public void setDefaultOrientation() {
    setOrientation(owner.getUnitFacingDirection());
  }

  /**
   * Change the state of this unit to active or idle.
   */
  public void setActive(boolean active) {
    if (active) {
      setState(GameObjectState.ACTIVE);
    } else {
      // Make sure that the change to idle is picked up by the event listeners
      setState(GameObjectState.ACTIVE);
      setState(GameObjectState.IDLE);
    }
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

  /**
   * Retrieve the first available attack weapon: Not null some ammo left and the ability to attack.
   * starting with the primary weapon
   *
   * @return The first available weapon that can attack. Null when no weapons are available.
   */
  public Weapon getAvailableAttackWeapon() {
    if (canFirePrimaryWeapon() && primaryWeapon.canAttack()) {
      return primaryWeapon;
    } else if (canFireSecondaryWeapon() && secondaryWeapon.canAttack()) {
      return secondaryWeapon;
    } else {
      return null;
    }
  }

  public Weapon getPrimaryWeapon() {
    return primaryWeapon;
  }

  public Weapon getSecondaryWeapon() {
    return secondaryWeapon;
  }

  /**
   * <lu>
   * <li>If there is a primary weapon return the primary ammo count</li>
   * <li>If there is no primary weapon but a secondary weapon return the secondary ammo count</li>
   * <li>If there is no weapon return 0</li>
   * </lu>
   */
  public int getAmmo() {
    if (hasPrimaryWeapon()) {
      return primaryWeapon.getAmmo();
    } else if (hasSecondaryWeapon()) {
      return secondaryWeapon.getAmmo();
    } else {
      return 0;
    }
  }

  /**
   * @return if this unit can fire on the defender
   */
  private boolean canFireOn(Defender defender) {
    ArmyBranch defenderArmyBranch = defender.getArmyBranch();
    boolean canFirePrimaryWeapon = hasPrimaryWeapon() && primaryWeapon.canFireOn(defenderArmyBranch);
    boolean canFireSecondaryWeapon = hasSecondaryWeapon() && secondaryWeapon.canFireOn(defenderArmyBranch);

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
    Weapon weapon = getAvailableAttackWeapon();

    if (weapon != null) {
      int minRange = weapon.getRange().getMinRange();
      int maxRange = weapon.getRange().getMaxRange();
      int coMaxRange = owner.getCO().fireRangeHook(this, maxRange);
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
   * getHp() return 2
   * hp=18
   * getHp() return 2
   * hp=0
   * getHp() return 0
   * 0>hp<10
   * getHp() returns 1
   *
   * @return The rounded Hp between 0 - 10
   */
  public int getHp() {
    if (hp == 0) {
      return 0;
    } else if (hp < 10) {
      return 1;
    } else {
      return (int) Math.floor(hp / 10 + 0.45f);
    }
  }

  /**
   * Converts the internal maxhp to a number between 0 - 10 rounding up to the nearest value.
   * maxhp=99
   * getMaxHp() return 10
   * maxhp=79
   * getMaxHp() return 8
   * maxhp=75
   * getMaxHp() return 7
   *
   * @return The max Hp between 0 - 10
   */
  public int getMaxHp() {
    return (int) Math.floor(stats.maxHp / 10 + 0.45f);
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

  public String getLocationString() {
    return getLocation().getLocationString();
  }

  public Location getLocation() {
    if (transport != null) {
      return transport.getLocation();
    } else {
      return location;
    }
  }

  public int getMovementType() {
    return stats.movementType;
  }

  public MoveStrategy getMoveStrategy() {
    return moveStrategy;
  }

  public int getMovePoints() {
    boolean lowSupplies = supplies < stats.movement;

    if (owner == null) {
      return lowSupplies ? supplies : stats.movement;
    } else {
      return owner.getCO().unitMovementHook(this, lowSupplies ? supplies : stats.movement);
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

  public boolean canHide() {
    return stats.canHide;
  }

  public boolean canDive() {
    return stats.canDive();
  }

  public int getCaptureRate() {
    if (owner == null) {
      return getHp();
    } else {
      return owner.getCO().captureRateHook(getHp());
    }
  }

  /**
   * @return Can this unit fire on adjacent enemies
   */
  public boolean canFireOnAdjacentEnemies() {
    Weapon weapon = getAvailableAttackWeapon();
    return weapon != null && weapon.canFireOnAdjacentEnemies();
  }

  /**
   * @return Can this unit fire on enemies that are max 1 tile away
   */
  public boolean isDirect() {
    Weapon weapon = getAvailableAttackWeapon();
    return weapon != null && weapon.isDirect();
  }

  /**
   * @return Can this unit fire on enemies that are 1 or more tiles away
   */
  public boolean isInDirect() {
    Weapon weapon = getAvailableAttackWeapon();
    return weapon != null && weapon.isInDirect();
  }

  public boolean isSubmerged() {
    return unitState == UnitState.SUBMERGED;
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

  public boolean isFacingTowards(Direction direction) {
    return orientation == direction;
  }

  /**
   * Checks if the given damage would destroy the unit.
   *
   * @param dmg the damage to subtract in health points.
   * @return if the unit will be destroyed when the given damage is applied.
   */
  public boolean willBeDestroyedAfterTakingDamage(int dmg) {
    return hp - dmg * 10 <= 0;
  }

  public int getCurrentConstructionMaterials() {
    return constructionMaterials;
  }

  @Override
  public String toString() {
    return String.format("%s(ID=%s) location=%s %s branch=%s owner=%s unit state=%s",
      stats.getName(), stats.getID(), getLocationText(), getStatsText(), stats.getArmyBranch(), getOwnerText(), unitState);
  }

  private String getLocationText() {
    if(location == null) {
      if(transport == null) {
        return "Not located";
      } else {
        return "in transport";
      }
    } else {
      return location.getLocationString();
    }
  }

  private String getOwnerText() {
    return owner == null ? "Not owned" : owner.toString();
  }

  private String getStatsText() {
    return String.format("hp=%s/%s supplies=%s/%s exp=%s/%s transport=%s/%s",
        hp, stats.getMaxHp(),
        supplies, stats.getMaxSupplies(),
        experience, stats.getMaxExperience(),
        unitsInTransport.size(), stats.getMaxTransportCount());
  }
}
