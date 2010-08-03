package com.customwars.client.model.gameobject;

import com.customwars.client.model.ArmyBranch;
import com.customwars.client.model.TurnHandler;
import com.customwars.client.model.fight.Attacker;
import com.customwars.client.model.fight.Defender;
import com.customwars.client.model.fight.Fight;
import com.customwars.client.model.game.Player;
import com.customwars.client.model.map.Direction;
import com.customwars.client.model.map.Location;
import com.customwars.client.tools.Args;
import com.customwars.client.tools.NumberUtil;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.List;

/**
 * Cities can be owned by 1 Player
 * are located on a Location
 * They have funds, The player that owns this city receives the funds on each turn start.
 * can heal and supply units of a specific ArmyBranch when they are located on the city
 * can be captured by a Unit of a specific unit ID
 * can build units of a specific Unit ID
 * <p/>
 * The capture process is reset when
 * another unit tries to capture this city, or by invoking resetCapturing()
 * <p/>
 * A City can fire a rocket once
 * A City can be destroyed
 *
 * @author stefan
 */
public class City extends Terrain implements PropertyChangeListener, TurnHandler, Locatable, Defender {
  private List<ArmyBranch> heals;       // The army branches this City can heal(Empty list means it cannot heal)
  private List<Integer> canBeCaptureBy; // The unit ids this City can be captured by(Empty list means it cannot be captured)
  private List<Integer> builds;         // The unit ids this City can build (Empty list means it cannot build)
  private List<Integer> canBeLaunchedBy;// The unit ids that can launch a rocket from this city (Empty list means it cannot launch rockets)
  private final int maxCapCount;
  private final int healRate;       // Healing/repairs this city can give to a Unit
  private final int maxHp;          // Maximum health points
  private int funds;                // Money that this city produces every turn
  private boolean canProduceFunds;  // Does this city has the ability to produce funds
  private int imgRowID;             // The row that contains an image for this city

  private Location location;  // The location this City is on
  private Player owner;       // Player owning this City
  private boolean launched;   // If this city already launched a rocket
  private Unit capturer;      // Unit that is capturing this city
  private int capCount;       // The current capture count(if capCount==maxCapCount then this city is considered to be captured)
  private int hp;             // Health points, if 0 this city is considered to be destroyed

  public City(int id, int imgRowID, String type, String connectType, String name, String description, int defenseBonus, int height, List<Integer> moveCosts,
              int vision, boolean hidden, List<Direction> connectedDirections,
              List<ArmyBranch> heals, List<Integer> canBeCaptureBy, List<Integer> builds, int maxCapCount, int healRate,
              int maxHp, boolean canProduceFunds) {
    super(id, type, connectType, name, description, defenseBonus, height, hidden, vision, moveCosts, connectedDirections, "");
    this.imgRowID = imgRowID;
    this.heals = heals;
    this.canBeCaptureBy = canBeCaptureBy;
    this.builds = builds;
    this.maxCapCount = maxCapCount;
    this.healRate = healRate;
    this.maxHp = maxHp;
    this.canProduceFunds = canProduceFunds;
    init();
  }

  /**
   * Create a city with the given ID all the other values are set to default values
   */
  public City(int id) {
    this(id, id, "", "", "dummy city", "", 0, 0, Arrays.asList(1), 0, false, null, null, null, null, 0, 0, 0, true);
  }

  @Override
  public void init() {
    super.init();
    this.heals = Args.createEmptyListIfNull(heals);
    this.canBeCaptureBy = Args.createEmptyListIfNull(canBeCaptureBy);
    this.builds = Args.createEmptyListIfNull(builds);
    this.canBeLaunchedBy = Args.createEmptyListIfNull(canBeLaunchedBy);
    Args.validate(maxCapCount < 0, "maxCapcount should be positive");
    Args.validate(maxHp < 0, "maxHP should be positive");
  }

  public City(City otherCity) {
    super(otherCity);
    this.imgRowID = otherCity.imgRowID;
    this.location = otherCity.location;
    this.owner = otherCity.owner;
    this.heals = otherCity.heals;
    this.canBeCaptureBy = otherCity.canBeCaptureBy;
    this.builds = otherCity.builds;
    this.canBeLaunchedBy = otherCity.canBeLaunchedBy;

    this.maxCapCount = otherCity.maxCapCount;
    this.healRate = otherCity.healRate;
    this.maxHp = otherCity.maxHp;

    this.capturer = otherCity.capturer;
    this.capCount = otherCity.capCount;
    this.funds = otherCity.funds;
    this.canProduceFunds = otherCity.canProduceFunds;
    this.hp = otherCity.hp;
  }

  /**
   * Resets the city state to default
   */
  void reset() {
    resetCapturing();
    hp = maxHp;
  }

