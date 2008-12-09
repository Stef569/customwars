package com.customwars;
/*
 *Port.java
 *Author: TheChronoMaster
 *Contributors:
 *Creation:
 *The Port class is used to create an instance of the Port Property.
 */

public class Port extends Property{
   
    //constructor, creates a neutral property
    public Port() {
        super();
        initStatistics();
    }
   
    //constructor, creates a City owned by an army
    public Port(Army army) {
        super(army);
        initStatistics();
        army.addProperty(this);
    }
    
    //constructor, creates a neutral property
    public Port(Tile t) {
        super(t);
        initStatistics();
    }
    
    //constructor, creates a Port owned by an army
    public Port(Army army, Tile t) {
        super(army,t);
        initStatistics();
        army.addProperty(this);
    }
    
    private void initStatistics(){
        //Statistics
        name = "Port";
        move = new double[] {1,1,1,1,1,1,1,1,-1,1};
        basemove = new double[] {1,1,1,1,1,1,1,1,-1,1};
        def  = 3;
        index = 13;
        income = 1000;
        totalcp = 20;
        isCapturable = true;
        repairLand = false;
        repairSea = true;
        repairAir = false;
        createLand = false;
        createSea = true;
        createAir = false;
        urban = true;
       
        //Set current Capture Points
        cp = totalcp;
    }
}