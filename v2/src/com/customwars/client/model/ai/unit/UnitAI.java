package com.customwars.client.model.ai.unit;

import java.util.List;

/**
 * The Unit AI finds the best units orders to perform
 */
public interface UnitAI {
  /**
   * Allows each Unit in the game to think about the best 'action' to perform.
   *
   * @return A list of Unit orders
   */
  List<UnitOrder> findBestUnitOrders();

  /**
   * @return if the game is over after executing the unit orders
   */
  boolean isGameOver();
}
