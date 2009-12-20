package com.customwars.client.ui.hud.panel;

import com.customwars.client.model.map.Tile;
import com.customwars.client.ui.Component;

/**
 * A GUI panel that shows Tile information
 */
public interface InfoPanel extends Component {
  public void setTile(Tile tile);
}
