package com.customwars.officer;

import com.customwars.unit.Unit;
import com.customwars.unit.UnitID;

public class Jake extends CO{
    
//Constructor
    public Jake() {
        name = "Jake";
        setId(5);
        
        String CObiox = "A young, energetic Orange Star CO who is a top-notch tank commander.";             //Holds the condensed CO bio'
        String titlex = "Just Plain Hip";
        String hitx = "Clubbin' "; //Holds the hit
        String missx = "Easy listening"; //Holds the miss
        String skillStringx = "Fights well in the open. Firepower of all units increased on plains.";
        String powerStringx = "Firepower is increased on plains. Firing range of indirect-combat units is increased by one."; //Holds the Power description
        String superPowerStringx = "Firepower is greatly increased on plains. Firing range of indirect units is increased by one, and movement of direct units by two"; //Holds the Super description
        //"                                    " sizing markers
        String intelx = "Being a top tank commander, Jake's  " +
                "tanks have high firepower. All units" +
                "also receive a firepower boost on   " +
                "plains. His powers boost attack on  " +
                "plains further while extending range" +
                "and movement of tanks too.";//Holds CO intel on CO select menu, 6 lines max
        
        CObio = CObiox;
        title = titlex;
        hit = hitx;
        miss = missx;
        skillString = skillStringx;
        powerString = powerStringx;
        superPowerString = superPowerStringx;
        intel = intelx;
        
        String[] TagCOsx = {"Rachel","Jess","Sasha","Kindle","Von Bolt"}; //Names of COs with special tags
        String[] TagNamesx = {"Orange Crush","Dual Strike","Heavy Metal","Dual Strike","Dual Strike"}; //Names of the corresponding Tags
        int[] TagStarsx = {2,1,0,0,0}; //Number of stars for each special tag.
        int[] TagPercentx = {120,110,105,90,90}; //Percent for each special tag.
        
        setTagCOs(TagCOsx);
        setTagNames(TagNamesx);
        setTagStars(TagStarsx);
        setTagPercent(TagPercentx);
        
        String[] COPowerx =
        {"Give it up, fool!",
         "Prepare to get served.",
         "Prepare for a subpoena of pain! Yeah, that's lawyer style!",
         "This is how I roll!",
         "Wassup now?!",
         "Here...have a taste!"};
        
        String[] Victoryx =
        {"Get the plates, 'cause you just got served!",
         "Owned!",
         "You got dropped like a phat beat!"};
        
        String[] Swapx =
        {"Back on the front lines!",
         "OK! It's game time!"};
        
        String[] defeatx =
        {"I got spanked out there! This combat is the real deal...",
         "Dude, we so don't have time for this."} ;
        
        setSwap(Swapx);
        setCOPower(COPowerx);
        Victory = Victoryx;
        defeat = defeatx;
        
        COPName = "Beat Down";
        SCOPName = "Block Rock";
        COPStars = 3.0;
        maxStars = 6.0;
        this.army = army;
        style = ORANGE_STAR;
    }
    
    //used to get the attack bonus for damage calculation
    public int getAtk(Unit attacker, Unit defender){
        int atk = 0;
        if(attacker.getUnitType() == UnitID.TANK || attacker.getUnitType() == UnitID.MEDTANK || attacker.getUnitType() == UnitID.MEGATANK || attacker.getUnitType() == UnitID.NEOTANK) {
            if(SCOP)
                atk += 30;
            else
                atk += 5;
        }
        if(army.getBattle().getMap().find(attacker).getTerrain().getName().equals("Plain")) {
            if(COP||SCOP)
                atk += 30;
            else
                atk += 15;
        }
        if(COP||SCOP)
            atk+= 10; //combined 40 for SCOP/COP : 15 for just normal
        return 100 + atk;
    }
    
    //used to get the defense bonus for damage calculation
    public int getDef(Unit attacker, Unit defender){
        if(COP || SCOP)
            return 110;
        return 100;
    }
    
    public void setChange(Unit u){};
    
    public void unChange(Unit u){};
    
    
    //carries out Jake's CO Power, called by CO.activateCOP()
    public void COPower(){
        COP = true;
        //Balance Stats
        if (army.getBattle().getBattleOptions().isBalance()==true){
            Unit[] u = army.getUnits();
            if(u != null)
                for(int i = 0; i < u.length; i++){
                if(u[i].getClass() != null){
                    if (u[i].getUType() == UnitID.ARTILLERY || u[i].getUType() == UnitID.ROCKET){
                        u[i].setMaxRange(u[i]
								.getMaxRange() + 1);
                        u[i].setChanged(true);
                    }
                    if(u[i].getUnitType() == UnitID.TANK || u[i].getUnitType() == UnitID.MEDTANK || u[i].getUnitType() == UnitID.MEGATANK || u[i].getUnitType() == UnitID.NEOTANK) {
                        u[i]
								.setMove(u[i].getMove() + 1);
                        u[i].setChanged(true);
                    }
                }
                }}
        //DS Stats
        if (army.getBattle().getBattleOptions().isBalance()== false){
            Unit[] u = army.getUnits();
            if(u != null)
                for(int i = 0; i < u.length; i++){
                if(u[i].getClass() != null){
                    if (u[i].getMinRange() > 1 && (u[i].getMoveType() == u[i].MOVE_TREAD || u[i].getMoveType() == u[i].MOVE_TIRE)){
                        u[i].setMaxRange(u[i]
								.getMaxRange() + 1);
                        u[i].setChanged(true);
                    }
                } } else
                    return;
        }
    }
    
