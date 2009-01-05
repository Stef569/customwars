package com.customwars.unit;

/*
 *Artillery.java
 *Author: Adam Dziuk
 *Contributors: Urusan
 *Creation: June 27, 2006, 7:51 PM
 *The Artillery class is used to create an instance of the Artillery Unit
 */
import com.customwars.map.Map;
import com.customwars.map.location.Location;

public class Artillery extends Unit{
    
    //constructor
    public Artillery(int row, int col, Army arm, Map m) {
        super(new Location(row, col),arm,m);
        
        //Statistics
        name = "Artillery";
        unitType = 7;
        setMoveType(MOVE_TREAD);
        setMove(5);
        price = 6000;
        setMaxGas(50);
        setMaxAmmo(9);
        setVision(1);
        minRange = 2;
        setMaxRange(3);
        
        starValue = 1.0;
        
        //Fills the Unit's gas and ammo
        setGas(getMaxGas());
        setAmmo(getMaxAmmo());
        
        //make CO adjustments
        arm.getCO().setChange(this);
    }
}