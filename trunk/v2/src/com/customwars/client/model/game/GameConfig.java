package com.customwars.client.model.game;


/**
 * Game specific configuration:
 * turns, weather etc
 */
public class GameConfig {
  private static final int DEFAULT_BUDGET = 2000;
  private static final int DEFAULT_PLAYER_BUDGET_START = 1000;
  private static final boolean DEFAULT_FOW = true;
  private int startWeather;
  private int cityFunds;
  private int dayLimit;
  private int playerBudgetStart;
  private boolean fogOfWar;

  /**
   * Create GameConfig with default values
   */
  public GameConfig() {
    this(0, DEFAULT_FOW, Turn.UNLIMITED, DEFAULT_BUDGET, DEFAULT_PLAYER_BUDGET_START);
  }

  public GameConfig(int weather, boolean fogOfWar, int dayLimit, int cityFunds, int playerBudgetStart) {
    this.startWeather = weather;
    this.fogOfWar = fogOfWar;
    this.dayLimit = dayLimit;
    this.cityFunds = cityFunds;
    this.playerBudgetStart = playerBudgetStart;
  }

  public void setStartWeather(int startWeather) {
    this.startWeather = startWeather;
  }

  public void setCityFunds(int cityFunds) {
    this.cityFunds = cityFunds;
  }

  public void setDayLimit(int dayLimit) {
    this.dayLimit = dayLimit;
  }

  public void setPlayerBudgetStart(int playerBudgetStart) {
    this.playerBudgetStart = playerBudgetStart;
  }

  public void setFogOfWar(boolean fogOfWar) {
    this.fogOfWar = fogOfWar;
  }

  public int getStartWeather() {
    return startWeather;
  }

  public int getCityFunds() {
    return cityFunds;
  }

  public int getDayLimit() {
    return dayLimit;
  }

  public int getPlayerBudgetStart() {
    return playerBudgetStart;
  }

  public boolean isFogOfWarOn() {
    return fogOfWar;
  }
}
