package com.customwars.client.ui.state;

import com.customwars.client.App;
import com.customwars.client.controller.PlayerOptionsController;
import com.customwars.client.io.loading.ThinglePageLoader;
import com.customwars.client.model.co.CO;
import com.customwars.client.model.game.Player;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import com.customwars.client.tools.ThingleUtil;
import com.customwars.client.ui.COSheet;
import com.customwars.client.ui.state.input.CWCommand;
import com.customwars.client.ui.state.input.CWInput;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.thingle.Page;
import org.newdawn.slick.thingle.Thingle;
import org.newdawn.slick.thingle.Widget;
import org.newdawn.slick.thingle.internal.slick.ImageWrapper;
import org.newdawn.slick.thingle.spi.ThingleColor;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

/**
 * Allows the user to choose a CO, team and color for each player
 */
public class PlayerOptionsState extends CWState {
  private Page page;
  private PlayerOptionsController controller;
  private Map<Tile> map;
  private Image backgroundImg;

  @Override
  public void init(GameContainer container, StateBasedGame game) throws SlickException {
    controller = new PlayerOptionsController(stateChanger, stateSession, resources);
    backgroundImg = resources.getSlickImg("light_menu_background");
    initPage();
  }

  private void initPage() {
    ThinglePageLoader thingleLoader = new ThinglePageLoader(App.get("gui.path"));
    page = thingleLoader.loadPage("PlayerOptions.xml", "greySkin.properties", controller);
  }

  @Override
  public void enter(GameContainer container, StateBasedGame stateBasedGame) throws SlickException {
    super.enter(container, stateBasedGame);
    controller.enter();

    if (this.map != stateSession.map) {
      this.map = stateSession.map;
      stateSession.setPlayerDefaults();
    }

    buildPage();
    page.enable();
  }

  private void buildPage() {
    Widget centeredPanel = page.getWidget("centerPanel");
    centeredPanel.removeChildren();

    Widget header = createHeaderRow();
    centeredPanel.add(header);

    int row = 0;
    for (Player player : map.getUniquePlayers()) {
      Widget playerRow = createPlayerRow(player, row++);
      centeredPanel.add(playerRow);
    }
    page.layout();
  }

  // todo this header is aligned using spaces!

  private Widget createHeaderRow() {
    Widget panel = page.createWidget("panel");
    panel.setInteger("gap", 20);

    Widget lblController = page.createWidget("label");
    lblController.setText("Controller");
    lblController.setChoice("alignment", "center");
    panel.add(lblController);

    Widget lblCo = page.createWidget("label");
    lblCo.setText("   CO ");
    lblCo.setChoice("alignment", "center");
    panel.add(lblCo);

    Widget lblTeam = page.createWidget("label");
    lblTeam.setText(" Team");
    lblTeam.setChoice("alignment", "center");
    panel.add(lblTeam);

    Widget lblColor = page.createWidget("label");
    lblColor.setText("Color");
    lblColor.setChoice("alignment", "center");
    panel.add(lblColor);
    return panel;
  }

  private Widget createPlayerRow(Player player, int row) {
    Widget panel = page.createWidget("panel");
    panel.setInteger("gap", 20);

    Widget cboControllerType = page.createWidget("combobox");
    cboControllerType.setString("name", "controller" + row);
    cboControllerType.setProperty("row", row);
    cboControllerType.setMethod("action", "controllerTypeChanged(this)", controller);
    cboControllerType.setBoolean("editable", false);

    List<String> translatedControllerValues = Arrays.asList(App.translate("ai"), App.translate("human"));
    ThingleUtil.fillCbo(page, cboControllerType, translatedControllerValues, Arrays.asList("ai", "human"));
    ThingleUtil.selectChild(cboControllerType, "human");
    panel.add(cboControllerType);

    CO co = stateSession.getCO(player);
    COSheet coSheet = resources.getCOSheet(co);
    Widget btnCO = page.createWidget("button");
    btnCO.setString("name", "co" + row);
    btnCO.setProperty("row", row);
    btnCO.setString("tooltip", App.translate(co.getName()));
    btnCO.setIcon(new ImageWrapper(coSheet.getLeftHead(3)));
    btnCO.setMethod("action", "selectCO(this)", controller);
    panel.add(btnCO);

    Widget cboTeams = page.createWidget("combobox");
    cboTeams.setString("name", "team" + row);
    cboTeams.setProperty("row", row);
    cboTeams.setMethod("action", "teamChanged(this)", controller);
    cboTeams.setBoolean("editable", false);
    ThingleUtil.fillCboWithNumbers(page, cboTeams, 1, 6, 1);
    ThingleUtil.selectChild(cboTeams, stateSession.getTeam(player) + "");
    panel.add(cboTeams);

    Widget cboColor = page.createWidget("combobox");
    cboColor.setString("name", "color" + row);
    cboColor.setProperty("row", row);
    cboColor.setMethod("action", "colorChanged(this)", controller);
    cboColor.setBoolean("editable", false);
    fillWithColors(cboColor, row);

    // Select color combobox child by background color
    ThingleUtil.selectChild(cboColor, "background", stateSession.getColor(player));
    panel.add(cboColor);
    return panel;
  }

  private void fillWithColors(Widget cboColor, int row) {
    for (Color color : resources.getSupportedColors()) {
      Widget colorChoice = createCboColorChoice(color);
      colorChoice.setProperty("row", row);
      cboColor.add(colorChoice);
    }
  }

  private Widget createCboColorChoice(Color color) {
    Widget colorChoice = page.createWidget("choice");
    colorChoice.setText("");
    ThingleColor background = Thingle.createColor(color.getRed(), color.getGreen(), color.getBlue());
    colorChoice.setColor("background", background);
    return colorChoice;
  }

  @Override
  public void leave(GameContainer container, StateBasedGame stateBasedGame) throws SlickException {
    super.leave(container, stateBasedGame);
    page.disable();
  }

  @Override
  public void render(GameContainer container, Graphics g) throws SlickException {
    g.drawImage(backgroundImg, 0, 0);
    page.render();
  }

  @Override
  public void update(GameContainer container, int delta) throws SlickException {
  }

  @Override
  public void controlPressed(CWCommand command, CWInput cwInput) {
    if (command == CWInput.CANCEL) {
      controller.back();
    }
  }

  @Override
  public int getID() {
    return 17;
  }
}
