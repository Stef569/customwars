package slick;

import com.customwars.client.ui.CWInput;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.command.Command;
import org.newdawn.slick.command.Control;
import org.newdawn.slick.command.KeyControl;
import org.newdawn.slick.command.MouseButtonControl;
import org.newdawn.slick.gui.TextField;
import org.newdawn.slick.state.StateBasedGame;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * Show current key binds and allow to remap them
 *
 * @author stefan
 */
public class RemapKeysTest extends CWState {
  private CWInput input;
  private List<TextField> fields = new ArrayList<TextField>();

  private TextField activeField;
  private InputState inputState;

  private enum InputState {
    SELECTING, MAPPING
  }

  public RemapKeysTest(CWInput input) {
    this.input = input;
  }

  public void init(GameContainer container, StateBasedGame game) throws SlickException {
    int lines = 0;
    Point startPoint = new Point(50, 100);

//    for (Command command : input.getCommands()) {
//      TextField field = new TextField(container, container.getGraphics().getFont(), 150, 20, 500, 35, new ComponentListener() {
//        public void componentActivated(AbstractComponent source) {
//          System.out.println(inputState);
//        }
//      });
//
//      fields.add(field);
//      field.setText(command.toString());
//      field.setLocation(startPoint.x, startPoint.y + (lines * field.getHeight()));
//      lines++;
//    }
  }

  public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
    g.drawString("Press key to change", 10, 45);

    for (TextField field : fields) {
      field.render(container, g);
    }
  }

  public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
    boolean foundActiveField = false;

    // find the field that has been clicked on, store it
    for (TextField field : fields) {
      if (field.hasFocus()) {
        inputState = InputState.MAPPING;
        activeField = field;
        activeField.setAcceptingInput(false);
        foundActiveField = true;
      }
    }

    if (!foundActiveField) {
      activeField = null;
      inputState = InputState.SELECTING;
    }
  }

  public void controlPressed(Command command) {
  }


  public void mousePressed(int button, int x, int y) {
    if (activeField != null) {
      int id = fields.indexOf(activeField);
      bind(new MouseButtonControl(button), null);
    }
  }

  public void keyPressed(int key, char c) {
    if (activeField != null && inputState == InputState.MAPPING)
      if (key == Input.KEY_BACK) {
        unBind(new KeyControl(key));
      } else {
        int id = fields.indexOf(activeField);
        //bind(new KeyControl(key), commands.get(id));
      }
  }

  private void bind(Control control, Command command) {
    System.out.println("Binding " + control + " to " + command);
    input.bindCommand(control, command);
    activeField.setFocus(false);
    inputState = InputState.SELECTING;
  }

  private void unBind(Control control) {
    System.out.println("unBinding " + control);
    input.unbindCommand(control);
    activeField.setFocus(false);
    activeField.setAcceptingInput(true);
    inputState = InputState.SELECTING;
  }

  public int getID() {
    return 2;
  }
}