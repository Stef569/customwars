package com.customwars.client.model.gameobject;

import com.customwars.client.model.TurnHandler;
import com.customwars.client.model.game.Player;
import com.customwars.client.model.map.Location;
import tools.Args;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

/**
 * Cities can be owned by 1 Player
 * in fog of war They have a vision aka line of sight,
 * are located on a Location
 * They have funds. The player that owns this city receives the funds each turn.
 * can heal units of a specific ID
 * can supply units of a specific ID
 * can be captured by a Units
 * can build units of a specific ID
 *
 * healrate and healRanges are used for both healing and supplying.
 *
 * @author stefan
 */
public class City extends Terrain implements PropertyChangeListener, TurnHandler {
  private int vision;         // Amount of tiles this terrain can 'see' in each direction
  private Location location;  // The location this City is on
  private Player owner;       // Player owning this City(never null)

  private List<Integer> heals;          // The ids this City can heal(Empty list means it cannot heal)
  private List<Integer> canBeCaptureBy; // The ids this City can be captured by(Empty list means it cannot be captured)
  private List<Integer> builds;         // The ids this City can build (Empty list means it cannot heal)
  private int maxCapCount;              // When capCount == max capCount this property is captured see capture().
  private int healRate;                 // Amount of healing/repairs/supplies this property can give to a Unit
  private int minHealRange, maxHealRange;    // In what range can this City heal/Supply

  private Unit capturer;      // Capturing unit that is capturing this property
  private int capCount;       // The current capture count(if capCount==maxCapCount then this property is considered to be captured)
  private int funds;          // Amount of money that this property produces every turn

  public City(int id, String name, String description, int defenseBonus, int height, List<Integer> moveCosts,
              int vision, boolean hidden,
              List<Integer> heals, List<Integer> canBeCaptureBy, List<Integer> builds, int maxCapCount, int healRate, int minHealRange, int maxHealRange) {
    super(id, name, description, defenseBonus, height, hidden, moveCosts);
    this.vision = vision;
    this.heals = heals;
    this.canBeCaptureBy = canBeCaptureBy;
    this.builds = builds;
    this.maxCapCount = maxCapCount;
    this.healRate = healRate;
    this.minHealRange = minHealRange;
    this.maxHealRange = maxHealRange;
    init();
  }

  void init() {
    super.init();
    this.heals = Args.createEmptyListIfNull(heals);
    this.canBeCaptureBy = Args.createEmptyListIfNull(canBeCaptureBy);
    this.builds = Args.createEmptyListIfNull(builds);

    Args.validate(maxHealRange < 0, "maxHealRange should be positive");
    Args.validate(minHealRange < 0, "minHealRange should be positive");
    Args.validate(maxHealRange < minHealRange, "minHealRange should be smaller then maxHealRange");
    Args.validate(maxCapCount < 0, "maxCapcount should be positive");
  }

  public City(City otherCity) {
    super(otherCity);
    this.vision = otherCity.vision;
    this.location = otherCity.location;
    this.owner = otherCity.owner;
    this.heals = otherCity.heals;
    this.canBeCaptureBy = otherCity.canBeCaptureBy;
    this.builds = otherCity.builds;
    this.maxCapCount = otherCity.maxCapCount;
    this.healRate = otherCity.healRate;
    this.minHealRange = otherCity.minHealRange;
    this.maxHealRange = otherCity.maxHealRange;

    this.capturer = otherCity.capturer;
    this.capCount = otherCity.capCount;
    this.funds = otherCity.funds;
  }

  /**
   * Resets the city state to default
   */
  void reset() {
    this.capCount = 0;
  }

  /**
   * For every start of a Turn
   * if this city is owned by the activePlayer then
   * the funds of this city are added to the player budget.
   *
   * @param activePlayer the Player that is active in this turn
   */
  public void startTurn(Player activePlayer) {
    if (owner == activePlayer) {
      activePlayer.addToBudget(funds);
    }
  }

  public void endTurn(Player currentPlayer) {
  }

  /**
   * Captures this property
   * Adds the captureRate value to the capCount value on each invocation
   * it remembers the capturing Unit, if another Unit attempts to capture
   * then the capcount will be reset to 0 again.
   * when capcount >= the maxCapcount
   * the property is considered captured and
   * the player that is owning the capturing unit will be the new owner of this property
   *
   * @param capturer The Unit that will perform the capture action
   */
  public void capture(Unit capturer) {
    if (canBeCapturedBy(capturer)) {
      int captureRate = capturer.getHp();
      Player newOwner = capturer.getOwner();

      if (this.capturer == capturer) {    // Try to capture some more
        addCapCount(captureRate);
      } else {                            // Not capturing with same unit, restart capping again with the new Unit
        setCapCount(captureRate);
        setCapturer(capturer);
      }

      if (isCaptured()) {
        owner.removeCity(this);
        newOwner.addCity(this);
        resetCapturing();
        firePropertyChange("captured", null, true);
      }
    }
  }

  private void resetCapturing() {
    setCapCount(0);
    setCapturer(null);
  }

  public void heal(Unit unit) {
    if (canHeal(unit)) {
      unit.addHp(healRate);
    }
  }

  public void supply(Unit unit) {
    if (canSupply(unit)) {
      unit.addSupplies(healRate);
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
    Unit oldVal = this.capturer;
    if (oldVal != null)
      oldVal.removePropertyChangeListener(this);

    this.capturer = capturer;
    if (capturer != null) capturer.addPropertyChangeListener(this);
    firePropertyChange("capturer", oldVal, this.capturer);
  }

  // ---------------------------------------------------------------------------
  // Getters
  // ---------------------------------------------------------------------------
  public boolean canHeal(Unit unit) {
    return unit != null && heals.contains(unit.getArmyBranch());
  }

  public boolean canSupply(Unit unit) {
    return unit != null && heals.contains(unit.getArmyBranch());
  }

  public boolean canBeCapturedBy(Unit unit) {
    return unit != null && canBeCaptureBy.contains(unit.getID());
  }

  public boolean canBuild(Unit unit) {
    return unit != null && builds.contains(unit.getArmyBranch());
  }

  public boolean canBuild() {
    return !builds.isEmpty();
  }

  public boolean isCapturedBy(Unit unit) {
    return isCaptured() && this.capturer == unit;
  }

  protected boolean isCaptured() {
    return capCount == maxCapCount;
  }

  public int getMinHealRange() {
    return minHealRange;
  }

  public int getMaxHealRange() {
    return maxHealRange;
  }

  /**
   * @return The capping process percentage,
   *         100 is never returned instead
   *         when the city is captured 0 is returned
   */
  public int getCapCountPercentage() {
    int percentage;
    if (maxCapCount <= 0) {
      percentage = 100;
    } else {
      double divide = (double) capCount / maxCapCount;
      percentage = (int) Math.round(divide * 100);
    }
    return percentage;
  }

  public int getVision() {
    return vision;
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
            "[" + super.toString() + " capCount=" + capCount + "/" + maxCapCount + " vision=" + vision);
    if (owner != null) strBuilder.append(" owner=").append(owner);
    return strBuilder.append("]").toString();
  }

  /**
   * Listen for changes of the capturing unit
   */
  public void propertyChange(PropertyChangeEvent evt) {
    assert capturer != null && evt.getSource() == capturer : "Only interested in events from the unit that is capturing";
    String propertyName = evt.getPropertyName();
    if (propertyName.equalsIgnoreCase("state")) {
      if (evt.getNewValue() == GameObjectState.DESTROYED) {
        resetCapturing();
      }
    }
  }
}