    //carries out Jake's Super CO Power, called by CO.activateSCOP()
    public void superCOPower(){
        SCOP = true;
        //Balance Stats
        if (army.getBattle().getBattleOptions().isBalance()==true){
            Unit[] u = army.getUnits();
            if(u != null){
                for(int i = 0; i < u.length; i++){
                    if(u[i].getClass() != null){
                        if(u[i].getUType() == UnitID.ARTILLERY || u[i].getUType() == UnitID.ROCKET){
                            u[i].setMaxRange(u[i]
									.getMaxRange() + 1);
                            u[i].setChanged(true);}
                        if(u[i].getUnitType() == UnitID.TANK || u[i].getUnitType() == UnitID.MEDTANK || u[i].getUnitType() == UnitID.MEGATANK || u[i].getUnitType() == UnitID.NEOTANK) {
                            u[i]
									.setMove(u[i].getMove() + 3);
                            u[i].setChanged(true);
                        }
                    } else
                        return;
                }
            }}
        //DS Stats
        if (army.getBattle().getBattleOptions().isBalance()==false){
            Unit[] u = army.getUnits();
            if(u != null){
                for(int i = 0; i < u.length; i++){
                    if(u[i].getClass() != null){
                        if(u[i].getMoveType() == u[i].MOVE_TREAD || u[i].getMoveType() == u[i].MOVE_TIRE){
                            u[i]
									.setMove(u[i].getMove() + 2);
                            if (u[i].getMinRange() > 1){
                                u[i].setMaxRange(u[i]
										.getMaxRange() + 1);
                            }
                            u[i].setChanged(true);
                        }
                    }}
                return;
            }
        }
    }
    
    //used to deactivate Adder's CO Power the next day
    public void deactivateCOP(){
        COP = false;
        Unit[] u = army.getUnits();
        for(int i = 0; i < u.length; i++){
            if(army.getBattle().getBattleOptions().isBalance()) {
                if (u[i].isChanged() && u[i].getUType() == UnitID.ARTILLERY || u[i].getUType() == UnitID.ROCKET){
                    u[i]
							.setMaxRange(u[i].getMaxRange() - 1);
                    u[i].setChanged(false);
                }
                if(u[i].isChanged() && u[i].getUnitType() == UnitID.TANK || u[i].getUnitType() == UnitID.MEDTANK || u[i].getUnitType() == UnitID.MEGATANK || u[i].getUnitType() == UnitID.NEOTANK) {
                    u[i].setMove(u[i].getMove() - 1);
                    u[i].setChanged(false);
                }
            }else{
                if(u[i].getClass() != null && u[i].isChanged()){
                    u[i]
							.setMaxRange(u[i].getMaxRange() - 1);
                    u[i].setChanged(false);
                }
            }
        }
    }
    
    //used to deactivate Jake's Super CO Power the next day
    public void deactivateSCOP(){
        SCOP = false;
        if (army.getBattle().getBattleOptions().isBalance()==true){
            Unit[] u = army.getUnits();
            for(int i = 0; i < u.length; i++){
                if(u[i] != null){
                    if (u[i].isChanged() && u[i].getUType() == UnitID.ARTILLERY || u[i].getUType() == UnitID.ROCKET){
                        u[i].setMaxRange(u[i]
								.getMaxRange() - 1);
                        u[i].setChanged(false);
                    }
                    if(u[i].isChanged() && u[i].getUnitType() == UnitID.TANK || u[i].getUnitType() == UnitID.MEDTANK || u[i].getUnitType() == UnitID.MEGATANK || u[i].getUnitType() == UnitID.NEOTANK) {
                        u[i]
								.setMove(u[i].getMove() - 3);
                        u[i].setChanged(false);
                    }
                }
            }
        }
        Unit[] u = army.getUnits();
        for(int i = 0; i < u.length; i++){
            if(u[i].getClass() != null && u[i].isChanged() == true && u[i].getMinRange() > 1){{
                
            }
            u[i].setMove(u[i].getMove() - 2);
            u[i].setMaxRange(u[i].getMaxRange() - 1);
            u[i].setChanged(false);
            }else if(u[i].getClass() != null && u[i].isChanged() == true && u[i].getMinRange() == 1){
                u[i].setMove(u[i].getMove() - 2);
                u[i].setChanged(false);
            }
        }
    }
}