package com.customwars.unit;

/*
 *BlackBomb.java
 *Author: Xenesis
 *Contributors: Intelligent Systems
 *Creation: July 17, 2006
 *The BlackBomb class is used to create an instance of the BlackBomb Unit
 */
import com.customwars.map.Map;
import com.customwars.map.location.Location;

public class BlackBomb extends Unit{
    
    //constructor
    public BlackBomb(int row, int col, Army arm, Map m) {
        super(new Location(row, col),arm,m);
        
        //Statistics
        name = "Black Bomb";
        unitType = 24;
        setMoveType(MOVE_AIR);
        setMove(9);
        if(arm.getBattle().getBattleOptions().isBalance())
            price = 15000;
        else
            price = 25000;
        setMaxGas(45);
        setMaxAmmo(-1);
        setVision(1);
        minRange = 0;
        setMaxRange(0);
        setDailyGas(5);
        
        starValue = 0.6;
        
        //Fills the Unit's gas and ammo
        setGas(getMaxGas());
        setAmmo(getMaxAmmo());
        
        //make CO adjustments
        arm.getCO().setChange(this);
    }
}