package com.customwars.client.io.converter;

import com.customwars.client.model.gameobject.Weapon;
import com.customwars.client.model.gameobject.WeaponFactory;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * Read a weapon ID + ammo from a xml element
 *
 * @author stefan
 */
public class UnitWeaponConverter implements Converter {

  public void marshal(Object object, HierarchicalStreamWriter hierarchicalStreamWriter, MarshallingContext marshallingContext) {
    throw new UnsupportedOperationException("Weapons only have read from xml support");
  }

  public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext unmarshallingContext) {
    Weapon weapon = WeaponFactory.getWeapon(reader.getAttribute("id"));
    reader.moveDown();
    int ammo = Integer.valueOf(reader.getValue());
    weapon.setAmmo(ammo);
    reader.moveUp();
    return weapon;
  }

  public boolean canConvert(Class aClass) {
    return aClass.equals(Weapon.class);
  }
}
