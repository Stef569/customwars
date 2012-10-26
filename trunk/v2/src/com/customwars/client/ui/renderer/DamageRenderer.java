package com.customwars.client.ui.renderer;

import com.customwars.client.model.fight.Attacker;
import com.customwars.client.model.fight.Fight;
import com.customwars.client.model.fight.FightFactory;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Map;
import com.customwars.client.ui.GUI;
import com.customwars.client.ui.layout.TextBox;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;

import java.awt.Insets;
import java.awt.Point;

/**
 * Render the damage percentage that an Attacker can do against a Defender.
 * Near the defender location.
 */
public class DamageRenderer implements Renderable {
  private static final Color BOX_BACKGROUND_COLOR = new Color(0, 0, 0, 0.4f);

  /**
   * The margin in pixels around the text
   */
  private static final int BOX_MARGIN = 2;

  /**
   * The offset in pixels the damage percentage is rendered relative to the defender location
   */
  private static final int OFFSET = 64;

  // The position of a centered map
  private final Point center;
  private final Location defenderLocation;
  private final TextBox dmgTextBox;
  private static Font textFont;

  public DamageRenderer(Map map, Attacker attacker, Point center, Location defenderLocation) {
    this.center = center;
    this.defenderLocation = defenderLocation;
    Fight fight = FightFactory.createFight(map, attacker, defenderLocation);
    String dmgPercentage = "Damage:" + fight.getBasicAttackDamagePercentage() + "%";

    Insets insets = new Insets(BOX_MARGIN, BOX_MARGIN, BOX_MARGIN, BOX_MARGIN);
    dmgTextBox = new TextBox(dmgPercentage, textFont, insets);
    positionTextBox(map);
  }

  private void positionTextBox(Map map) {
    int tileSize = map.getTileSize();
    int defenderInMapX = defenderLocation.getCol() * tileSize + tileSize / 2;
    int defenderInMapY = defenderLocation.getRow() * tileSize + tileSize / 2;

    // Add the center position and the offset
    int boxWorldX = center.x + defenderInMapX + OFFSET;
    int boxWorldY = center.y + defenderInMapY - OFFSET;

    // Make sure that the damage percentage fits to the screen
    if (!GUI.canFitToScreen(boxWorldX, boxWorldY, dmgTextBox.getWidth(), dmgTextBox.getHeight())) {
      Point boxScreenCoordinate = GUI.worldToScreenCoordinate(boxWorldX, boxWorldY);

      if (boxScreenCoordinate.x + dmgTextBox.getWidth() > GUI.getScreenWidth()) {
        boxWorldX -= dmgTextBox.getWidth() + 2 * OFFSET;
      } else if (boxScreenCoordinate.x < 0) {
        boxWorldX += dmgTextBox.getWidth() + 2 * OFFSET;
      }

      if (boxScreenCoordinate.y + dmgTextBox.getHeight() > GUI.getScreenHeight()) {
        boxWorldY -= dmgTextBox.getHeight() + 2 * OFFSET;
      } else if (boxScreenCoordinate.y < 0) {
        boxWorldY += dmgTextBox.getHeight() + 2 * OFFSET;
      }

    }

    // Graphics are auto centered, subtract center position
    dmgTextBox.setLocation(boxWorldX-center.x, boxWorldY-center.y);
  }

  @Override
  public void render(Graphics g) {
    renderBackground(g);
    dmgTextBox.render(g);
  }

  private void renderBackground(Graphics g) {
    Color prevColor = g.getColor();
    g.setColor(BOX_BACKGROUND_COLOR);
    g.fillRoundRect(dmgTextBox.getX(), dmgTextBox.getY(), dmgTextBox.getWidth(), dmgTextBox.getHeight(), 2);
    g.setColor(prevColor);
  }

  protected static void setTextFont(Font font) {
    DamageRenderer.textFont = font;
  }

  public boolean isShowingDamageFor(Location cursorLocation) {
    return defenderLocation.equals(cursorLocation);
  }
}
