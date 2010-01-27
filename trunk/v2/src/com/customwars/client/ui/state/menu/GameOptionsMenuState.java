package com.customwars.client.ui.state.menu;

import com.customwars.client.App;
import com.customwars.client.ui.GUI;
import com.customwars.client.ui.MenuItem;
import com.customwars.client.ui.PopupMenu;
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
 * Allow to change game configuration settings
 */
public class GameOptionsMenuState extends CWState implements ComponentListener {
  private PopupMenu mainMenu;

  public void init(GameContainer container, StateBasedGame game) throws SlickException {
    mainMenu = new PopupMenu(container);
    mainMenu.setBackGroundColor(new Color(0, 0, 0));
    mainMenu.setHoverColor(new Color(255, 255, 255, 0.08f));
    Font font = resources.getFont("menu");
    mainMenu.addItems(
      new MenuItem(App.translate("key_configure"), font, container),
      new MenuItem(App.translate("app_options"), font, container),
      new MenuItem(App.translate("back"), font, container)
    );

    mainMenu.addListener(this);
    mainMenu.setAcceptingInput(false);
    mainMenu.init();
    Point center = GUI.getCenteredRenderPoint(mainMenu.getSize(), container);
    mainMenu.setLocation(center.x + 30, center.y - 60);
  }

  @Override
  public void render(GameContainer container, Graphics g) throws SlickException {
    g.drawImage(resources.getSlickImg("dark_menu_background"), 0, 0);
    g.setColor(Color.white);
    mainMenu.render(container, g);

    g.setColor(Color.lightGray);
    switch (mainMenu.getCurrentItem()) {
      case 0:
        g.drawString("Configure the keyboard", 210, 440);
        break;
      case 1:
        g.drawString("Change other options", 210, 440);
        break;
      case 2:
        g.drawString("Go back to menu", 210, 440);
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
  }

  @Override
  public void leave(GameContainer container, StateBasedGame game) throws SlickException {
    super.leave(container, game);
    mainMenu.setAcceptingInput(false);
  }

  @Override
  public void controlPressed(CWCommand command, CWInput cwInput) {
    if (command == CWInput.CANCEL) {
      changeToPreviousState();
      return;
    }

    mainMenu.controlPressed(command);
  }

  public void componentActivated(AbstractComponent source) {
    PopupMenu popupMenu = (PopupMenu) source;
    switch (popupMenu.getCurrentItem()) {
      case 0:
        changeToState("REMAP_CONTROLS");
        break;
      case 1:
        changeToState("OPTIONS");
        break;
      case 2:
        changeToPreviousState();
        break;
    }
  }

  @Override
  public int getID() {
    return 30;
  }
}

