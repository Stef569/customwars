package com.customwars.client.ui.state;

import com.customwars.client.App;
import com.customwars.client.Config;
import com.customwars.client.SFX;
import com.customwars.client.io.ResourceManager;
import com.customwars.client.ui.GUI;
import com.customwars.client.ui.slick.CWStateBasedGame;
import com.customwars.client.ui.state.input.CWCommand;
import com.customwars.client.ui.state.menu.MainMenuState;
import com.customwars.client.ui.state.menu.OptionMenuState;
import com.customwars.client.ui.state.menu.SinglePlayerMenuState;
import org.apache.log4j.Logger;
import tools.IOUtil;

import java.io.IOException;
import java.util.Properties;

/**
 * Create each State and link it to a name
 *
 * @author stefan
 */
public class CWStates extends CWStateBasedGame {
  private static final Logger logger = Logger.getLogger(CWStates.class);

  public CWStates(String startStateName, ResourceManager resources, Config config) {
    super(App.get("game.name") + " - " + App.get("plugin.name"),
      startStateName, resources, config);
  }

  public void initStatesList() {
    buildStateList();
  }

  private void buildStateList() {
    addState("STARTUP", new StartupState());
    addState("MAIN_MENU", new MainMenuState());
    addState("KEY_MENU", new OptionMenuState());
    addState("SINGLE_PLAYER", new SinglePlayerMenuState());
    addState("IN_GAME", new InGameState());
    addState("END_TURN", new EndTurnState());
    addState("GAME_OVER", new GameOverState());
    addState("REMAP_CONTROLS", new ControlBindingState());
    addState("MAP_EDITOR", new MapEditorState());
  }

  protected void handleGlobalInput(CWCommand command) {
    switch (command.getEnum()) {
      case EXIT:
        logger.info("Exit pressed");
        gameContainer.exit();
        break;
      case TOGGLE_MUSIC:
        SFX.toggleMusic();
        break;
      case TOGGLE_CONSOLE:
        GUI.toggleConsoleFrame();
        break;
      case TOGGLE_EVENT_VIEWER:
        GUI.toggleEventFrame();
        break;
      case TOGGLE_FPS:
        gameContainer.setShowFPS(!gameContainer.isShowingFPS());
        break;
    }
  }

  public void shutDownHook() {
    Properties userProperties = App.getUserProperties();

    try {
      String userPropertiesPath = App.get("userproperties.path");
      IOUtil.storePropertyFile(userProperties, userPropertiesPath);
    } catch (IOException e) {
      logger.warn("Could not save user properties");
    }
  }
}
