package com.customwars.unit;

import com.customwars.Location;
import com.customwars.Map;
/*
 *Destroyer.java
 *Author: Xaif
 *Contributors:
 *Creation: 29/7/06
 *The Destroyer class is used to create an instance of the Destroyer Unit
 */

public class Destroyer extends Unit{
   
    //constructor
    public Destroyer(int row, int col, Army arm, Map m) {
        super(new Location(row, col),arm,m);
       
        //Statistics
        name = "Destroyer";
        unitType = 30;
        setMoveType(MOVE_SEA);
        setMove(6);
        if (army.getBattle().getBattleOptions().isBalance()==true){
            price = 15000;
        }
        if (army.getBattle().getBattleOptions().isBalance()==false){
        price = 18000;}
        setMaxGas(99);
        setMaxAmmo(9);
        setVision(2);
        minRange = 1;
        setMaxRange(1);
        setDailyGas(1);
       
        starValue = 1.6;
       
        //Fills the Unit's gas and ammo
        setGas(getMaxGas());
        setAmmo(getMaxAmmo());
       
        //make CO adjustments
        arm.getCO().setChange(this);
    }
}