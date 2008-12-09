package com.customwars;
/*
 *Graves.java
 *Author: Albert Lai
 *Contributors:
 *You're in grave danger! Ho ho ho!
 *You're digging your own grave! Hee hee hee!
 */

public class Graves extends CO{
   
    //constructor
    public Graves() {
        name = "Graves";
        id = 36;
       
        String CObiox = "A former assassin dissatisfied with where Wars World is headed. Secretly aids Hawke's cause and overtly aids Parallel Galaxy. No one knows where his true loyalties lie.";             //Holds the condensed CO bio'
        String titlex = "Stoic Slayer";
        String hitx = "Mystery Novels"; //Holds the hit
        String missx = "Romance Novels"; //Holds the miss
        String skillStringx = "Enemy units reduced to two or less HP by Graves' units become paralyzed. Low firepower against undamaged enemies.";
        String powerStringx = "Enemy units suffer one HP of damage. Enemy units with three or less HP become paralyzed."; //Holds the Power description
        String superPowerStringx = "Enemy units suffer two HP of damage. Enemy units with four or less HP become paralyzed."; //Holds the Super description
                      //"                                    " sizing markers
        String intelx = "" +
                        "" +
                        "" +
                        "" +
                        "" +
                        "";//Holds CO intel on CO select menu, 6 lines max

        CObio = CObiox;
        title = titlex;
        hit = hitx;
        miss = missx;
        skillString = skillStringx;
        powerString = powerStringx;
        superPowerString = superPowerStringx;
        intel = intelx;
        String[] COPowerx =
        {"Fear is a valuble tool. I suggest you learn how to use it.",
        "Do you desire death that greatly?",
        "You must give everything if you want to win.",
        "You are ill prepared to face me.",
        "A valiant effort. But futile, nonetheless.",
        "Prepare yourself." };
       
        String[] Victoryx =
        {"That was it? ...I overestimated you.",
        "Such a victory was... so rudely forced",
        //replace victory with rape, and this would be an excellent victory quote
        "Lives could have been spared had you just accepted your fate." };
       
        String[] Swapx =
        {"Fear the shadow that rises to meet you.",
        "I will show you fear in a handful of dust."};
       
        COPower = COPowerx;
        Victory = Victoryx;
        Swap = Swapx;
       
        //No special tags
        String[] TagCOsx = {"Ain", "Hawke", "Thanatos", "Falcone", "Eagle", "Adder", "Ember", "Rachel", "Nell"}; //Names of COs with special tags
        String[] TagNamesx = {"Ideal", "Antagonistic Desire", "Desecrate", "Memento Mori" , "Dual Strike", "Dual Strike", "Dual Strike", "Dual Strike", "Dual Strike"}; //Names of the corresponding Tags
        int[] TagStarsx = {2,2,1,1,0,0,0,0,0}; //Number of stars for each special tag.
        int[] TagPercentx = {120, 120,115,110,90,85,85,85,80}; //Percent for each special tag.
        //Here's to you, Mr. Massive-amounts-of-tags (chorus: he has a million different tag partners!)
       
        TagCOs = TagCOsx;
        TagNames = TagNamesx;
        TagStars = TagStarsx;
        TagPercent = TagPercentx;
       
        COPName = "Plague";
        SCOPName = "Perdition";
        COPStars = 4.0;
        maxStars = 7.0;
        this.army = army;
        style = PARALLEL_GALAXY;
//I sure am a boring CO. >_> *is shot*
    }
   
//used to get the attack bonus for damage calculation
    public int getAtk(Unit attacker, Unit defender){
        if(SCOP||COP)
                return 110;
        else
            return 100;
       
    }
   
    public void setChange(Unit u){
       
    }
   
    public void unChange(Unit u){
       
    }
   
   
//used to get the defense bonus for damage calculation
    public int getDef(Unit attacker, Unit defender){
        if(SCOP || COP)return 110;
        return 100;
    }
   
//carries out Blandie's CO Power, called by CO.activateCOP()
    public void COPower(){
        COP = true;
       
        Army[] armies = army.getBattle().getArmies();
        Unit[] u = army.getUnits();
        for(int i = 0; i < armies.length; i++){
            if(armies[i].getSide() != army.getSide() && armies[i].getUnits() != null){
                u = armies[i].getUnits();
                for(int s = 0; s < u.length; s++){
                    if(u[s].getClass() != null){
                        if(!u[s].isInTransport()){
                            //Damages and paralyzes
                            u[s].damage(10, false);
                            if(u[s].getDisplayHP()<=3)
                                u[s].paralyzed = true;
                            }
                        }
                     else
                        return;
                }
            }
        }
    }
   
//carries out Blandie's Super CO Power, called by CO.activateSCOP()
    public void superCOPower(){
        SCOP = true;
        Army[] armies = army.getBattle().getArmies();
        Unit[] u = army.getUnits();
        for(int i = 0; i < armies.length; i++){
            if(armies[i].getSide() != army.getSide() && armies[i].getUnits() != null){
                u = armies[i].getUnits();
                for(int s = 0; s < u.length; s++){
                    if(u[s].getClass() != null){
                        if(!u[s].isInTransport()){
                            //Damages and paralyzes
                            u[s].damage(20, false);
                            if(u[s].getDisplayHP()<=4)
                                u[s].paralyzed = true;
                            }
                        }
                     else
                        return;
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
    }
   
    public void afterAttack(Unit owned, Unit enemy, int damage, boolean destroy, boolean attack)
    {
        if(!destroy&&attack)
        {
                if(SCOP){
                    if(enemy.getDisplayHP()<=4)
                        enemy.paralyzed = true;
                }
                if(COP){
                    if(enemy.getDisplayHP()<=3)
                        enemy.paralyzed = true;
                }
                if(enemy.getDisplayHP()<=2)
                {enemy.paralyzed = true;}
            }
    }
    public void afterCounter(Unit owned, Unit enemy, int damage, boolean destroy, boolean attack)
    {
        if(damage>0 && !destroy && !attack) //If the defending unit (that's Graves) has dealt damage
        {
                if(SCOP){
                    if(enemy.getDisplayHP()<=4)
                        enemy.paralyzed = true;
                }
                if(COP){
                    if(enemy.getDisplayHP()<=3)
                        enemy.paralyzed = true;
                }
                if(enemy.getDisplayHP()<=2)
                {enemy.paralyzed = true;}
        }
    }
} 
