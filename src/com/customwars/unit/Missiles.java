package com.customwars.unit;

/*
 *Missiles.java
 *Author: Limbo the Monkey
 *Contributors:
 *Creation: 18/7/06
 *The Missiles class is used to create an instance of the Missiles Unit
 */
import com.customwars.map.Map;
import com.customwars.map.location.Location;

public class Missiles extends Unit{
   
    //constructor
    public Missiles(int row, int col, Army arm, Map m) {
        super(new Location(row, col),arm,m);
       
        //Statistics
        name = "Missiles";
        unitType = 6;
        setMoveType(MOVE_TIRE);
        setMove(5);
        price = 12000;
        setMaxGas(50);
        setMaxAmmo(6);
        setVision(5);
        minRange = 3;
        setMaxRange(6);
       
        starValue = 1.4;
       
        //Fills the Unit's gas and ammo
        setGas(getMaxGas());
        setAmmo(getMaxAmmo());
        
        //make CO adjustments
        arm.getCO().setChange(this);
    }
}