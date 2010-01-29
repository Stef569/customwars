package slick;

import com.customwars.client.App;
import com.customwars.client.Config;
import com.customwars.client.SFX;
import com.customwars.client.io.ResourceManager;
import com.customwars.client.model.game.Game;
import com.customwars.client.tools.StringUtil;
import com.customwars.client.ui.GUI;
import com.customwars.client.ui.slick.CWStateBasedGame;
import com.customwars.client.ui.state.ControlBindingState;
import com.customwars.client.ui.state.EndTurnState;
import com.customwars.client.ui.state.GameOverState;
import com.customwars.client.ui.state.InGameState;
import com.customwars.client.ui.state.MapEditorState;
import com.customwars.client.ui.state.input.CWCommand;
import org.apache.log4j.Logger;
import org.newdawn.slick.Input;

public class TestStates extends CWStateBasedGame {
  private static final Logger logger = Logger.getLogger(TestStates.class);

  public TestStates(String startStateName, ResourceManager resources, Config config) {
    super(
      App.get("game.name") + " - Slick tests",
      StringUtil.hasContent(startStateName) ? startStateName : "MAIN_MENU",
      resources, config);
  }

  public void initStatesList() {
    buildStateList();
    loadResources();
    initTestMode();
  }

  private void buildStateList() {
    addState("MAIN_MENU", new TestMenu());
    addState("terrainmenu", new TestMapRenderer());
    addState("keymenu", new ControlBindingState());
    addState("IN_GAME", new InGameState());
    addState("END_TURN", new EndTurnState());
    addState("MAP_PARSER", new TestMapParser());
    addState("GAME_OVER", new GameOverState());
    addState("MAP_EDITOR", new MapEditorState());
  }

  /**
   * Put a test game on the session
   * This allows to skip all the menus when startState="IN_GAME"
   * Because the ingame state grabs the game/map from the session
   */
  private void initTestMode() {
    logger.info("Init debug Mode");
    Game game = HardCodedGame.getGame();
    stateSession.game = game;
    stateSession.map = game.getMap();
  }

  @Override
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

  @Override
  public void keyPressed(int key, char c) {
    super.keyPressed(key, c);
    if (key == Input.KEY_ENTER) {
      changeToState("MAIN_MenU");
    }
  }

  @Override
  public void shutDownHook() {
  }
}