package com.customwars;

import com.customwars.unit.Army;
/*
 *Silo.java
 *Author: Urusan
 *Contributors:
 *Creation: July 27, 2006, 7:09 PM
 *The Silo class is used to create an instance of the Silo Property.
 */

public class Silo extends Property{
    boolean launched;
    
    //constructor, creates a tileless neutral Silo
    public Silo() {
        super();
        initStatistics();
    }
    
    //constructor, creates a tileless neutral Silo
    public Silo(Army army) {
        super();
        initStatistics();
    }
    
    //constructor, creates a neutral property
    public Silo(Tile t) {
        super(t);
        initStatistics();
    }
    
    //constructor, creates a Silo owned by an army
    public Silo(Army army, Tile t) {
        super(t);
        initStatistics();
    }
    
    private void initStatistics(){
        //Statistics
        name = "Silo";
        setMove(new double[] {1,1,1,1,1,-1,-1,1,-1,1});
        basemove = new double[] {1,1,1,1,1,-1,-1,1,-1,1};
        def = 3;
        index = 16;
        income = 0;
        setTotalcp(0);
        isCapturable = false;
        setRepairLand(false);
        setRepairSea(false);
        setRepairAir(false);
        setCreateLand(false);
        setCreateSea(false);
        setCreateAir(false);
        
        //Set current Capture Points
        setCp(getTotalcp());
        
        launched = false;
    }
    
    public void launch(){
        launched = true;
    }
    
    public boolean isLaunched(){
        return launched;
    }
}