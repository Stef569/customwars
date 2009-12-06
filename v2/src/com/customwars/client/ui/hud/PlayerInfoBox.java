package com.customwars.client.ui.hud;

import com.customwars.client.App;
import com.customwars.client.model.game.Game;
import com.customwars.client.ui.slick.BasicComponent;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.gui.GUIContext;

public class PlayerInfoBox extends BasicComponent {
  private final Game game;

  public PlayerInfoBox(GUIContext container, Game game) {
    super(container);
    this.game = game;
  }

  @Override
  public void renderimpl(GUIContext container, Graphics g) {
    if (game != null) {
      String day = App.translate("day") + ':' + game.getDay();
      String player = App.translate("player") + ':' + game.getActivePlayer().getName();
      String money = App.translate("money") + ':' + game.getActivePlayer().getBudget();

      g.drawString("     " + day + ' ' + player + ' ' + money, getX(), getY());
    }
  }
}
