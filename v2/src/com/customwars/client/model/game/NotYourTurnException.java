package com.customwars.client.model.game;

/**
 * Thrown when a player is trying to end the current turn, while it is not his turn.
 */
public class NotYourTurnException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public NotYourTurnException(String message) {
    super(message);
  }

  public NotYourTurnException(String message, Throwable cause) {
    super(message, cause);
  }

  public NotYourTurnException(Throwable cause) {
    super(cause);
  }
}
