package com.customwars.client.action.game;

import com.customwars.client.SFX;
import com.customwars.client.action.DirectAction;
import com.customwars.client.ui.state.InGameContext;

/**
 * Toggles the in game sound effect on or off
 */
public class ToggleInGameSoundAction extends DirectAction {

  public ToggleInGameSoundAction() {
    super("toggle sound", false);
  }

  @Override
  protected void init(InGameContext inGameContext) {
  }

  @Override
  protected void invokeAction() {
    float soundVolume = SFX.getSoundEffectVolume();

    if (soundVolume == 0) {
      SFX.setSoundEffectsVolume(1);
    } else {
      SFX.setSoundEffectsVolume(0);
    }
  }
}
