package com.customwars.client.ui.state.input;

import org.newdawn.slick.command.BasicCommand;

/**
 * Represents a Command in the game commands can be:
 * ATTACK, EXIT, SELECT, ...
 */
public class CWCommand extends BasicCommand {
  private CommandEnum commandEnum;

  public CWCommand(CommandEnum commandEnum) {
    super(commandEnum.toString().toLowerCase());
    this.commandEnum = commandEnum;
  }

  public CommandEnum getEnum() {
    return commandEnum;
  }

  public boolean isMoveCommand() {
    return commandEnum == CommandEnum.UP || commandEnum == CommandEnum.DOWN ||
      commandEnum == CommandEnum.LEFT || commandEnum == CommandEnum.RIGHT;
  }
}
