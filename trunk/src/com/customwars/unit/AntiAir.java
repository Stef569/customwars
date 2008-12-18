package com.customwars.unit;

/*
 *Anti-Air.java
 *Author: Limbo the Monkey
 *Contributors:
 *Creation: 18/7/06
 *The Anti-Air class is used to create an instance of the Anti-Air Unit
 */
import com.customwars.map.Map;
import com.customwars.map.location.Location;

public class AntiAir extends Unit{
   
    //constructor
    public AntiAir(int row, int col, Army arm, Map m) {
        super(new Location(row, col),arm,m);
       
        //Statistics
        name = "Anti-Air";
        unitType = 5;
        setMoveType(MOVE_TREAD);
        setMove(6);
        price = 8000;
        setMaxGas(60);
        setMaxAmmo(9);
        setVision(2);
        minRange = 1;
        setMaxRange(1);
       
        starValue = 1.0;
       
        //Fills the Unit's gas and ammo
        setGas(getMaxGas());
        setAmmo(getMaxAmmo());
        
        //make CO adjustments
        arm.getCO().setChange(this);
    }
}