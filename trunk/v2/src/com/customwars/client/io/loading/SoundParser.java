package com.customwars.client.io.loading;

import static com.customwars.client.io.ErrConstants.ERR_READING_LINE;
import static com.customwars.client.io.ErrConstants.ERR_WRONG_NUM_ARGS;
import com.customwars.client.io.ResourceManager;
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.util.ResourceLoader;

import java.util.StringTokenizer;

/**
 * Parse a text line into a function that will create a sound or music object
 *
 * @author stefan
 */
public class SoundParser extends LineParser {
  private static final char SOUND_SYMBOL = 's';
  private static final char MUSIC_SYMBOL = 'm';
  private final String soundPath;
  private final ResourceManager resources;

  public SoundParser(ResourceManager resources, String soundPath, String soundLoaderFileName) {
    super(ResourceLoader.getResourceAsStream(soundPath + soundLoaderFileName));
    this.resources = resources;
    this.soundPath = soundPath;
  }

  public void parseLine(String line) {
    char ch = Character.toLowerCase(line.charAt(0));

    try {
      if (ch == SOUND_SYMBOL) {
        loadSound(line);
      } else if (ch == MUSIC_SYMBOL) {
        loadMusic(line);
      } else
        throw new IllegalArgumentException(ERR_READING_LINE + " " + line + ", unknown Symbol: " + ch +
          " use " + SOUND_SYMBOL + " for sounds and " + MUSIC_SYMBOL + " for music");
    } catch (SlickException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * format:
   * S <soundName> <fileName>
   */
  private void loadSound(String line) throws SlickException {
    StringTokenizer tokens = new StringTokenizer(line);

    if (tokens.countTokens() != 3)
      throw new IllegalArgumentException(ERR_WRONG_NUM_ARGS + " for line: " + line +
        " Usage " + SOUND_SYMBOL + " <soundName> <fileName>");
    else {
      tokens.nextToken();    // skip command label
      String soundName = tokens.nextToken().toUpperCase();
      String soundPath = this.soundPath + tokens.nextToken();
      resources.addSound(soundName, new Sound(soundPath));
    }
  }

  /**
   * format:
   * M <musicName> <fileName>
   */
  private void loadMusic(String line) throws SlickException {
    StringTokenizer tokens = new StringTokenizer(line);

    if (tokens.countTokens() != 3)
      throw new IllegalArgumentException(ERR_WRONG_NUM_ARGS + " for line: " + line +
        " Usage " + MUSIC_SYMBOL + " <musicName> <fileName>");
    else {
      tokens.nextToken();    // skip command label
      String musicName = tokens.nextToken().toUpperCase();
      String musicPath = soundPath + tokens.nextToken();
      resources.addMusic(musicName, new Music(musicPath));
    }
  }
}
