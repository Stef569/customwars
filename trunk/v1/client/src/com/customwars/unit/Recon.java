package com.customwars.unit;

/*
 *Recon.java
 *Author: Eskimoconvntion
 *Contributors:
 *Creation: 17/7/06
 *The Recon class is used to create an instance of the Recon Unit
 */
import com.customwars.map.Map;
import com.customwars.map.location.Location;

public class Recon extends Unit{
   
    //constructor
    public Recon(int row, int col, Army arm, Map m) {
        super(new Location(row, col),arm,m);
       
        //Statistics
        name = "Recon";
        unitType = 4;
        setMoveType(MOVE_TIRE);
        setMove(8);
        price = 4000;
        setMaxGas(80);
        setMaxAmmo(-1);
        setVision(5);
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