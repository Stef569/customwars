package com.customwars.client.model.map;

/**
 * Default implementation of a Location
 * Subclasses should handle locatables on this location
 */
public abstract class AbstractLocation implements Location {
  private static final long serialVersionUID = 1L;
  private final int col, row;

  public AbstractLocation(int col, int row) {
    this.col = col;
    this.row = row;
  }

  public int getCol() {
    return col;
  }

  public int getRow() {
    return row;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof AbstractLocation)) return false;

    AbstractLocation that = (AbstractLocation) o;

    if (col != that.col) return false;
    if (row != that.row) return false;

    return true;
  }

  @Override
  public int hashCode() {
    return 31 * col + row;
  }

  @Override
  public String toString() {
    return "Location{" + getLocationString() + "}";
  }

  public String getLocationString() {
    return col + "," + row;
  }
}
