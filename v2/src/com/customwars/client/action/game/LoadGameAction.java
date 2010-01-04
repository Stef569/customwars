package com.customwars.client.action.game;

import com.customwars.client.App;
import com.customwars.client.action.DirectAction;
import com.customwars.client.io.loading.BinaryCW2GameParser;
import com.customwars.client.model.game.Game;
import com.customwars.client.ui.GUI;
import com.customwars.client.ui.state.InGameContext;
import com.customwars.client.ui.state.StateChanger;
import com.customwars.client.ui.state.StateSession;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Load a previously saved game from the file system located at the SAVE_PATH
 * A Dialog is shown in case of an error.
 */
public class LoadGameAction extends DirectAction {
  private static final Logger logger = Logger.getLogger(LoadGameAction.class);
  private static final String SAVE_PATH = App.get("save.path") + App.get("quick.save.game.file.name");
  private StateSession session;
  private File quickSaveGameFile;
  private BinaryCW2GameParser gameParser;
  private StateChanger stateChanger;

  public LoadGameAction() {
    super("Load game", false);
  }

  @Override
  protected void init(InGameContext context) {
    stateChanger = context.getStateChanger();
    session = context.getSession();
    quickSaveGameFile = new File(SAVE_PATH);
    gameParser = new BinaryCW2GameParser();
  }

  @Override
  protected void invokeAction() {
    if (quickSaveGameFile.exists()) {
      try {
        loadGame();
      } catch (IOException e) {
        GUI.showExceptionDialog("Could not load quick save game", e, "Error");
        logger.warn("Could not load quick save game", e);
      }
    } else {
      logger.info("No quick save game previously saved");
    }
  }

  private void loadGame() throws IOException {
    // Load the game
    InputStream fileInputStream = new FileInputStream(quickSaveGameFile);
    Game quickSaveGame = gameParser.readGame(fileInputStream);

    // Add the game to the session
    session.game = quickSaveGame;
    session.map = quickSaveGame.getMap();

    // Change the game mode, Allowing the IN_GAME state to handle this game
    // as a saved game
    App.changeGameMode(App.GAME_MODE.LOAD_SAVED_GAME);
    stateChanger.changeTo("IN_GAME");
  }
}
