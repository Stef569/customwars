package com.customwars.client.model.ai.unit;

import com.customwars.client.model.ai.fuzzy.Fuz;
import com.customwars.client.model.ai.unit.routine.AIRoutine;
import com.customwars.client.model.ai.unit.routine.AttackRoutine;
import com.customwars.client.model.ai.unit.routine.CaptureRoutine;
import com.customwars.client.model.ai.unit.routine.CoRoutine;
import com.customwars.client.model.ai.unit.routine.HealRoutine;
import com.customwars.client.model.ai.unit.routine.MoveRoutine;
import com.customwars.client.model.ai.unit.routine.OrderFactory;
import com.customwars.client.model.ai.unit.routine.RoutineResult;
import com.customwars.client.model.ai.unit.routine.SubMarineRoutine;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.gameobject.Unit;

import java.util.ArrayList;
import java.util.List;

/**
 * This adviser gives advice for 1 Unit. All AI logic is within routines.
 * 1 unit order will be returned. The AI can only react to stuff within his move zone.
 * It does not plan ahead to get somewhere.
 */
public class DefaultUnitAdvisor implements UnitAdvisor {
  private final OrderFactory orderFactory;
  private final Unit unit;
  private final List<AIRoutine> routines;
  private final PresetSituations presets;

  public DefaultUnitAdvisor(Game game, Unit unit) {
    this(game, unit, new PresetSituations(game));
  }

  public DefaultUnitAdvisor(Game game, Unit unit, PresetSituations presets) {
    this.presets = presets;
    this.orderFactory = new OrderFactory(game.getMap());
    this.unit = unit;
    routines = new ArrayList<AIRoutine>();

    GameInformation data = new GameInformation(game);
    addRoutines(game, data);
  }

  private void addRoutines(Game game, GameInformation data) {
    //routines.add(new JoinRoutine(game, unit));
    routines.add(new CaptureRoutine(game, unit));
    routines.add(new AttackRoutine(game, unit));
    routines.add(new HealRoutine(unit, data));
    routines.add(new SubMarineRoutine(unit));
    routines.add(new CoRoutine(unit));
    routines.add(new MoveRoutine(game, unit));
  }

  @Override
  public UnitOrder createBestOrder() {
    if (presets.hasPresetFor(unit)) {
      Fuz.UNIT_ORDER presetOrder = presets.getUnitOrder(unit);

      for (AIRoutine routine : routines) {
        if (routine.getSupportedOrders().contains(presetOrder)) {
          RoutineResult result = routine.think();

          if (result != null) {
            return createOrder(result);
          }
        }
      }
    }

    for (AIRoutine routine : routines) {
      RoutineResult result = routine.think();

      if (result != null) {
        return createOrder(result);
      }
    }

    return null;
  }

  public UnitOrder createOrder(RoutineResult result) {
    return orderFactory.createOrder(result);
  }
}
