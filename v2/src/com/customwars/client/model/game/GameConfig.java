package com.customwars.client.model.game;


/**
 * Game specific configuration:
 * turns, weather etc
 */
public class GameConfig {
  private static final int DEFAULT_START_BUDGET = 2000;
  private int startWeather;
  private int cityfunds;
  private int turnLimit;

  /**
   * Create GameConfig with default values
   */
  public GameConfig() {
    this(0, Turn.UNLIMITED, DEFAULT_START_BUDGET);
  }

  public GameConfig(int weather, int turnLimit, int cityfunds) {
    this.startWeather = weather;
    this.turnLimit = turnLimit;
    this.cityfunds = cityfunds;
  }

  public void setStartWeather(int startWeather) {
    this.startWeather = startWeather;
  }

  public void setCityfunds(int cityfunds) {
    this.cityfunds = cityfunds;
  }

  public void setTurnLimit(int limit) {
    this.turnLimit = limit;
  }

  public int getStartWeather() {
    return startWeather;
  }

  public int getCityFunds() {
    return cityfunds;
  }

  public int getTurnLimit() {
    return turnLimit;
  }
}
