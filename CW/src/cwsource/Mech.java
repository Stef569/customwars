package cwsource;
/*
 *Mech.java
 *Author: Adam Dziuk
 *Contributors: Urusan
 *Creation: July 14, 2006, 5:46 PM
 *The Mech class is used to create an instance of the Mech Unit
 */

public class Mech extends Unit{
    
    //constructor
    public Mech(int row, int col, Army arm, Map m) {
        super(new Location(row, col),arm,m);
        
        //Statistics
        name = "Mech";
        unitType = 1;
        moveType = MOVE_MECH;
        move = 2;
        price = 3000;
        maxGas = 70;
        maxAmmo = 3;
        vision = 2;
        minRange = 1;
        maxRange = 1;
        
        starValue = 0.4;
        
        //Fills the Unit's gas and ammo
        gas = maxGas;
        ammo = maxAmmo;
        
        //make CO adjustments
        arm.getCO().setChange(this);
    }
}

