package com.customwars.client.ui.state.menu;

import com.customwars.client.ui.GUI;
import com.customwars.client.ui.MenuItem;
import com.customwars.client.ui.PopupMenu;
import com.customwars.client.ui.state.CWState;
import com.customwars.client.ui.state.input.CWCommand;
import com.customwars.client.ui.state.input.CWInput;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.gui.AbstractComponent;
import org.newdawn.slick.gui.ComponentListener;
import org.newdawn.slick.state.StateBasedGame;

import java.awt.Point;

public class SinglePlayerMenuState extends CWState implements ComponentListener {
  private PopupMenu mainMenu;

  public void init(GameContainer container, StateBasedGame game) throws SlickException {
    mainMenu = new PopupMenu(container);
    mainMenu.setBackGroundColor(new Color(0, 0, 0));
    mainMenu.setHoverColor(new Color(255, 255, 255, 0.20f));
    mainMenu.addItems(
      new MenuItem(resources.getSlickImg("editor"), container),
      new MenuItem(resources.getSlickImg("back"), container)
    );

    mainMenu.addListener(this);
    mainMenu.init();
    Point center = GUI.getCenteredRenderPoint(mainMenu.getSize(), container);
    mainMenu.setLocation(center.x + 20, center.y - 100);
  }

  @Override
  public void render(GameContainer container, Graphics g) throws SlickException {
    mainMenu.render(container, g);

    g.drawImage(resources.getSlickImg("menu"), 0, 0);
    g.setColor(Color.white);
    mainMenu.render(container, g);

    g.setColor(Color.lightGray);
    switch (mainMenu.getCurrentItem()) {
      case 0:
        g.drawString("Save/Load maps with this editor", 210, 440);
        break;
      case 1:
        g.drawString("Go back to menu", 210, 440);
        break;
    }
  }

  @Override
  public void update(GameContainer container, int delta) throws SlickException {

  }

  public void controlPressed(CWCommand command, CWInput cwInput) {
    mainMenu.controlPressed(command);
  }

  public void componentActivated(AbstractComponent source) {
    PopupMenu popupMenu = (PopupMenu) source;
    switch (popupMenu.getCurrentItem()) {
      case 0:
        changeToState("MAP_EDITOR");
        break;
      case 1:
        changeToState("MAIN_MENU");
        break;
    }
  }

  @Override
  public int getID() {
    return 6;
  }
}
