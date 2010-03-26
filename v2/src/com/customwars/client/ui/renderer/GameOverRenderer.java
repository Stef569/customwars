package com.customwars.client.ui.renderer;

import com.customwars.client.App;
import com.customwars.client.io.loading.ThinglePageLoader;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.game.Player;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.thingle.Page;
import org.newdawn.slick.thingle.Widget;

import java.util.Map;

/**
 * This Renderer renders a game over screen in the form of a table.
 * Each column contains statistics for a player in the given game.
 */
public class GameOverRenderer implements Renderable {
  private static final int COLUMN_WIDTH = 100;
  private Page page;
  private Game game;
  private Iterable<Player> players;
  private Player firstPlayer;

  public void load(Object controller) {
    ThinglePageLoader loader = new ThinglePageLoader(App.get("gui.path"));
    this.page = loader.loadPage("gameOver.xml", "greySkin.properties", controller);
  }

  public void buildGUI(Game game) {
    this.game = game;
    this.players = game.getAllPlayers();
    this.firstPlayer = players.iterator().next();
    buildTable();
    page.enable();
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
    int rowCount = game.getPlayerStats(firstPlayer).size();
    Widget[] rows = new Widget[rowCount];

    for (int i = 0; i < rows.length; i++) {
      Widget row = page.createWidget("row");
      rows[i] = row;
    }
    return rows;
  }

  private void addFirstColumn(Widget[] rows) {
    Map<String, String> playerStats = game.getPlayerStats(firstPlayer);
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
  public void render(Graphics g) {
    page.render();
  }

  public void leave() {
    page.disable();
  }
}
