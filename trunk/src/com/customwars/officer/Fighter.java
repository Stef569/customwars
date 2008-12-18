package com.customwars.officer;

import com.customwars.Location;
import com.customwars.Map;
import com.customwars.unit.Army;
import com.customwars.unit.Unit;

public class Fighter extends Unit{
   
    //constructor
    public Fighter(int row, int col, Army arm, Map m) {
        super(new Location(row, col),arm,m);
       
        //Statistics
        name = "Fighter";
        unitType = 16;
        setMoveType(MOVE_AIR);
        setMove(9);
        price = 20000;
        setMaxGas(99);
        setMaxAmmo(9);
        setVision(2);
        minRange = 1;
        setMaxRange(1);
        setDailyGas(5);
       
        starValue = 1.8;
       
        //Fills the Unit's gas and ammo
        setGas(getMaxGas());
        setAmmo(getMaxAmmo());
        
        //make CO adjustments
        arm.getCO().setChange(this);
    }
}