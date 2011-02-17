package com.customwars.client.model.co;

import com.customwars.client.App;
import com.customwars.client.model.fight.Defender;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.TileMap;
import com.customwars.client.tools.Args;
import com.customwars.client.ui.renderer.GameRenderer;

import java.awt.Color;

/**
 * A commanding officer, subclasses should extend functions of interest.
 */
public abstract class AbstractCO implements CO {
  private static final double NUM_BARS_TO_EXTEND_COZONE = App.getDouble("plugin.co_num_bars_to_extend_zone");
  private static final double NUM_BARS_TO_ACTIVATE_POWER = App.getDouble("plugin.co_num_bars_to_activate_power");
  private static final double NUM_BARS_TO_ACTIVATE_SUPER_POWER = App.getDouble("plugin.co_num_bars_to_activate_superpower");
  private static final double MAX_NUM_BARS = 12;

  private final String name;
  private final COStyle style;
  private final String bio;
  private final String title;
  private final String hit;
  private final String miss;
  private final String skill;

  private Power power;
  private Power superpower;

  private final String intel;
  private final String[] defeat;
  private final String[] victory;
  private final String[] quotes;

  private final int coZone;
  private double bars;

  protected AbstractCO(String name) {
    this(name, new COStyle("", Color.white, 0, "white"), "", "", 0, "", "", "", new Power("", ""), new Power("", ""), "", null, null, null);
  }

  protected AbstractCO(String name, COStyle style, String bio, String title, int coZone,
                       String hit, String miss, String skill,
                       Power power, Power superPower,
                       String intel, String[] defeat, String[] victory, String[] quotes) {
    this.name = name;
    this.style = style;
    this.bio = bio;
    this.title = title;
    this.hit = hit;
    this.miss = miss;
    this.skill = skill;
    this.power = power;
    this.superpower = superPower;
    this.intel = intel;
    this.defeat = defeat;
    this.victory = victory;
    this.quotes = quotes;
    this.coZone = coZone;
    init();
  }

  public void init() {
    // Make sure power and super power are not null
    this.power = power == null ? Power.NONE : power;
    this.superpower = superpower == null ? Power.NONE : superpower;

    // Valid bar values are > -1  && < MAX_NUM_BARS
    int maxBars = (int) MAX_NUM_BARS + 1;
    Args.validateBetweenMinMax((int) NUM_BARS_TO_ACTIVATE_POWER, -1, maxBars, "num bars to activate power");
    Args.validateBetweenMinMax((int) NUM_BARS_TO_ACTIVATE_SUPER_POWER, -1, maxBars, "num bars to activate super power");
    Args.validateBetweenMinMax((int) NUM_BARS_TO_EXTEND_COZONE, -1, maxBars, "num bars to extend co zone");

    if (intel == null) {
      throw new IllegalArgumentException("No intel for co " + name);
    }
  }

  /**
   * Copy Constructor
   *
   * @param co the co to copy
   */
  protected AbstractCO(AbstractCO co) {
    this.name = co.name;
    this.style = co.style;
    this.bio = co.bio;
    this.title = co.title;
    this.hit = co.hit;
    this.miss = co.miss;
    this.skill = co.skill;
    this.power = new Power(co.power);
    this.superpower = new Power(co.superpower);
    this.intel = co.intel;
    this.defeat = co.defeat;
    this.victory = co.victory;
    this.quotes = co.quotes;
    this.coZone = co.coZone;
    this.bars = co.bars;
  }

  @Override
  public void chargePowerGauge(double chargeRate) {
    bars += chargeRate;

    if (bars < 0) {
      bars = 0;
    } else if (bars > MAX_NUM_BARS) {
      bars = MAX_NUM_BARS;
    }
  }

  @Override
  public void resetPowerGauge() {
    bars = 0;
  }

  @Override
  public void unitAttackedHook(Unit attacker, Defender defender) {
    if (attacker.getOwner().isInCOZone(defender.getLocation()) && defender instanceof Unit) {
      chargePowerGauge(attacker.getHp() / 50.0);
    }
  }

  @Override
  public void power(Game game, GameRenderer gameRenderer) {
    power.activate();
  }

  @Override
  public void deActivatePower() {
    power.deActivate();
  }

  @Override
  public void superPower(Game game, GameRenderer gameRenderer) {
    superpower.activate();
  }

  @Override
  public void deActivateSuperPower() {
    superpower.deActivate();
  }

  @Override
  public boolean isInCOZone(Unit unit, Location location) {
    int distance = TileMap.getDistanceBetween(unit.getLocation(), location);
    return distance - getZoneRange() <= 0;
  }

  @Override
  public int getZoneRange() {
    return hasExtendedZone() ? coZone + 1 : hasMaxedZone() ? coZone + 2 : coZone;
  }

  private boolean hasExtendedZone() {
    return bars >= NUM_BARS_TO_EXTEND_COZONE;
  }

  private boolean hasMaxedZone() {
    return bars == MAX_NUM_BARS;
  }

  public boolean canDoPower() {
    return bars == NUM_BARS_TO_ACTIVATE_POWER;
  }

  public boolean canDoSuperPower() {
    return bars == NUM_BARS_TO_ACTIVATE_SUPER_POWER;
  }

  public int getMaxBars() {
    return (int) MAX_NUM_BARS;
  }

  public int getBars() {
    return (int) (bars + 0.5);
  }

  public String getName() {
    return name;
  }

  public COStyle getStyle() {
    return style;
  }

  public String getBio() {
    return bio;
  }

  public String getTitle() {
    return title;
  }

  public String getHit() {
    return hit;
  }

  public String getMiss() {
    return miss;
  }

  public String getSkill() {
    return skill;
  }

  @Override
  public boolean isPowerActive() {
    return power.isActive();
  }

  @Override
  public String getPowerDescription() {
    return power.getDescription();
  }

  @Override
  public boolean isSuperPowerActive() {
    return superpower.isActive();
  }

  @Override
  public String getSuperPowerDescription() {
    return superpower.getDescription();
  }

  @Override
  public String getIntel() {
    return intel;
  }

  @Override
  public String[] getQuotes() {
    return quotes;
  }

  @Override
  public String[] getVictory() {
    return victory;
  }

  @Override
  public String[] getDefeat() {
    return defeat;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof AbstractCO)) return false;

    AbstractCO that = (AbstractCO) o;

    if (!name.equals(that.name)) return false;

    return true;
  }

  @Override
  public int hashCode() {
    return name.hashCode();
  }
}