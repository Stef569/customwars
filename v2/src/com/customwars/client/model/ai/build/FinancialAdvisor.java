package com.customwars.client.model.ai.build;

/**
 * The financial advisor controls the flow of money.
 */
public interface FinancialAdvisor {

  /**
   * @return the amount of money that can be spend this turn
   */
  public int getAvailableFunds();
}
