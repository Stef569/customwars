package cwsource;
/*
 *Artillery.java
 *Author: Adam Dziuk
 *Contributors: Urusan
 *Creation: June 27, 2006, 7:51 PM
 *The Artillery class is used to create an instance of the Artillery Unit
 */

public class Artillery extends Unit{
    
    //constructor
    public Artillery(int row, int col, Army arm, Map m) {
        super(new Location(row, col),arm,m);
        
        //Statistics
        name = "Artillery";
        unitType = 7;
        moveType = MOVE_TREAD;
        move = 5;
        price = 6000;
        maxGas = 50;
        maxAmmo = 9;
        vision = 1;
        minRange = 2;
        maxRange = 3;
        
        starValue = 1.0;
        
        //Fills the Unit's gas and ammo
        gas = maxGas;
        ammo = maxAmmo;
        
        //make CO adjustments
        arm.getCO().setChange(this);
    }
}