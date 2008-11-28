package cwsource;
/*
 *Infantry.java
 *Author: Adam Dziuk
 *Contributors: Urusan
 *Creation: June 24, 2006, 11:51 PM
 *The Infantry class is used to create an instance of the Infantry Unit
 */

public class Infantry extends Unit{
    
    //constructor
    public Infantry(int row, int col, Army arm, Map m) {
        super(new Location(row, col),arm,m);
        
        //Statistics
        name = "Infantry";
        unitType = 0;
        moveType =  MOVE_INFANTRY;
        move = 3;
        price = 1000;
        maxGas = 99;
        maxAmmo = -1;
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