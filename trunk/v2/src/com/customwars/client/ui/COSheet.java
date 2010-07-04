package com.customwars.client.ui;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

/**
 * A CO sheet is an image that contains different CO Images.
 * There is 1 body, 4 heads and 4 torso's. This class handles the cutting of sub images from the CO image.
 * And hides where these parts are located inside the co image.
 */
public class COSheet extends Image {
  private static final int HEAD_SIZE = 48;
  private static final int TORSO_WIDTH = 75;
  private static final int TORSO_HEIGHT = 89;
  private static final int BODY_HEIGHT = 350;
  private static final int BODY_WIDTH = 225;
  private final Image bodyImg;
  private final Image[] heads = new Image[4];
  private final Image[] torso = new Image[4];

  public COSheet(String ref) throws SlickException {
    super(ref);
    bodyImg = getSubImage(0, 0, BODY_WIDTH, BODY_HEIGHT);
    cutImages();
  }

  public COSheet(Image img) {
    super(img);
    bodyImg = getSubImage(0, 0, BODY_WIDTH, BODY_HEIGHT);
    cutImages();
  }

  private void cutImages() {
    for (int i = 0; i < 4; i++) {
      heads[i] = getSubImage(i * HEAD_SIZE, BODY_HEIGHT, HEAD_SIZE, HEAD_SIZE);
      torso[i] = getSubImage(i * TORSO_WIDTH, BODY_HEIGHT + HEAD_SIZE, TORSO_WIDTH, TORSO_HEIGHT);
    }

    // The last head is much smaller then the normal heads
    Image lastHead = heads[3];
    heads[3] = lastHead.getSubImage(0, 0, 32, 12);
  }

  public Image getLeftBodyImg() {
    return bodyImg;
  }

  public Image getRightBody() {
    return bodyImg.getFlippedCopy(true, false);
  }

  public Image getLeftHead(int index) {
    return heads[index];
  }

  public Image getRightHead(int index) {
    return heads[index].getFlippedCopy(true, false);
  }

  public Image getLeftTorso(int index) {
    return torso[index];
  }

  public Image getRightTorso(int index) {
    return torso[index].getFlippedCopy(true, false);
  }
}
