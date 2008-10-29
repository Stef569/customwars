package cwsource;
/*
 *Blandie.java
 *Author: Urusan
 *Contributors:
 *Creation: December 11, 2006
 *A bland CO, perhaps a generic commanding officer?
 */

public class Eniac extends CO{
    boolean sustain = false; 
    //constructor
    public Eniac() {
        name = "Eniac";
        id = 67;
        
        String CObiox = "A computer hardware genius that was rescued by Jared from Black Hole. He now fights for the army that saved him.";
        //This is seperated into blocks 40 characters long!
        //Use this as a guide for a better look proper word-wrapping.
        String titlex = "Didn't Survive A Revote";
        String hitx = "Programming"; //Holds the hit
        String missx = "Viruses"; //Holds the miss
        String skillStringx = "If Eniac attacks from a terrain with less defense than the enemy, enemy terrain stars are halved.";
        String powerStringx = "Eniac can pierce through terrain defence."; //Holds the Power description
        String superPowerStringx = "All enemies lose defense depending on how the defense of their surrounding tiles. Movement costs are doubled."; //Holds the Super description
                              //"                                    " sizing markers
        String intelx = "" +
                        "" +
                        "" +
                        "" +
                        "" +
                        "";//Holds CO intel on CO select menu, 6 lines max
        
        intel = intelx;
        CObio = CObiox;
        title = titlex;
        hit = hitx;
        miss = missx;
        skillString = skillStringx;
        powerString = powerStringx;
        superPowerString = superPowerStringx;
        
        String[] COPowerx =
        {"Can you survive against technology?",
         "Let’s defragment this battlefield!",
         "How will you cope without your defenses?",
         "People always bet on an underdog...",
         "It's your biggest advantage, as well as your weakness.",
         "Troops! Attack their shelter!" };
        
        String[] Victoryx =
        {"Did your hard-drive melt or what?",
         "You should probably delete Suck.dat... yeah.",
         "Finally! I can get back to that coding..." };
        
        String[] Swapx =
        {"So that's what alt+tab does...",
         "It comes down to me and you now!"};
        
        COPower = COPowerx;
        Victory = Victoryx;
        Swap = Swapx;
        
        //No special tags
        String[] TagCOsx = {"Jared"," Koshi"}; //Names of COs with special tags
        String[] TagNamesx = {"Fire and Thunder", "Hard Drive"}; //Names of the corresponding Tags
        int[] TagStarsx = {1,1}; //Number of stars for each special tag.
        int[] TagPercentx = {110,110}; //Percent for each special tag.
        
        TagCOs = TagCOsx;
        TagNames = TagNamesx;
        TagStars = TagStarsx;
        TagPercent = TagPercentx;
        
        COPName = "Reverse Surge";
        SCOPName = "Server Crash";
        COPStars = 2.0;
        maxStars = 6.0;
        this.army = army;
        style = JADE_COSMOS;
    }
    
//used to get the attack bonus for damage calculation
    public int getAtk(Unit attacker, Unit defender){
        if(defender == null) {
            if(COP||SCOP)return 110;
            return 100;
        }
        if(!COP) {
            enemyTerrainPenalty = 0;
            if(army.getBattle().getMap().find(attacker).getTerrain().getDef()*terrainDefenseMultiplier-defender.getArmy().getCO().enemyTerrainPenalty < army.getBattle().getMap().find(defender).getTerrain().getDef()*defender.getArmy().getCO().terrainDefenseMultiplier-enemyTerrainPenalty) {
                enemyTerrainPenalty = (int)((army.getBattle().getMap().find(defender).getTerrain().getDef()*defender.getArmy().getCO().terrainDefenseMultiplier-enemyTerrainPenalty)/2);
            }
        }
        
        if(COP||SCOP)return 110;
        return 100;
        
    }
    
    public void setChange(Unit u){
        
    }
    
    public void unChange(Unit u){
        
    }

    public void dayEnd(boolean main ) {
        //Resets the stuff for his D2D.
        if(!COP)
            enemyTerrainPenalty = 0;
    }
    
