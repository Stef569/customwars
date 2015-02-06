package com.customwars.client;

import com.customwars.client.io.ResourceManager;
import org.apache.log4j.Logger;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Music;
import org.newdawn.slick.Sound;

/**
 * Handles Sounds and Music
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
    Sound sound = resources.getSound(soundName);
    if (sound != null) {
      sound.play();
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
  public static void playMusic(String musicName) {
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

  /**
   * @param volume 0=Min 1=Max
   */
  public static void setMusicVolume(float volume) {
    container.setMusicVolume(volume);
  }

  /**
   * @param volume 0=Min 1=Max
   */
  public static void setSoundEffectsVolume(float volume) {
    container.setSoundVolume(volume);
  }

  public static void setResources(ResourceManager resources) {
    SFX.resources = resources;
  }

  public static void setGameContainer(GameContainer gameContainer) {
    SFX.container = gameContainer;
  }

  public static float getSoundEffectVolume() {
    return container.getSoundVolume();
  }

  public static float getMusicVolume() {
    return container.getMusicVolume();
  }
}
