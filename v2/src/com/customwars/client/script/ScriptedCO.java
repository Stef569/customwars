package com.customwars.client.script;

import com.customwars.client.model.co.AbstractCO;
import com.customwars.client.model.co.BasicCO;
import com.customwars.client.model.co.CO;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.ui.renderer.GameRenderer;

import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * The ScriptedCO will try to invoke the scripted method in a script file. The method looks like coname_methodname(){}.
 * If there is no scripted method in the script file the super method is used.
 */
public class ScriptedCO extends BasicCO {
  private final ScriptManager scriptManager;

  public ScriptedCO(CO co, ScriptManager scriptManager) {
    super((AbstractCO) co);
    this.scriptManager = scriptManager;
    init();
  }

  public void init() {
    super.init();
    scriptManager.set("superPower", isPowerActive());
    scriptManager.set("power", isSuperPowerActive());
  }

  @Override
  public void power(Game game, GameRenderer gameRenderer) {
    super.power(game, gameRenderer);
    String methodName = getName() + "_power";

    if (scriptManager.isMethod(methodName)) {
      scriptManager.set("power", true);
      scriptManager.invoke(methodName,
        new Parameter<Game>("game", game),
        new Parameter<GameRenderer>("gameRenderer", gameRenderer));
    } else {
      throw new IllegalArgumentException("No scripted power method for " + getName());
    }
  }

  @Override
  public void deActivatePower() {
    super.deActivatePower();
    scriptManager.set("power", false);
  }

  @Override
  public void superPower(Game game, GameRenderer gameRenderer) {
    super.superPower(game, gameRenderer);
    String methodName = getName() + "_superPower";

    if (scriptManager.isMethod(methodName)) {
      scriptManager.set("superPower", true);
      scriptManager.invoke(methodName,
        new Parameter<Game>("game", game),
        new Parameter<GameRenderer>("gameRenderer", gameRenderer));
    } else {
      throw new IllegalArgumentException("No scripted super power method for " + getName());
    }
  }

  @Override
  public void deActivateSuperPower() {
    super.deActivateSuperPower();
    scriptManager.set("superPower", false);
  }

  public int unitMovementHook(Unit mover, int movement) {
    String methodName = getName() + "_unitMovementHook";

    if (scriptManager.isMethod(methodName)) {
      return (Integer) scriptManager.invoke(methodName,
        new Parameter<Unit>("mover", mover),
        new Parameter<Integer>("movement", movement));
    } else {
      return super.unitMovementHook(mover, movement);
    }
  }

  public int getAttackBonusPercentage(Unit attacker, Unit defender) {
    String methodName = getName() + "_getAttackBonusPercentage";

    if (scriptManager.isMethod(methodName)) {
      return (Integer) scriptManager.invoke(methodName,
        new Parameter<Unit>("attacker", attacker),
        new Parameter<Unit>("defender", defender));
    } else {
      return super.getAttackBonusPercentage(attacker, defender);
    }
  }

  @Override
  public int getDefenseBonusPercentage(Unit attacker, Unit defender) {
    String methodName = getName() + "_getDefenseBonusPercentage";

    if (scriptManager.isMethod(methodName)) {
      return (Integer) scriptManager.invoke(methodName,
        new Parameter<Unit>("attacker", attacker),
        new Parameter<Unit>("defender", defender));
    } else {
      return super.getDefenseBonusPercentage(attacker, defender);
    }
  }

  @Override
  public int captureRateHook(int captureRate) {
    String methodName = getName() + "_captureRateHook";

    if (scriptManager.isMethod(methodName)) {
      return (Integer) scriptManager.invoke(methodName, new Parameter<Integer>("captureRate", captureRate));
    } else {
      return super.captureRateHook(captureRate);
    }
  }

  @Override
  public int cityFundsHook(int funds) {
    String methodName = getName() + "_cityFundsHook";

    if (scriptManager.isMethod(methodName)) {
      return (Integer) scriptManager.invoke(methodName, new Parameter<Integer>("funds", funds));
    } else {
      return super.cityFundsHook(funds);
    }
  }

  @Override
  public int unitPriceHook(int price) {
    String methodName = getName() + "_unitPriceHook";

    if (scriptManager.isMethod(methodName)) {
      return (Integer) scriptManager.invoke(methodName, new Parameter<Integer>("price", price));
    } else {
      return super.unitPriceHook(price);
    }
  }

  @Override
  public int healRateHook(int healRate) {
    String methodName = getName() + "_healRateHook";

    if (scriptManager.isMethod(methodName)) {
      return (Integer) scriptManager.invoke(methodName, new Parameter<Integer>("healRate", healRate));
    } else {
      return super.healRateHook(healRate);
    }
  }

  @Override
  public int terrainDefenseHook(int terrainDefenseBonus) {
    String methodName = getName() + "_terrainDefenseHook";

    if (scriptManager.isMethod(methodName)) {
      return (Integer) scriptManager.invoke(methodName, new Parameter<Integer>("terrainDefense", terrainDefenseBonus));
    } else {
      return super.terrainDefenseHook(terrainDefenseBonus);
    }
  }

  @Override
  public int fireRangeHook(int fireRange) {
    String methodName = getName() + "_fireRangeHook";

    if (scriptManager.isMethod(methodName)) {
      return (Integer) scriptManager.invoke(methodName, new Parameter<Integer>("fireRange", fireRange));
    } else {
      return super.fireRangeHook(fireRange);
    }
  }

  @Override
  public int visionHook(int vision) {
    String methodName = getName() + "_visionHook";

    if (scriptManager.isMethod(methodName)) {
      return (Integer) scriptManager.invoke(methodName, new Parameter<Integer>("vision", vision));
    } else {
      return super.visionHook(vision);
    }
  }

  @Override
  public void dayStart(Game game) {
    String methodName = getName() + "_dayStart";

    if (scriptManager.isMethod(methodName)) {
      scriptManager.invoke(methodName, new Parameter<Game>("game", game));
    }
  }

  private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
    in.defaultReadObject();
    init();
  }
}