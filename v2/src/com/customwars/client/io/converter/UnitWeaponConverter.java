package com.customwars.client.io.converter;

import com.customwars.client.model.gameobject.Weapon;
import com.customwars.client.model.gameobject.WeaponFactory;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.core.util.Fields;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import java.lang.reflect.Field;

/**
 * Read a weapon ID + max ammo from an xml element
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
    int maxAmmo = Integer.valueOf(reader.getValue());
    Field field = Fields.find(Weapon.class, "maxAmmo");
    Fields.write(field, weapon, maxAmmo);
    reader.moveUp();
    return weapon;
  }

  public boolean canConvert(Class aClass) {
    return aClass.equals(Weapon.class);
  }
}
