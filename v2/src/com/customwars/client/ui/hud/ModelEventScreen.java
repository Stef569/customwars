package com.customwars.client.ui.hud;

import com.customwars.client.model.game.Game;
import com.customwars.client.model.game.TurnBasedGame;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import com.customwars.client.tools.IOUtil;
import org.apache.log4j.Logger;

import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Shows Events send from the model in a swing Jlist
 * This is usefull for debugging,
 * the list can be cleared and saved to a file
 * Some events are less usefull so they can be ignored by adding the
 * Class and the event to ignore to the filters Map
 *
 * @author stefan
 */
public class ModelEventScreen implements PropertyChangeListener {
  private static final Logger logger = Logger.getLogger(ModelEventScreen.class);
  private final JPanel content = new JPanel(new BorderLayout());
  private final JList list;
  private final DefaultListModel listModel = new DefaultListModel();
  private HashMap<Class, List<String>> filters;
  private Game game;

  public ModelEventScreen(JFrame frame) {
    list = new JList(listModel);
    content.add(new JScrollPane(list));

    JMenuBar menuBar = new JMenuBar();
    menuBar.add(buildActionMenu());
    frame.setJMenuBar(menuBar);
    initFilters();
  }

  private void initFilters() {
    filters = new HashMap<Class, List<String>>();
    filters.put(Tile.class, Arrays.asList("fog", "locatable"));
    filters.put(Unit.class, Arrays.asList("location", "moveZone", "attackZone", "orientation", "state"));
  }

  private JMenu buildActionMenu() {
    JMenu menu = new JMenu("Actions");
    menu.add(new JMenuItem(new ClearAction()));
    menu.add(new JMenuItem(new SaveAction()));
    return menu;
  }

  /**
   * Registers for events from game
   * if the event screen was showing events from a previous game
   * then stop receiving events from that game.
   */
  public void setGame(Game game) {
    if (this.game != null || game == null) {
      removeModelEventListeners(this.game);
    } else {
      addModelEventListeners(game);
    }
    this.game = game;
  }

  private void addModelEventListeners(TurnBasedGame game) {
    game.addPropertyChangeListener(this);
    Map<Tile> map = game.getMap();

    for (Tile t : map.getAllTiles()) {
      t.addPropertyChangeListener(this);

      Unit unit = map.getUnitOn(t);
      if (unit != null) unit.addPropertyChangeListener(this);

      City city = map.getCityOn(t);
      if (city != null) city.addPropertyChangeListener(this);
    }
  }

  private void removeModelEventListeners(Game game) {
    game.removePropertyChangeListener(this);
    Map<Tile> map = game.getMap();

    for (Tile t : map.getAllTiles()) {
      t.removePropertyChangeListener(this);

      Unit unit = map.getUnitOn(t);
      if (unit != null) unit.removePropertyChangeListener(this);

      City city = map.getCityOn(t);
      if (city != null) city.removePropertyChangeListener(this);
    }
  }

  public void propertyChange(PropertyChangeEvent evt) {
    if (!isFiltered(evt.getSource().getClass(), evt.getPropertyName()) && game != null && game.isStarted()
      && evt.getPropertyName() != null && evt.getPropertyName().trim().length() != 0) {
      showEvent(evt);
    }
  }

  private void showEvent(PropertyChangeEvent event) {
    listModel.addElement(
      "[" + event.getSource().getClass().getSimpleName() +
        "] Change=" + event.getPropertyName() +
        " oldVal=" + event.getOldValue() +
        " newVal=" + event.getNewValue());
    list.ensureIndexIsVisible(listModel.size() - 1);
  }

  public JPanel getGui() {
    return content;
  }

  private boolean isFiltered(Class eventSourceClass, String propertyName) {
    if (filters.containsKey(eventSourceClass)) {
      List<String> filteredProperties = filters.get(eventSourceClass);
      return filteredProperties.contains(propertyName);
    } else {
      return false;
    }
  }

  class ClearAction extends AbstractAction {

    public ClearAction() {
      super("Clear");
    }

    public void actionPerformed(ActionEvent e) {
      listModel.clear();
    }
  }

  class SaveAction extends AbstractAction {
    private static final String HEADER = "Saved Events from Events Screen";
    private static final String FILE_NAME = "events.txt";
    private static final String SUCCESS_MSG = "Events have been saved as " + FILE_NAME;
    private static final String ERR_MSG = "Events could not be saved as " + FILE_NAME;


    public SaveAction() {
      super("Save");
    }

    public void actionPerformed(ActionEvent e) {
      Writer out = null;
      try {
        out = new BufferedWriter(new FileWriter(FILE_NAME));
        out.write(HEADER + "\n");

        for (Object eventLine : listModel.toArray()) {
          out.write(eventLine + "\n");
        }
        JOptionPane.showMessageDialog(null, SUCCESS_MSG);
      } catch (IOException ex) {
        JOptionPane.showMessageDialog(null, ERR_MSG, "Error " + ex.getMessage(), JOptionPane.WARNING_MESSAGE);
        logger.fatal(ex);
      } finally {
        IOUtil.closeStream(out);
      }
    }
  }
}
