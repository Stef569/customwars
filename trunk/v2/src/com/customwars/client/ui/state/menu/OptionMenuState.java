package com.customwars.client.ui.state.menu;

import com.customwars.client.ui.state.CWState;
import com.customwars.client.ui.state.CWInput;
import com.customwars.client.ui.PopupMenu;
import com.customwars.client.ui.MenuItem;
import org.newdawn.slick.gui.ComponentListener;
import org.newdawn.slick.gui.AbstractComponent;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.command.Command;
import org.newdawn.slick.state.StateBasedGame;

public class OptionMenuState extends CWState implements ComponentListener {
  private PopupMenu mainMenu;

  public void init(GameContainer container, StateBasedGame game) throws SlickException {
    mainMenu = new PopupMenu(container);
    mainMenu.setBackGroundColor(new Color(0, 0, 0));
    mainMenu.setHoverColor(new Color(255, 255, 255, 0.20f));
    mainMenu.addItems(
      new MenuItem(resources.getSlickImg("key"), container),
      new MenuItem(resources.getSlickImg("back"), container)
    );

    mainMenu.addListener(this);
    mainMenu.init();
    mainMenu.setLocation((container.getWidth() / 2 - mainMenu.getWidth() / 2)+20,
            (container.getHeight() / 2 - mainMenu.getHeight() / 2)-100);
  }

  @Override
  public void render(GameContainer container, Graphics g) throws SlickException {
    mainMenu.render(container, g);

    g.drawImage(resources.getSlickImg("menu"), 0, 0);
    g.setColor(Color.white);
    //g.drawString("ENTER: TO GO BACK TO MENU", 400, 10);
    mainMenu.render(container, g);

    g.setColor(Color.lightGray);
    switch (mainMenu.getCurrentItem()) {
      case 0:
        g.drawString("Configure the keyboard", 210, 440);
        break;
      case 1:
        g.drawString("Go back to menu", 210, 440);
        break;
    }
  }

  @Override
  public void update(GameContainer container, int delta) throws SlickException {

  }

  public void controlPressed(Command command, CWInput cwInput) {
    mainMenu.controlPressed(command, cwInput);
  }

  public void componentActivated(AbstractComponent source) {
    PopupMenu popupMenu = (PopupMenu) source;
    switch (popupMenu.getCurrentItem()) {
      case 0:
        changeGameState("KEY_MENU");
        break;
      case 1:
        changeGameState("MAIN_MENU");
        break;
    }
  }

  @Override
  public int getID() {
    return 7;
  }
}

