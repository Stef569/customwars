package com.customwars.client.model.game;


/**
 * Game specific configuration:
 * turns, weather etc
 * -1 means infinitive
 */
public class GameConfig {
  private int startWeather = 0;
  private int turnLimit;
  private int cityfunds;
  private int dayLimit;

  public GameConfig() {
  }

  public GameConfig(int weather, int turnLimit, int dayLimit, int cityfunds) {
    this.startWeather = weather;
    this.turnLimit = turnLimit;
    this.dayLimit = dayLimit;
    this.cityfunds = cityfunds;
  }

  public void setStartWeather(int startWeather) {
    this.startWeather = startWeather;
  }

  public void setTurnLimit(int turnLimit) {
    this.turnLimit = turnLimit;
  }

  public void setCityfunds(int cityfunds) {
    this.cityfunds = cityfunds;
  }

  public void setDayLimit(int dayLimit) {
    this.dayLimit = dayLimit;
  }

  public int getStartWeather() {
    return startWeather;
  }

  public int getTurnLimit() {
    return turnLimit;
  }

  public int getCityFunds() {
    return cityfunds;
  }

  public int getDayLimit() {
    return dayLimit;
  }
}
