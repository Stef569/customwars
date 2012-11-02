package com.customwars.client.tools;

import org.apache.log4j.Logger;
import org.newdawn.slick.util.LogSystem;

/**
 * Implementation to route internal slick log calls to our log4j system.
 * I don't recommend using org.newdawn.slick.util.Log for logging in your game
 * as you will lose log4j information such as class name, context and such.
 *
 * @author Kova
 */
public class Log4jLogSystem implements LogSystem {
  private static final Logger _log = Logger.getLogger(Log4jLogSystem.class);

  @Override
  public void error(String message, Throwable e) {
    _log.error(message, e);
  }

  @Override
  public void error(Throwable e) {
    _log.error("", e);
  }

  @Override
  public void error(String message) {
    _log.error(message);
  }

  @Override
  public void warn(String message) {
    _log.warn(message);
  }

  public void warn(String message, Throwable e) {
    _log.warn(message, e);
  }

  @Override
  public  void info(String message) {
    _log.info(message);
  }

  @Override
  public void debug(String message) {
    _log.debug(message);
  }

}