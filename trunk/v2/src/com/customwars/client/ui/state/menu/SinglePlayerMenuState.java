package com.customwars.client.ui.state.menu;

import com.customwars.client.App;
import com.customwars.client.action.CWAction;
import com.customwars.client.action.game.LoadReplayAction;
import com.customwars.client.ui.state.CWState;
import com.customwars.client.ui.state.input.CWCommand;
import com.customwars.client.ui.state.input.CWInput;
import com.customwars.client.ui.thingle.MenuListener;
import com.customwars.client.ui.thingle.ThingleMenu;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public class SinglePlayerMenuState extends CWState {
  private Image backGroundImage;
  private int windowHeight;
  private ThingleMenu mainMenu;

  public void init(GameContainer container, StateBasedGame game) throws SlickException {
    windowHeight = container.getHeight();
    backGroundImage = resources.getSlickImg("dark_menu_background");
    Font font = resources.getFont("menu");
    String guiPath = App.get("gui.path");

    mainMenu = new ThingleMenu(guiPath, "singlePlayerMenu.xml", new MenuListener() {
      public void selected(int selectedIndex) {
        switch (selectedIndex) {
          case 0:
            changeToState("MAP_SELECT");
            break;
          case 1:
            CWAction loadReplayAction = new LoadReplayAction(stateSession, stateChanger);
            loadReplayAction.invoke(null);
            break;
          case 2:
            // Clear stored user data(map, co, ...) from session when going to previous state
            stateSession.clear();
            changeToPreviousState();
            break;
        }
      }
    });
    mainMenu.setFont(font);
    mainMenu.translateItems();
  }

  @Override
  public void enter(GameContainer container, StateBasedGame game) throws SlickException {
    super.enter(container, game);
    mainMenu.enable();
    App.changeGameMode(App.GAME_MODE.SINGLE_PLAYER);
  }

  @Override
  public void update(GameContainer container, int delta) throws SlickException {
  }

  @Override
  public void render(GameContainer container, Graphics g) throws SlickException {
    g.drawImage(backGroundImage, 0, 0);
    mainMenu.render();

    g.setColor(Color.lightGray);
    switch (mainMenu.getSelectedIndex()) {
      case 0:
        g.drawString("New Game", 210, windowHeight - 40);
        break;
      case 1:
        g.drawString("Load replay", 210, windowHeight - 40);
        break;
      case 2:
        g.drawString("Go back to main menu", 210, windowHeight - 40);
        break;
    }
  }

  @Override
  public void leave(GameContainer container, StateBasedGame game) throws SlickException {
    super.leave(container, game);
    mainMenu.disable();
  }

  @Override
  public void controlPressed(CWCommand command, CWInput cwInput) {
    if (command == CWInput.CANCEL) {
      changeToPreviousState();
    }
  }

  @Override
  public void keyPressed(int key, char c) {
    mainMenu.keyPressed(key);
  }

  @Override
  public int getID() {
    return 10;
  }
}
