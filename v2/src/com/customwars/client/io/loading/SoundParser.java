package com.customwars.client.io.loading;

import static com.customwars.client.io.ErrConstants.ERR_READING_LINE;
import static com.customwars.client.io.ErrConstants.ERR_WRONG_NUM_ARGS;
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import tools.IOUtil;
import tools.StringUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * Parse a text command into a function that will create a sound or music object
 *
 * @author stefan
 */
public class SoundParser {
  private final static String COMMENT_PREFIX = "//";
  private static final char SOUND_SYMBOL = 's';
  private static final char MUSIC_SYMBOL = 'm';
  private HashMap<String, Sound> sounds;
  private HashMap<String, Music> music;
  private String fullSoundPath;

  public SoundParser(HashMap<String, Sound> sounds, HashMap<String, Music> music) {
    this.sounds = sounds;
    this.music = music;
  }

  public void loadConfigFile(InputStream in) throws IOException {
    String line;

    BufferedReader br = new BufferedReader(new InputStreamReader(in));

    try {
      while ((line = br.readLine()) != null) {
        if (line.length() == 0)
          continue;
        if (line.startsWith(COMMENT_PREFIX))
          continue;
        parseCmd(line);
      }
    } finally {
      IOUtil.closeStream(in);
    }
  }

  public void parseCmd(String line) throws IOException {
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
      String soundPath = fullSoundPath + tokens.nextToken();
      sounds.put(soundName, new Sound(soundPath));
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
      String musicPath = fullSoundPath + tokens.nextToken();
      music.put(musicName, new Music(musicPath));
    }
  }

  public void setSoundPath(String fullSoundPath) {
    fullSoundPath = StringUtil.appendTrailingSuffix(fullSoundPath, '/');
    this.fullSoundPath = fullSoundPath;
  }
}
