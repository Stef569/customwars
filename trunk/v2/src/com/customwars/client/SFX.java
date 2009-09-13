package com.customwars.client;

import com.customwars.client.io.ResourceManager;
import org.apache.log4j.Logger;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Music;
import org.newdawn.slick.Sound;

/**
 * Handles Sounds and Music
 *
 * @author stefan
 */
public class SFX {
  private static final Logger logger = Logger.getLogger(SFX.class);
  private static ResourceManager resources;
  private static GameContainer container;

  /**
   * Pause or resume the music depending on it's current state
   */
  public static void toggleMusic(Music music) {
    if (music.playing()) {
      music.pause();
    } else {
      music.resume();
    }
  }

  /**
   * Play a sound once
   *
   * @param soundName The sound to play as defined in the ResourceManager
   */
  public static void playSound(String soundName) {
    Sound s = resources.getSound(soundName);
    if (s != null) {
      s.play();
    } else {
      logger.warn("No sound for " + soundName);
    }
  }

  /**
   * Play music, Only one piece of music can play at any given time
   * if another music piece is already playing it is stopped
   *
   * @param musicName The music to play as defined in the ResourceManager
   */
  public void playMusic(String musicName) {
    Music music = resources.getMusic(musicName);
    if (music != null) {
      music.play();
    } else {
      logger.warn("No music for " + musicName);
    }
  }

  public static void toggleMusic() {
    container.setMusicOn(!container.isMusicOn());
  }

  public void mute() {
    container.setSoundOn(false);
  }

  public void resume() {
    container.setSoundOn(true);
  }

  public static void setResources(ResourceManager resources) {
    SFX.resources = resources;
  }

  public static void setGameContainer(GameContainer gameContainer) {
    SFX.container = gameContainer;
  }
}
