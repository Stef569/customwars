package com.customwars;
/*
 *ComTower.java
 *Author: Jon Christensen
 *Contributors:
 *Creation: 7/18/06
 *The ComTower class is used to create an instance of the Communication Tower Property.
 */

public class ComTower extends Property{
    
    //constructor, creates a neutral property
    public ComTower() {
        super();
        initStatistics();
    }
    
    //constructor, creates a ComTower owned by an army
    public ComTower(Army army) {
        super(army);
        initStatistics();
        army.addProperty(this);
    }
    
    //constructor, creates a neutral property
    public ComTower(Tile t) {
        super(t);
        initStatistics();
    }
    
    //constructor, creates a ComTower owned by an army
    public ComTower(Army army, Tile t) {
        super(army,t);
        initStatistics();
        army.addProperty(this);
    }
    
    private void initStatistics(){
        //Statistics
        name = "Com Tower";
        move = new double[] {1,1,1,1,1,-1,-1,1,-1,1};
        basemove = new double[] {1,1,1,1,1,-1,-1,1,-1,1};
        def = 3;
        index = 14;
        income = 0;
        totalcp = 20;
        isCapturable = true;
        repairLand = false;
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