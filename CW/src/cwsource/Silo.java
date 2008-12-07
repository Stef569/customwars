package cwsource;
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
        move = new double[] {1,1,1,1,1,-1,-1,1,-1,1};
        basemove = new double[] {1,1,1,1,1,-1,-1,1,-1,1};
        def = 3;
        index = 16;
        income = 0;
        totalcp = 0;
        isCapturable = false;
        repairLand = false;
        repairSea = false;
        repairAir = false;
        createLand = false;
        createSea = false;
        createAir = false;
        
        //Set current Capture Points
        cp = totalcp;
        
        launched = false;
    }
    
    public void launch(){
        launched = true;
    }
    
    public boolean isLaunched(){
        return launched;
    }
}