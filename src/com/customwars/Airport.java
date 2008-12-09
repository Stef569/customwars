package com.customwars;
/*
 *Airport.java
 *Author: veggiehunter
 *Contributors:
 *Creation: 7/17/2006
 *The Airport class is used to create an instance of the Airport Property.
 */

public class Airport extends Property{
   
    //constructor, creates a neutral property
    public Airport() {
        super();
        initStatistics();
    }
   
    //constructor, creates a Airport owned by an army
    public Airport(Army army) {
        super(army);
        initStatistics();
        army.addProperty(this);
    }
    
    //constructor, creates a neutral property
    public Airport(Tile t) {
        super(t);
        initStatistics();
    }
    
    //constructor, creates an Airport owned by an army
    public Airport(Army army, Tile t) {
        super(army,t);
        initStatistics();
        army.addProperty(this);
    }
    
    private void initStatistics(){
        //Statistics
        name = "Airport";
        move = new double[] {1,1,1,1,1,-1,-1,1,-1,1};
        basemove = new double[] {1,1,1,1,1,-1,-1,1,-1,1};
        def  = 3;
        index = 12;
        income = 1000;
        totalcp = 20;
        isCapturable = true;
        repairLand = false;
        repairSea = false;
        repairAir = true;
        createLand = false;
        createSea = false;
        createAir = true;
        urban = true;
       
        //Set current Capture Points
        cp = totalcp;
    }
}