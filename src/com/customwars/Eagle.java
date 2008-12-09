package com.customwars;
/*
 *Eagle.java
 *Author: Adam Dziuk and Kosheh
 *Contributors: 
 *Creation:
 *The Eagle class is used to create an instance of the Green Earth CO Eagle (copyright Intelligent Systems).
 */

public class Eagle extends CO{
   
    //constructor
    public Eagle() {
        name = "Eagle";
        id = 11;
        
        String CObiox = "Green Earth's daring pilot hero. Joined the air force to honor his father's legacy.";             //Holds the condensed CO bio'
        String titlex = "Prince of the Skies";
        String hitx = "Lucky goggles"; //Holds the hit
        String missx = "Swimming"; //Holds the miss
        String skillStringx = "Air units use less fuel and have superior firepower. Naval units have weaker firepower.";
        String powerStringx = "All non-infantry units that have already carried out orders may move again, but their firepower is cut in half."; //Holds the Power description
        String superPowerStringx = "All non-infantry units that have already carried out orders may move again."; //Holds the Super description
                      //"                                    " sizing markers
        String intelx = "Eagle has a superior air force but  " +
                        "an inferior navy. Both the power and" +
                        "super allow his non-soldier units to" +
                        "move twice within the same turn,    " +
                        "however the power limits the attack " +
                        "strength his units possess.";//Holds CO intel on CO select menu, 6 lines max

        CObio = CObiox;
        title = titlex;
        hit = hitx;
        miss = missx;
        skillString = skillStringx;
        powerString = powerStringx;
        superPowerString = superPowerStringx;
        intel = intelx;
        
        String[] TagCOsx = {"Andy","Sami","Drake","Jess","Lash","Hawke","Von Bolt"}; //Names of COs with special tags
        String[] TagNamesx = {"Airlift","Earth and Sky","Stormwatch","Dual Strike","Dual Strike","Dual Strike","Dual Strike"}; //Names of the corresponding Tags
        int[] TagStarsx = {2,3,2,0,0,0,0}; //Number of stars for each special tag.
        int[] TagPercentx = {115,120,115,105,90,70,90}; //Percent for each special tag.
        
        TagCOs = TagCOsx;
        TagNames = TagNamesx;
        TagStars = TagStarsx;
        TagPercent = TagPercentx;

        String[] COPowerx =
        {"Checkmate!",
        "All units, ready for attack! Don't even give them time for regret!",
        "Do you think you can keep up?",
        "The Eagle soars above you!",
        "I have no interest in underlings. Begone!",
        "I've caught you with your guard down!"};
       
        String[] Victoryx =
        {"Hmph! As expected!",
         "Where's the challenge in that?",
         "Another victory! Was there ever any doubt?"};
       
        String[] Swapx =
        {"Do you really want to challenge me?",
        "You're wastin' my time!"};
       
        String[] defeatx =
        {"This is what happens when you lose focus!",
         "Impossible! I'm getting angrier by the moment!"} ;
        
        Swap = Swapx;       
        COPower = COPowerx;
        Victory = Victoryx;
        defeat = defeatx;           
       
        COPName = "Lightning Drive";
        SCOPName = "Lightning Strike";
        COPStars = 5.0;
        maxStars = 9.0;
        this.army = army;
        style = GREEN_EARTH;
    }
     //used to get the attack bonus for damage calculation
    public int getAtk(Unit attacker, Unit defender){
        //Balance Mode Stats
        if (army.getBattle().getBattleOptions().isBalance() == true){
            if(SCOP && attacker.moveType == attacker.MOVE_AIR)
                return 130;
            else if(SCOP && attacker.moveType == attacker.MOVE_SEA)
                return 100;
            else if(COP && attacker.moveType == attacker.MOVE_AIR)
                return 65;
            else if(COP && attacker.moveType == attacker.MOVE_SEA)
                return 50;
            else if(COP)
                return 55;
            else if(SCOP || (COP && attacker.moveType < 3))
                return 110;
            else return 100;
        }
        //DS Mode Stats
        else{
        if(SCOP && attacker.moveType == attacker.MOVE_AIR)
            return 130;
        else if(SCOP && attacker.moveType == attacker.MOVE_SEA)
            return 100;
        else if(COP && attacker.moveType == attacker.MOVE_AIR)
            return 70;
        else if(COP && attacker.moveType == attacker.MOVE_AIR)
            return 55;
        else if(COP)
            return 60;
        else if(SCOP || (COP && attacker.moveType < 3))
            return 110;
        else return 100;}
            
    }
    
    //used to get the defense bonus for damage calculation
    public int getDef(Unit attacker, Unit defender){
        if(SCOP)
            return 110;
        if(COP)
            return 90;
        return 100;
    }
    
    //changes unit for this CO
    public void setChange(Unit u){
        if(u.moveType == u.MOVE_AIR)
            u.dailyGas -= 2;
    }
    
    //unchanges unit
    public void unChange(Unit u){
        if(u.moveType == u.MOVE_AIR)
            u.dailyGas += 2;
    }
    
    //carries out Eagle's CO Power, called by CO.activateCOP()
    public void COPower(){
        COP = true;
        Unit[] u = army.getUnits();
        if(u != null)
        for(int i = 0; i < u.length; i++)
            if(u[i].moveType != u[i].MOVE_INFANTRY && u[i].moveType != u[i].MOVE_MECH)
                u[i].setActive(true);
            
    }
    
    //carries out Max's Super CO Power, called by CO.activateSCOP()
    public void superCOPower(){
        SCOP = true;
        Unit[] u = army.getUnits();
        if(u != null)
        for(int i = 0; i < u.length; i++)
            if(u[i].moveType != u[i].MOVE_INFANTRY && u[i].moveType != u[i].MOVE_MECH)
                u[i].setActive(true);
        }    
    
    //used to deactivate Max's CO Power the next day
    public void deactivateCOP(){
        COP = false;
    }
    
    //used to deactivate Max's Super CO Power the next day
    public void deactivateSCOP(){
        SCOP = false;
        }  
}