package com.customwars.client.ui.state;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.Color;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Handles transitions between states, each state has an ID starting from 0 to uniqueStateCount
 * It maps state names to their stateID, state names are always stored in upper case.
 * multiple stateNames can point to the same stateID.
 *
 * Each time we change to a state, we store that stateID into previousStates which uses a LIFO Queue
 * This allows to go back 1 state, clearPreviousStates removes all stored stateID's.
 */
public class StateLogic {
  private static final int PREVIOUS_STATE_LIMIT = 20;

  private StateBasedGame stategame;
  private AppGameContainer appcontainer;
  private Deque<Integer> previousStates;
  private Map<String, Integer> states;
  private int uniqueStateCount;

  public StateLogic(StateBasedGame game, AppGameContainer container) {
    this.stategame = game;
    this.appcontainer = container;
    this.states = new HashMap<String, Integer>();
    this.previousStates = new LinkedList<Integer>();
  }

  public void addState(String stateName, int stateID) {
    if (stateName != null) {
      if (!states.values().contains(stateID)) {
        uniqueStateCount++;
      }

      String key = stateName.toUpperCase();
      states.put(key, stateID);
    }
  }

  public void clearPreviousStates() {
    previousStates.clear();
  }

  /**
   * Change to another state
   *
   * @param stateName case insensitive state name
   * @throws IllegalArgumentException thrown when a stateName is not mapped to a stateID
   * or when the stateID is not within state bounds(>0 <states.size)
   */
  public void changeTo(String stateName) throws IllegalArgumentException {
    if (stateName != null) {
      gotoState(stateName.toUpperCase());
    }
  }

  public void changeToNext() {
    gotoState(getNextStateID());
  }

  public void changeToPrevious() {
    if (!previousStates.isEmpty()) {
      int lastStateID = previousStates.removeLast();
      gotoState(lastStateID);
    }
  }

  private void gotoState(String stateName) throws IllegalArgumentException {
    if (states.containsKey(stateName)) {
      int stateID = states.get(stateName);
      gotoState(stateID);
    } else {
      throw new IllegalArgumentException(
              "Attention: " + stateName + " does not exist!!! states:" + states.keySet());
    }
  }

  public void gotoState(int stateID) throws IllegalArgumentException {
    if (stateID >= 0 && stateID < uniqueStateCount) {
      stategame.enterState(stateID, null, new FadeInTransition(Color.black));
      appcontainer.setTitle(stategame.getState(stateID).getClass().toString());
      if (previousStates.size() < PREVIOUS_STATE_LIMIT)
        previousStates.add(stateID);
    } else {
      throw new IllegalArgumentException(
              "StateID " + stateID + " is not within state bounds >=0 <" + uniqueStateCount);
    }
  }

  /**
   * @return the next valid stateID
   *         returns 0 when the next stateID is off state bounds.
   */
  private int getNextStateID() {
    int nextID = stategame.getCurrentStateID() + 1;
    return nextID < uniqueStateCount ? nextID : 0;
  }
}
