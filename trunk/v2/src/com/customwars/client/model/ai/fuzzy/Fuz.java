package com.customwars.client.model.ai.fuzzy;

public class Fuz {
  public enum MAP_SIZE {
    TINY,
    SMALL,
    NORMAL,
    LARGE,
    HUGE
  }

  public enum MAP_TYPE {
    PANGEA,
    ISLANDS,
    PENINSULA,
  }

  public enum BATTLE_CONDITION {
    WINNING,
    LOSING,
    EVEN
  }

  public enum CONSTRUCTION_POSSIBILITY {
    SEA,
    LAND,
    AIR
  }

  public enum GAME_PROGRESS {
    EARLY_GAME,
    IN_GAME
  }

  public enum DISTANCE {
    VERY_CLOSE,
    CLOSE,
    FAR,
    VERY_FAR,
    UNREACHABLE
  }

  public enum PLAYER_FINANCE {
    BANKRUPT,
    LOW,
    FAIR,
    MUCH,
    RICH
  }

  public enum BUILD_PRIORITY {
    VERY_LOW,
    LOW,
    NORMAL,
    HIGH,
    VERY_HIGH,
    CRITICAL
  }

  public enum UNIT_TYPE {
    CAPTURE,
    OFFENSE,
    DEFENSE,
    SCOUT
  }

  public enum UNIT_ORDER {
    DO_NOTHING,
    WAIT,
    CAPTURE,
    ATTACK_UNIT,
    ATTACK_CITY,
    SUPPLY,
    READY_FOR_TRANSPORT,
    JOIN,
    HEAL,
    HIDE,
    SUBMERGE,
    SURFACE,
    UNLOAD,
    BUILD_TEMP_BASE,
    FIRE_SILO_ROCKET,
    LOAD_CO,
    MOVE,
    DO_CO_POWER
  }

}

