package com.customwars.map.location;

import com.customwars.map.Tile;
import com.customwars.unit.Army;
/*
 *Base.java
 *Author: Urusan
 *Contributors:
 *Creation: July 15, 2006, 4:19 PM
 *The Base class is used to create an instance of the Base Property.
 */

public class Base extends Property{

    //constructor, creates a neutral base
    public Base() {
        super();
        initStatistics();
    }
    
    //constructor, creates a Base owned by an army
    public Base(Army army) {
        super(army);
        initStatistics();
        army.addProperty(this);
    }
    
    //constructor, creates a neutral property
    public Base(Tile t) {
        super(t);
        initStatistics();
    }
    
    //constructor, creates a Base owned by an army
    public Base(Army army, Tile t) {
        super(army,t);
        initStatistics();
        army.addProperty(this);
    }
    
    private void initStatistics(){
        //Statistics
        name = "Base";
        setMove(new double[] {1,1,1,1,1,-1,-1,1,1,1});
        basemove = new double[] {1,1,1,1,1,-1,-1,1,1,1};
        def  = 3;
        index = 11;
        income = 1000;
        setTotalcp(20);
        isCapturable = true;
        setRepairLand(true);
        setRepairSea(false);
        setRepairAir(false);
        setCreateLand(true);
        setCreateSea(false);
        setCreateAir(false);
        urban = true;
        
        //Set current Capture Points
        setCp(getTotalcp());
    }
}