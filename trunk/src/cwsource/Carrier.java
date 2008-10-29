package cwsource;
/*
 *Carrier.java
 *Author: Xaif
 *Contributors:
 *Creation: 17/7/06
 *The Carrier class is used to create an instance of the Carrier Unit
 */

public class Carrier extends Transport{
   
    //constructor
    public Carrier(int row, int col, Army arm, Map m) {
        super(new Location(row, col),arm,m);
       
        //Statistics
        name = "Carrier";
        unitType = 22;
        moveType = MOVE_SEA;
        move = 5;
        price = 25000;
        maxGas = 99;
        maxAmmo = 9;
        vision = 4;
        minRange = 3;
        maxRange = 8;
        dailyGas = 1;
       
        starValue = 2.2;
        
        //Transport Statistics
        maxUnits = 2;
        transportTable[14]=true;
        transportTable[15]=true;
        transportTable[16]=true;
        transportTable[17]=true;
        transportTable[23]=true;
        transportTable[24]=true;
        transportTable[28]=true;
        transportTable[29]=true;
       
        //Fills the Unit's gas and ammo
        gas = maxGas;
        ammo = maxAmmo;
        
        //make CO adjustments
        arm.getCO().setChange(this);
    }
}