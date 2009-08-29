package com.customwars.client.ui.state.menu;

import com.customwars.client.ui.MenuItem;
import com.customwars.client.ui.PopupMenu;
import com.customwars.client.ui.state.CWInput;
import com.customwars.client.ui.state.CWState;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.command.Command;
import org.newdawn.slick.gui.AbstractComponent;
import org.newdawn.slick.gui.ComponentListener;
import org.newdawn.slick.state.StateBasedGame;

import java.util.Arrays;
import java.util.List;

public class MainMenuState extends CWState implements ComponentListener {
  private PopupMenu mainMenu;

  public void init(GameContainer container, StateBasedGame game) throws SlickException {
    mainMenu = new PopupMenu(container);
    mainMenu.setBackGroundColor(new Color(0, 0, 0));
    mainMenu.setHoverColor(new Color(255, 255, 255, 0.20f));

    List<MenuItem> items = Arrays.asList(
      new MenuItem("Single player", container),
      new MenuItem("Multi player", container),
      new MenuItem("Options", container),
      new MenuItem("Exit", container)
    );

    for (MenuItem item : items) {
      mainMenu.addItem(item);
    }
    mainMenu.addListener(this);
    mainMenu.init();
    mainMenu.setLocation(container.getWidth() / 2 - mainMenu.getWidth() / 2, container.getHeight() / 2 - mainMenu.getHeight() / 2);
  }

  @Override
  public void render(GameContainer container, Graphics g) throws SlickException {
    mainMenu.render(container, g);
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
      // not yet implemented
    }
  }

  @Override
  public int getID() {
    return 2;
  }
}
