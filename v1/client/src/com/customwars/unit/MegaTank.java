package com.customwars.unit;

/*
 *MegaTank.java
 *Author: Limbo the Monkey
 *Contributors:
 *Creation: 18/7/06
 *The MegaTank class is used to create an instance of the MegaTank Unit
 */
import com.customwars.map.Map;
import com.customwars.map.location.Location;

public class MegaTank extends Unit{
   
    //constructor
    public MegaTank(int row, int col, Army arm, Map m) {
        super(new Location(row, col),arm,m);
       
        //Statistics
        name = "Megatank";
        unitType = 19;
        setMoveType(MOVE_TREAD);
        setMove(4);
        price = 20000;
        setMaxGas(50);
        setMaxAmmo(3);
        setVision(1);
        minRange = 1;
        setMaxRange(1);
       
        starValue = 2.2;
       
        //Fills the Unit's gas and ammo
        setGas(getMaxGas());
        setAmmo(getMaxAmmo());
        
        //make CO adjustments
        arm.getCO().setChange(this);
    }
}