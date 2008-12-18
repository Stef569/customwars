package com.customwars.officer;

import com.customwars.Battle;
import com.customwars.unit.Unit;

public class Lash extends CO{
    
    //constructor
    public Lash(Battle bat){
        name = "Lash";
        setId(20);

        String CObiox = "The wunderkind of the Black Hole forces. She's small but fierce. Designed most of Black Hole's recent weaponry.";             //Holds the condensed CO bio'
        String titlex = "Feisty Genius";
        String hitx = "Getting her way"; //Holds the hit
        String missx = "Not getting it"; //Holds the miss
        String skillStringx = "Skilled at taking advantage of terrain features. Can turn terrain effects into firepower bonuses.";
        String powerStringx = "All units' movements are unhindered by terrain."; //Holds the Power description
        String superPowerStringx = "Terrain effects are doubled and used to increase firepower. Additionally, all units' movements are unhindered by terrain."; //Holds the Super description
                      //"                                    " sizing markers
        String intelx = "Lash takes advantage of terrain and " +
                        "uses terrain defense to boost her   " +
                        "firepower.  Her powers both allow   " +
                        "perfect movement on terrain with her" +
                        "super also doubling terrain stars.";//Holds CO intel on CO select menu, 6 lines max

        CObio = CObiox;
        title = titlex;
        hit = hitx;
        miss = missx;
        skillString = skillStringx;
        powerString = powerStringx;
        superPowerString = superPowerStringx;
        intel = intelx;
        
        String[] TagCOsx = {"Hawke","Flak","Sonja","Olaf","Colin","Eagle","Von Bolt"}; //Names of COs with special tags
        String[] TagNamesx = {"Rebel Yell","Bruise Cruise","Brainstorm","Dual Strike","Dual Strike","Dual Strike","Dual Strike"}; //Names of the corresponding Tags
        int[] TagStarsx = {1,1,0,0,0,0,0}; //Number of stars for each special tag.
        int[] TagPercentx = {110,110,105,80,90,90,90}; //Percent for each special tag.
        
        setTagCOs(TagCOsx);
        setTagNames(TagNamesx);
        setTagStars(TagStarsx);
        setTagPercent(TagPercentx);
        
        String[] COPowerx =
        {"Ooh!  You're so annoying!  I'm gonna have to get rid of you now!",
         "You're no fun... I don't like you at all!",
         "I've had enough!  I'm bored now!",
         "Oooo, you're driving me nuts!",
         "You want to match wits with me? You're so silly!",
         "Tee hee! C'mon! Let's play!"};
        
        String[] Victoryx =
        {"Hee hee hee... Hm? Broken already?",
         "Tee hee hee! You're a loser!",
         "Huh? That's all you got? Wow, you're no fun!"};
        
        String[] Swapx =
        {"Smashin' you is gonna be fun!",
         "Tee hee hee!"};
        
        String[] defeatx =
        {"Boo! Nothing's going right! That's enough. I'm going home!",
         "Oh, well. I guess I'll have to find someplace new to play. Toodles!"} ;
        
        setSwap(Swapx);       
        setCOPower(COPowerx);
        Victory = Victoryx;
        defeat = defeatx;
        
        COPName = "Terrain Tactics";
        SCOPName = "Prime Tactics";
        if (bat.getBattleOptions().isBalance() == true){
            COPStars = 2.0;
            maxStars = 6.0;
        } else{
            COPStars = 4.0;
            maxStars = 7.0;}
        this.army = army;
        style = BLACK_HOLE;
    }
    
    //used to get the attack bonus for damage calculation
    public int getAtk(Unit attacker, Unit defender){
        if(defender != null){
            //Balance Stats
            if(army.getBattle().getBattleOptions().isBalance()== true){
                int tdef = 0;
                tdef = attacker.getMap().find(attacker).getTerrain().getDef();
                if(tdef < 0)tdef = 0;
                tdef *= attacker.getArmy().getCO().getTerrainDefenseMultiplier();
                if(SCOP)
                    return 110 + tdef*10;
                if(COP)
                    return 110 + tdef*5;
                return 100 + tdef*5;
            }
            //DS Stats
            int tdef = 0;
            if(attacker.getMType() != attacker.MOVE_AIR)
                tdef = attacker.getMap().find(attacker).getTerrain().getDef();
            else
                tdef = 0;
            tdef -= defender.getArmy().getCO().getEnemyTerrainPenalty();
            if(tdef < 0)tdef = 0;
            tdef *= attacker.getArmy().getCO().getTerrainDefenseMultiplier();
            
            if(SCOP || COP)
                return 110 + tdef*5;
            return 100 + tdef*5;
        }
        //if defender == null, it's an invention
        if(COP || SCOP)return 110;
        return 100;
    }
    
    public void setChange(Unit u){
        
    }
    
    public void unChange(Unit u){
        
    }
    
    
//used to get the defense bonus for damage calculation
    public int getDef(Unit attacker, Unit defender){
        if(SCOP || COP)
            return 110;
        return 100;
    }
    
//carries out Lash's CO Power, called by CO.activateCOP()
    public void COPower(){
        COP = true;
        perfectMovement = true;
    }
    
//carries out Lash's Super CO Power, called by CO.activateSCOP()
    public void superCOPower(){
        SCOP = true;
        perfectMovement = true;
        setTerrainDefenseMultiplier(2);
    }
    
//used to deactivate Sami's CO Power the next day
    public void deactivateCOP(){
        COP = false;
        perfectMovement = false;
    }
    
//used to deactivate Sami's Super CO Power the next day
    public void deactivateSCOP(){
        SCOP = false;
        setTerrainDefenseMultiplier(1);
        perfectMovement = false;
    }
}
