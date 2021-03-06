package com.customwars.map.location;

import com.customwars.map.Tile;
import com.customwars.unit.Army;
/*
 *HQ.java
 *Author: Urusan
 *Contributors:
 *Creation: July 16, 2006, 4:00 AM
 *The HQ class is used to create an instance of the HQ Property.
 */

public class HQ extends Property{
    //constructor, creates a tileless HQ owned by an army
    public HQ(Army army) {
        super(army);
        initStatistics();
        army.addProperty(this);
    }
    
    //constructor, creates a HQ owned by an army
    public HQ(Army army, Tile t){
        super(army,t);
        initStatistics();
        army.addProperty(this);
    }
    
    private void initStatistics(){
        //special for HQ's only
        color--;
        
        //Statistics
        name = "HQ";
        setMove(new double[] {1,1,1,1,1,-1,-1,1,-1,1});
        basemove = new double[] {1,1,1,1,1,-1,-1,1,-1,1};
        def  = 4;
        index = 9;
        income = 1000;
        setTotalcp(20);
        isCapturable = true;
        setRepairLand(true);
        setRepairSea(false);
        setRepairAir(false);
        setCreateLand(false);
        setCreateSea(false);
        setCreateAir(false);
        urban = true;
        
        //Set current Capture Points
        setCp(getTotalcp());
    }
}
