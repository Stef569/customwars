package com.customwars.unit;

/*
 *Spyplane.java
 *Author: Xaif
 *Contributors:
 *Creation: 29/7/06
 *The Spyplane class is used to create an instance of the Spyplane Unit
 */
import com.customwars.map.Map;
import com.customwars.map.location.Location;

public class Spyplane extends Unit{
   
    //constructor
    public Spyplane(int row, int col, Army arm, Map m) {
        super(new Location(row, col),arm,m);
       
        //Statistics
        name = "Spyplane";
        unitType = 29;
        setMoveType(MOVE_AIR);
        setMove(8);
        price = 15000;
        setMaxGas(99);
        setMaxAmmo(-1);
        setVision(7);
        minRange = 0;
        setMaxRange(0);
        setDailyGas(5);
       
        starValue = 2.2;
       
        //Fills the Unit's gas and ammo
        setGas(getMaxGas());
        setAmmo(getMaxAmmo());
       
        //make CO adjustments
        arm.getCO().setChange(this);
    }
}