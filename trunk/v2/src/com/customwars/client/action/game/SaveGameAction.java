package com.customwars.client.action.game;

import com.customwars.client.App;
import com.customwars.client.action.DirectAction;
import com.customwars.client.io.loading.BinaryCW2GameParser;
import com.customwars.client.model.game.Game;
import com.customwars.client.tools.IOUtil;
import com.customwars.client.ui.GUI;
import com.customwars.client.ui.state.InGameContext;
import org.apache.log4j.Logger;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Save the current game as a binary file to the SAVE_PATH
 * A Dialog is shown in case of success and error.
 */
public class SaveGameAction extends DirectAction {
  private static final Logger logger = Logger.getLogger(SaveGameAction.class);
  private static final String SAVE_PATH = App.get("save.path") + App.get("quick.save.game.file.name");
  private BinaryCW2GameParser gameParser;
  private Game game;

  public SaveGameAction() {
    super("Save Game", false);
  }

  @Override
  protected void init(InGameContext inGameContext) {
    gameParser = new BinaryCW2GameParser();
    game = inGameContext.getObj(Game.class);
  }

  @Override
  protected void invokeAction() {
    OutputStream out = null;

    try {
      out = saveGame();
      logger.info("Game saved to " + SAVE_PATH);
      GUI.showdialog("The Game has been saved", "Success!");
    } catch (IOException ex) {
      logger.warn("Could not save game", ex);
      GUI.showExceptionDialog("Could not save the game", ex, "Save error");
    } finally {
      IOUtil.closeStream(out);
    }
  }

  private OutputStream saveGame() throws IOException {
    OutputStream out = new FileOutputStream(SAVE_PATH);
    gameParser.writeGame(game, out);
    return out;
  }
}
