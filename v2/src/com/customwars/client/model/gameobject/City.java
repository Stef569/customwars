package com.customwars.client.model.gameobject;

import com.customwars.client.model.ArmyBranch;
import com.customwars.client.model.TurnHandler;
import com.customwars.client.model.game.Player;
import com.customwars.client.model.map.Direction;
import com.customwars.client.model.map.Location;
import com.customwars.client.tools.Args;
import com.customwars.client.tools.NumberUtil;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

/**
 * Cities can be owned by 1 Player
 * are located on a Location
 * They have funds, The player that owns this city receives the funds on each turn start.
 * can heal and supply units of a specific ArmyBranch ID
 * can be captured by a Units of a specific unit ID
 * can build units of a specific ArmyBranch ID
 * <p/>
 * When a city is captured
 * getCapturePercentage() == 100
 * isCaptured() == true
 * <p/>
 * Preparing for another capture process is triggered when
 * another unit tries to capture this city, or by invoking resetCapturing()
 *
 * A City can fire a rocket once
 *
 * @author stefan
 */
public class City extends Terrain implements PropertyChangeListener, TurnHandler {
  private List<ArmyBranch> heals;       // The army branches this City can heal(Empty list means it cannot heal)
  private List<Integer> canBeCaptureBy; // The ids this City can be captured by(Empty list means it cannot be captured)
  private List<ArmyBranch> builds;      // The army branches this City can build (Empty list means it cannot build)
  private List<Integer> canBeLaunchedBy;// The ids that can launch a rocket (Empty list means it cannot launch rockets)
  private int maxCapCount;
  private int healRate;       // Healing/repairs this city can give to a Unit
  private int funds;          // Money that this city produces every turn

  private Location location;  // The location this City is on
  private Player owner;       // Player owning this City
  private boolean launched;   // If this city already launched a rocket
  private Unit capturer;      // Unit that is capturing this city
  private int capCount;       // The current capture count(if capCount==maxCapCount then this city is considered to be captured)

  public City(int id, String type, String name, String description, int defenseBonus, int height, List<Integer> moveCosts,
              int vision, boolean hidden, List<Direction> connectedDirections,
              List<ArmyBranch> heals, List<Integer> canBeCaptureBy, List<ArmyBranch> builds, int maxCapCount, int healRate) {
    super(id, type, name, description, defenseBonus, height, hidden, vision, moveCosts, connectedDirections);
    this.heals = heals;
    this.canBeCaptureBy = canBeCaptureBy;
    this.builds = builds;
    this.maxCapCount = maxCapCount;
    this.healRate = healRate;
    init();
  }

  public void init() {
    super.init();
    this.heals = Args.createEmptyListIfNull(heals);
    this.canBeCaptureBy = Args.createEmptyListIfNull(canBeCaptureBy);
    this.builds = Args.createEmptyListIfNull(builds);
    this.canBeLaunchedBy = Args.createEmptyListIfNull(canBeLaunchedBy);
    Args.validate(maxCapCount < 0, "maxCapcount should be positive");
  }

  public City(City otherCity) {
    super(otherCity);
    this.location = otherCity.location;
    this.owner = otherCity.owner;
    this.heals = otherCity.heals;
    this.canBeCaptureBy = otherCity.canBeCaptureBy;
    this.builds = otherCity.builds;
    this.canBeLaunchedBy = otherCity.canBeLaunchedBy;

    this.maxCapCount = otherCity.maxCapCount;
    this.healRate = otherCity.healRate;

    this.capturer = otherCity.capturer;
    this.capCount = otherCity.capCount;
    this.funds = otherCity.funds;
  }

  /**
   * Resets the city state to default
   */
  void reset() {
    resetCapturing();
  }

  /**
   * For every start of a Turn
   * if this city is owned by the player then
   * the funds of this city are added to the player budget.
   *
   * @param player the Player that is active in this turn
   */
  public void startTurn(Player player) {
    if (owner == player) {
      player.addToBudget(funds);
    }
  }

  public void endTurn(Player currentPlayer) {
  }

  /**
   * Captures this city
   * Adds the unit capture rate to the capCount value on each invocation
   * it remembers the capturing Unit, if another Unit attempts to capture
   * then the capcount will be reset to 0 again.
   * when capcount >= the maxCapcount
   * the city is considered captured and
   * the player that is owning the capturing unit will be the new owner of this city
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
      unit.heal(healRate);
    }
  }

  /**
   * Supply the unit to 100%
   *
   * @param unit unit to supply
   */
  public void supply(Unit unit) {
    if (canSupply(unit)) {
      unit.resupply();
    }
  }

  public void launchRocket(Unit unit) {
    if (canLaunchRocket(unit)) {
      launched = true;
      firePropertyChange("launched", false, true);
    }
  }

  // ---------------------------------------------------------------------------
  // Setters
  // ---------------------------------------------------------------------------
  public void setOwner(Player owner) {
    Player oldVal = this.owner;
    this.owner = owner;
    firePropertyChange("owner", oldVal, owner);
  }

  public void setLocation(Location location) {
    Location oldVal = this.location;
    this.location = location;
    firePropertyChange("location", oldVal, location);
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

  // ---------------------------------------------------------------------------
  // Getters
  // ---------------------------------------------------------------------------
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
      owner.isAlliedWith(unit.getOwner()) && unit.getLocation() == location;
  }

  public boolean canBeCapturedBy(Unit unit) {
    return unit != null && canBeCaptureBy.contains(unit.getStats().getID()) && unit.getLocation() == location;
  }

  public boolean canBuild(Unit unit) {
    return unit != null && builds.contains(unit.getArmyBranch());
  }

  public boolean canBuild() {
    return !builds.isEmpty();
  }

  /**
   * This function can only be used before resetCapturing() is invoked
   *
   * @return if this city is captured by the given unit
   */
  public boolean isCapturedBy(Unit unit) {
    return isCaptured() && this.capturer == unit;
  }

  protected boolean isCaptured() {
    return capCount == maxCapCount;
  }

  public boolean canLaunchRocket(Unit unit) {
    return unit != null && canLaunchRocket() && canBeLaunchedBy.contains(unit.getStats().getID());
  }

  public boolean canLaunchRocket() {
    return !canBeLaunchedBy.isEmpty() && !launched;
  }

  public boolean isRocketLaunched() {
    return launched;
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
  public String toString() {
    StringBuilder strBuilder = new StringBuilder(
      "[" + super.toString() + " capCount=" + capCount + "/" + maxCapCount);
    if (owner != null) strBuilder.append(" owner=").append(owner);
    return strBuilder.append("]").toString();
  }

  /**
   * Listen for changes of the capturing unit
   * Reset the capturing process:
   * if the unit dies or when the unit moves off this city
   */
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
}
