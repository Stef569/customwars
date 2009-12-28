package com.customwars.client.controller;

import com.customwars.client.model.game.Game;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Direction;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Tile;
import com.customwars.client.model.map.TileMap;
import com.customwars.client.ui.sprite.SpriteManager;

import java.util.List;

/**
 * Lock the cursor at the move zone edge when using the keyboard
 */
public class InGameCursorController extends CursorController {
  private final SpriteManager spriteManager;
  private final Game game;
  private boolean cursorAtZoneEdge, enableCursorZoneCheck;

  public InGameCursorController(Game game, TileMap<Tile> map, SpriteManager spriteManager) {
    super(map, spriteManager);
    this.game = game;
    this.spriteManager = spriteManager;
  }

  @Override
  public void moveCursor(Direction direction) {
    Location originalLocation = spriteManager.getCursorLocation();
    super.moveCursor(direction);

    if (!isTraversing()) {
      lockCursorAtMoveZoneEdge(originalLocation);
    }
  }

  /**
   * Locks the cursor when it is at the movezone edge of the active unit
   * until moveControlReleased() is invoked
   *
   * @param prevCursorLocation The location of the cursor before it made a move
   */
  private void lockCursorAtMoveZoneEdge(Location prevCursorLocation) {
    Unit activeUnit = game.getActiveUnit();

    if (activeUnit != null) {
      List<Location> moveZone = activeUnit.getMoveZone();
      Location cursorLocation = spriteManager.getCursorLocation();

      if (moveZone.contains(cursorLocation)) {
        cursorAtZoneEdge = false;
        enableCursorZoneCheck = true;
      } else {
        // The cursor moved outside the moveZone!
        if (enableCursorZoneCheck) {
          // Snap the cursor back, until enableCursorZoneCheck is put to false see moveControlReleased()
          moveCursor(prevCursorLocation);
          setCursorLocked(true);
          cursorAtZoneEdge = true;
        }
      }
    }
  }

  /**
   * When a move control has been released and the cursor is at the zone edge
   * free the cursor so it can move without limitations
   */
  public void moveControlReleased() {
    setCursorLocked(false);

    if (cursorAtZoneEdge) {
      enableFreeCursorMovement();
    }
  }

  private void enableFreeCursorMovement() {
    enableCursorZoneCheck = false;
    cursorAtZoneEdge = false;
  }
}