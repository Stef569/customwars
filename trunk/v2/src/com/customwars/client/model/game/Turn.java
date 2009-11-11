package com.customwars.client.model.game;

import com.customwars.client.tools.Args;

/**
 * Stores the current turn/day and their limits
 */
public class Turn {
  public static final int UNLIMITED = -1;
  private int turn;
  private int turnLimit;
  private int day;
  private int dayLimit;

  public Turn(int turn, int day, int turnLimit, int dayLimit) {
    this.turn = turn;
    this.day = day;
    this.turnLimit = turnLimit;
    this.dayLimit = dayLimit;
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
    if (turnLimit != UNLIMITED)
      this.turn = Args.getBetweenZeroMax(turn, turnLimit);
    else {
      this.turn = Args.getBetweenZeroMax(turn, Integer.MAX_VALUE);
    }
  }

  public int getTurnCount() {
    return turn;
  }

  public int getTurnLimit() {
    return turnLimit;
  }

  public int getDay() {
    return day;
  }


  public int getDayLimit() {
    return dayLimit;
  }

  public boolean isTurnLimitReached() {
    return turn == UNLIMITED || turn == turnLimit;
  }

  public boolean isDayLimitReached() {
    return day == UNLIMITED || day == dayLimit;
  }

  public String toString() {
    return String.format("[Turn %s/%s %s/%s]", turn, turnLimit, day, dayLimit);
  }
}
