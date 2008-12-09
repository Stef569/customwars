package com.customwars;
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
        move = new double[] {1,1,1,1,1,-1,-1,1,-1,1};
        basemove = new double[] {1,1,1,1,1,-1,-1,1,-1,1};
        def  = 3;
        index = 10;
        income = 1000;
        totalcp = 20;
        isCapturable = true;
        repairLand = true;
        repairSea = false;
        repairAir = false;
        createLand = false;
        createSea = false;
        createAir = false;
        urban = true;
        
        //Set current Capture Points
        cp = totalcp;
    }
}
