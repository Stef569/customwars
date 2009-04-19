package com.customwars.client.model.gameobject;

import com.customwars.client.model.Attacker;
import com.customwars.client.model.Defender;
import com.customwars.client.model.Fight;
import com.customwars.client.model.TurnHandler;
import com.customwars.client.model.game.Player;
import com.customwars.client.model.map.Direction;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.path.MoveStrategy;
import com.customwars.client.model.map.path.Mover;
import tools.Args;
import tools.NumberUtil;

import java.util.LinkedList;
import java.util.List;

/**
 * Is a Mover meaning it has the ability to be put on and removed from a Location.
 * Is a Location, meaning it can transport Locatables
 * It has a Facing direction, this is one of the compass Direction(N,S,E,W)
 * Units have 2 weapons(both are optional): 1 Primary and 1 Secundary
 *
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
  private static final int MAX_EXP = 10;
  private int id;               // The unit Type ie(1->INF, 2->APC,...)
  private String name;          // Full name ie Infantry, Tank, ...
  private String description;   // Information about this Unit
  private int price;            // The price for buying this Unit
  private int movement;         // The move points
  private int vision;           // The amount of tiles this unit can see in all directions aka line of sight

  private int maxHp;              // The value when this unit is 100% Healthy
  private int minSupplyRange, maxSupplyRange; // Range in which the unit can supply
  private int maxSupplies;        // The value when this unit has 100% supplies
  private int maxTransportCount;  // Amount of units that can be transported
  private int dailyUse;           // Amount of supplies that are subtracted each turn from supplies

  private boolean canCapture;     // Abilities
  private boolean canDive;
  private boolean canSupply;
  private boolean canTransport;
  private boolean canJoin;

  private int armyBranch;       // Naval, Ground, Air
  private int movementType;     // Inf, Mech, Tires, Tread, Air, Naval ...
  private MoveStrategy moveStrategy;

  private int hp;               // Health Points, if 0 the unit is dead
  private int supplies;         // Each unit has supplies, this can be in the form of rations(troops) or fuel(motorized vehicles)
  private int experience;       // Each time a unit wins a fight his Experience rises(starting from 0 to 10)
  private UnitState unitState;  // Current Unitstate (submerged, capturing a property,...)
  private Player owner;         // Player owning this unit
  private Location location;    // current Location
  private List<Location> moveZone;  // A zone where this unit can move in
  private List<Location> attZone;   // A zone where this unit can attack in
  private Direction orientation;    // The direction this unit is looking at
  private boolean hidden;           // In fog of war, is this unit visible within enemy moveZone

  private Weapon primaryWeapon, secondaryWeapon;
  private List<Locatable> transport;      // Units that are within this Transport (empty when this unit can't transport)
  private List<Integer> transportTypes;   // Movement Types that can be transported (empty when this unit can't transport)

  public Unit(int id, String name, String description,
              int cost, int movement, int vision,
              int maxHp, int maxSupplies, int maxTransportCount, int suppliesPerTurn,
              boolean canCapture, boolean canDive, boolean canSupply, boolean canTransport, boolean canJoin, List<Integer> transportTypes,
              int armyBranch, int movementType, int minSupplyRange, int maxSupplyRange) {
    super(GameObjectState.ACTIVE);
    this.id = id;
    this.name = name;
    this.description = description;
    this.price = cost;
    this.movement = movement;
    this.vision = vision;

    this.maxHp = maxHp;
    this.maxSupplies = maxSupplies;
    this.maxTransportCount = maxTransportCount;
    this.dailyUse = suppliesPerTurn;

    this.canCapture = canCapture;
    this.canDive = canDive;
    this.canSupply = canSupply;
    this.canTransport = canTransport;
    this.canJoin = canJoin;
    this.transportTypes = transportTypes;

    this.armyBranch = armyBranch;
    this.movementType = movementType;
    this.minSupplyRange = minSupplyRange;
    this.maxSupplyRange = maxSupplyRange;
    init();
    reset();
  }

  void init() {
    this.transportTypes = Args.createEmptyListIfNull(transportTypes);
    this.transport = new LinkedList<Locatable>();
    if (name == null) name = "";
    if (description == null) description = "";
    unitState = UnitState.IDLE;
  }

  /**
   * Copy Constructor, primitive values are just copied
   * objects need to be wrapped into a new Object. Unless 1 object should be shared between all units
   *
   * @param otherUnit unit to copy
   */
  public Unit(Unit otherUnit) {
    super(otherUnit);
    hp = otherUnit.hp;
    supplies = otherUnit.supplies;
    experience = otherUnit.experience;
    unitState = otherUnit.unitState;

    name = otherUnit.name;
    id = otherUnit.id;
    price = otherUnit.price;
    movement = otherUnit.movement;
    vision = otherUnit.vision;

    maxHp = otherUnit.maxHp;
    minSupplyRange = otherUnit.minSupplyRange;
    maxSupplyRange = otherUnit.maxSupplyRange;
    maxSupplies = otherUnit.maxSupplies;
    maxTransportCount = otherUnit.maxTransportCount;
    dailyUse = otherUnit.dailyUse;

    canCapture = otherUnit.canCapture;
    canDive = otherUnit.canDive;
    canSupply = otherUnit.canSupply;
    canTransport = otherUnit.canTransport;
    canJoin = otherUnit.canJoin;
    transportTypes = new LinkedList<Integer>(otherUnit.transportTypes);

    armyBranch = otherUnit.armyBranch;
    movementType = otherUnit.movementType;
    moveStrategy = otherUnit.moveStrategy;

    primaryWeapon = otherUnit.primaryWeapon != null ? new Weapon(otherUnit.primaryWeapon) : null;
    secondaryWeapon = otherUnit.secondaryWeapon != null ? new Weapon(otherUnit.secondaryWeapon) : null;
    experience = otherUnit.experience;

    location = otherUnit.location;
    owner = otherUnit.owner;
    description = otherUnit.description;
    transport = new LinkedList<Locatable>(otherUnit.transport);
  }

  /**
   * Resets the unit state to default:
   * 100% Supplies, health
   * 100% ammo in both weapons(or 1 or none)
   * Facing east, Active state, no exp
   */
  void reset() {
    resupply();
    hp = maxHp;
    experience = 0;
    setOrientation(DEFAULT_ORIENTATION);
    setState(GameObjectState.ACTIVE);
  }

  /**
   * Removes all references to this unit
   * so that it can be garbage collected
   */
  public void destroy() {
    clearTransport();
    owner.removeUnit(this);
    owner = null;
    location.remove(this);
    location = null;
    setState(GameObjectState.DESTROYED);
  }

  /**
   * Destroy each unit in the transport
   */
  private void clearTransport() {
    while (transport.size() > 0) {
      Unit unit = (Unit) transport.get(transport.size() - 1);
      unit.destroy();
    }
  }

  public void startTurn(Player player) {
  }

  public void endTurn(Player invoker) {
    addSupplies(-dailyUse);
  }

  /**
   * Restore
   * supplies to max
   * ammo to max
   */
  public void resupply() {
    setSupplies(maxSupplies);
    restock();
  }

  // ----------------------------------------------------------------------------
  // Actions :: weapons
  // ----------------------------------------------------------------------------
  public void restock() {
    if (hasPrimaryWeapon()) {
      primaryWeapon.restock();
    }
    if (hasSecondaryWeapon()) {
      secondaryWeapon.restock();
    }
  }

  // ----------------------------------------------------------------------------
  // Actions :: Attack/Defend
  // ----------------------------------------------------------------------------
  /**
   * This unit is the attacker
   * and attacking the defender
   */
  public void attack(Defender defender, Fight fight) {
    if (canAttack(defender)) {
      tryToFireWeapon(defender, fight);
      defender.defend(this, fight);

      if (defender.isDestroyed()) {
        if (++experience > MAX_EXP) {
          experience = MAX_EXP;
        }
      }
    }
  }

  public boolean canAttack(Defender defender) {
    return defender != null && canFire(defender.getArmyBranch()) && !isDestroyed() &&
            !defender.isDestroyed() && !defender.getOwner().isAlliedWith(owner);
  }

  private void tryToFireWeapon(Defender defender, Fight fight) {
    if (fight.calcAttackDamagePercentage(defender) > 0) {
      Fight.WeaponType weaponType = fight.getBestAttackWeaponType();
      fireWeapon(weaponType, 1);
    }
  }

  /**
   * When a unit fires the shots are subtracted from the ammoCount in weapon.
   * Some units cannot fire, they should have set primaryWeapon and secondaryWeapon to null.
   *
   * @param weaponType The weapon type that was used to attack(Primary or Secondary)
   * @param shots      amount of bullets, missiles or shells the weapon will fire
   * @see Weapon
   */
  private void fireWeapon(Fight.WeaponType weaponType, int shots) {
    if (weaponType == Fight.WeaponType.PRIMARY) {
      primaryWeapon.fire(shots);
    } else if (weaponType == Fight.WeaponType.SECONDARY) {
      secondaryWeapon.fire(shots);
    }
  }

  public void defend(Attacker attacker, Fight fight) {
    receiveDamage(fight);

    if (getHp() <= 0) {
      destroy();
      return;
    }

    if (fight.canCounterAttack(attacker, this)) {
      fight.counterAttack();
    }
  }

  /**
   * This unit is the defender
   * and receives damage from a Fight.
   *
   * @param fight The fight context
   */
  public void receiveDamage(Fight fight) {
    int attackPercentage = fight.calcAttackDamagePercentage(this);
    int attackValue = (int) (((double) attackPercentage / maxHp) * 100);
    setHp(hp - attackValue);
  }

  /**
   * The unit can counter Attack when
   * it didn't die from the attack
   * has a weapon that can return fire.
   */
  public boolean canCounterAttack(Attacker attacker) {
    return canFire(attacker.getArmyBranch()) && !isDestroyed();
  }

  public List<Location> getAttackZone() {
    return attZone;
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

  public Locatable getLocatable(int id) {
    if (id > 0 || id < transport.size()) {
      return transport.get(id);
    } else {
      return null;
    }
  }

  public boolean remove(Locatable locatable) {
    if (!contains(locatable) || locatable == null) {
      return false;
    } else {
      locatable.setLocation(null);    // Keep locatable and tile in sync
      transport.remove(locatable);
      firePropertyChange("transport", locatable, null);
    }
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
      return canTransport(unit.getMovementType()) && transport.size() < maxTransportCount;
    } else {
      return false;
    }
  }

  public boolean canTransport(int id) {
    return canTransport && transportTypes.contains(id);
  }

  /**
   * @return Amount of units in the transport
   */
  public int getLocatableCount() {
    return transport.size();
  }

  // ----------------------------------------------------------------------------
  // Actions :: supply, heal, capture
  // ----------------------------------------------------------------------------
  public void supply(Unit unit) {
    if (canSupply(unit))
      unit.resupply();
  }

  /**
   * This unit is the supplier
   *
   * @return Can this unit supply the given unit
   */
  public boolean canSupply(Unit unit) {
    return canSupply && unit != null && unit.getSuppliesPercentage() != 100 && owner == unit.getOwner();
  }

  public void heal(int healRate) {
    int healCost = getHealCost(healRate);
    if (canHeal(healCost)) {
      addHp(healRate);
      owner.addToBudget(-healCost);
    }
  }

  public boolean canHeal(int healCost) {
    return hp != maxHp && owner.isWithinBudget(healCost);
  }

  private int getHealCost(int healRate) {
    int healAmount = getMaxHp() - getHp();
    if (healAmount > healRate) {
      healAmount = healRate;
    }
    return healAmount * (price / maxHp);
  }

  // ---------------------------------------------------------------------------
  // Setters
  // ---------------------------------------------------------------------------
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
    int oldVal = this.hp;
    this.hp = Args.getBetweenZeroMax(hp, maxHp);
    firePropertyChange("hp", oldVal, this.hp);
  }

  public void addSupplies(int additionalSupplies) {
    setSupplies(supplies + additionalSupplies);
  }

  protected void setSupplies(int amount) {
    int oldVal = this.supplies;
    this.supplies = Args.getBetweenZeroMax(amount, maxSupplies);
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

  public void setPrice(int price) {
    int oldVal = this.price;
    this.price = price;
    firePropertyChange("price", oldVal, price);
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
    if (hasPrimaryWeapon() && primaryWeapon.getAmmo() > 0) {
      return primaryWeapon;
    } else if (hasSecondaryWeapon() && secondaryWeapon.getAmmo() > 0) {
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
   * @return if this unit can fire on the given armyBranch
   */
  public boolean canFire(int armyBranch) {
    return hasPrimaryWeapon() && primaryWeapon.canFire(armyBranch) ||
            hasSecondaryWeapon() && secondaryWeapon.canFire(armyBranch);
  }

  /**
   * @return if one of the two weapons has enough ammo
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

  public int getMinAttackRange() {
    Weapon weapon = getAvailableWeapon();
    if (weapon != null) {
      return weapon.getMinRange();
    } else {
      return 0;
    }
  }

  public int getMaxAttackRange() {
    Weapon weapon = getAvailableWeapon();
    if (weapon != null) {
      return weapon.getMaxRange();
    } else {
      return 0;
    }
  }

  // ---------------------------------------------------------------------------
  // Getters :: Supplies, hp
  // ---------------------------------------------------------------------------
  public int getSupplies() {
    return supplies;
  }

  protected int getMaxSupplies() {
    return maxSupplies;
  }

  public int getSuppliesPercentage() {
    return NumberUtil.calcPercentage(supplies, maxSupplies);
  }

  public boolean hasLowSupplies() {
    return getSuppliesPercentage() <= 20;
  }

  /**
   * Converts the internal hp to a number between 0 - 10 rounding up to the nearest absolute value.
   * hp=25
   * getHp() return 3
   * hp=19
   * getHp() return 2
   *
   * @return The Hp
   */
  public int getHp() {
    return (int) Math.ceil((double) hp / 10);
  }

  /**
   * Converts the internal maxhp to a number between 0 - 10 rounding up to the nearest absolute value.
   * maxhp=99
   * getMaxHp() return 10
   * maxhp=79
   * getMaxHp() return 8
   *
   * @return The max Hp
   */
  public int getMaxHp() {
    return (int) Math.ceil((double) maxHp / 10);
  }

  public int getHpPercentage() {
    return NumberUtil.calcPercentage(hp, maxHp);
  }

  /**
   * @return if the hp dropped under the max hp
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
    return movementType;
  }

  public MoveStrategy getMoveStrategy() {
    return moveStrategy;
  }

  public int getMovePoints() {
    return movement;
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
      return !trapper.getOwner().isAlliedWith(owner);
    } else {
      return false;
    }
  }

  public List<Location> getMoveZone() {
    return moveZone;
  }

  // ---------------------------------------------------------------------------
  // Getters :: Other
  // ---------------------------------------------------------------------------
  public int getID() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public int getArmyBranch() {
    return armyBranch;
  }

  public int getVision() {
    return vision;
  }

  public int getPrice() {
    return price;
  }

  public Player getOwner() {
    return owner;
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

  /**
   * @return True when one of the weapons has less then 33% ammo.
   */
  public boolean hasLowAmmo() {
    boolean lowAmmo = false;
    if (hasPrimaryWeapon()) {
      if (primaryWeapon.getAmmoPercentage() < 33) {
        lowAmmo = true;
      }
    } else if (hasSecondaryWeapon()) {
      if (secondaryWeapon.getAmmoPercentage() < 33) {
        lowAmmo = true;
      }
    }
    return lowAmmo;
  }

  public boolean isHidden() {
    return hidden;
  }

  public boolean canCapture() {
    return canCapture;
  }

  public boolean canDive() {
    return canDive;
  }

  public boolean canTransport() {
    return canTransport;
  }

  public boolean canJoin() {
    return canJoin;
  }

  public int getMinSupplyRange() {
    return minSupplyRange;
  }

  public int getMaxSupplyRange() {
    return maxSupplyRange;
  }

  public int getCaptureRate() {
    return getHp();
  }

  @Override
  public String toString() {
    return String.format("[name=%s id=%s owner=%s location=%s transport=%s state=%s]",
            name, id, owner, getLocationText(), transport, unitState);
  }

  private String getLocationText() {
    return location == null ? "Not located" : location.getLocationString();
  }
}
