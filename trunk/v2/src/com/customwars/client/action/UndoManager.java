package com.customwars.client.action;

import com.customwars.client.ui.state.InGameContext;
import org.apache.log4j.Logger;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 * UndoManager uses the javax.swing.undo.UndoManager to allow
 * CWActions to be stored and undone.
 *
 * @author stefan
 */
public class UndoManager {
  private final SwingUndoManager swingUndoManager;
  private Logger logger = Logger.getLogger(UndoManager.class);
  private int undoCount = 0;

  public UndoManager() {
    swingUndoManager = new SwingUndoManager();
  }

  public void undoAll() {
    while (canUndo()) {
      undo();
    }
  }

  public boolean canUndo() {
    return swingUndoManager.canUndo();
  }

  public void undo() {
    if (swingUndoManager.canUndo()) {
      logger.debug(" << " + swingUndoManager.getUndoPresentationName() + " >> ");
      swingUndoManager.undo();
      redoLast();
    }
  }

  private void redoLast() {
    if (swingUndoManager.canUndo()) {
      swingUndoManager.undo();
    } else {
      // Last action
      logger.debug(undoCount-- + ". Removing " + swingUndoManager.getPresentationName() + " from undo list");
      swingUndoManager.removeLastEdit();
    }

    if (swingUndoManager.canRedo()) {
      swingUndoManager.redo();
      logger.debug(undoCount-- + ". Removing " + swingUndoManager.getPresentationName() + " from undo list");
      swingUndoManager.removeLastEdit();
    }
  }

  public void addUndoAction(CWAction action, InGameContext gameContext) {
    if (action.canUndo()) {
      swingUndoManager.addEdit(new UndoWrapper(action, gameContext));
      logger.debug(++undoCount + ". Adding " + swingUndoManager.getPresentationName() + " to undo list");
    }
  }

  public void discartAllEdits() {
    undoCount = 0;
    swingUndoManager.discardAllEdits();
    logger.debug("Undo history cleared");
  }

  /**
   * Wrap a CWAction in a AbstractUndoableEdit
   * so it can be used by the swing undoManager
   */
  class UndoWrapper extends AbstractUndoableEdit {
    private CWAction action;
    private InGameContext gameContext;

    public UndoWrapper(CWAction action, InGameContext gameContext) {
      this.action = action;
      this.gameContext = gameContext;
    }

    public String getPresentationName() {
      return action.getName();
    }

    public void undo() throws CannotUndoException {
      super.undo();
      action.undo();
    }

    public void redo() throws CannotRedoException {
      super.redo();
      action.invoke(gameContext);
    }
  }

  /**
   * Extend the swing undo manager
   * so we can access the protected methods/fields.
   */
  private class SwingUndoManager extends javax.swing.undo.UndoManager {
    public void removeLastEdit() {
      int last = edits.size() - 1;

      if (last >= 0)
        trimEdits(last, last);
    }
  }
}
