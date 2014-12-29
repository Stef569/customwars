package com.customwars.client.action.unit;

import com.customwars.client.App;
import com.customwars.client.action.ActionCommandEncoder;
import com.customwars.client.action.DirectAction;
import com.customwars.client.model.GameController;
import com.customwars.client.model.gameobject.Terrain;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Location;
import com.customwars.client.network.MessageSender;
import com.customwars.client.network.NetworkException;
import com.customwars.client.ui.GUI;
import com.customwars.client.ui.state.InGameContext;
import com.customwars.client.ui.thingle.DialogListener;
import com.customwars.client.ui.thingle.DialogResult;
import org.apache.log4j.Logger;

/**
 * Overwrite the terrain on transformLocation to transformToTerrain
 */
public class TransformTerrainAction extends DirectAction {
  private static final Logger logger = Logger.getLogger(TransformTerrainAction.class);
  private final Unit unit;
  private final Location transformLocation;
  private final Terrain transformToTerrain;
  private InGameContext inGameContext;
  private GameController gameController;
  private MessageSender messageSender;

  public TransformTerrainAction(Unit unit, Location transformLocation, Terrain transformToTerrain) {
    super("Transform terrain", false);
    this.unit = unit;
    this.transformLocation = transformLocation;
    this.transformToTerrain = transformToTerrain;
  }

  @Override
  protected void init(InGameContext inGameContext) {
    this.inGameContext = inGameContext;
    gameController = inGameContext.getObj(GameController.class);
    messageSender = inGameContext.getObj(MessageSender.class);
  }

  @Override
  protected void invokeAction() {
    if (!inGameContext.isTrapped()) {
      transformTerrain();
    }
  }

  private void transformTerrain() {
    gameController.transformTerrain(unit, transformLocation, transformToTerrain);
    if (App.isMultiplayer()) sendTransformTerrain();
  }

  private void sendTransformTerrain() {
    try {
      messageSender.transformTerrain(transformLocation, transformToTerrain);
    } catch (NetworkException ex) {
      logger.warn("Could not send transform terrain", ex);
      GUI.askToResend(ex, new DialogListener() {
        public void buttonClicked(DialogResult button) {
          if (button == DialogResult.YES) {
            sendTransformTerrain();
          }
        }
      });
    }
  }

  @Override
  public String getActionCommand() {
    return new ActionCommandEncoder().add(transformToTerrain).build();
  }
}
