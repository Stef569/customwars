package com.customwars.client.action.game;

import com.customwars.client.App;
import com.customwars.client.action.DirectAction;
import com.customwars.client.io.loading.BinaryCW2GameParser;
import com.customwars.client.model.game.GameReplay;
import com.customwars.client.ui.GUI;
import com.customwars.client.ui.state.InGameContext;
import com.customwars.client.ui.state.StateChanger;
import com.customwars.client.ui.state.StateSession;
import org.apache.log4j.Logger;

import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Load a previously saved replay file.
 * set the game mode to REPLAY and change to the in game state
 *
 * If the replay file does not exist then nothing happens
 */
public class LoadReplayAction extends DirectAction {
  private static final Logger logger = Logger.getLogger(LoadReplayAction.class);
  private StateSession session;
  private BinaryCW2GameParser gameParser;
  private StateChanger stateChanger;

  public LoadReplayAction(StateSession session, StateChanger stateChanger) {
    this();
    this.session = session;
    this.stateChanger = stateChanger;
  }

  public LoadReplayAction() {
    super("Load replay", false);
    gameParser = new BinaryCW2GameParser();
  }

  @Override
  protected void init(InGameContext inGameContext) {
    if (inGameContext != null) {
      stateChanger = inGameContext.getObj(StateChanger.class);
      session = inGameContext.getObj(StateSession.class);
    }
  }

  @Override
  protected void invokeAction() {
    FileFilter filter = new FileNameExtensionFilter("CW2 Replays", "replay");
    File replayFile = GUI.browseForFile("Load replay", filter, "Load");

    if (replayFile != null) {
      loadReplay(replayFile);
    } else {
      logger.warn("No replay file chosen");
    }
  }

  private void loadReplay(File replayFile) {
    try {
      loadReplayFromFile(replayFile);
    } catch (IOException e) {
      GUI.showExceptionDialog("Could not load replay file", e, "Error");
      logger.warn("Could not load replay file", e);
    }
  }

  private void loadReplayFromFile(File replayGameFile) throws IOException {
    InputStream in = new FileInputStream(replayGameFile);
    GameReplay replay = gameParser.readReplay(in);

    // Add the replay game to the session
    session.replay = replay;
    session.game = replay.getInitialGame();
    session.map = replay.getInitialGame().getMap();
    session.initialGame = null;

    // Change the game mode, so the IN_GAME state handles this game as a replay game
    App.changeGameMode(App.GAME_MODE.REPLAY);
    stateChanger.changeTo("IN_GAME");
  }
}