  /**
   * For every start of a Turn
   * #1 If the city can produce funds. The funds of this city are added to the player budget.
   * #2 If a friendly unit is located on this city it is healed and supplied.
   *
   * @param player the Player that is active in this turn
   */
  public void startTurn(Player player) {
    if (canProduceFunds) {
      int coFunds = player.getCO().cityFundsHook(funds);
      player.addToBudget(coFunds);
    }
    healFriendlyUnitOnCity();
  }

  private void healFriendlyUnitOnCity() {
    if (location.getLocatableCount() > 0) {
      Unit unit = (Unit) location.getLastLocatable();

      if (canSupply(unit) && canHeal(unit)) {
        supply(unit);
        heal(unit);
      }
    }
  }

  public void endTurn(Player currentPlayer) {
  }

  /**
   * Captures this city, capturing a city can take multiple turns.
   * The unit capture rate is added to the capCount on each invocation.
   * If another Unit attempts to capture then the capcount will be reset to 0 again.
   * when the capcount >= the maxCapcount the city is considered captured and
   * the player that is owning the capturing unit will be the new owner of this city
   * <p/>
   * Pre: canBeCapturedBy(unit) == true
   * Post: isCaptured() == true isCapturedBy(unit) == true
   *
   * @param capturer The Unit that will perform the capture action
   */
  public void capture(Unit capturer) {
    int captureRate = capturer.getCaptureRate();
    Player newOwner = capturer.getOwner();

    if (this.capturer == capturer) {    // Try to capture some more
      addCapCount(captureRate);
    } else {                            // Not capturing with same unit, restart capping again with the new Unit
      setCapCount(captureRate);
      setCapturer(capturer);
    }

    if (isCaptured()) {
      if (owner != null) owner.removeCity(this);
      newOwner.addCity(this);
      firePropertyChange("captured", null, true);
    }
  }

  /**
   * Reset the capture progress and remove the last capturing unit reference
   */
  public void resetCapturing() {
    setCapCount(0);
    setCapturer(null);
  }

  /**
   * Heal the unit with healRate
   *
   * @param unit unit to heal
   */
  public void heal(Unit unit) {
    if (canHeal(unit)) {
      int coHealRate = owner.getCO().healRateHook(healRate);
      unit.heal(coHealRate);
    }
  }

  /**
   * Supply the unit on this city  to 100%
   *
   * @param unit unit to supply
   */
  public void supply(Unit unit) {
    if (canSupply(unit)) {
      unit.resupply();
    }
  }

  /**
   * Launch the single rocket missile from this city
   *
   * @param unit The unit that wants to fire the missile
   */
  public void launchRocket(Unit unit) {
    if (canLaunchRocket(unit)) {
      launched = true;
      firePropertyChange("launched", false, true);
    }
  }

  @Override
  public void defend(Attacker attacker, Fight fight) {
    int attackPercentage = fight.getAttackDamagePercentage();
    int attackValue = (int) Math.ceil((attackPercentage / 100.0) * maxHp);
    setHp(hp - attackValue);
  }

  @Override
  public boolean canCounterAttack(Attacker attacker) {
    return false;
  }

  @Override
  public ArmyBranch getArmyBranch() {
    int height = getHeight();

    if (height < 0) {
      return ArmyBranch.NAVAL;
    } else if (isLand()) {
      return ArmyBranch.LAND;
    } else {
      return ArmyBranch.AIR;
    }
  }

  public boolean canBeDestroyed() {
    return maxHp > 0 && hp != 0;
  }

  @Override
  public boolean isDestroyed() {
    return hp == 0;
  }

  // ---------------------------------------------------------------------------
  // Setters
  // ---------------------------------------------------------------------------

  public void setOwner(Player owner) {
    Player oldVal = this.owner;
    this.owner = owner;
    firePropertyChange("owner", oldVal, owner);
  }

  @Override
  public void setLocation(Location newLocation) {
    Location oldVal = this.location;
    this.location = newLocation;
    firePropertyChange("location", oldVal, newLocation);
  }

  public void setFunds(int funds) {
    int oldVal = this.funds;
    this.funds = funds;
    firePropertyChange("funds", oldVal, this.funds);
  }

  private void addCapCount(int additionalCapCount) {
    setCapCount(capCount + additionalCapCount);
  }

  private void setCapCount(int capCount) {
    int oldVal = this.capCount;
    this.capCount = Args.getBetweenZeroMax(capCount, maxCapCount);
    firePropertyChange("capcount", oldVal, this.capCount);
  }

  private void setCapturer(Unit capturer) {
    removeCapturingUnitListener();
    Unit oldVal = this.capturer;
    this.capturer = capturer;
    addCapturingUnitListener();
    firePropertyChange("capturer", oldVal, this.capturer);
  }

