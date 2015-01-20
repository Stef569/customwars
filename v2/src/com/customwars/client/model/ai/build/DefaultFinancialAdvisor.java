package com.customwars.client.model.ai.build;

import com.customwars.client.model.game.Game;

public class DefaultFinancialAdvisor implements FinancialAdvisor {
  private final Game game;

  public DefaultFinancialAdvisor(Game game) {
    this.game = game;
  }

  /**
   * @return the amount of money that can be spend this turn
   */
  public int getAvailableFunds() {
    int toSpend;
    int availableFunds = game.getActivePlayer().getBudget();
    int minimumFunds = 8000;
    int nextRoundFunds = availableFunds * 2;

    if (game.getTurn() > 1) {
      if (nextRoundFunds < availableFunds) {
        toSpend = availableFunds;
      } else {
        if (availableFunds <= minimumFunds) {
          toSpend = availableFunds;
        } else {
          toSpend = nextRoundFunds - 16000;
        }
      }
    } else {
      toSpend = availableFunds;
    }

    return toSpend;
  }
}
