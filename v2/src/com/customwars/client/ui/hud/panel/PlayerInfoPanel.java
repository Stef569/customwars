package com.customwars.client.ui.hud.panel;

import com.customwars.client.App;
import com.customwars.client.model.game.Game;
import com.customwars.client.ui.layout.Box;
import org.newdawn.slick.Graphics;

public class PlayerInfoPanel extends Box {
  private final Game game;

  public PlayerInfoPanel(Game game) {
    this.game = game;
  }

  @Override
  public void renderImpl(Graphics g) {
    if (game != null) {
      String day = App.translate("day") + ':' + game.getDay();
      String player = App.translate("player") + ':' + game.getActivePlayer().getName();
      String money = App.translate("money") + ':' + game.getActivePlayer().getBudget();

      g.drawString("     " + day + ' ' + player + " $" + money, getX(), getY());
    }
  }
}
