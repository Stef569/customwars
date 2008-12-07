package cwsource;
/*
 *Piperunner.java
 *Author: Limbo the Monkey
 *Contributors:
 *Creation: 18/7/06
 *The Piperunner class is used to create an instance of the Piperunner Unit
 */

public class Piperunner extends Unit{
   
    //constructor
    public Piperunner(int row, int col, Army arm, Map m) {
        super(new Location(row, col),arm,m);
       
        //Statistics
        name = "Piperunner";
        unitType = 20;
        moveType = MOVE_PIPE;
        move = 9;
        price = 18000;
        maxGas = 99;
        maxAmmo = 9;
        vision = 4;
        minRange = 2;
        maxRange = 5;
       
        starValue = 2.0;
       
        //Fills the Unit's gas and ammo
        gas = maxGas;
        ammo = maxAmmo;
        
        //make CO adjustments
        arm.getCO().setChange(this);
    }
}