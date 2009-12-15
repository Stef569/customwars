package com.customwars.client.model.game;


/**
 * Game specific configuration:
 * turns, weather etc
 */
public class GameConfig {
  private static final int DEFAULT_BUDGET = 2000;
  private static final int DEFAULT_INCOME = 1000;
  private static final boolean DEFAULT_FOW = true;
  private int startWeather;
  private int cityfunds;
  private int dayLimit;
  private int playerIncome;
  private boolean fogOfWar;

  /**
   * Create GameConfig with default values
   */
  public GameConfig() {
    this(0, DEFAULT_FOW, Turn.UNLIMITED, DEFAULT_BUDGET, DEFAULT_INCOME);
  }

  public GameConfig(int weather, boolean fogOfWar, int dayLimit, int cityfunds, int playerIncome) {
    this.startWeather = weather;
    this.fogOfWar = fogOfWar;
    this.dayLimit = dayLimit;
    this.cityfunds = cityfunds;
    this.playerIncome = playerIncome;
  }

  public void setStartWeather(int startWeather) {
    this.startWeather = startWeather;
  }

  public void setCityfunds(int cityfunds) {
    this.cityfunds = cityfunds;
  }

  public void setDayLimit(int dayLimit) {
    this.dayLimit = dayLimit;
  }

  public void setPlayerIncome(int playerIncome) {
    this.playerIncome = playerIncome;
  }

  public void setFogOfWar(boolean fogOfWar) {
    this.fogOfWar = fogOfWar;
  }

  public int getStartWeather() {
    return startWeather;
  }

  public int getCityFunds() {
    return cityfunds;
  }

  public int getDayLimit() {
    return dayLimit;
  }

  public int getPlayerIncome() {
    return playerIncome;
  }

  public boolean isFogOfWarOn() {
    return fogOfWar;
  }
}
