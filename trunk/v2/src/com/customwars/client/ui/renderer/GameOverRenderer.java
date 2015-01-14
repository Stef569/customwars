package com.customwars.client.ui.renderer;

import com.customwars.client.App;
import com.customwars.client.io.loading.ThinglePageLoader;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.game.GameStatistics;
import com.customwars.client.model.game.Player;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.thingle.Page;
import org.newdawn.slick.thingle.Widget;

/**
 * This Renderer renders a game over screen in the form of a table.
 * The first column contains the translated player statistic keys
 * All other columns contain statistics for a player.
 * <p/>
 * Example:
 * Player1 Player2
 * Units lost    5       8
 * Units killed  0       2
 */
public class GameOverRenderer implements Renderable {
  private static final int COLUMN_WIDTH = 100;
  private Page page;
  private Iterable<Player> players;
  private Player firstPlayer;
  private GameStatistics gameStats;

  public void load(Object controller) {
    ThinglePageLoader loader = new ThinglePageLoader(App.get("gui.path"));
    this.page = loader.loadPage("gameOver.xml", "greySkin.properties", controller);
  }

  public void buildGUI(Game game) {
    this.gameStats = game.getStats();
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

    // The table will always stretch across the screen.
    // Add an empty header to prevent the last column to be stretched.
    Widget emptyColumn = createColumn("");
    header.add(emptyColumn);
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
    int rowCount = gameStats.getStatKeys(firstPlayer.getId()).size();
    Widget[] rows = new Widget[rowCount];

    for (int i = 0; i < rows.length; i++) {
      Widget row = page.createWidget("row");
      rows[i] = row;
    }
    return rows;
  }

  private void addFirstColumn(Widget[] rows) {
    int rowIndex = 0;
    for (String statKey : gameStats.getStatKeys(firstPlayer.getId())) {
      if (!statKey.startsWith("array_")) {
        String translatedKey = App.translate(statKey);
        Widget cell = createCell(translatedKey);
        rows[rowIndex++].add(cell);
      }
    }
  }

  private void addColumn(Widget[] rows) {
    for (Player player : players) {
      int rowIndex = 0;
      for (String statKey : gameStats.getStatKeys(player.getId())) {
        if (!statKey.startsWith("array_")) {
          String value = gameStats.getTextStat(player.getId(), statKey);
          Widget cell = createCell(statKey, value);
          rows[rowIndex++].add(cell);
        }
      }
    }
  }

  private Widget createCell(String key, String value) {
    Widget cell;

    if (canTranslateStatValueOf(key) && !value.equals("")) {
      cell = createCell(App.translate(value));
    } else {
      cell = createCell(value);
    }
    return cell;
  }

  private boolean canTranslateStatValueOf(String key) {
    return "favorite_unit".equals(key);
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
