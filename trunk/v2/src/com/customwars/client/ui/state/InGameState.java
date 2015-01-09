package com.customwars.client.ui.state;

import com.customwars.client.App;
import com.customwars.client.SFX;
import com.customwars.client.controller.ControllerManager;
import com.customwars.client.controller.CursorController;
import com.customwars.client.controller.InGameCursorController;
import com.customwars.client.controller.InGameInputHandler;
import com.customwars.client.controller.ReplayInputHandler;
import com.customwars.client.controller.UserInGameInputHandler;
import com.customwars.client.io.ResourceManager;
import com.customwars.client.model.CWGameController;
import com.customwars.client.model.GameController;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.game.GameReplay;
import com.customwars.client.model.game.Player;
import com.customwars.client.model.map.Direction;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import com.customwars.client.model.map.TileMap;
import com.customwars.client.model.map.path.MoveTraverse;
import com.customwars.client.network.MessageSender;
import com.customwars.client.network.MessageSenderFactory;
import com.customwars.client.script.BeanShell;
import com.customwars.client.tools.ColorUtil;
import com.customwars.client.ui.Camera2D;
import com.customwars.client.ui.GUI;
import com.customwars.client.ui.HUD;
import com.customwars.client.ui.renderer.GameRenderer;
import com.customwars.client.ui.renderer.MapRenderer;
import com.customwars.client.ui.sprite.TileSprite;
import com.customwars.client.ui.state.input.CWCommand;
import com.customwars.client.ui.state.input.CWInput;
import com.customwars.client.ui.state.input.CommandEnum;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.gui.GUIContext;
import org.newdawn.slick.state.StateBasedGame;

import java.awt.Dimension;
import java.awt.Point;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * In this state the user can play and win a game against opponents
 * Rendering is handled by GameRenderer and hud
 * Input is handled by InGameInputHandler
 * <p/>
 * The Game to display is read from the session when entering this state.
 * This class listens for game events and when the game is over changes to the GAME_OVER State
 */
public class InGameState extends CWState implements PropertyChangeListener {
  // Model
  private Game game;
  private Map map;
  private boolean gameOver;
  private InGameContext inGameContext;

  // GUI
  private GUIContext guiContext;
  private HUD hud;
  private Camera2D camera;
  private GameRenderer gameRenderer;
  private Point center;

  // Control
  private InGameInputHandler inputHandler;
  private InGameCursorController cursorControl;
  private Input input;
  private boolean inputAllowed;

  public void init(GameContainer container, StateBasedGame stateBasedGame) throws SlickException {
    this.guiContext = container;
    this.input = guiContext.getInput();
  }

  @Override
  public void enter(GameContainer container, StateBasedGame stateBasedGame) throws SlickException {
    super.enter(container, stateBasedGame);

    switch (App.getGameMode()) {
      case SINGLE_PLAYER:
        enterSinglePlayerMode(stateSession.game);
        break;
      case NETWORK_SNAIL_GAME:
        enterMultiPlayerMode(stateSession.game);
        break;
      case LOAD_SAVED_GAME:
        initSubSystems(stateSession.game);
        App.changeGameMode(App.GAME_MODE.SINGLE_PLAYER);
        break;
      case REPLAY:
        enterReplayMode(stateSession.game);
        break;
    }

    hud.moveOverTile(gameRenderer.getCursorLocation());
    stateChanger.clearPreviousStatesHistory();
    stateChanger.stopRecordingStateHistory();
    cursorControl.addListener(this);
  }

  /**
   * In SP mode the game is started once and
   * game sub systems(gui, ingamecontext) are inited once.
   */
  private void enterSinglePlayerMode(Game game) {
    if (!game.isStarted() && !game.isGameOver()) {
      game.startGame();
      initSubSystems(game);
      stateSession.initialGame = new Game(game);
    }
  }

  /**
   * In MP mode the game is not started and
   * game sub systems(gui, ingamecontext) are inited each time when the user enters this state
   */
  private void enterMultiPlayerMode(Game game) {
    initSubSystems(game);
  }

  private void enterReplayMode(Game game) {
    initSubSystems(game);
    createReplayController();
  }

  private void createReplayController() {
    inputHandler = new ReplayInputHandler(stateSession.replay, inGameContext);
    inGameContext.registerObj(InGameInputHandler.class, inputHandler);
  }

  private void initSubSystems(Game game) {
    initGame(game);
    initGameContext(game, guiContext);
    initScriptObjects();
  }

  private void initGame(Game game) {
    initGameListener(game);
    this.game = game;
    this.map = game.getMap();
    initCamera(map);
    GUI.setGame(game);
    center = GUI.getCenteredRenderPoint(map.getSize(), guiContext);
  }

