package com.customwars.client.ui.state;

import com.customwars.client.App;
import com.customwars.client.Config;
import com.customwars.client.SFX;
import com.customwars.client.io.ResourceManager;
import com.customwars.client.tools.IOUtil;
import com.customwars.client.ui.GUI;
import com.customwars.client.ui.slick.CWStateBasedGame;
import com.customwars.client.ui.state.input.CWCommand;
import com.customwars.client.ui.state.menu.GameOptionsMenuState;
import com.customwars.client.ui.state.menu.MainMenuState;
import com.customwars.client.ui.state.menu.MultiPlayerMenuState;
import com.customwars.client.ui.state.menu.SinglePlayerMenuState;
import com.customwars.client.ui.state.multiplayer.ServerGameCreateState;
import com.customwars.client.ui.state.multiplayer.ServerGameJoinState;
import com.customwars.client.ui.state.multiplayer.ServerGameLoginState;
import com.customwars.client.ui.state.multiplayer.ServerGameRoomState;
import org.apache.log4j.Logger;

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
    loadResources();
  }

  private void buildStateList() {
    addState("MAIN_MENU", new MainMenuState());
    addState("SINGLE_PLAYER", new SinglePlayerMenuState());
    addState("MULTI_PLAYER", new MultiPlayerMenuState());
    addState("MAP_EDITOR", new MapEditorState());
    addState("OPTIONS_MENU", new GameOptionsMenuState());
    addState("REMAP_CONTROLS", new ControlBindingState());
    addState("OPTIONS", new AppOptionsState());
    addState("MAP_SELECT", new MapSelectState());
    addState("PLAYER_OPTIONS", new PlayerOptionsState());
    addState("CO_SELECT", new COSelectState());
    addState("GAME_RULES", new GameRulesState());
    addState("IN_GAME", new InGameState());
    addState("END_TURN", new EndTurnState());
    addState("GAME_OVER", new GameOverState());
    addState("CREATE_SERVER_GAME", new ServerGameCreateState());
    addState("JOIN_SERVER_GAME", new ServerGameJoinState());
    addState("LOGIN_SERVER_GAME", new ServerGameLoginState());
    addState("SERVER_GAME_ROOM", new ServerGameRoomState());
  }

  protected void handleGlobalInput(CWCommand command) {
    switch (command.getEnum()) {
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
      logger.warn("Could not save user properties", e);
    }
  }
}
