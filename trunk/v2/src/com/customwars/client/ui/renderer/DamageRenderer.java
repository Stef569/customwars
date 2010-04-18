package com.customwars.client.ui.renderer;

import com.customwars.client.model.fight.Attacker;
import com.customwars.client.model.fight.Fight;
import com.customwars.client.model.fight.FightFactory;
import com.customwars.client.model.map.Direction;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import com.customwars.client.ui.GUI;
import com.customwars.client.ui.layout.TextBox;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.Graphics;

import java.awt.Insets;

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

  private final Location defenderLocation;
  private final TextBox dmgTextBox;
  private final Direction quadrant;
  private static Font textFont;

  public DamageRenderer(Map<Tile> map, Attacker attacker, Location defenderLocation) {
    this.defenderLocation = defenderLocation;
    Fight fight = FightFactory.createFight(map, attacker, defenderLocation);
    String dmgPercentage = "Damage:" + fight.getAttackDamagePercentage() + "%";

    Insets insets = new Insets(BOX_MARGIN, BOX_MARGIN, BOX_MARGIN, BOX_MARGIN);
    dmgTextBox = new TextBox(dmgPercentage, textFont, insets);
    positionTextBox(map);
    quadrant = map.getQuadrantFor(defenderLocation);
  }

  private void positionTextBox(Map<Tile> map) {
    int tileSize = map.getTileSize();
    int defenderX = defenderLocation.getCol() * tileSize + tileSize / 2;
    int defenderY = defenderLocation.getRow() * tileSize + 5;
    dmgTextBox.setLocation(defenderX + OFFSET, defenderY - OFFSET);

    // If the damage percentage does not fit to the gui make sure that it does
    // by positioning the dmg percentage to the opposite quadrant as where the defender is located.
    // If the defender is located NORTH EAST in the map show the dmg percentage at SOUTH WEST
    if (!GUI.canFitToScreen(dmgTextBox.getX(), dmgTextBox.getY(), dmgTextBox.getWidth(), dmgTextBox.getHeight())) {
      int boxX, boxY;
      switch (quadrant) {
        case NORTHEAST:
          boxX = dmgTextBox.getX() - OFFSET;
          boxY = dmgTextBox.getY() + OFFSET;
          break;
        case NORTHWEST:
          boxX = dmgTextBox.getX() + OFFSET;
          boxY = dmgTextBox.getY() + OFFSET;
          break;
        case SOUTHEAST:
          boxX = dmgTextBox.getX() - OFFSET;
          boxY = dmgTextBox.getY() - OFFSET;
          break;
        case SOUTHWEST:
          boxX = dmgTextBox.getX() + OFFSET;
          boxY = dmgTextBox.getY() - OFFSET;
          break;
        default:
          throw new AssertionError("Illegal Quadrant " + quadrant + " expected NE,NW,SE or SW");
      }
      dmgTextBox.setLocation(boxX, boxY);
    }
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
