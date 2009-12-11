package com.customwars.client.io.loading;

import com.customwars.client.ui.slick.ThemeLoader;
import org.newdawn.slick.thingle.Page;
import org.newdawn.slick.thingle.Theme;
import org.newdawn.slick.thingle.spi.ThingleException;

/**
 * Load a Thingle page and skin(optional) from a source dir
 */
public class ThinglePageLoader {
  private final String thingleResourcesDir;

  public ThinglePageLoader(String thingleResourcesDir) {
    this.thingleResourcesDir = thingleResourcesDir;
  }

  public Page loadPage(String xmlDocName, String themeName, Object controller) {
    Page page = loadPage(xmlDocName, controller);
    Theme theme = loadTheme(themeName);
    page.setTheme(theme);
    return page;
  }

  public Page loadPage(String xmlDocName, Object controller) {
    try {
      return new Page(thingleResourcesDir + xmlDocName, controller);
    } catch (ThingleException e) {
      throw new RuntimeException("Could not load Thingle Page", e);
    }
  }

  private Theme loadTheme(String themeName) {
    ThemeLoader themeLoader = new ThemeLoader();
    return themeLoader.load(thingleResourcesDir + themeName);
  }
}
