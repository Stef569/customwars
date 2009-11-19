package com.customwars.client.io.loading;

import static com.customwars.client.io.ErrConstants.ERR_WRONG_NUM_ARGS;
import com.customwars.client.io.img.AnimLib;
import com.customwars.client.io.img.ImageLib;
import com.customwars.client.io.img.slick.ImageStrip;
import com.customwars.client.tools.IOUtil;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;
import org.newdawn.slick.SpriteSheet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 * Create animations from a text command, add the resulting animation to the animLib keyed by anim name.
 *
 * @author stefan
 */
public class AnimationParser {
  private final static String COMMENT_PREFIX = "//";
  private final ImageLib imageLib;
  private final AnimLib animLib;

  public AnimationParser(ImageLib imageLib, AnimLib animLib) {
    this.imageLib = imageLib;
    this.animLib = animLib;
  }

  /**
   * @param stream the config stream
   * @throws IOException when the stream could not be read
   */
  public void loadConfigFile(InputStream stream) throws IOException {
    BufferedReader br = new BufferedReader(new InputStreamReader(stream));

    try {
      String line;
      while ((line = br.readLine()) != null) {
        if (line.length() == 0)
          continue;
        if (line.startsWith(COMMENT_PREFIX))
          continue;
        parseAnim(line);
      }
    } finally {
      IOUtil.closeStream(stream);
    }
  }

  /**
   * format:
   * AM AnimName imgName frameDuration firstFrameCol firstFrameRow lastFrameCol lastFrameRow loopForever
   * AS AnimName imgName frameDuration startFrame totalframecount loopForever
   */
  private void parseAnim(String line) throws NumberFormatException {
    StringTokenizer tokens = new StringTokenizer(line);
    Scanner cmdScanner = new Scanner(line);

    if (!(tokens.countTokens() >= 5))
      throw new IllegalArgumentException(ERR_WRONG_NUM_ARGS + " for " + line);
    else {
      char animType = Character.toLowerCase(cmdScanner.next().charAt(0));
      String animName = cmdScanner.next();
      String imgRef = cmdScanner.next();
      int frameDuration = cmdScanner.nextInt();

      Animation anim;
      switch (animType) {
        case 's':
          ImageStrip imageStrip = (ImageStrip) imageLib.getSlickImg(imgRef);
          anim = parseImagestripAnimations(cmdScanner, imageStrip, frameDuration);
          break;
        case 'm':
          SpriteSheet spriteSheet = (SpriteSheet) imageLib.getSlickImg(imgRef);
          anim = parseImageSpriteSheetAnimation(cmdScanner, spriteSheet, frameDuration);
          break;
        default:
          throw new IllegalArgumentException("Don't know about animation type " + animType + " use S(Strip) or M(Matrix) instead, Problem line: " + line);
      }
      animLib.addAnim(animName, anim);
    }
  }

  private Animation parseImagestripAnimations(Scanner cmdScanner, ImageStrip imgStrip, int frameDuration) {
    int firstFrameCol, lastFrameCol;

    if (cmdScanner.hasNextBoolean()) {
      firstFrameCol = 0;
      lastFrameCol = imgStrip.getCols();
    } else {
      firstFrameCol = cmdScanner.nextInt();
      lastFrameCol = cmdScanner.nextInt();
    }
    boolean loop = cmdScanner.nextBoolean();

    List<Image> result = new ArrayList<Image>();
    for (int col = firstFrameCol; col < lastFrameCol && col < imgStrip.getCols(); col++) {
      result.add(imgStrip.getSubImage(col));
    }
    Animation anim = new Animation(result.toArray(new Image[result.size()]), frameDuration);
    anim.setAutoUpdate(false);
    anim.setLooping(loop);
    return anim;
  }

  private Animation parseImageSpriteSheetAnimation(Scanner cmdScanner, SpriteSheet sheet, int frameDuration) {
    int firstFrameCol = cmdScanner.nextInt();
    int firstFrameRow = cmdScanner.nextInt();
    int lastFrameCol = cmdScanner.nextInt();
    int lastFrameRow = cmdScanner.nextInt();
    boolean loop = cmdScanner.nextBoolean();

    return new Animation(sheet, firstFrameCol, firstFrameRow, lastFrameCol, lastFrameRow, true, frameDuration, loop);
  }
}
