package com.customwars.client.model.gameobject;

import com.customwars.client.tools.Args;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Holds the unit ID's that a unit can transport. This object is immutable.
 * APC example:
 * transports={inf, mech}
 * maxTransportCount=1
 */
public class TransportStats implements Serializable {
  private List<String> transports;        // Units that can be transported (empty when this unit can't transport)
  public final int maxTransportCount;     // Amount of units that can be transported

  /**
   * Create a NULL transport stats object
   */
  public TransportStats() {
    transports = Collections.<String>emptyList();
    maxTransportCount = 0;
  }

  public TransportStats(int maxTransportCount) {
    this.transports = new ArrayList<String>();
    this.maxTransportCount = maxTransportCount;
  }

  public void addAll(Iterable<String> transports) {
    if (transports != null) {
      for (String unitID : transports) {
        add(unitID);
      }
    }
  }

  public void add(String unitID) {
    transports.add(unitID);
  }

  public void init(UnitStats stats) {
    transports = Args.createEmptyListIfNull(transports);
    Args.validate(!transports.isEmpty() && maxTransportCount <= 0,
      "Max transport count should be >0 for unit " + stats.getName());
  }

  public void validate(UnitStats stats) {
    for (String unitName : transports) {
      Args.validate(!UnitFactory.hasUnitForName(unitName),
        "Illegal unit name " + unitName + " in transports stats for unit " + stats.getName());
    }
  }

  public boolean canTransport() {
    return !transports.isEmpty();
  }

  public boolean canTransport(String unitID) {
    return transports.contains(unitID);
  }

  public List<String> getTransports() {
    return Collections.unmodifiableList(transports);
  }

  @Override
  public String toString() {
    return "transports=" + transports + " max=" + maxTransportCount;
  }
}
