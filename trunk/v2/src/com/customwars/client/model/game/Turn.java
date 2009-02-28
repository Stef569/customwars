package com.customwars.client.model.game;

import tools.Args;

public class Turn {
  private int turn;
  private int turnLimit;

  public Turn(int turn, int turnLimit) {
    this.turn = turn;
    this.turnLimit = turnLimit;
  }

  public void increaseTurn() {
    setTurn(turn + 1);
  }

  void setTurn(int turn) {
    this.turn = Args.getBetweenZeroMax(turn, turnLimit);
  }

  public int getTurnCount() {
    return turn;
  }

  public int getTurnLimit() {
    return turnLimit;
  }

  public boolean isTurnLimitReached() {
    return turn == turnLimit;
  }
}
