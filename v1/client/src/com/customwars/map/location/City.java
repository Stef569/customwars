package com.customwars.map.location;

import com.customwars.map.Tile;
import com.customwars.unit.Army;
/*
 *City.java
 *Author: Urusan
 *Contributors:
 *Creation: July 12, 2006, 1:51 AM
 *The City class is used to create an instance of the City Property.
 */

public class City extends Property{
    
    //constructor, creates a tileless neutral property
    public City() {
        super();
        initStatistics();
    }
    
    //constructor, creates a tileless City owned by an army
    public City(Army army) {
        super(army);
        initStatistics();
        army.addProperty(this);
    }
    
    //constructor, creates a neutral property
    public City(Tile t) {
        super(t);
        initStatistics();
    }
    
    //constructor, creates a City owned by an army
    public City(Army army, Tile t) {
        super(army,t);
        initStatistics();
        army.addProperty(this);
    }
    
    private void initStatistics(){
        //Statistics
        name = "City";
        setMove(new double[] {1,1,1,1,1,-1,-1,1,-1,1});
        basemove = new double[] {1,1,1,1,1,-1,-1,1,-1,1};
        def  = 3;
        index = 10;
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
