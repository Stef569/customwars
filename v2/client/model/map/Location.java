package client.model.map;

/**
 * @author stefan
 */
public class Location {
  int col, row;

  protected Location(int col, int row) {
    this.col = col;
    this.row = row;
  }

  public int getCol() {
    return col;
  }

  public int getRow() {
    return row;
  }
}
