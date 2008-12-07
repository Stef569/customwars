package cwsource;
/*
 *MegaTank.java
 *Author: Limbo the Monkey
 *Contributors:
 *Creation: 18/7/06
 *The MegaTank class is used to create an instance of the MegaTank Unit
 */

public class MegaTank extends Unit{
   
    //constructor
    public MegaTank(int row, int col, Army arm, Map m) {
        super(new Location(row, col),arm,m);
       
        //Statistics
        name = "Megatank";
        unitType = 19;
        moveType = MOVE_TREAD;
        move = 4;
        price = 20000;
        maxGas = 50;
        maxAmmo = 3;
        vision = 1;
        minRange = 1;
        maxRange = 1;
       
        starValue = 2.2;
       
        //Fills the Unit's gas and ammo
        gas = maxGas;
        ammo = maxAmmo;
        
        //make CO adjustments
        arm.getCO().setChange(this);
    }
}