package com.customwars.client.model.game;


/**
 * Game specific configuration:
 * turns, weather etc
 */
public class GameConfig {
  private static final int DEFAULT_BUDGET = 2000;
  private static final int DEFAULT_INCOME = 1000;
  private int startWeather;
  private int cityfunds;
  private int dayLimit;
  private int playerIncome;

  /**
   * Create GameConfig with default values
   */
  public GameConfig() {
    this(0, Turn.UNLIMITED, DEFAULT_BUDGET, DEFAULT_INCOME);
  }

  public GameConfig(int weather, int dayLimit, int cityfunds, int playerIncome) {
    this.startWeather = weather;
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
}
