package com.customwars;

import com.customwars.unit.Army;
/*
 *Pipestation.java
 *Author: Xaif
 *Contributors: Urusan
 *Creation: August 13, 2006, 7:34 AM
 *The Pipestation class is used to create an instance of the Pipestation Property.
 */

public class Pipestation extends Property{
    //constructor, creates a neutral base
    public Pipestation() {
        super();
        initStatistics();
    }
    
    //constructor, creates a Base owned by an army
    public Pipestation(Army army) {
        super(army);
        initStatistics();
        army.addProperty(this);
    }
    
    //constructor, creates a neutral property
    public Pipestation(Tile t) {
        super(t);
        initStatistics();
    }
    
    //constructor, creates a Base owned by an army
    public Pipestation(Army army, Tile t) {
        super(army,t);
        initStatistics();
        army.addProperty(this);
    }
    
    private void initStatistics(){
        //Statistics
        name = "Pipestation";
        setMove(new double[] {1,1,1,1,1,-1,-1,1,1,1});
        basemove = new double[] {1,1,1,1,1,-1,-1,1,1,1};
        def  = 3;
        index = 17;
        income = 1000;
        setTotalcp(20);
        isCapturable = true;
        setRepairLand(false);
        setRepairSea(false);
        setRepairAir(false);
        setCreateLand(false);
        setCreateSea(false);
        setCreateAir(false);
        setRepairPipe(true);
        setCreatePipe(true);
        urban = true;
        
        //Set current Capture Points
        setCp(getTotalcp());
    }
}
