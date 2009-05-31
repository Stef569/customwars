package com.customwars.client;

import com.customwars.client.io.ResourceManager;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Music;
import org.newdawn.slick.Sound;

/**
 * Handles Sounds and Music
 *
 * @author stefan
 */
public class SFX {
  private static ResourceManager resources;
  private static GameContainer container;

  public static void toggleMusic(Music music) {
    if (music.playing()) {
      music.pause();
    } else {
      music.resume();
    }
  }

  public static void playSound(String soundName) {
    Sound s = resources.getSound(soundName);
    if (s != null) {
      s.play();
    }
  }

  public void playMusic(String musicName) {
    Music music = resources.getMusic(musicName);
    if (music != null) {
      music.play();
    }
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
