package com.customwars.client.network;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Information of a server game.
 * The userName 'empty' is used to denote empty slots.
 *
 * The user names are ordered in the turn order eg.
 * given following user names (u1, u2, u3)
 * u1 plays the first turn, u2 the second...
 */
public class ServerGameInfo {
  private static final String EMPTY_SLOT = "empty";
  private final int day;
  private final int turn;
  private final int numPlayers;
  private final List<String> userNames;

  public ServerGameInfo(int day, int turn, int numPlayers, String[] userNames) {
    this.day = day;
    this.turn = turn;
    this.numPlayers = numPlayers;
    this.userNames = Arrays.asList(userNames);

    assert numPlayers == userNames.length;
  }

  public int getDay() {
    return day;
  }

  public int getTurn() {
    return turn;
  }

  public int getNumPlayers() {
    return numPlayers;
  }

  public String[] getUserNames() {
    return userNames.toArray(new String[userNames.size()]);
  }

  /**
   * Get a slot number for a user, The slot numbering starts at 1.
   *
   * @param userName the user to retrieve the slot number for
   * @return the slot number this user has taken or
   *         -1 if this user did not joined this game
   */
  public int getSlotNrForUser(String userName) {
    if (!userNames.contains(userName)) {
      return -1;
    }


    int index = userNames.indexOf(userName);
    return index + 1;
  }

  public int[] getFreeSlots() {
    List<Integer> freeSlots = new ArrayList<Integer>(userNames.size());

    for (int i = 0; i < userNames.size(); i++) {
      String userName = userNames.get(i);
      if (userName.equals(EMPTY_SLOT)) {
        freeSlots.add(i + 1);
      }
    }

    int[] retFreeSlots = new int[freeSlots.size()];
    for (int index = 0; index < freeSlots.size(); index++) {
      int freeSlot = freeSlots.get(index);
      retFreeSlots[index] = freeSlot;
    }

    return retFreeSlots;
  }

  /**
   * A Slot is considered 'free' if it is not taken by a user
   *
   * @param slotNr The number of the slot to check, The slot numbering starts at 1.
   * @return if the slot number is free
   */
  public boolean isFreeSlot(int slotNr) {
    String userName = userNames.get(slotNr - 1);
    return EMPTY_SLOT.equals(userName);
  }

  public boolean isFirstTurn() {
    return turn == 1 && day == 1;
  }

  public int getUserIdFor(String userName) {
    return userNames.indexOf(userName);
  }

  @Override
  public String toString() {
    return "ServerGameInfo{" +
      "day=" + day +
      ", turn=" + turn +
      ", numPlayers=" + numPlayers +
      ", userNames=" + (userNames == null ? null : userNames) +
      '}';
  }
}
