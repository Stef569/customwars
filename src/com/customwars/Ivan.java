package com.customwars;
/*
 *Grimm.java
 *Author: Adam Dziuk, Kosheh, Paul Whan
 *Contributors:
 *Creation:
 *The Grimm class is used to create an instance of the Yellow Comet CO Grimm (copyright Intelligent Systems).
 */

public class Ivan extends CO{
    
    int store;
//constructor
    public Ivan() {
        name = "Ivan";
        id = 23; //placeholder
        
        String[] TagCOsx = {"Sensei","Sasha","Javier","Von Bolt"}; //Names of COs with special tags
        String[] TagNamesx = {"Rolling Thunder","Dual Strike","Dual Strike","Dual Strike"}; //Names of the corresponding Tags
        int[] TagStarsx = {1,0,0,0}; //Number of stars for each special tag.
        int[] TagPercentx = {110,105,105,90}; //Percent for each special tag.
        
        TagCOs = TagCOsx;
        TagNames = TagNamesx;
        TagStars = TagStarsx;
        TagPercent = TagPercentx;
        
        
        String[] COPowerx =
        {"Y'all can run, but you can't hide!",
         "I ain't gonna take your guff!",
         "Y'all gimme no choice... Time to bring in the big guns!",
         "Let's do us some target practice!",
         "Well, shuck my corn! Yer history!",
         "Aw, shucks. I was hopin' it wouldn't come to this.",};
        
        String[] Victoryx =
        {"I wait like a snake in the grass... then I strike!",
         "Ayup. That's just about what I figured.",
         "I hope this gets easier. That was harder'n college!"};
        
        String[] Swapx =
        {"...Hmm? Oh, sorry... I spaced out there.",
         "Keep your distance, or y'all might get hurt!"};
        
        Swap = Swapx;
        COPower = COPowerx;
        Victory = Victoryx;
        
        COPName = "Knuckleduster";
        SCOPName = "HippAwesome";
        COPStars = 3.0;
        maxStars = 7.0;
        this.army = army;
        style = YELLOW_COMET;
    }
//used to get the attack bonus for damage calculation
    public int getAtk(Unit attacker, Unit defender){
        if(SCOP)
            return 110;
        if(COP)
            return 110;
        else 
            return 100;        
    }
    
    //Ivan has +1 range D2D
    public void setChange(Unit u){ 
        if(u.getMinRange() > 1)
                u.setMaxRange(u.getMaxRange() + 1);
    }
    
    //unchanges unit
    public void unChange(Unit u){
        if(u.getMinRange() > 1)
                u.setMaxRange(u.getMaxRange() - 1);
    }    
    
//used to get the defense bonus for damage calculation
    public int getDef(Unit attacker, Unit defender){
        if(defender instanceof Infantry || defender instanceof Mech){
            if(SCOP||COP)
                return 100;
            else
                return 90;
        }
        else if (SCOP || COP)
            return 110;
        else
            return 100;
    }
    
    //Ivan has +1 range D2D

//carries out Grimm's CO Power, called by CO.activateCOP()
    public void COPower(){
        COP = true;
    }
    
//lol, +1 range
    public void superCOPower(){
        SCOP = true;
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
    
//used to deactivate Grimm's CO Power the next day
    public void deactivateCOP(){
        COP = false;
    }
    
//used to deactivate Grimm's Super CO Power the next day
    public void deactivateSCOP(){
        SCOP = false;
        Unit[] u = army.getUnits();
        for(int i = 0; i < u.length; i++){
            if(u[i].getClass() != null && u[i].isChanged()){
                u[i]
						.setMaxRange(u[i].getMaxRange() - 1);
                u[i].setChanged(false);} else
                    return;
        }
    }
    
    public void beforeAttack(Unit attacker, Unit defender, boolean attack)
    {
        if(attack)
        {
            store = defender.getDisplayHP();
        }
    }
    
    public void beforeCounter(Unit attacker, Unit defender, int damage, boolean destroy, boolean attack) 
    {
        int temp;
        if(attack) //if Ivan attacked
            if(attacker.getMinRange()>1) //if ranged unit
                if((Math.abs(defender.getLocation().getRow() - attacker.getLocation().getRow()) + Math.abs(defender.getLocation().getCol() - attacker.getLocation().getCol())) == attacker.getMaxRange())
                {
                        temp = (damage/2) * (defender.getPrice()/attacker.getPrice());
                        attacker.damage(temp, false);
                }
    }
    
    public void afterCounter(Unit attacker, Unit defender, int damage, boolean destroy, boolean attack) 
    {
        if(attack)
            if(attacker.getMinRange()>1)
            {
                attacker.getCOstore()[0] +=1;
                if(attacker.getCOstore()[0] > 1) //1 = fired once, etc.
                {attacker.damage(10,false);}
                if(attacker.getCOstore()[0]<5)
                {attacker.setActive(true);}
                
            }
    }
}

