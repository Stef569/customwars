package com.customwars.client.ui.state.menu;

import com.customwars.client.App;
import com.customwars.client.ui.GUI;
import com.customwars.client.ui.PopupMenu;
import com.customwars.client.ui.StandardMenuItem;
import com.customwars.client.ui.state.CWState;
import com.customwars.client.ui.state.input.CWCommand;
import com.customwars.client.ui.state.input.CWInput;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.gui.AbstractComponent;
import org.newdawn.slick.gui.ComponentListener;
import org.newdawn.slick.state.StateBasedGame;

import java.awt.Point;

/**
 * Allow the user to create/join or log in to a server game
 */
public class MultiPlayerMenuState extends CWState implements ComponentListener {
  private PopupMenu mainMenu;
  private int windowHeight;

  public void init(GameContainer container, StateBasedGame game) throws SlickException {
    windowHeight = container.getHeight();
    mainMenu = new PopupMenu(container);
    mainMenu.setBackGroundColor(new Color(0, 0, 0));
    mainMenu.setHoverColor(new Color(255, 255, 255, 0.08f));
    Font font = resources.getFont("menu");

    mainMenu.addItems(
      new StandardMenuItem(App.translate("create_server_game"), font, container),
      new StandardMenuItem(App.translate("join_server_game"), font, container),
      new StandardMenuItem(App.translate("log_into_server_game"), font, container),
      new StandardMenuItem(App.translate("back"), font, container)
    );

    mainMenu.addListener(this);
    mainMenu.setAcceptingInput(false);
    mainMenu.init();
    Point center = GUI.getCenteredRenderPoint(mainMenu.getSize(), container);
    mainMenu.setLocation(center.x + 45, center.y - 60);
  }

  @Override
  public void render(GameContainer container, Graphics g) throws SlickException {
    g.drawImage(resources.getSlickImg("dark_menu_background"), 0, 0);
    mainMenu.render(container, g);

    g.setColor(Color.lightGray);
    switch (mainMenu.getCurrentItem()) {
      case 0:
        g.drawString("Create new server game", 210, windowHeight - 40);
        break;
      case 1:
        g.drawString("Join existing server game", 210, windowHeight - 40);
        break;
      case 2:
        g.drawString("Log into existing server game", 210, windowHeight - 40);
        break;
      case 3:
        g.drawString("Back to previous menu", 210, windowHeight - 40);
        break;
    }
  }

  @Override
  public void update(GameContainer container, int delta) throws SlickException {
  }

  @Override
  public void enter(GameContainer container, StateBasedGame game) throws SlickException {
    super.enter(container, game);
    mainMenu.setAcceptingInput(true);
    App.changeGameMode(App.GAME_MODE.NETWORK_SNAIL_GAME);
  }

  @Override
  public void leave(GameContainer container, StateBasedGame game) throws SlickException {
    super.leave(container, game);
    mainMenu.setAcceptingInput(false);
  }

  public void controlPressed(CWCommand command, CWInput cwInput) {
    if (command == CWInput.CANCEL) {
      stateChanger.changeToPrevious();
      return;
    }

    mainMenu.controlPressed(command);
  }

  public void componentActivated(AbstractComponent source) {
    PopupMenu popupMenu = (PopupMenu) source;
    switch (popupMenu.getCurrentItem()) {
      case 0:
        changeToState("CREATE_SERVER_GAME");
        break;
      case 1:
        changeToState("JOIN_SERVER_GAME");
        break;
      case 2:
        changeToState("LOGIN_SERVER_GAME");
        break;
      case 3:
        changeToPreviousState();
        break;
    }
  }

  @Override
  public int getID() {
    return 20;
  }
}
