package com.customwars.client.model.game;

import tools.Args;

public class Turn {
  private int turnStart;
  private int turn;
  private int turnLimit;

  public Turn(int turn, int turnLimit) {
    this.turnStart = turn;
    this.turn = turn;
    this.turnLimit = turnLimit;
  }

  public void increaseTurn() {
    setTurn(turn + 1);
  }

  private void setTurn(int turn) {
    this.turn = Args.validateBetweenZeroMax(turn, turnLimit);
  }

  public int getTurnCount() {
    return turn - turnStart;
  }

  public int getTurnLimit() {
    return turnLimit - turnStart;
  }

  public boolean isTurnLimitReached() {
    return turn == turnLimit;
  }
}
