package slick;

import com.customwars.client.SFX;
import com.customwars.client.controller.ControllerManager;
import com.customwars.client.controller.CursorController;
import com.customwars.client.controller.GameController;
import com.customwars.client.io.ResourceManager;
import com.customwars.client.io.img.slick.ImageStrip;
import com.customwars.client.model.Statistics;
import com.customwars.client.model.fight.Fight;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.gameobject.UnitFight;
import com.customwars.client.model.map.Direction;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import com.customwars.client.model.map.path.MoveTraverse;
import com.customwars.client.ui.Camera2D;
import com.customwars.client.ui.HUD;
import com.customwars.client.ui.renderer.GameRenderer;
import com.customwars.client.ui.sprite.TileSprite;
import com.customwars.client.ui.state.CWInput;
import com.customwars.client.ui.state.CWState;
import com.customwars.client.ui.state.InGameContext;
import org.apache.log4j.Logger;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.command.Command;
import org.newdawn.slick.gui.GUIContext;
import org.newdawn.slick.state.StateBasedGame;

import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class TestInGameState extends CWState implements PropertyChangeListener {
  private static final Logger logger = Logger.getLogger(TestInGameState.class);
  private InGameContext context;
  private GUIContext guiContext;
  private GameRenderer gameRenderer;
  private HUD hud;
  private Game game;
  private Fight fight = new UnitFight();
  private boolean gameOver;
  private Camera2D camera;
  private GameController gameControl;
  private CursorController cursorControl;

  public TestInGameState() {
  }

  public void init(GameContainer container, StateBasedGame stateBasedGame) throws SlickException {
    guiContext = container;
  }

  @Override
  public void enter(GameContainer container, StateBasedGame stateBasedGame) throws SlickException {
    super.enter(container, stateBasedGame);
    Game game = stateSession.game;

    if (this.game == null) {
      game.startGame();
      setGame(game, container);
      stateSession.stats = new Statistics(game);
    }
  }

  @Override
  public void leave(GameContainer container, StateBasedGame game) throws SlickException {
    super.leave(container, game);
    guiContext.getInput().setOffset(0, 0);
    guiContext.getInput().setScale(1, 1);
  }

  private void setGame(Game game, GameContainer container) {
    this.game = game;
    initGameListener(game);
    initCamera(game.getMap());
    MoveTraverse moveTraverse = new MoveTraverse(game.getMap());
    hud = new HUD(container);

    gameRenderer = new GameRenderer(game, camera, hud, moveTraverse);
    gameRenderer.loadResources(resources);
    initCursors(resources, game.getMap());

    context = new InGameContext();
    context.setMoveTraverse(moveTraverse);
    context.setGame(game);
    context.setResources(resources);
    context.setContainer(container);
    context.setHud(hud);
    context.setGameRenderer(gameRenderer);

    ControllerManager controllerManager = new ControllerManager(context);
    context.setControllerManager(controllerManager);

    gameControl = gameRenderer.getGameControl();
    gameControl.setInGameContext(context);
    gameControl.setStateLogic(statelogic);

    cursorControl = gameControl.getCursorController();
    context.setGameController(gameControl);

    controllerManager.initCityControllers();
    controllerManager.initUnitControllers();
  }

  private void initCursors(ResourceManager resources, Map<Tile> map) {
    ImageStrip selectCursorImgs = resources.getSlickImgStrip("selectCursor");
    ImageStrip aimCursorImgs = resources.getSlickImgStrip("aimCursor");
    Image siloCursorImg = resources.getSlickImg("siloCursor");

    Tile randomTile = map.getRandomTile();
    TileSprite selectCursor = new TileSprite(selectCursorImgs, 250, randomTile, map);
    TileSprite aimCursor = new TileSprite(aimCursorImgs, randomTile, map);
    TileSprite siloCursor = new TileSprite(siloCursorImg, randomTile, map);

    selectCursor.addPropertyChangeListener(this);
    aimCursor.addPropertyChangeListener(this);
    siloCursor.addPropertyChangeListener(this);

    // Use the silo explosion cursor Image height to calculate the effect Range ie
    // If the image has a height of 160/32 is 5 tiles 5/2 rounded to int becomes 2.
    int effectRange = siloCursorImg.getHeight() / map.getTileSize() / 2;
    siloCursor.setEffectRange(effectRange);

    gameRenderer.addCursor("SELECT", selectCursor);
    gameRenderer.addCursor("ATTACK", aimCursor);
    gameRenderer.addCursor("SILO", siloCursor);
    gameRenderer.activateCursor("SELECT");
  }

  private void initCamera(Map<Tile> map) {
    Dimension screenSize = new Dimension(guiContext.getWidth(), guiContext.getHeight());
    Dimension worldSize = new Dimension(map.getWidth(), map.getHeight());
    this.camera = new Camera2D(screenSize, worldSize, map.getTileSize());
  }

  private void initGameListener(Game game) {
    if (this.game != game && this.game != null) {
      this.game.removePropertyChangeListener(this);
    }

    game.addPropertyChangeListener(this);
  }

  public void update(GameContainer container, int delta) throws SlickException {
    if (gameRenderer != null) {
      gameRenderer.update(delta);

      context.update(delta);
      if (gameOver && context.isActionCompleted()) {
        changeGameState("GAME_OVER");
      }
      container.getInput().setOffset(camera.getX(), camera.getY());
    }
  }

  public void render(GameContainer container, Graphics g) throws SlickException {
    if (gameRenderer != null) {
      gameRenderer.render(g);
      renderAttackDamagePercentage(g);
    }
  }

  private void renderAttackDamagePercentage(Graphics g) {
    if (context.isUnitAttackMode()) {
      Location cursorLocation = gameRenderer.getCursorLocation();
      Unit attacker = game.getActiveUnit();
      Unit defender = game.getMap().getUnitOn(cursorLocation);
      if (defender != null) {
        fight.initFight(attacker, defender);
        int tileSize = game.getMap().getTileSize();
        int x = cursorLocation.getCol() * tileSize;
        int y = cursorLocation.getRow() * tileSize;

        // Draw The damage percentage at the top right of the cursorlocation
        g.drawString("Damage:" + fight.getAttackDamagePercentage() + "", x + 50, y - 50);
      }
    }
  }

  public void controlPressed(Command command, CWInput cwInput) {
    if (gameRenderer != null) {
      if (cwInput.isCancel(command)) {
        if (context.canUndo()) {
          gameControl.undo();
          return;
        }
      }

      moveCursor(command, cwInput);

      if (context.isGUIMode()) {
        hud.controlPressed(command, cwInput);
      } else {
        Tile cursorLocation = gameRenderer.getCursorLocation();
        Unit activeUnit = game.getActiveUnit();
        Unit selectedUnit = game.getMap().getUnitOn(cursorLocation);
        City city = game.getMap().getCityOn(cursorLocation);

        Unit unit;
        if (activeUnit != null) {
          unit = activeUnit;
        } else {
          unit = selectedUnit;
        }

        if (cwInput.isSelect(command)) {
          gameControl.handleA(unit, city, cursorLocation);
        }

        if (cwInput.isCancel(command)) {
          gameControl.handleB(activeUnit, selectedUnit);
        }

        if (cwInput.isEndTurn(command)) {
          gameControl.endTurn(statelogic);
        }

        if (cwInput.isZoomIn(command)) {
          camera.zoomIn();
        } else if (cwInput.isZoomOut(command)) {
          camera.zoomOut();
        }
      }
    }
  }

  public void moveCursor(Command command, CWInput cwInput) {
    if (cwInput.isUp(command)) {
      cursorControl.moveCursor(Direction.NORTH);
    }

    if (cwInput.isDown(command)) {
      cursorControl.moveCursor(Direction.SOUTH);
    }

    if (cwInput.isLeft(command)) {
      cursorControl.moveCursor(Direction.WEST);
    }

    if (cwInput.isRight(command)) {
      cursorControl.moveCursor(Direction.EAST);
    }
  }

  public void mouseWheelMoved(int newValue) {
    if (entered) {
      if (newValue > 0) {
        camera.zoomIn();
      } else {
        camera.zoomOut();
      }
    }
  }

  @Override
  public void mouseMoved(int oldx, int oldy, int newx, int newy) {
    if (gameRenderer != null) {
      cursorControl.moveCursor(newx, newy);
    }
  }

  public int getID() {
    return 3;
  }

  public void propertyChange(PropertyChangeEvent evt) {
    String propertyName = evt.getPropertyName();

    if (propertyName.equals("state")) {
      gameStateChanged();
    } else if (evt.getSource() instanceof TileSprite && propertyName.equals("position")) {
      cursorPositionChanged(evt);
    }
  }

  private void gameStateChanged() {
    if (game.isGameOver()) {
      this.gameOver = true;
    }
  }

  private void cursorPositionChanged(PropertyChangeEvent evt) {
    TileSprite cursor = (TileSprite) evt.getSource();
    SFX.playSound("maptick");
    Tile newCursorLocation = (Tile) cursor.getLocation();
    hud.moveOverTile(newCursorLocation);
  }
}
