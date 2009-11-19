package com.customwars.client.ui.mapMaker;

import com.customwars.client.io.ResourceManager;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.CityFactory;
import org.newdawn.slick.Image;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.gui.GUIContext;

import java.awt.Color;

public class CitySelectPanel extends SelectPanel {
  private ResourceManager resources;
  private SpriteSheet citySpriteSheet;

  public CitySelectPanel(GUIContext container) {
    super(container);
  }

  @Override
  public void loadResources(ResourceManager resources) {
    this.resources = resources;
  }

  @Override
  public void recolor(Color color) {
    citySpriteSheet = resources.getCitySpriteSheet(color);
    buildComponent();
  }

  private void buildComponent() {
    clear();
    for (City city : CityFactory.getAllCities()) {
      Image cityImage = citySpriteSheet.getSubImage(1, city.getID());
      add(cityImage);
    }

    // Init makes sure that getHeight() returns the correct height.
    init();
    setLocation(0, container.getHeight() - getHeight());
  }
}