package com.customwars.client.ui.mapeditor;

import com.customwars.client.io.ResourceManager;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.CityFactory;
import org.newdawn.slick.Image;
import org.newdawn.slick.gui.GUIContext;

import java.awt.Color;

public class CitySelectPanel extends SelectPanel {
  private ResourceManager resources;

  public CitySelectPanel(GUIContext container) {
    super(container);
  }

  @Override
  public void loadResources(ResourceManager resources) {
    this.resources = resources;
  }

  @Override
  public void recolor(Color color) {
    buildComponent(color);
  }

  private void buildComponent(Color color) {
    clear();
    for (City city : CityFactory.getAllCities()) {
      Image cityImage = resources.getCityImage(city, 0, color);
      add(cityImage);
    }

    // Init makes sure that getHeight() returns the correct height.
    init();
    setLocation(0, container.getHeight() - getHeight());
  }
}