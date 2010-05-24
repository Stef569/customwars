package com.customwars.client.ui.mapeditor;

import com.customwars.client.io.ResourceManager;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.gameobject.UnitFactory;
import com.customwars.client.model.map.Direction;
import com.customwars.client.model.map.Tile;
import org.newdawn.slick.Image;
import org.newdawn.slick.gui.GUIContext;

import java.awt.Color;

/**
 * Allows a unit to be selected from all the units available in the game
 * Renders a list of available units looking right
 */
public class UnitSelectPanel extends SelectPanel {
  private ResourceManager resources;
  private Color color;

  public UnitSelectPanel(GUIContext container) {
    super(container);
  }

  @Override
  public void loadResources(ResourceManager resources) {
    this.resources = resources;
  }

  public void recolor(Color color) {
    this.color = color;
    buildComponent();
  }

  @Override
  public void select(Tile location) {
    Unit unit = (Unit) location.getLastLocatable();
    int unitIndex = unit.getStats().getID();
    setSelectedIndex(unitIndex);
  }

  @Override
  public boolean canSelect(Tile cursorLocation) {
    return cursorLocation.getLastLocatable() instanceof Unit;
  }

  private void buildComponent() {
    clear();
    for (Unit unit : UnitFactory.getAllUnits()) {
      Image unitImg = resources.getUnitImg(unit, color, Direction.EAST);
      add(unitImg);
    }

    // Init makes sure that getHeight() returns the correct height.
    init();
    setLocation(0, container.getHeight() - getHeight());
  }
}
