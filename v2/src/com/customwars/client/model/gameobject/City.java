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
 * They have funds, The player that owns this city receives the funds on each turn start.
 * can heal units of a specific ArmyBranch ID
 * can supply units of a specific ArmyBranch ID
 * can be captured by a Units of a specific unit ID
 * can build units of a specific ArmyBranch ID
 * <p/>
 * When a city is captured
 * getCapturePercentage == 100
 * isCaptured() == true
 * <p/>
 * Preparing for another capture is done when
 * another unit tries to capture this city, or manually through resetCapturing()
 *
 * @author stefan
 */
public class City extends Terrain implements PropertyChangeListener, TurnHandler {
  private Location location;  // The location this City is on
  private Player owner;       // Player owning this City(never null)

  private List<Integer> heals;          // The ids this City can heal(Empty list means it cannot heal)
  private List<Integer> canBeCaptureBy; // The ids this City can be captured by(Empty list means it cannot be captured)
  private List<Integer> builds;         // The ids this City can build (Empty list means it cannot heal)
  private int maxCapCount;              // When capCount == max capCount this city is captured see capture().
  private int healRate;                 // Amount of healing/repairs this city can give to a Unit

  private Unit capturer;      // Capturing unit that is capturing this city
  private int capCount;       // The current capture count(if capCount==maxCapCount then this city is considered to be captured)
  private int funds;          // Amount of money that this city produces every turn

  public City(int id, String name, String description, int defenseBonus, int height, List<Integer> moveCosts,
              int vision, boolean hidden,
              List<Integer> heals, List<Integer> canBeCaptureBy, List<Integer> builds, int maxCapCount, int healRate) {
    super(id, name, description, defenseBonus, height, hidden, vision, moveCosts);
    this.heals = heals;
    this.canBeCaptureBy = canBeCaptureBy;
    this.builds = builds;
    this.maxCapCount = maxCapCount;
    this.healRate = healRate;
    init();
  }

  void init() {
    super.init();
    this.heals = Args.createEmptyListIfNull(heals);
    this.canBeCaptureBy = Args.createEmptyListIfNull(canBeCaptureBy);
    this.builds = Args.createEmptyListIfNull(builds);
    Args.validate(maxCapCount < 0, "maxCapcount should be positive");
  }

  public City(City otherCity) {
    super(otherCity);
    this.location = otherCity.location;
    this.owner = otherCity.owner;
    this.heals = otherCity.heals;
    this.canBeCaptureBy = otherCity.canBeCaptureBy;
    this.builds = otherCity.builds;
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
   * Adds the captureRate value to the capCount value on each invocation
   * it remembers the capturing Unit, if another Unit attempts to capture
   * then the capcount will be reset to 0 again.
   * when capcount >= the maxCapcount
   * the city is considered captured and
   * the player that is owning the capturing unit will be the new owner of this city
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
        firePropertyChange("captured", null, true);
      }
    }
  }

  public void resetCapturing() {
    setCapCount(0);
    setCapturer(null);
  }

  public void heal(Unit unit) {
    if (canHeal(unit)) {
      unit.heal(healRate);
    }
  }

  public void supply(Unit unit) {
    if (canSupply(unit)) {
      unit.resupply();
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

  /**
   * @return The capping process percentage
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
