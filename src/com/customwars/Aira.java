package com.customwars;

import com.customwars.officer.CO;
import com.customwars.unit.Army;
import com.customwars.unit.Unit;
/*
 *Blandie.java
 *Author: Urusan
 *Contributors:
 *Creation: December 11, 2006
 *A bland CO, perhaps a generic commanding officer?
 */

public class Aira extends CO{
    int timer = 0;
    boolean sustainSCOP;
    //constructor
    public Aira() {
        name = "Aira";
        setId(55);
        
        String CObiox = "A reserved commander who is often found daydreaming. Likes to walk in the wind.";
        //This is seperated into blocks 40 characters long!
        //Use this as a guide for a better look proper word-wrapping.
        String titlex = "Morning Breeze";
        String hitx = "Cool Breezes"; //Holds the hit
        String missx = "Surprises"; //Holds the miss
        String skillStringx = "Aira never felt a need to specialize, so all units are average";
        String powerStringx = "Enemies expend 5 times more fuel for the next two days. They cannot be supplied or repaired by other units."; //Holds the Power description
        String superPowerStringx = "Weak enemy units and air units suffer 3 HP of damage. All other enemy units have double movement costs. "; //Holds the Super description
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
        {"Amazing what a little gust can do...",
         "How can one fight against what they can't see or damage?",
         "This is the only surprise I don't mind...",
         "The winds will move the tides of war.",
         "A victory depends on whether my enemy can survive this weather.",
         "I am certain that my determination is not what one would call sub par." };
        
        String[] Victoryx =
        {"...Huh? Oh, right. I won. Woohoo?",
         "I'm glad I expected this. A surprise would have been unbearable.",
         "Victory blew in my direction today." };
        
        String[] Swapx =
        {"I won't do anything worse than you...I hope.",
         "I guess it's time for a second wind." };
        
        setCOPower(COPowerx);
        Victory = Victoryx;
        setSwap(Swapx);
        
        //No special tags
        String[] TagCOsx = {"Olaf", "Andy", "Colin", "Sasha", "Grimm", "Drake"}; //Names of COs with special tags
        String[] TagNamesx = {"Endless Storm", "A is for Average", "Dual Strike", "Dual Strike", "Dual Strike", "Dual Strike"}; //Names of the corresponding Tags
        int[] TagStarsx = {1,1,0,0,0,0}; //Number of stars for each special tag.
        int[] TagPercentx = {110, 110, 105,105,90,70}; //Percent for each special tag.
        
        setTagCOs(TagCOsx);
        setTagNames(TagNamesx);
        setTagStars(TagStarsx);
        setTagPercent(TagPercentx);
        
        COPName = "Gust Storm";
        SCOPName = "Hurricannon";
        COPStars = 3.0;
        maxStars = 7.0;
        this.army = army;
        style = BLUE_MOON;
        setCleanEnemyStoreBegin(false); //cleans enemyCOstore at the beginning of every day - used to store persistent enemy problems.
        setCleanEnemyStoreEnd(false);
    }
    
//used to get the attack bonus for damage calculation
    public int getAtk(Unit attacker, Unit defender){
        if(COP||SCOP)return 110;
        return 100;
        
    }
    
    public void setChange(Unit u){
        
    }
    
    public void unChange(Unit u){
        
    }
    
    public void dayStart(boolean main) {
        timer++;
        if(timer>=2) //Two day aspect of fuelMod
        {
            Army[] armies = army.getBattle().getArmies();
            
            for(int i = 0; i< armies.length; i++) {
                Unit[] u = armies[i].getUnits();
                if(armies[i].getSide() != army.getSide())
                    if(u!= null)
                        for(int t = 0; t<u.length; t++) {
                        u[t].setFuelMult(1);
                        if(u[t].getEnemyCOstore()[getStatIndex()][0]%10 != 1)
                            u[t].setNoRepaired(false);
                        if(u[t].getEnemyCOstore()[getStatIndex()][0]<10 )
                            u[t].setNoResupplied(false);
                        }
            }
            
            if(sustainSCOP) {
                sustainSCOP = false;
                for(int i = 0; i< armies.length; i++) {
                    Unit[] u = armies[i].getUnits();
                    if(armies[i].getSide() != army.getSide())
                        for(int t = 0; t<u.length; t++) {
                        u[t].setMove(u[t].getMove()
								+ u[t].getEnemyCOstore()[getStatIndex()][0]);
                        }
                }
            }
        }
        
    }
//used to get the defense bonus for damage calculation
    public int getDef(Unit attacker, Unit defender){
        if(SCOP || COP)return 110;
        return 100;
    }
    
//carries out Blandie's CO Power, called by CO.activateCOP()
    public void COPower(){
        COP = true;
        timer = 0;
        Army[] armies = army.getBattle().getArmies();
        
        for(int i = 0; i< armies.length; i++) {
            Unit[] u = armies[i].getUnits();
            if(armies[i].getSide() != army.getSide()) {
                for(int t = 0; t<u.length; t++) {
                    u[t].setFuelMult(5);
                    //The following if statements are to store the knowledge if the CO inherently cannot repair or resupply his or her own units
                    if(u[t].isNoRepaired())
                        u[t].getEnemyCOstore()[getStatIndex()][0] = 1;
                    u[t].setNoRepaired(true);
                    if(u[t].isNoResupplied())
                        u[t].getEnemyCOstore()[getStatIndex()][0] += 10;
                    u[t].setNoResupplied(true);
                }
            }
        }
    }
    
//carries out Blandie's Super CO Power, called by CO.activateSCOP()
    public void superCOPower(){
        SCOP = true;
        sustainSCOP = true;
        Army[] armies = army.getBattle().getArmies();
        
        for(int i = 0; i< armies.length; i++) {
            Unit[] u = armies[i].getUnits();
            if(armies[i].getSide() != army.getSide())
                for(int t = 0; t<u.length; t++) {
                u[t].getEnemyCOstore()[getStatIndex()][0]= 0;
                if(u[t].getMoveType() == u[t].MOVE_AIR || u[t].getPrice()<16000)
                    if(!u[t].isInTransport())u[t].damage(30, false);
                if(u[t].getPrice()>=16000 && u[t].getMoveType() != u[t].MOVE_AIR) {
                    u[t].getEnemyCOstore()[getStatIndex()][0] = ((int)(u[t].getMove()/2.0+.5));
                    u[t].setMove(u[t].getMove()
							- ((int)(u[t].getMove()/2.0+.5)));
                }
                }
        }
    }
    
    
    
//used to deactivate Blandie's CO Power the next day
    public void deactivateCOP(){
        COP = false;
    }
    
//used to deactivate Blandie's Super CO Power the next day
    public void deactivateSCOP(){
        SCOP = false;
        //-1 move
        Unit[] u = army.getUnits();
        for(int i = 0; i < u.length; i++){
            if(u[i].getClass() != null){
                if(u[i].isChanged()){
                    u[i].setMove(u[i].getMove() - 1);
                    u[i].setChanged(false);
                }
            } else
                return;
        }
    }
}
