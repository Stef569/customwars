package com.customwars.client.ui.state;

import com.customwars.client.App;
import com.customwars.client.io.loading.ThinglePageLoader;
import com.customwars.client.model.Statistics;
import com.customwars.client.model.game.Player;
import org.apache.log4j.Logger;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.thingle.Page;
import org.newdawn.slick.thingle.Widget;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Show the statistics as a table
 */
public class GameOverState extends CWState {
  private static final Logger logger = Logger.getLogger(GameOverState.class);
  private Page page;
  private List<Player> players;
  private Statistics statistics;

  public void init(GameContainer gameContainer, StateBasedGame stateBasedGame) throws SlickException {
    ThinglePageLoader loader = new ThinglePageLoader(App.get("gui.path"));
    this.page = loader.loadPage("gameOver.xml", "greySkin.properties", this);
  }

  @Override
  public void enter(GameContainer container, StateBasedGame game) throws SlickException {
    super.enter(container, game);
    statistics = stateSession.stats;
    buildPlayers();
    buildTable();
    page.enable();
  }

  private void buildPlayers() {
    this.players = new ArrayList<Player>();
    for (Player player : stateSession.game.getAllPlayers()) {
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
    Widget emptyColumn = page.createWidget("column");
    emptyColumn.setText("Statistics");
    header.add(emptyColumn);

    for (Player p : players) {
      Widget column = page.createWidget("column");
      column.setText(p.getName());
      header.add(column);
    }
    return header;
  }

  private Widget[] createRows() {
    int rowCount = statistics.getPlayerStats(players.get(0)).size();
    Widget[] rows = new Widget[rowCount];

    for (int i = 0; i < rows.length; i++) {
      Widget row = page.createWidget("row");
      rows[i] = row;
    }
    return rows;
  }

  private void addFirstColumn(Widget[] rows) {
    Map<String, String> playerStats = statistics.getPlayerStats(players.get(0));
    int rowIndex = 0;

    for (String statKey : playerStats.keySet()) {
      Widget cell = page.createWidget("cell");
      cell.setText(statKey);
      rows[rowIndex++].add(cell);
    }
  }

  private void addColumn(Widget[] rows) {
    for (Player player : players) {
      int rowIndex = 0;
      Map<String, String> stats = statistics.getPlayerStats(player);

      for (Map.Entry<String, String> entry : stats.entrySet()) {
        Widget cell = page.createWidget("cell");
        cell.setText(entry.getValue());
        rows[rowIndex].add(cell);
        rowIndex++;
      }
    }
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
