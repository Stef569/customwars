package slick;

import com.customwars.client.ui.slick.InputField;
import com.customwars.client.ui.state.CWState;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.command.BasicCommand;
import org.newdawn.slick.gui.TextField;
import org.newdawn.slick.state.StateBasedGame;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * Show current Command binds and allow to remap them
 *
 * @author stefan
 */
public class RemapKeysTest extends CWState {
  private static final boolean DEBUG = false;
  private static final int LBL_FIELD_WIDTH = 120;
  private static final int INPUT_FIELD_WIDTH = 430;
  private static final int FIELD_HEIGHT = 20;
  private static final Point leftTop = new Point(10, 80);
  private static final List<TextField> fields = new ArrayList<TextField>();

  public void init(GameContainer container, StateBasedGame game) throws SlickException {
    int rowCount = 0;

    for (Object obj : cwInput.getUniqueCommands()) {
      BasicCommand command = (BasicCommand) obj;
      int px = leftTop.x;
      int py = leftTop.y + (rowCount * FIELD_HEIGHT);

      TextField label = createLabel(container, px, py, command);
      fields.add(label);

      TextField inputField = createInputField(container, px + LBL_FIELD_WIDTH, py, command);
      fields.add(inputField);
      rowCount++;
    }
  }

  private TextField createLabel(GameContainer container, int x, int y, BasicCommand command) {
    TextField label = new TextField(container, container.getDefaultFont(), x, y, LBL_FIELD_WIDTH, FIELD_HEIGHT, null);
    label.setText(command.getName());
    label.setAcceptingInput(false);
    label.setBorderColor(null);
    label.setCursorVisible(false);
    return label;
  }

  private TextField createInputField(GameContainer container, int x, int y, BasicCommand command) {
    InputField inputField = new InputField(container, x, y, INPUT_FIELD_WIDTH, FIELD_HEIGHT, command, cwInput);
    inputField.setBindingLimit(10);
    inputField.initDisplayText();
    return inputField;
  }

  @Override
  public void enter(GameContainer container, StateBasedGame game) throws SlickException {
    super.enter(container, game);
    // Don't execute commands when we are editing them.
    cwInput.setActive(false);
    for (TextField field : fields) {
      field.setAcceptingInput(true);
    }
  }

  @Override
  public void leave(GameContainer container, StateBasedGame game) throws SlickException {
    super.leave(container, game);
    for (TextField field : fields) {
      field.setAcceptingInput(false);
      field.setFocus(false);
    }
    cwInput.setActive(true);
  }

  public void render(GameContainer container, Graphics g) throws SlickException {
    g.drawString("Click on a key to select, Press any key to change, backspace clears the selected mappings", 10, 45);
    g.drawString("Limit = 10", 10, 60);

    for (TextField field : fields) {
      if (DEBUG) {
        if (field.hasFocus()) {
          g.drawString(fields.indexOf(field) + " has focus", 150, 350);
        }
      }
      field.render(container, g);
    }
  }

  public void update(GameContainer container, int delta) throws SlickException {
    for (TextField field : fields) {

      if (field.hasFocus()) {
        // When clicked on field(uneven numbers), show blue border
        if (fields.indexOf(field) % 2 != 0) {
          field.setBorderColor(Color.blue);
        } else {
          // select the field when clicked on lbl
          // The field is the next index in the fields list
          fields.get(fields.indexOf(field) + 1).setFocus(true);
          fields.get(fields.indexOf(field) + 1).setBorderColor(Color.blue);
        }
      } else {
        if (DEBUG) {
          field.setBorderColor(Color.white);
        } else {
          field.setBorderColor(null);
        }
      }
    }
  }

  public int getID() {
    return 2;
  }
}