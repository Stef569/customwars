package com.customwars.client.tools;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * This Map stores it's keys as strings in upper case, null and duplicate keys are not allowed
 * Attempting to add a duplicate object in this map results in an IllegalArgumentException
 * Null values are not allowed
 *
 * @param <T> The type of the object stored in this UCaseMap
 */
public class UCaseMap<T> implements Map<String, T> {
  private final Map<String, T> cache;

  public UCaseMap() {
    cache = new HashMap<String, T>();
  }

  public UCaseMap(int initialCapacity) {
    cache = new HashMap<String, T>(initialCapacity);
  }

  public boolean containsKey(Object key) {
    if (key != null) {
      String s = (String) key;
      return cache.containsKey(s.toUpperCase());
    } else {
      return false;
    }
  }

  public boolean containsValue(Object value) {
    return cache.containsValue(value);
  }

  public T get(Object objKey) {
    if (objKey == null) {
      throw new IllegalArgumentException("objKey cannot be null");
    }

    String strKey = (String) objKey;
    String uCaseKey = strKey.toUpperCase();

    if (cache.containsKey(uCaseKey)) {
      return cache.get(uCaseKey);
    } else {
      throw new IllegalArgumentException("No obj found for '" + uCaseKey + "' available keys " + cache.keySet());
    }
  }

  public T put(String key, T value) {
    String uCaseKey = key.toUpperCase();
    if (value == null) {
      return get(key);
    }

    if (!cache.containsKey(uCaseKey)) {
      return cache.put(uCaseKey, value);
    } else {
      throw new IllegalArgumentException("key " + uCaseKey + " is already cached");
    }
  }

  public T remove(Object key) {
    return cache.remove(key);
  }

  public void putAll(Map<? extends String, ? extends T> map) {
    cache.putAll(map);
  }

  public int size() {
    return cache.size();
  }

  public boolean isEmpty() {
    return cache.isEmpty();
  }

  public void clear() {
    cache.clear();
  }

  public Set<String> keySet() {
    return cache.keySet();
  }

  public Collection<T> values() {
    return cache.values();
  }

  public Set<Entry<String, T>> entrySet() {
    return cache.entrySet();
  }

  @Override
  public String toString() {
    return cache.toString();
  }
}