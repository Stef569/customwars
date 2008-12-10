package com.customwars;
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
        move = new double[] {1,1,1,1,1,-1,-1,1,1,1};
        basemove = new double[] {1,1,1,1,1,-1,-1,1,1,1};
        def  = 3;
        index = 11;
        income = 1000;
        totalcp = 20;
        isCapturable = true;
        repairLand = true;
        repairSea = false;
        repairAir = false;
        createLand = true;
        createSea = false;
        createAir = false;
        urban = true;
        
        //Set current Capture Points
        cp = totalcp;
    }
}