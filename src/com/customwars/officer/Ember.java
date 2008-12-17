package com.customwars.officer;

import com.customwars.Army;
import com.customwars.CO;
import com.customwars.Unit;
/*
 *Ember.java (Version 1.1)
 *Author: Paul Whan
 *Contributors: Adam Dziuk
 *Creation: Tronn Bonne
 *The Ember class is used to create an instance of the Bolt Guard CO Ember.
 *Xen would like to note that the Always delete skill of Ember's will have to wait until the delete function itself is implemented.
 */

public class Ember extends CO{
   
//constructor
    public Ember() {
        name = "Ember";
        id = 28;
        
        String CObiox = "An extremely belligerent member of the Bolt Guard that is notorious for her ruthlessness. She fights purely for the thrill of battle and is feared by both enemies and allies.";             //Holds the condensed CO bio'
        String titlex = "Ruthless Flame";
        String hitx = "Roses"; //Holds the hit
        String missx = "Violets"; //Holds the miss
        String skillStringx = "Ember’s units show no mercy. Increased firepower when attacking a unit with less HP. Can delete units even after they have performed an action.";
        String powerStringx = "Offensive boost. Firepower is increased even further when attacking a unit with less HP."; //Holds the Power description
        String superPowerStringx = "All enemies suffer one HP of damage. Firepower is greatly increased when attacking a unit with less HP."; //Holds the Super description
                      //"                                    " sizing markers
        String intelx = "Ember's units focus on destroying   " +
                        "damaged enemies more quickly. She   " +
                        "can also delete her own units at any" +
                        "time.  Her powers enhance the speed " +
                        "can take out lower HP foes, and her " +
                        "super also deals mass damage.";//Holds CO intel on CO select menu, 6 lines max

        CObio = CObiox;
        title = titlex;
        hit = hitx;
        miss = missx;
        skillString = skillStringx;
        powerString = powerStringx;
        superPowerString = superPowerStringx;
        intel = intelx;
        
        String[] TagCOsx = {"Kindle","Koal","Flak","Mina","Falcone","Graves","Artemis"}; //Names of COs with special tags
        String[] TagNamesx = {"Firestarters","Road Rage","Dual Strike","Dual Strike","Dual Strike","Dual Strike","Dual Strike"}; //Names of the corresponding Tags
        int[] TagStarsx = {1,2,0,0,0,0,0}; //Number of stars for each special tag.
        int[] TagPercentx = {110,110,105,105,105,85,80}; //Percent for each special tag.
       
        TagCOs = TagCOsx;
        TagNames = TagNamesx;
        TagStars = TagStarsx;
        TagPercent = TagPercentx;
       
        String[] COPowerx =
        {"Take no prisoners!",
         "Ahahaha… I’ll destroy you all!",
         "You won't like me when I am angry!",
         "I swear to make you rue this day!",
         "Burn everything! I don’t want to see anything left untouched!",
         "I have fury!",};
       
        String[] Victoryx =
        {"You better hope we do not meet again.",
         "I love this job!",
         "I wished the battle had lasted a little longer. Oh well."};
       
        String[] Swapx =
        {"I'm going to enjoy this...",
         "Time to die!"};
       
        COPower = COPowerx;
        Victory = Victoryx;
        Swap = Swapx;
       
        COPName = "Rampage";
        SCOPName = "Scorched Earth";
        COPStars = 3.0;
        maxStars = 7.0;
        this.army = army;
        style = BLACK_HOLE;
       
        alwaysDelete = true;
    }
   
//used to get the attack bonus for damage calculation
    public int getAtk(Unit attacker, Unit defender){
      if(defender != null)
      {
          if(SCOP){
              if (defender.getDisplayHP() < 10)
                 return 180;
              else
                 return 110;
          }
          if(COP){  
              if (defender.getDisplayHP() < 10)
                 return 160;
              else
                 return 130;
          }
          if (defender.getDisplayHP() < 10)
            return 120;
          else
            return 100;
      }
        return 100;
    }
   
    public int getInventionAtk(Unit attacker, Unit inv){
      if(SCOP){
          if (inv.getDisplayHP() < 10)
             return 180;
          else
             return 110;
      }
      if(COP){  
          if (inv.getDisplayHP() < 10)
             return 160;
          else
             return 130;
      }
      if (inv.getDisplayHP() < 10)
        return 120;
      else
        return 100;
    }
   
//used to get the defense bonus for damage calculation
    public int getDef(Unit attacker, Unit defender){
   if (COP || SCOP)
      return 110;
        return 100;
    }
   
//carries out Andy's CO Power, called by CO.activateCOP()
    public void COPower(){
        COP = true;
    }
   
   
//carries out Andy's Super CO Power, called by CO.activateSCOP()
    public void superCOPower(){
       
        SCOP = true;
        Army[] armies = army.getBattle().getArmies();
        Unit[] u;
       
        for(int i = 0; i < armies.length; i++){
            if(armies[i].getSide() != army.getSide() && armies[i].getUnits() != null){
                u = armies[i].getUnits();
                for(int s = 0; s < u.length; s++){
                    if(u[s].getClass() != null){
                        u[s].damage(10, false);
                    } else
                        return;
                }
               
               
            }
        }
    }
   
    public void setChange(Unit u) { ;}
   
    public void unChange(Unit u) { ;}
   
//used to deactivate Ember's CO Power the next day
    public void deactivateCOP(){
        COP = false;
    }
   
//used to deactivate Ember's Super CO Power the next day
    public void deactivateSCOP(){
        SCOP = false;
    }
}