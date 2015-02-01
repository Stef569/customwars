package com.customwars.client.model.ai.unit.routine;

import com.customwars.client.model.fight.Defender;

class AttackPriority implements Comparable<AttackPriority> {
  public Defender enemy;
  public int priority;

  public AttackPriority(Defender enemy, int priority) {
    this.enemy = enemy;
    this.priority = priority;
  }

  @Override
  public int compareTo(AttackPriority o) {
    return o.priority - priority;
  }
}