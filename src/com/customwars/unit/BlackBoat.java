package com.customwars.unit;

/*
 *BlackBoat.java
 *Author: Xaif
 *Contributors:
 *Creation: 17/7/06
 *The BlackBoat class is used to create an instance of the BlackBoat Unit
 */
import com.customwars.map.Map;
import com.customwars.map.location.Location;

public class BlackBoat extends Transport{
   
    //constructor
    public BlackBoat(int row, int col, Army arm, Map m) {
        super(new Location(row, col),arm,m);
       
        //Statistics
        name = "Black Boat";
        unitType = 21;
        setMoveType(MOVE_TRANSPORT);
        setMove(7);
        price = 7500;
        setMaxGas(60);
        setMaxAmmo(-1);
        setVision(1);
        minRange = 0;
        setMaxRange(0);
        setDailyGas(1);
       
        starValue = 1.0;
        
        //Transport Statistics
        maxUnits = 2;
        transportTable[0]=true;
        transportTable[1]=true;
       
        //Fills the Unit's gas and ammo
        setGas(getMaxGas());
        setAmmo(getMaxAmmo());
        
        //make CO adjustments
        arm.getCO().setChange(this);
    }
}