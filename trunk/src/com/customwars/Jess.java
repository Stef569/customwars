package com.customwars;
/*
 *Jess.java
 *Author: Adam Dziuk, Paul Whan
 *Contributors: Kosheh
 *Creation: July 4, 2006, 10:17 PM
 *The Andy class is used to create an instance of the Green Earth CO Jess (copyright Intelligent Systems).
 */

public class Jess extends CO{
    
//constructor
    public Jess() {
        name = "Jess";
        id = 13;
        
        String CObiox = "A gallant tank-driving commander who excels at analyzing information. Often argues with Eagle.";             //Holds the condensed CO bio'
        String titlex = "Jeanne D�Tank";
        String hitx = "Dandelions"; //Holds the hit
        String missx = "Unfit COs"; //Holds the miss
        String skillStringx = "Vehicular units have superior firepower. Air and naval units are comparatively weak.";
        String powerStringx = "Movement range of vehicles increases by one space and their firepower increases. All units' fuel and ammunition supplies are replenished."; //Holds the Power description
        String superPowerStringx = "Movement range of vehicles increases by two spaces and their firepower greatly increases. All units' fuel and ammunition supplies are replenished."; //Holds the Super description
                      //"                                    " sizing markers
        String intelx = "Jess has an optimal land force, but " +
                        "her air and naval support isn't very" +
                        "strong.  Her powers enhance movement" +
                        "and offense of her land vehicles,   " +
                        "while also fully resupplying her    " +
                        "entire army.";//Holds CO intel on CO select menu, 6 lines max

        CObio = CObiox;
        title = titlex;
        hit = hitx;
        miss = missx;
        skillString = skillStringx;
        powerString = powerStringx;
        superPowerString = superPowerStringx;
        intel = intelx;
        
        String[] TagCOsx = {"Javier","Jake","Eagle","Drake","Hawke","Von Bolt"}; //Names of COs with special tags
        String[] TagNamesx = {"Green Flash","Heavy Metal","Dual Strike","Dual Strike","Dual Strike","Dual Strike"}; //Names of the corresponding Tags
        int[] TagStarsx = {1,1,0,0,0,0}; //Number of stars for each special tag.
        int[] TagPercentx = {110,110,105,105,90,90}; //Percent for each special tag.
        
        TagCOs = TagCOsx;
        TagNames = TagNamesx;
        TagStars = TagStarsx;
        TagPercent = TagPercentx;
        
        String[] COPowerx =
        {"Battle should never be taken lightly. I always give 100%! Always!!!",
         "I won't let this chance pass me by!",
         "Weakness has no place on the battlefield!",
         "Here's my chance...",
         "Time to turn the tables...",
         "I've been waiting for this moment!"};
        
        String[] Victoryx =
        {"This battle will advance my tactical research...",
         "Looks like I survived another day.",
         "We made it... I'm glad that's done."};
        
        String[] Swapx =
        {"Let's switch now. It's for the best.",
         "Just another CO? Guess again!"};
        
        String[] defeatx =
        {"This is looking grim...",
         "It was inevitable under these conditions."} ;
        
        Swap = Swapx;       
        COPower = COPowerx;
        Victory = Victoryx;
        defeat = defeatx;
        
        COPName = "Turbo Charge";
        SCOPName = "Overdrive";
        COPStars = 3.0;
        maxStars = 6.0;
        this.army = army;
        style = GREEN_EARTH;
        
        
    }
    
//used to get the attack bonus for damage calculation
    public int getAtk(Unit attacker, Unit defender){
        //Balance Stats
        if(army.getBattle().getBattleOptions().isBalance()== true){
            if(SCOP && (attacker.moveType == attacker.MOVE_TREAD || attacker.moveType == attacker.MOVE_TIRE))
                return 160;
            else if(SCOP && (attacker.moveType == attacker.MOVE_AIR || attacker.moveType == attacker.MOVE_SEA))
                return 100;
            else if(SCOP)
                return 110;
            if(COP && (attacker.moveType == attacker.MOVE_TREAD || attacker.moveType == attacker.MOVE_TIRE))
                return 140;
            else if(COP && (attacker.moveType == attacker.MOVE_AIR || attacker.moveType == attacker.MOVE_SEA))
                return 100;
            else if(COP)
                return 110;
            if(attacker.moveType == attacker.MOVE_TREAD || attacker.moveType == attacker.MOVE_TIRE)
                return 110;
            else if (attacker.moveType == attacker.MOVE_AIR || attacker.moveType == attacker.MOVE_SEA)
                return 90;
            else return 100;
        }
        //DS Stats
        if(SCOP && (attacker.moveType == attacker.MOVE_TREAD || attacker.moveType == attacker.MOVE_TIRE))
            return 170;
        else if(SCOP && (attacker.moveType == attacker.MOVE_AIR || attacker.moveType == attacker.MOVE_SEA))
            return 100;
        else if(SCOP)
            return 110;
        if(COP && (attacker.moveType == attacker.MOVE_TREAD || attacker.moveType == attacker.MOVE_TIRE))
            return 150;
        else if(COP && (attacker.moveType == attacker.MOVE_AIR || attacker.moveType == attacker.MOVE_SEA))
            return 100;
        else if(COP)
            return 110;
        if(attacker.moveType == attacker.MOVE_TREAD || attacker.moveType == attacker.MOVE_TIRE)
            return 120;
        else if (attacker.moveType == attacker.MOVE_AIR || attacker.moveType == attacker.MOVE_SEA)
            return 90;
        else return 100;
    }
    
