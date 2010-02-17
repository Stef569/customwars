package com.customwars.client.controller;

import com.customwars.client.model.game.GameReplay;
import com.customwars.client.model.map.Tile;
import com.customwars.client.ui.renderer.GameRenderer;
import com.customwars.client.ui.state.InGameContext;

/**
 * The Game Replay controller executes the next replay action
 * when any input is received.
 */
public class GameReplayController implements GameController {
  private final GameReplay replay;
  private final InGameContext inGameContext;
  private final InGameCursorController cursorControl;

  public GameReplayController(GameReplay replay, GameRenderer gameRenderer, InGameContext inGameContext) {
    this.replay = replay;
    this.inGameContext = inGameContext;
    this.cursorControl = new InGameCursorController(replay.getInitialGame(), gameRenderer.getMapRenderer().getSpriteManager());
  }

  @Override
  public void handleA(Tile cursorLocation) {
    executeNextReplayAction();
  }

  @Override
  public void handleB(Tile cursorLocation) {
    executeNextReplayAction();
  }

  @Override
  public void undo() {
    executeNextReplayAction();
  }

  @Override
  public void startUnitCycle() {
    executeNextReplayAction();
  }

  @Override
  public void endTurn() {
    executeNextReplayAction();
  }

  private void executeNextReplayAction() {
    if (replay.hasMoreActions()) {
      replay.execNextReplayAction(inGameContext);
    } else {
      inGameContext.getStateChanger().changeTo("GAME_OVER");
    }
  }

  @Override
  public InGameCursorController getCursorController() {
    return cursorControl;
  }
}
