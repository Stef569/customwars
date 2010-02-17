package com.customwars.client.controller;

import com.customwars.client.model.map.Tile;

/**
 * Handles user input while playing a game
 */
public interface GameController {
  void handleA(Tile cursorLocation);

  void handleB(Tile cursorLocation);

  void undo();

  void startUnitCycle();

  void endTurn();

  InGameCursorController getCursorController();
}
