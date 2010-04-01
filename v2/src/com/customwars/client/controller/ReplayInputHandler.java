package com.customwars.client.controller;

import com.customwars.client.model.game.GameReplay;
import com.customwars.client.model.map.Tile;
import com.customwars.client.ui.state.InGameContext;
import com.customwars.client.ui.state.StateChanger;

/**
 * The Game Replay controller executes the next replay action
 * when any input is received.
 */
public class ReplayInputHandler implements InGameInputHandler {
  private final GameReplay replay;
  private final InGameContext inGameContext;
  private StateChanger stateChanger;

  public ReplayInputHandler(GameReplay replay, InGameContext inGameContext) {
    this.replay = replay;
    this.inGameContext = inGameContext;
    stateChanger = inGameContext.getObj(StateChanger.class);
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
      stateChanger.changeTo("GAME_OVER");
    }
  }
}
