package cwsource;
/*
 *Rockets.java
 *Author: Eskimoconvntion
 *Contributors:
 *Creation: 17/7/06
 *The Rockets class is used to create an instance of the Rockets Unit
 */

public class Rockets extends Unit{
   
    //constructor
    public Rockets(int row, int col, Army arm, Map m) {
        super(new Location(row, col),arm,m);
       
        //Statistics
        name = "Rockets";
        unitType = 8;
        moveType = MOVE_TIRE;
        move = 5;
        price = 14000;
        maxGas = 50;
        maxAmmo = 6;
        vision = 2;
        minRange = 3;
        maxRange = 5;
       
        starValue = 1.4;
       
        //Fills the Unit's gas and ammo
        gas = maxGas;
        ammo = maxAmmo;
        
        //make CO adjustments
        arm.getCO().setChange(this);
    }
}