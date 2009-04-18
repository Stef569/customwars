package com.customwars.client.ui.hud;

import com.customwars.client.App;
import com.customwars.client.model.game.Game;
import com.customwars.client.ui.slick.BasicComponent;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.gui.GUIContext;

public class PlayerInfoBox extends BasicComponent {
  private Game game;

  public PlayerInfoBox(GUIContext container, Game game) {
    super(container);
    this.game = game;
  }

  @Override
  public void renderimpl(GUIContext container, Graphics g) {
    if (game != null) {
      g.drawString(App.getMsg("day") + ":" + game.getDay(), 150, 10);
      g.drawString(App.getMsg("player") + ":" + game.getActivePlayer().getName(), 150, 20);
      g.drawString(App.getMsg("money") + ":" + game.getActivePlayer().getBudget(), 150, 30);
    }
  }
}