    public void dayStart(boolean main ){
        if(sustain) {
            sustain = false;
        }
    }
//used to get the defense bonus for damage calculation
    public int getDef(Unit attacker, Unit defender){
        if(SCOP || COP)return 110;
        return 100;
    }
    public void enemyDayStart(boolean main) {
        
        if(sustain) {
            for(int i = 0; i< army.getBattle().getMap().getMaxCol(); i++) {
                for(int t = 0; t< army.getBattle().getMap().getMaxRow(); t++) {
                    //move[] 0=infantry, 1=mech, 2=tread, 3=tires, 4=air, 5=sea, 6=transport, 7=oozium, 8=pipe, 9=hover
                    //doubles terrain costs
                    for(int s = 0; s<10; s++) {
                        if(army.getBattle().getMap().find(new Location(i,t)).getTerrain().moveCost(s) != -1) {
                            army.getBattle().getMap().find(new Location(i,t)).getTerrain().addCost(s, army.getBattle().getMap().find(new Location(i,t)).getTerrain().getBaseMove()[s]);}
                    } 
                }
            }
        }
    }
    
    public void enemyDayEnd(boolean main ){
        if(sustain) {
            for(int i = 0; i< army.getBattle().getMap().getMaxCol(); i++) {
                for(int t = 0; t< army.getBattle().getMap().getMaxRow(); t++) {
                    //For some reason referencing baseMove doesn't work - apparently there's some strange bug where it's altered
                    //even when it should be impossible to do so!
                    for(int s = 0; s<10; s++) {
                        if(army.getBattle().getMap().find(new Location(i,t)).getTerrain().moveCost(s) != -1) {
                            army.getBattle().getMap().find(new Location(i,t)).getTerrain().addCost(s, -army.getBattle().getMap().find(new Location(i,t)).getTerrain().getBaseMove()[s]);
                        }
                    }
                }
            }
        }
    }
    
//carries out Blandie's CO Power, called by CO.activateCOP()
    public void COPower(){
        COP = true;
        enemyTerrainPenalty = 10; //Woo, yousa lose all stars
    }
    
//carries out Blandie's Super CO Power, called by CO.activateSCOP()
    public void superCOPower(){
        SCOP = true;
        sustain = true;
        Army[] armies = army.getBattle().getArmies();
        for(int i = 0; i<armies.length; i++) {
            if(armies[i].getSide() != army.getSide()) {
                Unit[] u = armies[i].getUnits();
                for(int s = 0; s<u.length; s++) {
                    Tile north = army.getBattle().getMap().find(new Location(u[s].getLocation().getCol(), u[s].getLocation().getRow()-1));
                    Tile south = army.getBattle().getMap().find(new Location(u[s].getLocation().getCol(), u[s].getLocation().getRow()+1));
                    Tile east = army.getBattle().getMap().find(new Location(u[s].getLocation().getCol()+1, u[s].getLocation().getRow()));
                    Tile west = army.getBattle().getMap().find(new Location(u[s].getLocation().getCol()-1, u[s].getLocation().getRow()));
                    if(army.getBattle().getMap().onMap(north.getLocation())) {
                        u[s].defensePenalty += north.getTerrain().def*5;
                        u[s].enemyCOstore[statIndex] += north.getTerrain().def*5;
                    }
                    if(army.getBattle().getMap().onMap(south.getLocation())) {
                        u[s].defensePenalty += south.getTerrain().def*5;
                        u[s].enemyCOstore[statIndex] += south.getTerrain().def*5;
                    }
                    if(army.getBattle().getMap().onMap(east.getLocation())) {
                        u[s].defensePenalty += east.getTerrain().def*5;
                        u[s].enemyCOstore[statIndex] += east.getTerrain().def*5;
                    }
                    if(army.getBattle().getMap().onMap(west.getLocation())) {
                        u[s].defensePenalty += west.getTerrain().def*5;
                        u[s].enemyCOstore[statIndex] += west.getTerrain().def*5;
                    }
                }
            }
        }
        sustain = true;
    }
    
//used to deactivate Blandie's CO Power the next day
    public void deactivateCOP(){
        COP = false;
        enemyTerrainPenalty = 0;
    }
    
//used to deactivate Blandie's Super CO Power the next day
    public void deactivateSCOP(){
        SCOP = false;
        Army[] armies = army.getBattle().getArmies();
        Unit[] u;
        for(int i = 0; i<armies.length; i++) {
            if(armies[i].getSide() != army.getSide()) {
                u = armies[i].getUnits();
                for(int s = 0; s<u.length; s++) {
                    u[s].defensePenalty -= u[s].enemyCOstore[statIndex];
                }
            }
        }
    }
}
