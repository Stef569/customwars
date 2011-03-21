package slick;

import com.customwars.client.io.img.ImageLib;
import com.customwars.client.io.img.slick.RecolorManager;
import com.customwars.client.io.loading.RecolorDataParser;
import com.customwars.client.tools.ColorUtil;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.util.Log;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Tests:
 * Loading+storing of awt images
 * Recoloring awt images
 * converting to slick images
 */
public class RecolorTest extends BasicGame {
  private static final String IMAGE_FILTER_FILE = "res/plugin/dor/data/colors.xml";
  private static final String UNIT_IMG_PATH = "res/plugin/dor/images/units.png";
  private static final String CITY_IMG_PATH = "res/plugin/dor/images/cities.png";

  // Images
  private ImageLib imageLib;
  private RecolorManager recolorManager;
  private SpriteSheet currentUnitImgStrip;
  private Image currentUnitImg;
  private Image currentCityImg;

  // Recoloring
  private static List<Color> colors;
  private int currentColorPos;

  public RecolorTest() {
    super("recolor test");
  }

  public void init(GameContainer container) throws SlickException {
    imageLib = new ImageLib();
    recolorManager = imageLib.getRecolorManager();

    // Load the image filters, they provide the data needed to recolor images
    // Store the colors they support in colors
    loadImgFilters();
    load();
  }

  private void loadImgFilters() {
    RecolorDataParser imgFilterParser = new RecolorDataParser(imageLib, IMAGE_FILTER_FILE);

    try {
      imgFilterParser.load();
    } catch (IOException e) {
      Log.error("Could not load img filter", e);
    }
    colors = new ArrayList<Color>(recolorManager.getSupportedColors());
  }

  public void load() {
    recolorManager.setBaseRecolorSpriteSheet("unit", UNIT_IMG_PATH, 32, 40);
    recolorManager.setBaseRecolorSpriteSheet("city", CITY_IMG_PATH, 32, 40);

    recolorImages();

    // Retrieve a slick images, case doesn't matter
    currentUnitImgStrip = (SpriteSheet) imageLib.getSlickImg("UNIT_BluE");
  }

  private void recolorImages() {
    recolorManager.recolor("unit", colors);
    recolorManager.recolor("city", colors);
    recolorManager.recolor("unit", "darker", colors, 60);
    recolorManager.recolor("city", "darker", colors, 20);
    imageLib.clearImageSources();
  }

  public void update(GameContainer container, int delta) throws SlickException {
  }

  public void render(GameContainer container, Graphics g) throws SlickException {
    g.setColor(org.newdawn.slick.Color.green.darker());
    g.fillRect(0, 0, container.getWidth(), container.getHeight());
    g.setColor(org.newdawn.slick.Color.white);
    g.drawString("LOADING COMPLETED   scroll to see what has been loaded", 100, 50);

    Color currentColor = colors.get(currentColorPos);
    g.setColor(new org.newdawn.slick.Color(currentColor.getRGB()));
    g.drawString(ColorUtil.toString(currentColor), 50, 70);

    if (currentUnitImg == null) {
      currentUnitImgStrip.getSubImage(0, 1).draw(100, 100);
      currentUnitImgStrip.getSubImage(1, 1).draw(100, 140);
      currentUnitImgStrip.getSubImage(1, 2).draw(100, 180);
    } else {
      g.drawImage(currentUnitImg, 100, 100);
      if (currentCityImg != null) g.drawImage(currentCityImg, 500, 100);
    }
  }

  public void mouseWheelMoved(int newValue) {
    recolor(currentColorPos -= (newValue / 120));
  }

  private void recolor(int i) {
    if (i >= colors.size()) {
      i = 0;
    }

    if (i < 0) {
      i = colors.size() - 1;
    }

    currentColorPos = i;
    recolor();
  }

  private void recolor() {
    Color color = colors.get(currentColorPos);
    String colorName = ColorUtil.toString(color);
    currentUnitImg = imageLib.getSlickImg("UNIT_" + colorName);
    currentCityImg = imageLib.getSlickImg("city_" + colorName);
  }

  public static void main(String[] args) throws SlickException {
    AppGameContainer appGameContainer = new AppGameContainer(new RecolorTest());
    appGameContainer.setDisplayMode(800, 600, false);
    appGameContainer.setTargetFrameRate(60);
    appGameContainer.start();
  }
}