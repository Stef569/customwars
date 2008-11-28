package cwsource;
/*
 *Recon.java
 *Author: Eskimoconvntion
 *Contributors:
 *Creation: 17/7/06
 *The Recon class is used to create an instance of the Recon Unit
 */

public class Recon extends Unit{
   
    //constructor
    public Recon(int row, int col, Army arm, Map m) {
        super(new Location(row, col),arm,m);
       
        //Statistics
        name = "Recon";
        unitType = 4;
        moveType = MOVE_TIRE;
        move = 8;
        price = 4000;
        maxGas = 80;
        maxAmmo = -1;
        vision = 5;
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