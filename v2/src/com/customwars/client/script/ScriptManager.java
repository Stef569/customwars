package com.customwars.client.script;

import bsh.EvalError;
import bsh.Interpreter;
import bsh.UtilEvalError;
import com.customwars.client.tools.IOUtil;
import org.apache.log4j.Logger;
import org.newdawn.slick.util.ResourceLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.Reader;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * The ScriptManager simplifies interaction with BeanShell scripts.
 * It allows to load a script file. After loading the script a script method with optional parameters can be invoked.
 * This method can then alter the java object passed as a parameter and optionally return a value.
 * <p/>
 * Usage:
 * scriptManager.loadScript(myScriptPath)
 * <p/>
 * Invoking the method 'myMethod' with 1 parameter 'myParam'
 * if (scriptManager.isMethod("myMethod")) {
 * scriptManager.invoke("myMethod", new Parameter<Integer>("myParam", value));
 * }
 */
public class ScriptManager implements Serializable {
  private static final Logger logger = Logger.getLogger(ScriptManager.class);
  private static final Class[] NO_ARGS = new Class[0];
  private transient Interpreter bsh;
  private final Set<String> scriptFiles;

  public ScriptManager() {
    scriptFiles = new HashSet<String>();
    initBeanshell();
  }

  private void initBeanshell() {
    bsh = new Interpreter();

    try {
      bsh.eval("setAccessibility(true)");
      bsh.set("out", System.out);
    } catch (EvalError evalError) {
      throw new RuntimeException(evalError);
    }
  }

  public void init(String... scriptFiles) {
    for (String scriptFile : scriptFiles) {
      loadScript(scriptFile);
    }
  }

  /**
   * Reload all scripts files. This method should be performed after editing a script file.
   * Since they are not reloaded automatically.
   */
  public void reload() {
    for (String scriptFile : scriptFiles) {
      loadScript(scriptFile);
    }
  }

  /**
   * Loads a beanshell script file and evaluates the text.
   * The location of the script file is saved to allow the script to be reloaded.
   */
  public void loadScript(String scriptFile) {
    Reader scriptInputStream = null;

    try {
      InputStream in = ResourceLoader.getResourceAsStream(scriptFile);
      scriptInputStream = new BufferedReader(new InputStreamReader(in));
      bsh.eval(scriptInputStream);
      scriptFiles.add(scriptFile);
    } catch (EvalError evalError) {
      logger.warn("Failed to load script file", evalError);
    } finally {
      IOUtil.closeStream(scriptInputStream);
    }
  }

  public void eval(String text) {
    try {
      bsh.eval(text);
    } catch (EvalError evalError) {
      logger.warn("Failed to eval text", evalError);
    }
  }

  /**
   * Returns true if the specified method name is an existing
   * scripted method.
   *
   * @param methodName The scripted method name to check for existence
   * @return If the scripted method exists
   */
  public boolean isMethod(String methodName) {
    try {
      return (bsh.getNameSpace().getMethod(methodName, NO_ARGS) != null);
    } catch (UtilEvalError err) {
      throw new RuntimeException(err);
    }
  }

  /**
   * Invokes the specified scripted method with the given parameters.
   * The parameters are set before the scripted method is invoked and unset afterwards.
   * and return the result.
   * If the method does not exist then an exception is thrown.
   * <p/>
   * The scripted method can use the parameters.
   * For example:
   * </code>invoke("doIt", new Parameter<String>("param","now please"));</code>
   * The following scripted method would be executed and prints 'now please' on the console.
   * String doIt() {
   * out.println(param);
   * }
   * @see #isMethod(String)
   * @param methodName The name of the scripted method to execute
   * @param parameters The parameter name, value pairs that are available in the scripted method.
   * @return The return value of the scripted method. Null if there is no return value.
   */
  public Object invoke(String methodName, Parameter... parameters) {
    if (isMethod(methodName)) {
      try {
        setParameters(parameters);
        Object result = bsh.eval(methodName + "()");
        unSetParameters(parameters);
        return result;
      }
      catch (EvalError e) {
        logger.warn("Could not execute method " + methodName + " parameters " + Arrays.toString(parameters), e);
        return null;
      }
    }
    throw new IllegalArgumentException("Scripted method " + methodName + " not found");
  }

  private void setParameters(Parameter[] parameters) {
    for (Parameter parameter : parameters) {
      set(parameter);
    }
  }

  private void unSetParameters(Parameter[] parameters) {
    for (Parameter parameter : parameters) {
      unSet(parameter);
    }
  }

  public void set(Parameter parameter) {
    set(parameter.getName(), parameter.getValue());
  }

  public void unSet(Parameter parameter) {
    unSet(parameter.getName());
  }

  public void set(String name, Object object) {
    try {
      bsh.set(name, object);
    } catch (EvalError evalError) {
      logger.warn("Could not set", evalError);
    }
  }

  public void unSet(String name) {
    try {
      bsh.unset(name);
    } catch (EvalError evalError) {
      logger.warn("Could not unset", evalError);
    }
  }

  /**
   * When the ScriptManager is persisted, the beanshell interpreter is not saved.
   * Instead when loading the ScriptManager from the object stream,
   * a new beanshell instance is created, and the script files are reloaded.
   */
  private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
    in.defaultReadObject();
    initBeanshell();
    reload();
  }
}
