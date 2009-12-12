package com.customwars.client.ui.state;

import com.customwars.client.controller.GameRulesController;
import com.customwars.client.io.loading.ThinglePageLoader;
import com.customwars.client.ui.state.input.CWCommand;
import com.customwars.client.ui.state.input.CWInput;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.thingle.Page;
import org.newdawn.slick.thingle.Widget;

/**
 * Allow the user to define the game rules
 */
public class GameRulesState extends CWState {
  private Page page;
  private Image backGroundImage;
  private GameRulesController controller;

  public void init(GameContainer gameContainer, StateBasedGame stateBasedGame) throws SlickException {
    controller = new GameRulesController(stateChanger, stateSession);
    initPage();
    initPageContent();
    backGroundImage = resources.getSlickImg("light_menu_background");
  }

  private void initPage() {
    ThinglePageLoader thingleLoader = new ThinglePageLoader("res/data/gui/");
    page = thingleLoader.loadPage("gameRules.xml", "greySkin.properties", controller);
    page.setDrawDesktop(false);
  }

  private void initPageContent() {
    Widget cboDays = page.getWidget("day_limit");
    fillCboWithNumbers(cboDays, 5, 99, 1);
    Widget cboFunds = page.getWidget("funds");
    fillCboWithNumbers(cboFunds, 1000, 10000, 1000);
    Widget cboIncome = page.getWidget("income");
    fillCboWithNumbers(cboIncome, 1000, 10000, 1000);
  }

  private void fillCboWithNumbers(Widget cboWidget, int start, int end, int increment) {
    for (int i = start; i < end; i += increment) {
      Widget choice = page.createWidget("choice");
      choice.setText(i + "");
      cboWidget.add(choice);
    }
  }

  @Override
  public void enter(GameContainer container, StateBasedGame game) throws SlickException {
    super.enter(container, game);
    page.enable();
  }

  @Override
  public void leave(GameContainer container, StateBasedGame game) throws SlickException {
    super.leave(container, game);
    page.disable();
  }

  @Override
  public void render(GameContainer container, Graphics g) throws SlickException {
    g.drawImage(backGroundImage, 0, 0);
    page.render();
  }

  @Override
  public void update(GameContainer container, int delta) throws SlickException {
  }

  @Override
  public void controlPressed(CWCommand command, CWInput cwInput) {
    if (command == CWInput.CANCEL) {
      stateChanger.changeToPrevious();
    }
  }

  @Override
  public int getID() {
    return 13;
  }
}
