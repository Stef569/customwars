package com.customwars.client.ui.hud.panel;

import com.customwars.client.io.ResourceManager;
import com.customwars.client.model.game.Game;
import com.customwars.client.ui.layout.Box;
import com.customwars.client.ui.renderer.COPowerGaugeRenderer;
import org.newdawn.slick.Graphics;

public class PlayerInfoPanel extends Box {
  private final Game game;
  private final COPowerGaugeRenderer coPowerGaugeRenderer;

  public PlayerInfoPanel(Game game, int guiWidth) {
    this.game = game;
    this.coPowerGaugeRenderer = new COPowerGaugeRenderer(game, guiWidth);
  }

  public void loadResources(ResourceManager resources) {
    coPowerGaugeRenderer.loadResources(resources);
  }

  @Override
  public void setAlignment(ALIGNMENT alignment) {
    coPowerGaugeRenderer.setRenderLeftToRight(alignment == ALIGNMENT.LEFT);
  }

  @Override
  public void renderImpl(Graphics g) {
    if (game != null) {
      coPowerGaugeRenderer.render(g);
    }
  }
}