package com.customwars.client.model.game;


/**
 * Game specific configuration:
 * turns, weather etc
 * -1 for a int value means infinitive or off.
 */
public class GameConfig {
  private int startWeather = 0;
  private int turnLimit;
  private int cityfunds;

  public GameConfig() {
  }

  public GameConfig(int weather, int turnLimit, int cityfunds) {
    this.startWeather = weather;
    this.turnLimit = turnLimit;
    this.cityfunds = cityfunds;
  }

  // ---------------------------------------------------------------------------
  // SETTERS
  // --------------------------------------------------------------------------
  public void setStartWeather(int startWeather) {
    this.startWeather = startWeather;
  }

  public void setTurnLimit(int turnLimit) {
    this.turnLimit = turnLimit;
  }

  public void setCityfunds(int cityfunds) {
    this.cityfunds = cityfunds;
  }

  // ---------------------------------------------------------------------------
  // GETTERS
  // --------------------------------------------------------------------------
  public int getStartWeather() {
    return startWeather;
  }

  public int getTurnLimit() {
    return turnLimit;
  }

  public int getCityFunds() {
    return cityfunds;
  }
}
