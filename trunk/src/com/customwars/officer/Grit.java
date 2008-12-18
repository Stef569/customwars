package com.customwars.officer;

import com.customwars.unit.Unit;

public class Grit extends CO{
    
    //constructor
    public Grit() {
        name = "Grit";
        setId(8);

        String CObiox = "A laid-back style masks his dependability. A peerless marksman. Works well with Olaf.";             //Holds the condensed CO bio'
        String titlex = "The Lone Ranger";
        String hitx = "Cats"; //Holds the hit
        String missx = "Rats"; //Holds the miss
        String skillStringx = "Indirect-combat units cause more damage. Weak in non-infantry direct combat.";
        String powerStringx = "Increases range of indirect units by one space. Firepower of these units also rise."; //Holds the Power description
        String superPowerStringx = "Increases range of indirect units by two spaces. Firepower of these units greatly rise."; //Holds the Super description
                      //"                                    " sizing markers
        String intelx = "Grit has superior indirect units    " +
                        "and as such they have top firepower," +
                        "but his directs have weak firepower." +
                        "Both of his powers increase range   " +
                        "and firepower of his indirects.";//Holds CO intel on CO select menu, 6 lines max

        CObio = CObiox;
        title = titlex;
        hit = hitx;
        miss = missx;
        skillString = skillStringx;
        powerString = powerStringx;
        superPowerString = superPowerStringx;
        intel = intelx;
        
        String[] TagCOsx = {"Olaf", "Max", "Colin", "Von Bolt"};              //Names of COs with special tags
        String[] TagNamesx  = {"Snow Patrol", "Big Country", "Dual Strike", "Dual Strike"};          //Names of the corresponding Tags
        int[] TagStarsx = {1 ,2, 0, 0 };           //Number of stars for each special tag.
        int[] TagPercentx = {110, 110, 105, 90};       //Percent for each special tag.
        
        setTagCOs(TagCOsx);
        setTagNames(TagNamesx);
        setTagStars(TagStarsx);
        setTagPercent(TagPercentx);
        
        String[] COPowerx =
        {"Once you're in my sights, there's no gettin' away!",
         "Reckon it's time to take you down!",
         "Where's the fool who wants to help me with target practice?",
         "Y'all can run, but you can't hide!",
         "Y'all gimme no choice... Time to bring in the big guns!",
         "Aw, shucks. I was hopin' it wouldn't come to this."};
        
        String[] Victoryx =
        {"This ain't for show.",
         "Maybe now I can get some shut-eye.",
         "I hope this gets easier. That was harder'n college!"};
        
        String[] Swapx =
        {"What's the ruckus?",
         "Keep your distance, or y'all might get hurt!"};
        
        String[] defeatx =
        {"Aw, possum spit!",
         "Just as I reckoned... This ain't gonna be no Sunday stroll."} ;
        
        setSwap(Swapx);       
        setCOPower(COPowerx);
        Victory = Victoryx;
        defeat = defeatx;
        
        COPName = "Snipe Attack";
        SCOPName = "Super Snipe";
        COPStars = 3.0;
        maxStars = 6.0;
        this.army = army;
        style = BLUE_MOON;
    }
    //used to get the attack bonus for damage calculation
    public int getAtk(Unit attacker, Unit defender){
        //Balance Stats
        if (army.getBattle().getBattleOptions().isBalance()== true){
            if(attacker.getMinRange() > 1){
                if(SCOP)
                    return 160;
                if(COP)
                    return 140;
                return 120;}
            if(attacker.getMinRange() == 1 && attacker.getUType() > 1){
                if(COP || SCOP)
                    return 100;
                else
                    return 90;}
            if(attacker.getMinRange() == 1 && attacker.getUType() < 1){
                if (COP || SCOP)
                    return 110;
                else
                    return 100;}
        }
        //DS Mode Stats
        if(army.getBattle().getBattleOptions().isBalance()==false){
            if(attacker.getMinRange() > 1){
                if(SCOP || COP)return 160;
                return 120;
            }
            if(attacker.getMinRange() == 1 && attacker.getUType() > 1){
                if(COP || SCOP)return 90;
                return 80;
            }
            if(SCOP || COP){
                return 110;
            }
            return 100;} else return 100;
    }
    
    //used to get the defense bonus for damage calculation
    public int getDef(Unit attacker, Unit defender){
        //Balance Stats
        if (army.getBattle().getBattleOptions().isBalance()== true){
            if (SCOP||COP)
                return 110;
            else
                return 100;}
        //DS Mode Stats
        else{
            if(COP || SCOP)
                return 110;
            return 100;}
    }
    
    //changes unit for this CO
    public void setChange(Unit u){
        if(!army.getBattle().getBattleOptions().isBalance())
            if(u.getMinRange() > 1)
                u.setMaxRange(u.getMaxRange() + 1);
    }
    
    //unchanges unit
    public void unChange(Unit u){
        if(!army.getBattle().getBattleOptions().isBalance())
            if(u.getMinRange() > 1)
                u.setMaxRange(u.getMaxRange() - 1);
    }
    
    //carries out Adder's CO Power, called by CO.activateCOP()
    public void COPower(){
        COP = true;
        Unit[] u = army.getUnits();
        for(int i = 0; i < u.length; i++){
            if(u[i].getClass() != null){
                if(u[i].getMinRange() > 1){
                    u[i]
							.setMaxRange(u[i].getMaxRange() + 1);
                    u[i].setChanged(true);
                }
            } else
                return;
        }
    }
    
    //carries out Adder's Super CO Power, called by CO.activateSCOP()
    public void superCOPower(){
        SCOP = true;
        Unit[] u = army.getUnits();
        for(int i = 0; i < u.length; i++){
            if(u[i].getClass() != null){
                if(u[i].getMinRange() > 1){
                    u[i]
							.setMaxRange(u[i].getMaxRange() + 2);
                    u[i].setChanged(true);
                }
            } else
                return;
        }
    }
    
    //used to deactivate Adder's CO Power the next day
    public void deactivateCOP(){
        COP = false;
        Unit[] u = army.getUnits();
        for(int i = 0; i < u.length; i++){
            if(u[i].getClass() != null){
                if(u[i].isChanged()){
                    u[i]
							.setMaxRange(u[i].getMaxRange() - 1);
                    u[i].setChanged(false);
                }
            } else
                return;
        }
    }
    
    //used to deactivate Adder's Super CO Power the next day
    public void deactivateSCOP(){
        SCOP = false;
        Unit[] u = army.getUnits();
        for(int i = 0; i < u.length; i++){
            if(u[i].getClass() != null){
                if(u[i].isChanged()){
                    u[i]
							.setMaxRange(u[i].getMaxRange() - 2);
                    u[i].setChanged(false);
                }
            } else
                return;
        }
    }
}