  private void initGameContext(Game game, GUIContext container) {
    MoveTraverse moveTraverse = new MoveTraverse(map);
    hud = new HUD(container);

    gameRenderer = new GameRenderer(game, camera, hud, center, moveTraverse);
    gameRenderer.loadResources(resources);

    inGameContext = new InGameContext();
    inGameContext.registerObj(MoveTraverse.class, moveTraverse);
    inGameContext.registerObj(Game.class, game);
    inGameContext.registerObj(ResourceManager.class, resources);
    inGameContext.registerObj(GUIContext.class, container);
    inGameContext.registerObj(HUD.class, hud);
    inGameContext.registerObj(GameRenderer.class, gameRenderer);
    inGameContext.registerObj(MapRenderer.class, gameRenderer.getMapRenderer());
    inGameContext.registerObj(StateChanger.class, stateChanger);
    inGameContext.registerObj(StateSession.class, stateSession);
    inGameContext.registerObj(MessageSender.class, MessageSenderFactory.getInstance().createMessageSender());

    ControllerManager controllerManager = new ControllerManager(inGameContext);
    inGameContext.registerObj(ControllerManager.class, controllerManager);
    inGameContext.registerObj(GameController.class, new CWGameController(game, controllerManager));

    cursorControl = new InGameCursorController(game, gameRenderer.getMapRenderer().getSpriteManager());
    inGameContext.registerObj(CursorController.class, cursorControl);

    inputHandler = new UserInGameInputHandler(inGameContext);
    inGameContext.registerObj(InGameInputHandler.class, inputHandler);

    controllerManager.initCityControllers();
    controllerManager.initUnitControllers();
    initCursors(map);
  }

  /**
   * Add objects to beanshell, accessible by their name
   */
  private void initScriptObjects() {
    BeanShell bsh = BeanShell.get();
    for (Player p : game.getAllPlayers()) {
      String colorName = ColorUtil.toString(p.getColor());
      bsh.set("p_" + colorName, p);
    }

    bsh.set("game", game);
    bsh.set("map", map);
  }

  private void initGameListener(Game game) {
    if (this.game != null) {
      this.game.removePropertyChangeListener(this);
    }
    game.addPropertyChangeListener(this);
  }

  private void initCamera(TileMap<Tile> map) {
    Dimension screenSize = new Dimension(guiContext.getWidth(), guiContext.getHeight());
    Dimension worldSize = new Dimension(map.getWidth(), map.getHeight());
    this.camera = new Camera2D(screenSize, worldSize, map.getTileSize());
    boolean zoomEnabled = App.getBoolean("display.zoom");
    camera.setZoomingEnabled(zoomEnabled);
    GUI.setCamera(camera);
  }

  private void initCursors(Map map) {
    TileSprite selectCursor = resources.createCursor(map, "SELECT");
    TileSprite attackCursor = resources.createCursor(map, "ATTACK");
    TileSprite siloCursor = resources.createCursor(map, "SILO");
    TileSprite hammerCursor = resources.createCursor(map, "HAMMER");
    TileSprite cancelCursor = resources.createCursor(map, "CANCEL");

    cursorControl.addCursor("SELECT", selectCursor);
    cursorControl.addCursor("ATTACK", attackCursor);
    cursorControl.addCursor("SILO", siloCursor);
    cursorControl.addCursor("HAMMER", hammerCursor);
    cursorControl.addCursor("CANCEL", cancelCursor);

    cursorControl.activateCursor("SELECT");
  }

  @Override
  public void leave(GameContainer container, StateBasedGame stateBasedGame) throws SlickException {
    super.leave(container, stateBasedGame);
    cwInput.resetInputTransform();
    cursorControl.removeListener(this);

    if (gameOver) {
      removeScriptObjects();
      gameOver = false;
    }
  }

  private void removeScriptObjects() {
    BeanShell bsh = BeanShell.get();
    for (Player p : game.getAllPlayers()) {
      String colorName = ColorUtil.toString(p.getColor());
      bsh.unset("p_" + colorName);
    }

    bsh.unset("game");
    bsh.unset("map");
  }

  @Override
  public void update(GameContainer container, int delta) throws SlickException {
    if (entered) {
      gameRenderer.update(delta);
      gameRenderer.setRenderAttackDamage(inGameContext.isUnitAttackMode());

      inGameContext.update(delta);
      if (gameOver && isInputAllowed()) {
        gameOver();
      }

      inputAllowed = true;
      if (GUI.isRenderingDialog()) {
        inputAllowed = false;
        input.setOffset(0, 0);
      } else if (hud.isRenderingAbsolutePopup()) {
        input.setOffset(0, 0);
      } else {
        input.setOffset(camera.getX() - center.x, camera.getY() - center.y);
      }
    }
  }

  private void gameOver() {
    storeReplayActions();
    inGameContext.clearQueuedActions();
    changeToState("GAME_OVER");
    inGameContext = null;
    gameOver = false;
  }

  private void storeReplayActions() {
    GameReplay gameReplay = stateSession.replay;
    if (gameReplay != null) {
      gameReplay.addActions(inGameContext.getExecutedActions());
    }
  }

