package cwsource;
/*
 *MD Tank.java
 *Author: Limbo the Monkey
 *Contributors:
 *Creation: 18/7/06
 *The MDTank class is used to create an instance of the MDTank Unit
 */

public class MDTank extends Unit{
   
    //constructor
    public MDTank(int row, int col, Army arm, Map m) {
        super(new Location(row, col),arm,m);
       
        //Statistics
        name = "Md Tank";
        unitType = 3;
        moveType = MOVE_TREAD;
        move = 5;
        price = 15000;
        maxGas = 50;
        maxAmmo = 8;
        vision = 1;
        minRange = 1;
        maxRange = 1;
       
        starValue = 1.6;
       
        //Fills the Unit's gas and ammo
        gas = maxGas;
        ammo = maxAmmo;
        
        //make CO adjustments
        arm.getCO().setChange(this);
    }
}