package com.customwars.client.model.co;

import java.awt.Color;
import java.io.Serializable;

/**
 * The visual style for a CO. Like: Blue Moon, Orange Star, Intelligent Defense Systems (IDS)‎
 */
public class COStyle implements Serializable {
  private final String name;
  private final Color color;
  private final int id;
  private final String colorname;

  public COStyle(String name, Color color, int id, String colorName) {
    this.name = name;
    this.color = color;
    this.id = id;
    this.colorname = colorName;
  }

  public String getName() {
    return name;
  }

  public int getID() {
    return id;
  }

  /**
   * Retrieves the color of this style used for rendering.
   * For example Blue moon -> Color(5,5,255)
   */
  public Color getColor() {
    return color;
  }

  /**
   * Retrieves the name of the color of this style
   * For example Blue moon -> "Blue"
   */
  public String getColorName() {
    return colorname;
  }
}