  @Override
  public void render(GameContainer container, Graphics g) throws SlickException {
    if (entered) {
      g.translate(center.x, center.y);
      gameRenderer.render(g);
      g.resetTransform();
      hud.renderAbsolute(g);
    }
  }

  @Override
  public void controlPressed(CWCommand command, CWInput cwInput) {
    if (isInputAllowed()) {
      if (inGameContext.canUndo() && command == CWInput.CANCEL) {
        inputHandler.undo();
        return;
      }

      if (inGameContext.isGUIMode()) {
        hud.controlPressed(command);
      } else {
        if (command.isMoveCommand()) {
          moveCursor(command);
        }

        Tile cursorLocation = gameRenderer.getCursorLocation();
        switch (command.getEnum()) {
          case SELECT:
            inputHandler.handleA(cursorLocation);
            break;
          case CANCEL:
            inputHandler.handleB(cursorLocation);
            break;
          case END_TURN:
            inputHandler.endTurn();
            break;
          case ZOOM_IN:
            camera.zoomIn();
            break;
          case ZOOM_OUT:
            camera.zoomOut();
            break;
          case UNIT_CYCLE:
            inputHandler.startUnitCycle();
            break;
        }
      }
    }
  }

  @Override
  public void mousePressed(int button, int x, int y) {
    if (isInputAllowed()) {
      Tile cursorLocation = gameRenderer.getCursorLocation();

      if (button == Input.MOUSE_LEFT_BUTTON) {
        if (hud.isPopupVisible()) {
          if (!hud.isWithinPopupMenu(x, y)) {
            inputHandler.undo();
            input.consumeEvent();
          }
        } else {
          int mouseX = cwInput.getMouseX();
          int mouseY = cwInput.getMouseY();
          Tile mouseTile = map.pixelsToTile(mouseX, mouseY);

          if (cursorLocation.equals(mouseTile)) {
            inputHandler.handleA(cursorLocation);
          } else {
            inputHandler.undo();
          }
          input.consumeEvent();
        }
      } else if (button == Input.MOUSE_RIGHT_BUTTON) {
        if (inGameContext.canUndo()) {
          inputHandler.undo();
        } else {
          inputHandler.handleB(cursorLocation);
        }
      }
    }
  }

  @Override
  public void controlReleased(CWCommand command, CWInput cwInput) {
    if (isInputAllowed()) {
      if (command.isMoveCommand()) {
        cursorControl.moveControlReleased();
      }
    }
  }

  public void moveCursor(CWCommand command) {
    Location oldLocation = gameRenderer.getCursorLocation();
    moveCursor(command.getEnum());
    Location newLocation = gameRenderer.getCursorLocation();
    Direction moveDirection = map.getDirectionTo(oldLocation, newLocation);
    inputHandler.cursorMoved(oldLocation, newLocation, moveDirection);
  }

  private void moveCursor(CommandEnum command) {
    switch (command) {
      case UP:
        cursorControl.moveCursor(Direction.NORTH);
        break;
      case DOWN:
        cursorControl.moveCursor(Direction.SOUTH);
        break;
      case LEFT:
        cursorControl.moveCursor(Direction.WEST);
        break;
      case RIGHT:
        cursorControl.moveCursor(Direction.EAST);
        break;
    }
  }

  /**
   * Input is allowed when all animations and actions are finished
   *
   * @return If any input is allowed.
   */
  private boolean isInputAllowed() {
    return entered && gameRenderer != null && gameRenderer.isDyingUnitAnimationCompleted() &&
      inGameContext != null && inGameContext.isActionCompleted() && inputAllowed;
  }

  public void mouseWheelMoved(int newValue) {
    if (isInputAllowed()) {
      if (newValue > 0) {
        camera.zoomIn();
      } else {
        camera.zoomOut();
      }
    }
  }

  @Override
  public void mouseMoved(int oldx, int oldy, int newx, int newy) {
    if (isInputAllowed()) {
      Location oldLocation = gameRenderer.getCursorLocation();
      cursorControl.moveCursor(newx, newy);
      Location newLocation = gameRenderer.getCursorLocation();
      Direction moveDirection = map.getDirectionTo(oldLocation, newLocation);
      inputHandler.cursorMoved(oldLocation, newLocation, moveDirection);
    }
  }

  public void propertyChange(PropertyChangeEvent evt) {
    String propertyName = evt.getPropertyName();

    if (evt.getSource() instanceof Game && propertyName.equals("state")) {
      gameStateChanged(evt);
    } else if (evt.getSource() instanceof TileSprite && propertyName.equals("position")) {
      cursorPositionChanged(evt);
    }
  }

  private void gameStateChanged(PropertyChangeEvent evt) {
    this.gameOver = game.isGameOver();
  }

  private void cursorPositionChanged(PropertyChangeEvent evt) {
    TileSprite cursor = (TileSprite) evt.getSource();
    Tile newCursorLocation = (Tile) cursor.getLocation();
    hud.moveOverTile(newCursorLocation);
  }

  @Override
  public int getID() {
    return 14;
  }
}
