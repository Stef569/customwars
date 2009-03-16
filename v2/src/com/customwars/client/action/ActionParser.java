package com.customwars.client.action;

import com.customwars.client.action.unit.SelectAction;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.TileMap;

import java.util.Scanner;

/**
 * Parse a string into an Action
 *
 * @author stefan
 */
public class ActionParser {
  private ActionManager actionManager;
  private TileMap map;

  public ActionParser(ActionManager actionManager, TileMap map) {
    this.actionManager = actionManager;
    this.map = map;
  }

  public CWAction parse(String str) {
    String cmd = str.trim().toLowerCase();
    Scanner scanner = new Scanner(cmd);

    String actionName = scanner.next();
    if (actionName.equals("SELECT_UNIT")) {
      return parseSelect(scanner);
    }
    return null;
  }

  private CWAction parseSelect(Scanner scanner) {
    int col = scanner.nextInt();
    int row = scanner.nextInt();
    Location selectTile = map.getTile(col, row);

    SelectAction action = (SelectAction) actionManager.getAction("SELECT_UNIT");
    return action;
  }
}
