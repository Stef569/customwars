package com.customwars;
/*
 *Carrier.java
 *Author: Xaif
 *Contributors:
 *Creation: 17/7/06
 *The Carrier class is used to create an instance of the Carrier Unit
 */

public class Carrier extends Transport{
        boolean launched;
       boolean builtUnit;
    //constructor
    public Carrier(int row, int col, Army arm, Map m) {
        
        super(new Location(row, col),arm,m);
        launched = false;
        builtUnit = false;
        //Statistics
        name = "Carrier";
        unitType = 22;
        setMoveType(MOVE_SEA);
        setMove(5);
        price = 25000;
        setMaxGas(99);
        setMaxAmmo(9);
        setVision(4);
        minRange = 3;
        setMaxRange(8);
        setDailyGas(1);
       
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
        setGas(getMaxGas());
        setAmmo(getMaxAmmo());
        
        //make CO adjustments
        arm.getCO().setChange(this);
    }
    //for a carrier, limited only to the movement of the unit being unloaded
    public boolean checkUnloadRange(Location l, int unloadslot){
        Map m = getMap();
        if(m.onMap(l)){
            int r = Math.abs(l.getRow() - getLoc().getRow()) + Math.abs(l.getCol() - getLoc().getCol());
            if(r > 0 && r <= 1){
                Tile t = m.find(new Location(l.getCol(),l.getRow()));
                if(t!=null){
                    if(t.getTerrain().moveCost(this.getUnit(unloadslot).getMType())!=-1 && !(t.hasUnit() && !t.getUnit().isHidden()))return true;
                }
            }
        }
        return false;
    }
    public boolean usedAction(){
        return (builtUnit || launched);
    }
}