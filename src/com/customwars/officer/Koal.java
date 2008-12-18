package com.customwars.officer;

import com.customwars.unit.Unit;

public class Koal extends CO{
    
//Constructor
public Koal() {
        name = "Koal";
        setId(25);

        String CObiox = "A commander of the Black Hole army who is always planning his next destructive act.";             //Holds the condensed CO bio'
        String titlex = "Endless March";
        String hitx = "Proverbs"; //Holds the hit
        String missx = "Fondue"; //Holds the miss
        String skillStringx = "A master of road-based battles. Firepower of all units increased on roads.";
        String powerStringx = "Movement range for all units is increased by one space. Units have more firepower on roads."; //Holds the Power description
        String superPowerStringx = "Movement range for all units is increased by two spaces. Greatly increases firepower of units on roads."; //Holds the Super description
                      //"                                    " sizing markers
        String intelx = "Koal takes advantage of the road    " +
                        "network and gets a firepower boost  " +
                        "from them.  His powers increase the " +
                        "movement of units along with attack " +
                        "power while situated on roads.";//Holds CO intel on CO select menu, 6 lines max

        CObio = CObiox;
        title = titlex;
        hit = hitx;
        miss = missx;
        skillString = skillStringx;
        powerString = powerStringx;
        superPowerString = superPowerStringx;
        intel = intelx;
        
        String[] TagCOsx = {"Rachel","Adder","Kindle","Jugger"}; //Names of COs with special tags
        String[] TagNamesx = {"Dual Strike","Creepy Crawly","Flash Point","Power Surge"}; //Names of the corresponding Tags
        int[] TagStarsx = {0,1,1,2}; //Number of stars for each special tag.
        int[] TagPercentx = {65,110,105,110}; //Percent for each special tag.

        setTagCOs(TagCOsx);
        setTagNames(TagNamesx);
        setTagStars(TagStarsx);
        setTagPercent(TagPercentx);
        
        String[] COPowerx =
        {"I will crush your units, one by one!",
        "I am a warrior and a scholar. My victory is all but certain.",
        "Heh heh heh. I'll not surrender! Bend your knees and beg for mercy!",
        "Heh heh heh... None shall escape.",
        "My speed knows no equal!",
        "Heh heh heh... Fate has smiled on me this day!"};
        
        String[] Victoryx =
        {"I have no equal on the field of war!",
        "Wallow in your shame, swine! Wallow, I say!",
        "Victory is no accident."};
        
        
        String[] Swapx =
        {"This isn't over yet...",
        "Inflict pain..."};
        
        String[] defeatx =
        {"How? How could I lose to a miserable band of misfits such as this?",
         "The shame...it burns like a brand."} ;
        
        setSwap(Swapx);       
        setCOPower(COPowerx);
        Victory = Victoryx;
        defeat = defeatx;
        
        COPName = "Forced March";
        SCOPName = "Trail of Woe";
        COPStars = 3.0;
        maxStars = 5.0;
        this.army = army;
        style = BLACK_HOLE;
    }
    
    //used to get the attack bonus for damage calculation
    public int getAtk(Unit attacker, Unit defender){
                //Balance Stats
        if(army.getBattle().getBattleOptions().isBalance()== true){
            if((army.getBattle().getMap().find(attacker).getTerrain().getName().equals("Road")||army.getBattle().getMap().find(attacker).getTerrain().getName().equals("Bridge")) && SCOP)
                return 140;
            if((army.getBattle().getMap().find(attacker).getTerrain().getName().equals("Road")||army.getBattle().getMap().find(attacker).getTerrain().getName().equals("Bridge")) && COP)
                return 130;
            if((army.getBattle().getMap().find(attacker).getTerrain().getName().equals("Road")||army.getBattle().getMap().find(attacker).getTerrain().getName().equals("Bridge")))
                return 110;
            if(COP||SCOP)
                return 110;
            return 100;
        }
        //DS Stats
        else{
        if(army.getBattle().getMap().find(attacker).getTerrain().getName().equals("Road") && SCOP)
            return 140;
        if(army.getBattle().getMap().find(attacker).getTerrain().getName().equals("Road") && COP)
            return 130;
        if(army.getBattle().getMap().find(attacker).getTerrain().getName().equals("Road"))
            return 110;
        if(COP||SCOP)
            return 110;
        return 100;}
    }
    
    //used to get the defense bonus for damage calculation
    public int getDef(Unit attacker, Unit defender){
        if(army.getBattle().getBattleOptions().isBalance()== true){
            if((army.getBattle().getMap().find(defender).getTerrain().getName().equals("Road")||army.getBattle().getMap().find(defender).getTerrain().getName().equals("Bridge")) && SCOP)
                return 130;
            if((army.getBattle().getMap().find(defender).getTerrain().getName().equals("Road")||army.getBattle().getMap().find(defender).getTerrain().getName().equals("Bridge")) && COP)
                return 120;
            if((army.getBattle().getMap().find(defender).getTerrain().getName().equals("Road")||army.getBattle().getMap().find(defender).getTerrain().getName().equals("Bridge")))
                return 110;
            if(COP||SCOP)
                return 110;
            return 100;
        }
        if(COP || SCOP)
            return 110;
        return 100;
    }
    
        public void setChange(Unit u){};
    
    public void unChange(Unit u){};
    
    //carries out Adder's CO Power, called by CO.activateCOP()
    public void COPower(){
        COP = true;
        Unit[] u = army.getUnits();
        for(int i = 0; i < u.length; i++){
            if(u[i].getClass() != null){
                u[i].setMove(u[i].getMove() + 1);
                u[i].setChanged(true);
            }
            else
                return;
        }
    }
    
    //carries out Adder's Super CO Power, called by CO.activateSCOP()
    public void superCOPower(){
        SCOP = true;
        Unit[] u = army.getUnits();
        for(int i = 0; i < u.length; i++){
            if(u[i].getClass() != null){
                u[i].setMove(u[i].getMove() + 2);
                u[i].setChanged(true);}
            else
                return;
        }
    }    
    
    //used to deactivate Adder's CO Power the next day
    public void deactivateCOP(){
           COP = false;
        Unit[] u = army.getUnits();
        for(int i = 0; i < u.length; i++){
            if(u[i].getClass() != null && u[i].isChanged()){
                u[i].setMove(u[i].getMove() - 1);}
            else
                return;
    }
    }
    
    //used to deactivate Adder's Super CO Power the next day
    public void deactivateSCOP(){
        SCOP = false;
        Unit[] u = army.getUnits();
        for(int i = 0; i < u.length; i++){
            if(u[i].getClass() != null && u[i].isChanged()){
                u[i].setMove(u[i].getMove() - 2);}
            else
                return;
        }
    }
}