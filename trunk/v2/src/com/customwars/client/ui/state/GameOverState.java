package com.customwars.client.ui.state;

import com.customwars.client.App;
import com.customwars.client.action.game.SaveReplayAction;
import com.customwars.client.io.loading.ThinglePageLoader;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.game.Player;
import com.customwars.client.network.NetworkException;
import com.customwars.client.network.NetworkManager;
import com.customwars.client.network.NetworkManagerSingleton;
import com.customwars.client.ui.GUI;
import org.apache.log4j.Logger;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.thingle.Page;
import org.newdawn.slick.thingle.Widget;

import javax.swing.JOptionPane;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Show the statistics as a table
 * In SP and MP mode:
 * The continue button takes the user back to the MAIN_MENU and the session is cleared.
 *
 * In MP Snail mode:
 * Send a game over message to the server
 */
public class GameOverState extends CWState {
  private static final Logger logger = Logger.getLogger(GameOverState.class);
  private static final int COLUMN_WIDTH = 100;
  private Page page;
  private List<Player> players;
  private Game game;
  private NetworkManager networkManager;

  public void init(GameContainer gameContainer, StateBasedGame stateBasedGame) throws SlickException {
    ThinglePageLoader loader = new ThinglePageLoader(App.get("gui.path"));
    this.page = loader.loadPage("gameOver.xml", "greySkin.properties", this);
    networkManager = NetworkManagerSingleton.getInstance();
  }

  @Override
  public void enter(GameContainer container, StateBasedGame stateBasedGame) throws SlickException {
    super.enter(container, stateBasedGame);
    game = stateSession.game;
    buildPlayers();
    buildTable();
    page.enable();
    logger.info("Game Over");

    if (App.isMultiplayerSnailGame() && game.isGameOver()) {
      endServerGame();
    }

    if (App.getBoolean("recordreplay")) {
      if (GUI.showConfirmationDialog("Save replay", "save") == JOptionPane.YES_OPTION) {
        new SaveReplayAction(stateSession.replay).invoke(null);
      }
    }
  }

  private void endServerGame() {
    App.execute(new Runnable() {
      public void run() {
        sendEndGameToServer();
      }
    });
  }

  private void sendEndGameToServer() {
    String serverGameName = stateSession.serverGameName;
    String userName = stateSession.user.getName();
    String userPassword = stateSession.user.getPassword();

    try {
      networkManager.endGame(serverGameName, userName, userPassword);
    } catch (NetworkException ex) {
      logger.warn("Could not end game", ex);
      GUI.showExceptionDialog("Could not end game", ex);
    }
  }

  private void buildPlayers() {
    this.players = new ArrayList<Player>();
    for (Player player : game.getAllPlayers()) {
      players.add(player);
    }
  }

  private void buildTable() {
    Widget table = page.getWidget("table");
    table.removeChildren(); // Remove previously build table
    table.add(buildHeader());

    Widget[] rows = createRows();
    addFirstColumn(rows);
    addColumn(rows);

    for (Widget row : rows) {
      table.add(row);
    }
  }

  private Widget buildHeader() {
    Widget header = page.createWidget("header");
    Widget firstColumn = createColumn("Statistics");
    header.add(firstColumn);

    for (Player p : players) {
      Widget column = createColumn(p.getName());
      header.add(column);
    }
    return header;
  }

  private Widget createColumn(String value) {
    Widget column = page.createWidget("column");
    column.setText(value);
    column.setInteger("width", COLUMN_WIDTH);
    column.setChoice("alignment", "center");
    return column;
  }

  private Widget[] createRows() {
    int rowCount = game.getPlayerStats(players.get(0)).size();
    Widget[] rows = new Widget[rowCount];

    for (int i = 0; i < rows.length; i++) {
      Widget row = page.createWidget("row");
      rows[i] = row;
    }
    return rows;
  }

  private void addFirstColumn(Widget[] rows) {
    Map<String, String> playerStats = game.getPlayerStats(players.get(0));
    int rowIndex = 0;

    for (String statKey : playerStats.keySet()) {
      Widget cell = createCell(statKey);
      rows[rowIndex++].add(cell);
    }
  }

  private void addColumn(Widget[] rows) {
    for (Player player : players) {
      int rowIndex = 0;
      Map<String, String> stats = game.getPlayerStats(player);

      for (Map.Entry<String, String> entry : stats.entrySet()) {
        Widget cell = createCell(entry.getValue());
        rows[rowIndex].add(cell);
        rowIndex++;
      }
    }
  }

  private Widget createCell(String value) {
    Widget cell = page.createWidget("cell");
    cell.setText(value);
    cell.setChoice("alignment", "center");
    return cell;
  }

  @Override
  public void leave(GameContainer container, StateBasedGame game) throws SlickException {
    super.leave(container, game);
    page.disable();
  }

  public void render(GameContainer container, Graphics g) throws SlickException {
    page.render();
  }

  public void update(GameContainer container, int delta) throws SlickException {
  }

  public void continueToNextState() {
    stateSession.clear();
    stateChanger.resumeRecordingStateHistory();
    changeToState("MAIN_MENU");
  }

  public int getID() {
    return 16;
  }
}
