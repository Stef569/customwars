package com.customwars.client.controller;

import com.customwars.client.App;
import com.customwars.client.io.ResourceManager;
import com.customwars.client.model.co.CO;
import com.customwars.client.ui.COSheet;
import com.customwars.client.ui.GUI;
import com.customwars.client.ui.state.StateChanger;
import com.customwars.client.ui.state.StateSession;
import org.newdawn.slick.thingle.Page;
import org.newdawn.slick.thingle.Widget;
import org.newdawn.slick.thingle.internal.slick.ImageWrapper;
import org.newdawn.slick.thingle.spi.ThingleColor;

import java.awt.Color;

/**
 * Handle user input in the Player option state:
 * <lu>
 * <li>The Controller type of a player Human or AI</li>
 * <li>The team</li>
 * <li>The CO</li>
 * <li>The color</li>
 * </lu>
 */
public class PlayerOptionsController {
  private static final int MAX_NUM_TEAMS = 20;
  private final StateChanger stateChanger;
  private final StateSession stateSession;
  private final ResourceManager resources;

  /**
   * The last button pressed to select a CO
   */
  private Widget coButton;
  private Page page;

  public PlayerOptionsController(StateChanger stateChanger, StateSession stateSession, ResourceManager resources) {
    this.stateChanger = stateChanger;
    this.stateSession = stateSession;
    this.resources = resources;
  }

  public void init(Page page) {
    this.page = page;
  }

  public void enter() {
    CO selectedCO = stateSession.selectedCO;

    // If the user selected a CO in the CO select state
    // update the state session
    //  the CO button
    if (selectedCO != null) {
      int playerRow = getPlayerRow(coButton);
      stateSession.setCO(selectedCO, playerRow);

      COSheet coSheet = resources.getCOSheet(selectedCO);
      coButton.setIcon(new ImageWrapper(coSheet.getLeftHead(3)));
      stateSession.selectedCO = null;
    }
  }

  public void selectCO(Widget coButton) {
    this.coButton = coButton;
    stateChanger.changeTo("CO_SELECT");
  }

  public void teamChanged(Widget cboTeam) {
    int currentPlayerRow = getPlayerRow(cboTeam);
    int team = Integer.parseInt(cboTeam.getText());
    stateSession.setTeam(team, currentPlayerRow);
  }

  public void colorChanged(Widget cboColor) {
    int currentPlayerRow = getPlayerRow(cboColor);
    ThingleColor color = cboColor.getChild(cboColor.getSelectedIndex()).getColor("background");
    cboColor.setColor("background", color);
    stateSession.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue()), currentPlayerRow);
  }

  public void controllerTypeChanged(Widget cboControllerType) {
    int currentPlayerRow = getPlayerRow(cboControllerType);
    String controllerType = cboControllerType.getText();
    stateSession.setControllerType(controllerType, currentPlayerRow);
  }

  private int getPlayerRow(Widget widget) {
    return (Integer) widget.getProperty("row");
  }

  public void back() {
    stateChanger.changeToPrevious();
  }

  public void continueToNextState() {
    if (validInput()) {
      stateChanger.changeTo("GAME_RULES");
    }
  }

  private boolean validInput() {
    if (!atLeast2Teams()) {
      GUI.showErrDialog(App.translate("gui_teams_same_msg"), App.translate("gui_teams_same_title"));
      return false;
    } else if (!colorsAreUnique()) {
      GUI.showErrDialog(App.translate("gui_colors_unique_msg"), App.translate("gui_colors_unique_title"));
      return false;
    }
    return true;
  }

  /**
   * Check if there are at least 2 unique teams.
   * Teams should be in the inclusive range [1-19]
   * Teams start at 1. There is no team 0.
   *
   * @return true if there are at least 2 different teams
   */
  private boolean atLeast2Teams() {
    int teamCount[] = countTeams();

    int numTeams = 0;
    for (int team = 1; team < MAX_NUM_TEAMS; team++) {
      if (teamCount[team] > 0) {
        numTeams++;
      }
    }

    return numTeams >= 2;
  }

  private int[] countTeams() {
    int rows = stateSession.map.getUniquePlayers().size();
    int[] teamCount = new int[MAX_NUM_TEAMS];

    for (int team = 0; team < MAX_NUM_TEAMS; team++) {
      for (int row = 0; row < rows; row++) {
        if (stateSession.getTeam(row) == team) {
          teamCount[team]++;
        }
      }
    }
    return teamCount;
  }

  /**
   * Checks if each player has an unique color.
   * If however all CO colors are used up allow any color to be used.
   *
   * @return true if each color of each player is unique
   */
  private boolean colorsAreUnique() {
    if (stateSession.hasMoreCOsThenStyles()) return true;

    int rows = stateSession.map.getUniquePlayers().size();

    for (int row = 0; row < rows; row++) {
      int colorCount = getColorCount(stateSession.getColor(row));
      if (colorCount > 1) {
        return false;
      }
    }
    return true;
  }

  private int getColorCount(Color color) {
    int colorCount = 0;
    int rows = stateSession.map.getUniquePlayers().size();
    for (int row = 0; row < rows; row++) {
      Color colorOnRow = stateSession.getColor(row);
      if (colorOnRow.equals(color)) {
        colorCount++;
      }
    }
    return colorCount;
  }
}
