package com.customwars.unit;

/*
 *Battlecraft.java
 *Author: Xaif
 *Contributors:
 *Creation: 23/7/06
 *The Battlecraft class is used to create an instance of the Battlecraft Unit
 */
import com.customwars.map.Map;
import com.customwars.map.location.Location;

public class Battlecraft extends Unit{
   
    //constructor
    public Battlecraft(int row, int col, Army arm, Map m) {
        super(new Location(row, col),arm,m);
       
        //Statistics
        name = "Bcraft";
        unitType = 25;
        setMoveType(MOVE_HOVER);
        setMove(5);
        if(m.find(this).getTerrain().getName().equals("Port"))
            price = 15000;
        else
            price = 10000;
        setMaxGas(70);
        setMaxAmmo(9);
        setVision(3);
        minRange = 1;
        setMaxRange(1);
       
        starValue = 1.2;
       
        //Fills the Unit's gas and ammo
        setGas(getMaxGas());
        setAmmo(getMaxAmmo());
        
        //make CO adjustments
        arm.getCO().setChange(this);
    }
}