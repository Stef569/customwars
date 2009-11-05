package slick;

import com.customwars.client.App;
import com.customwars.client.Config;
import com.customwars.client.SFX;
import com.customwars.client.io.ResourceManager;
import com.customwars.client.model.game.Game;
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
import org.newdawn.slick.command.InputProviderListener;
import tools.StringUtil;

import java.io.IOException;

public class TestStates extends CWStateBasedGame implements InputProviderListener {
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

  private void loadResources() {
    logger.info("Loading resources");
    try {
      resources.loadModel();
      resources.loadResources();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void initTestMode() {
    logger.info("Init debug Mode");
    Game game = HardCodedGame.getGame();
    stateSession.game = game;
    stateSession.map = game.getMap();
  }

  @Override
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
      case TOGGLE_FPS:
        gameContainer.setShowFPS(!gameContainer.isShowingFPS());
        break;
    }
  }

  @Override
  public void keyPressed(int key, char c) {
    if (key == Input.KEY_ENTER) {
      changeToState("MAIN_MenU");
    }
  }

  @Override
  public void shutDownHook() {
  }
}