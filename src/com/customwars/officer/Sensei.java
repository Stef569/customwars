package com.customwars.officer;

import com.customwars.map.location.Property;
import com.customwars.unit.Infantry;
import com.customwars.unit.Transport;
import com.customwars.unit.Unit;

public class Sensei extends CO{
    
    public Sensei() {
        setId(17);
        
        name = "Sensei";
        
        String CObiox = "A former paratrooper rumored to have been quite the commander in his day.";             //Holds the condensed CO bio'
        String titlex = "Cranky Paratrooper";
        String hitx = "Lazy, rainy days"; //Holds the hit
        String missx = "Busy malls"; //Holds the miss
        String skillStringx = "Copters have incredibly high firepower, but naval units have weak attacks. Foot soldiers have increased firepower.";
        String powerStringx = "Copter firepower increases and copter movement increases. Infantry firepower rises greatly."; //Holds the Power description
        String superPowerStringx = "Copter firepower increases. Infantry units with 9 HP appear in 2/3rds of allied cities, ready to be moved."; //Holds the Super description
        //"                                    " sizing markers
        String intelx = "As a former paratrooper Sensei's    " +
                "soldiers have increased offense and " +
                "he deploys infantry on cities during" +
                "his power while mechs for his super." +
                "Additionally his copters are immense" +
                "but his navy is weak in comparison. ";//Holds CO intel on CO select menu, 6 lines max
        
        CObio = CObiox;
        title = titlex;
        hit = hitx;
        miss = missx;
        skillString = skillStringx;
        powerString = powerStringx;
        superPowerString = superPowerStringx;
        intel = intelx;
        
        String[] TagCOsx = {"Grimm","Hachi","Kanbei","Javier","Von Bolt","Sonja"}; //Names of COs with special tags
        String[] TagNamesx  = {"Dual Strike","Dual Strike","Rolling Thunder","Dual Strike","Dual Strike","Grizzled Vets"}; //Names of the corresponding Tags
        int[] TagStarsx = {1,2,0,0,0,0}; //Number of stars for each special tag.
        int[] TagPercentx = {110,115,105,105,90,105}; //Percent of each special tag.
        
        setTagCOs(TagCOsx);
        setTagNames(TagNamesx);
        setTagStars(TagStarsx);
        setTagPercent(TagPercentx);
        
        String[] COPowerx =
        {"Nothing good ever comes from war... when this is done, let's go home.",
         "I've still got what it takes to defeat you youngsters!",
         "Paratroopers! Prepare to jump!",
         "Ha ha hah!  This will be a crushing victory!",
         "See what experience can do for you!?",
         "Ha ha! I didn't get old for nothing!"};
        
        String[] Victoryx =
        {"Hm hm hmmm... I've still got what it takes!",
         "Ah, time for a nap.",
         "Now we can take things nice and slow again..."};
        
        String[] Swapx =
        {"The old soldier in me wants to fight!",
         "Oh, time to switch, eh? Boy, that was quick."};
        
        String[] defeatx =
        {"Hah ha! We were defeated fair and square. Score one for the young 'uns.",
         "Eh? The battle's over? When did that happen?"} ;
        
        setSwap(Swapx);       
        setCOPower(COPowerx);
        Victory = Victoryx;
        defeat = defeatx;
        
        COPName = "Copter Command";
        SCOPName = "Airborne Assault";
        COPStars = 2.0;
        maxStars = 6.0;
        this.army = army;
        style = YELLOW_COMET;
    }
    
    
    public int getAtk(Unit attacker, Unit defender){
        if(attacker.getMType() == attacker.MOVE_INFANTRY || attacker.getMType() == attacker.MOVE_MECH) {
            if(SCOP)
                return 140;
            if(COP)
                return 150;
            return 110;
        } else if(attacker.getUnitType() == 15) {
            if(SCOP)
                return 180;
            if(COP)
                return 140;
            return 120;
        } else if (attacker.getUType() == 2 || attacker.getUType() == 3 || attacker.getUType() == 19 || attacker.getUType() == 18) {
            if(SCOP || COP)
                return 100;
            return 90;
        }
        if(SCOP || COP)
            return 110;
        return 100;
        
    }
    
    //used to get the defense bonus for damage calculation
    public int getDef(Unit attacker, Unit defender){
        if(COP || SCOP)
            return 110;
        return 100;
    }
    
    //changes unit for this CO
    public void setChange(Unit u){
        if(u instanceof Transport)u.setMove(u.getMove() + 1);
    }
    
    //unchanges unit
    public void unChange(Unit u){
        if(u instanceof Transport)u.setMove(u.getMove() - 1);
    }
    
    //carries out Eagle's CO Power, called by CO.activateCOP()
    public void COPower(){
        COP = true;
        Unit[] u = army.getUnits();
        for(int i = 0; i < u.length; i++){
            if(u[i].getClass() != null){
                if(u[i].getUType() == 15) {
                    u[i].setMove(u[i].getMove() + 1);
                    u[i].setChanged(true);
                }
            }else{
                return;
            }
        }
    }
    
    //carries out Max's Super CO Power, called by CO.activateSCOP()
    public void superCOPower(){
        SCOP = true;
        //army.setAllActive(false);
        Property[] prop = army.getProperties();
        for(int i = 0; i < prop.length; i++){
            if(prop[i].getName().equals("City") && !(prop[i].getTile().hasUnit()) && i%3 != 0){
                prop[i].getTile().addUnit(new Infantry(prop[i].getTile().getLocation().getCol(),prop[i].getTile().getLocation().getRow(), army, army.getBattle().getMap()));
                prop[i].getTile().getUnit().damage(10,false);
                prop[i].getTile().getUnit().setActive(true);
            }
        }
    }
    
    //used to deactivate Max's CO Power the next day
    public void deactivateCOP(){
        COP = false;
        Unit[] u = army.getUnits();
        for(int i = 0; i < u.length; i++){
            if(u[i].getClass() != null && u[i].isChanged()){
                if(u[i].getUType() == 15) {
                    u[i].setMove(u[i].getMove() - 1);
                    u[i].setChanged(false);
                }
            }else{
                return;
            }
        }
    }
    
    //used to deactivate Max's Super CO Power the next day
    public void deactivateSCOP(){
        SCOP = false;
    }
}