  private void removeCapturingUnitListener() {
    if (capturer != null) capturer.removePropertyChangeListener(this);
  }

  private void addCapturingUnitListener() {
    if (capturer != null) capturer.addPropertyChangeListener(this);
  }

  private void setHp(int hp) {
    if (hp <= 0) {
      destroy();
    }

    int oldVal = this.hp;
    this.hp = Args.getBetweenZeroMax(hp, maxHp);
    firePropertyChange("hp", oldVal, this.hp);
  }

  private void destroy() {
    owner.removeCity(this);
    owner = null;
    location.remove(this);
    location = null;
    setState(GameObjectState.DESTROYED);
  }

  // ---------------------------------------------------------------------------
  // Getters
  // ---------------------------------------------------------------------------

  /**
   * @see #canSupply(Unit)
   */
  public boolean canHeal(Unit unit) {
    return canSupply(unit);
  }

  /**
   * This city can supply and/or heal the given unit:
   * #1 the unit is within the heals list
   * #2 the unit owner is allied with the city owner
   * #3 the unit is located on the city
   */
  public boolean canSupply(Unit unit) {
    return unit != null && heals.contains(unit.getArmyBranch()) &&
      owner.isAlliedWith(unit.getOwner()) && unit.getLocation().equals(location);
  }

  public boolean canBeCapturedBy(Unit unit) {
    return unit != null && canBeCaptureBy.contains(unit.getStats().getID()) && unit.getLocation().equals(location);
  }

  public boolean canBeCaptured() {
    return !canBeCaptureBy.isEmpty();
  }

  /**
   * @return does this city has the ability to build the given unit
   */
  public boolean canBuild(Unit unit) {
    return unit != null && builds.contains(unit.getStats().getID());
  }

  /**
   * @return does this city has the ability to build any units
   */
  public boolean canBuild() {
    return !builds.isEmpty();
  }

  /**
   * Note that the return value changes when {@link #resetCapturing} is used
   * Before resetCapturing() true is returned if the unit equals the unit that just captured this city
   * After resetCapturing() this method always returns false
   *
   * @return if this city is captured by the given unit
   */
  public boolean isCapturedBy(Unit unit) {
    return isCaptured() && this.capturer == unit;
  }

  /**
   * Note that the return value changes when {@link #resetCapturing} is used
   * Before resetCapturing() true is returned if the city has been captured
   * After resetCapturing() this method always returns false
   *
   * @return if this city has been captured
   */
  protected boolean isCaptured() {
    return capCount == maxCapCount;
  }

  /**
   * Can the given unit launch a rocket from this city
   *
   * @param unit The unit that will attempt to launch the rocket
   * @return if the unit can launch a rocket from this city
   */
  public boolean canLaunchRocket(Unit unit) {
    return unit != null && canLaunchRocket() && canBeLaunchedBy.contains(unit.getStats().getID());
  }

  /**
   * @return Does this city has the ability to launch a rocket
   */
  public boolean canLaunchRocket() {
    return !canBeLaunchedBy.isEmpty() && !launched;
  }

  /**
   * @return The capping process percentage
   */
  public int getCapCountPercentage() {
    return NumberUtil.calcPercentage(capCount, maxCapCount);
  }

  public int getCapCount() {
    return capCount;
  }

  public Location getLocation() {
    return location;
  }

  public Player getOwner() {
    return owner;
  }

  public boolean isOwnedBy(Player player) {
    return owner == player;
  }

  @Override
  public boolean isAlliedWith(Player player) {
    return owner.isAlliedWith(player);
  }

  public boolean isHQ() {
    return getName().equalsIgnoreCase("HQ");
  }

  public boolean isNeutral() {
    return owner != null && owner.isNeutral();
  }

  public int getHp() {
    return hp;
  }

  public int getImgRowID() {
    return imgRowID;
  }

  @Override
  public String toString() {
    return String.format(
      "[%s capCount=%s/%s hp=%s/%s owner=%s]",
      super.toString(), capCount, maxCapCount, hp, maxHp, owner);
  }

  /**
   * Listen for changes of the capturing unit
   * Reset the capturing process:
   * if the unit dies or when the unit moves off this city
   */
  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    assert capturer != null && evt.getSource() == capturer : "Only interested in events from the unit that is capturing";
    String propertyName = evt.getPropertyName();
    if (propertyName.equalsIgnoreCase("state")) {
      if (evt.getNewValue() == GameObjectState.DESTROYED) {
        resetCapturing();
      }
    } else if (propertyName.equalsIgnoreCase("location")) {
      if (evt.getNewValue() != location) {
        resetCapturing();
      }
    }
  }

  private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
    in.defaultReadObject();
    init();
  }
}
