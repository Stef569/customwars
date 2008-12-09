package com.customwars;
/*
 *APC.java
 *Author: Limbo the Monkey
 *Contributors:
 *Creation: 18/7/06
 *The APC class is used to create an instance of the APC Unit
 */

public class APC extends Transport{
    
    //constructor
    public APC(int row, int col, Army arm, Map m) {
        super(new Location(row, col),arm,m);
        
        //Statistics
        name = "APC";
        unitType = 9;
        moveType = MOVE_TREAD;
        move = 6;
        price = 5000;
        maxGas = 70;
        maxAmmo = -1;
        vision = 1;
        minRange = 0;
        maxRange = 0;
        
        starValue = 0.8;
        
        //Transport Statistics
        maxUnits = 1;
        transportTable[0]=true;
        transportTable[1]=true;
        
        //Fills the Unit's gas and ammo
        gas = maxGas;
        ammo = maxAmmo;
        
        //make CO adjustments
        arm.getCO().setChange(this);
    }
    
    public void resupplyAdjacent(){
        int x = this.getLocation().getCol(), y = this.getLocation().getRow();
        Tile north = map.find(new Location(x,y-1));
        Tile east = map.find(new Location(x+1,y));
        Tile south = map.find(new Location(x,y+1));
        Tile west = map.find(new Location(x-1,y));
        
        if(north != null && north.hasUnit() && north.getUnit().getArmy() == this.getArmy() && !north.getUnit().noResupplied){
            north.getUnit().resupply();
            doAfterAction(north);
        }
        if(east != null && east.hasUnit() && east.getUnit().getArmy() == this.getArmy() && !east.getUnit().noResupplied){
            east.getUnit().resupply();
            doAfterAction(east);
        }
        if(south != null && south.hasUnit() && south.getUnit().getArmy() == this.getArmy() && !south.getUnit().noResupplied){
            south.getUnit().resupply();
            doAfterAction(south);
        }
        if(west != null && west.hasUnit() && west.getUnit().getArmy() == this.getArmy() && !west.getUnit().noResupplied){
            west.getUnit().resupply();
            doAfterAction(west);
        }
    }
    
    private void doAfterAction(Tile unitTile){
        this.getArmy().getCO().afterAction(this, 3, unitTile.getUnit(), true);
        if(this.getArmy().getAltCO()!=null)
            this.getArmy().getAltCO().afterAction(this, 3, unitTile.getUnit(), false);
        
        Army[] armies = this.getArmy().getBattle().getArmies();
        for(int i = 0; i < armies.length; i++) {
            if(this.getArmy().getSide()!= armies[i].getSide()) {
                armies[i].getCO().afterEnemyAction(this, 3, unitTile.getUnit(), true);
                if(armies[i].getAltCO() != null)
                    armies[i].getAltCO().afterEnemyAction(this, 3, unitTile.getUnit(), false);
            }
        }
    }
}