package com.customwars.client.controller;

import com.customwars.client.model.map.Tile;

import java.util.Arrays;

/**
 * A History of clicks
 * starting at index 1(the first click) with a maximum of maxClicks(the last click)
 *
 * Usage:
 * ClickRecord clickRecord = new ClickRecord(5);
 * clickRecord.setClick(1, tile);
 * clickRecord.setClick(2, tile);
 * clickRecord.setClick(2, tile);
 * clickRecord.setClick(5, tile);
 */
public class ClickHistory {
  private final Tile[] clicks;

  /**
   * @param maxClicks The maximum amount of clicks that will be stored
   */
  public ClickHistory(int maxClicks) {
    clicks = new Tile[maxClicks];
  }

  public void clear() {
    Arrays.fill(clicks, null);
  }

  /**
   * @param index   The click index between [1-maxClickHistory]
   * @param clicked the tile that was clicked on
   */
  public void registerClick(int index, Tile clicked) {
    clicks[index - 1] = clicked;
  }

  public Tile getClick(int index) {
    return clicks[index - 1];
  }

  @Override
  public String toString() {
    return "ClickHistory{" +
      "clicks=" + Arrays.asList(clicks) +
      '}';
  }
}
