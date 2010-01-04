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
  protected void init(InGameContext context) {
    gameParser = new BinaryCW2GameParser();
    game = context.getGame();
  }

  @Override
  protected void invokeAction() {
    OutputStream out = null;

    try {
      out = saveGame(out);
      logger.info("Game saved to " + SAVE_PATH);
      GUI.showdialog("The Game has been saved", "Success!");
    } catch (IOException e) {
      logger.warn("Could not save game", e);
      GUI.showExceptionDialog("Could not save the game", e, "Save error");
    } finally {
      IOUtil.closeStream(out);
    }
  }

  private OutputStream saveGame(OutputStream out) throws IOException {
    out = new FileOutputStream(SAVE_PATH);
    gameParser.writeGame(game, out);
    return out;
  }
}
