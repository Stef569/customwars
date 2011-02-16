package com.customwars.client.controller;

import com.customwars.client.App;
import com.customwars.client.SFX;
import com.customwars.client.ui.GUI;
import com.customwars.client.ui.state.StateChanger;
import org.newdawn.slick.thingle.Widget;

public class AppOptionsController {
  private final StateChanger stateChanger;

  public AppOptionsController(StateChanger stateChanger) {
    this.stateChanger = stateChanger;
  }

  public void snailServerURLChanged(String newValue) {
    App.put("user.snailserver_url", newValue);
  }

  public void soundEffectsChanged(Widget soundSlider) {
    float soundVolume = soundSlider.getInteger("value") / 100f;
    App.put("user.sfx.sound_volume", soundVolume + "");
    SFX.setSoundEffectsVolume(soundVolume);
  }

  public void musicChanged(Widget musicSlider) {
    float musicVolume = musicSlider.getInteger("value") / 100f;
    App.put("user.sfx.music_volume", musicVolume + "");
    SFX.setMusicVolume(musicVolume);
  }

  public void userNameChanged(String newValue) {
    App.put("user.name", newValue);
  }

  public void userPasswordChanged(String newValue) {
    App.put("user.password", newValue);
  }

  public void pluginChanged(Widget pluginCbo) {
    String selectedPlugin = pluginCbo.getChild(pluginCbo.getSelectedIndex()).getText();
    String currentPlugin = App.get("user.activeplugin");

    if (!selectedPlugin.equals(currentPlugin)) {
      App.put("user.activeplugin", selectedPlugin);
      GUI.showdialog("The plugin " + selectedPlugin + " will be loaded on the next restart", "Plugin changed");
    }
  }

  public void continueToNextState() {
    stateChanger.clearPreviousStatesHistory();
    stateChanger.changeTo("MAIN_MENU");
  }

  public void back() {
    stateChanger.changeToPrevious();
  }
}
