package cwsource;
/*
 *Tank.java
 *Author: Killian Hanlon
 *Contributors:
 *Creation: 17/7/06
 *The Tank class is used to create an instance of theTank Unit
 */

public class Tank extends Unit{
   
    //constructor
    public Tank(int row, int col, Army arm, Map m) {
        super(new Location(row, col),arm,m);
       
        //Statistics
        name = "Tank";
        unitType = 2;
        moveType = MOVE_TREAD;
        move = 6;
        price = 7000;
        maxGas = 70;
        maxAmmo = 9;
        vision = 3;
        minRange = 1;
        maxRange = 1;
       
        starValue = 1.0;
       
        //Fills the Unit's gas and ammo
        gas = maxGas;
        ammo = maxAmmo;
        
        //make CO adjustments
        arm.getCO().setChange(this);
    }
}