    public void unChange(Unit u) {}
    
    public void setChange(Unit u) {}
    
//used to get the defense bonus for damage calculation
    public int getDef(Unit attacker, Unit defender){
        //Balance Stats
        if(army.getBattle().getBattleOptions().isBalance()== true){
            if (SCOP||COP)
                return 110;
            else
                return 100;
        }
        //DS Stats
        if(COP || SCOP)
            return 110;
        return 100;
    }
    
//carries out Andy's CO Power, called by CO.activateCOP()
    public void COPower(){
        COP = true;
        Unit[] u = army.getUnits();
        for(int i = 0; i < u.length; i++){
            if(u[i].getClass() != null){
                u[i].gas = u[i].maxGas;
                u[i].ammo = u[i].maxAmmo;
                if(u[i].moveType == u[i].MOVE_TREAD || u[i].moveType == u[i].MOVE_TIRE){
                    u[i].move++;
                    u[i].changed = true;
                }
            }else{
                return;
            }
        }
    }
    
//carries out Andy's Super CO Power, called by CO.activateSCOP()
    public void superCOPower(){
        SCOP = true;
        Unit[] u = army.getUnits();
        for(int i = 0; i < u.length; i++){
            if(u[i].getClass() != null){
                u[i].gas = u[i].maxGas;
                u[i].ammo = u[i].maxAmmo;
                if(u[i].moveType == u[i].MOVE_TREAD || u[i].moveType == u[i].MOVE_TIRE){
                    u[i].move+=2;
                    u[i].changed = true;
                }
            }else{
                return;
            }
        }
    }
    
//used to deactivate Adder's CO Power the next day
    public void deactivateCOP(){
        COP = false;
        Unit[] u = army.getUnits();
        for(int i = 0; i < u.length; i++){
            if((u[i].moveType == u[i].MOVE_TREAD || u[i].moveType == u[i].MOVE_TIRE) ){
                if(u[i].changed)
                    u[i].move--;
            }
        }
    }
    
//used to deactivate Adder's Super CO Power the next day
    public void deactivateSCOP(){
        SCOP = false;
        Unit[] u = army.getUnits();
        for(int i = 0; i < u.length; i++){
            if(u[i].moveType == u[i].MOVE_TREAD || u[i].moveType == u[i].MOVE_TIRE)
                if(u[i].changed)
                    u[i].move -= 2;
        }
    }
}
