package com.customwars.client.model.ai.unit;

public interface UnitAdvisor {
  /**
   * The advisor will evaluate the game and return the best Unit Order
   *
   * @return The best unit order given the current game state
   */
  UnitOrder createBestOrder();
}
