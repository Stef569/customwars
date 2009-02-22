package com.customwars.client.io.img;

import org.newdawn.slick.Animation;
import tools.Args;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Contains all the animations, keyed by name
 *
 * @author stefan
 */
public class AnimLib {
  private static Map<String, Animation> animations = new HashMap<String, Animation>();

  public void addAnim(String animName, Animation anim) {
    Args.checkForNull(anim);
    if (!isAnimLoaded(animName)) {
      animations.put(animName, anim);
    }
  }

  public Animation getAnim(String animName) {
    if (!animations.containsKey(animName)) {
      throw new IllegalArgumentException(
              "Animation cache does not contain " + animName + " animations: " + animations.keySet());
    }
    return animations.get(animName);
  }

  public boolean isAnimLoaded(String animName) {
    return animations.containsKey(animName);
  }

  public Collection<Animation> getAllAnims() {
    return Collections.unmodifiableCollection(animations.values());
  }
}
