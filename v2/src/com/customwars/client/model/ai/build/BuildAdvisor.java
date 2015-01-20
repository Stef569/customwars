package com.customwars.client.model.ai.build;

/**
 * The Build Advisor attempts to find the best Build Strategy.
 */
public interface BuildAdvisor {
  BuildStrategy think();
}
