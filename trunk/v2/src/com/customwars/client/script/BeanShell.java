package com.customwars.client.script;

import bsh.EvalError;
import bsh.Interpreter;
import bsh.util.JConsole;
import org.apache.log4j.Logger;

import javax.swing.JFrame;

/**
 * The BeanShell class provides global access to a single interactive Beanshell interpreter.
 * Java objects can be placed into the script-engine scope by using the {@link #set(String, Object)} method.
 * A swing JFrame console can be retrieved by calling getConsole();
 * This console allows direct access to the script objects.
 * All checked exceptions are logged.
 *
 * @see Interpreter
 * @see JConsole
 */
public class BeanShell {
  private static final Logger logger = Logger.getLogger(BeanShell.class);
  private static final BeanShell instance = new BeanShell();
  private final Interpreter bsh;
  private final JFrame consoleFrame;

  /**
   * This class is a singleton. Direct instantiation is not allowed.
   *
   * @see #get
   */
  private BeanShell() {
    consoleFrame = new JFrame("Console");
    consoleFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    JConsole console = new JConsole();

    consoleFrame.add(console);
    consoleFrame.setBounds(0, 0, 400, 400);
    bsh = new Interpreter(console);
    new Thread(bsh).start();
  }

  /**
   * @return The single instance of this class.
   */
  public static BeanShell get() {
    return instance;
  }

  /**
   * @see Interpreter#set(String, Object)
   */
  public void set(String name, Object obj) {
    try {
      bsh.set(name, obj);
    } catch (EvalError ex) {
      logger.warn("Could not add " + name, ex);
    }
  }

  /**
   * @see Interpreter#unset(String)
   */
  public void unset(String name) {
    try {
      bsh.unset(name);
    } catch (EvalError ex) {
      logger.warn("Could not unset " + name, ex);
    }
  }

  /**
   * Retrieves a scripted variable from beanshell.
   *
   * @throws IllegalArgumentException When there is no var assigned to the given name.
   * @see Interpreter#get(String)
   */
  public Object get(String name) throws IllegalArgumentException {
    try {
      return bsh.get(name);
    } catch (EvalError ex) {
      throw new IllegalArgumentException("Could not get " + name, ex);
    }
  }

  /**
   * @see Interpreter#eval(String)
   */
  public void eval(String expression) {
    try {
      bsh.eval(expression);
    } catch (EvalError ex) {
      logger.warn("Could not evaluate expression " + expression, ex);
    }
  }

  /**
   * @return A list of variable names added to beanshell using the set method.
   */
  public String[] getAllVars() {
    return bsh.getNameSpace().getVariableNames();
  }

  /**
   * @return A Swing JFrame console attached to a beanshell Interpreter.
   */
  public JFrame getConsole() {
    return consoleFrame;
  }
}
