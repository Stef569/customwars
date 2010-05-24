package com.customwars.client.model.co;

import java.awt.Color;
import java.io.Serializable;

/**
 * The visual style for a CO.
 */
public class COStyle implements Serializable {
  private final String name;
  private final Color color;
  private final int id;

  public COStyle(String name, Color color, int id) {
    this.name = name;
    this.color = color;
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public int getID() {
    return id;
  }

  public Color getColor() {
    return color;
  }
}
