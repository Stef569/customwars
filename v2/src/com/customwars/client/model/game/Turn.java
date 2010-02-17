package com.customwars.client.model.game;

import com.customwars.client.tools.Args;

import java.io.Serializable;

/**
 * Stores the current turn/day and the day limit
 */
public class Turn implements Serializable {
  public static final int UNLIMITED = -1;
  private final int dayLimit;
  private int turn;
  private int day;

  public Turn(Turn otherTurn) {
    this(otherTurn.turn, otherTurn.day, otherTurn.dayLimit);
  }

  public Turn(int dayLimit) {
    this(0, 1, dayLimit);
  }

  public Turn(int turn, int day, int dayLimit) {
    this.turn = turn;
    this.day = day;
    this.dayLimit = dayLimit;
    Args.validateBetweenMinMax(dayLimit, -1, Integer.MAX_VALUE, "day limit");
  }

  public void increaseDay() {
    setDay(day + 1);
  }

  private void setDay(int day) {
    if (dayLimit != UNLIMITED) {
      this.day = Args.getBetweenZeroMax(day, dayLimit);
    } else {
      this.day = Args.getBetweenZeroMax(day, Integer.MAX_VALUE);
    }
  }

  public void increaseTurn() {
    setTurn(turn + 1);
  }

  private void setTurn(int turn) {
    this.turn = Args.getBetweenZeroMax(turn, Integer.MAX_VALUE);
  }

  public int getTurnCount() {
    return turn;
  }

  public int getDay() {
    return day;
  }

  public int getLimit() {
    return dayLimit;
  }

  public boolean isLimitReached() {
    return day == UNLIMITED || day == dayLimit;
  }

  public String toString() {
    return String.format("[Turn %s %s/%s]", turn, day, dayLimit);
  }
}
