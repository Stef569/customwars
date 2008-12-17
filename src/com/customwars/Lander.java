package com.customwars;


public class Lander extends Transport{
   
    //constructor
    public Lander(int row, int col, Army arm, Map m) {
        super(new Location(row, col),arm,m);
       
        //Statistics
        name = "Lander";
        unitType = 10;
        setMoveType(MOVE_TRANSPORT);
        setMove(6);
        if (army.getBattle().getBattleOptions().isBalance()==true){
            price = 10000;
        }
        if (army.getBattle().getBattleOptions().isBalance()==false){
        price = 12000;}
        setMaxGas(99);
        setMaxAmmo(-1);
        setVision(1);
        minRange = 0;
        setMaxRange(0);
        setDailyGas(1);
        
        starValue = 1.0;
        
        //Transport Statistics
        maxUnits = 2;
        for(int i=0;i<10;i++)transportTable[i]=true;
        transportTable[18]=true;
        transportTable[19]=true;
        //transportTable[20]=true;
       
        //Fills the Unit's gas and ammo
        setGas(getMaxGas());
        setAmmo(getMaxAmmo());
        
        //make CO adjustments
        arm.getCO().setChange(this);
    }
}