package com.customwars.client.action.game;

import com.customwars.client.action.DirectAction;
import com.customwars.client.io.loading.BinaryCW2GameParser;
import com.customwars.client.model.game.GameReplay;
import com.customwars.client.tools.IOUtil;
import com.customwars.client.tools.StringUtil;
import com.customwars.client.ui.GUI;
import com.customwars.client.ui.state.InGameContext;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Save the replay queue and initial game as a binary file to the user chosen location
 * A Dialog is shown in case of success or error.
 */
public class SaveReplayAction extends DirectAction {
  private static final Logger logger = Logger.getLogger(SaveReplayAction.class);
  private final BinaryCW2GameParser gameParser;
  private GameReplay replay;

  public SaveReplayAction() {
    super("Save replay", false);
    gameParser = new BinaryCW2GameParser();
  }

  public SaveReplayAction(GameReplay replay) {
    this();
    this.replay = replay;
  }

  @Override
  protected void init(InGameContext inGameContext) {
    if (inGameContext != null) {
      replay = new GameReplay(inGameContext.getSession().initialGame);
      replay.addActions(inGameContext.getExecutedActions());
    }
  }

  @Override
  protected void invokeAction() {
    File userChosenFile = GUI.browseForFile("Save replay", "Save");

    if (userChosenFile != null) {
      String replayFileName = StringUtil.appendTrailingSuffix(userChosenFile.getPath(), ".replay");
      saveReplay(new File(replayFileName));
    }
  }

  private void saveReplay(File replayFile) {
    OutputStream out = null;

    try {
      out = saveReplayFile(replayFile);
      logger.info("Replay saved to " + replayFile);
      GUI.showdialog("Your replay " + replayFile.getName() + " has been saved\n to " + replayFile.getPath(), "Success!");
    } catch (IOException e) {
      logger.warn("Could not save replay", e);
      GUI.showExceptionDialog("Could not save the replay", e, "Save error");
    } finally {
      IOUtil.closeStream(out);
    }
  }

  private OutputStream saveReplayFile(File replayFile) throws IOException {
    OutputStream out = new FileOutputStream(replayFile);
    gameParser.writeReplay(replay, out);
    return out;
  }
}
