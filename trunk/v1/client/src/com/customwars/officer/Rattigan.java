package com.customwars.officer;

import com.customwars.unit.Army;
import com.customwars.unit.Unit;

/** Rattigan: A dutiful CO of the Yellow Comet army addled by a
 * childhood phobia. Blames Sensei for Yellow Comet’s lack of
 * former glory. Excels in crowd control, but weak against lone
 * targets.*/
public class Rattigan extends CO{
   //constructor
   public Rattigan() {
      name = "Rattigan";
      setId(59);

      String CObiox =
         "A dutiful CO of the Yellow Comet army   " +
         "addled by a childhood phobia. Blames    " +
         "Sensei for Yellow Comet’s lack of former" +
         "glory.                                  ";

      String titlex = "Exterminator";
      String hitx = "Yellow Comet";
      String missx = "Infestations";
      String skillStringx =
         "Rattigan's ground troops are trained to " +
         "be extremely capable at dealing with    " +
         "large regiments of units at a time, but " +
         "are taken off guard by lone units.      ";
      String powerStringx =
         "Receives a small offensive boost when   " +
         "attacking enemies in groups. Movement is" +
         "increased by one space.                 ";
      String superPowerStringx =
         "Receives a firepower and defense boost  " +
         "for engaging enemies in groups. Enemy   " +
         "units suffer one HP of damage.          ";
              //"                                    " sizing markers
        String intelx = "" +
                        "" +
                        "" +
                        "" +
                        "" +
                        "";//Holds CO intel on CO select menu, 6 lines max
        
        intel = intelx;
      CObio = CObiox;
      title = titlex;
      hit = hitx;
      miss = missx;
      skillString = skillStringx;
      powerString = powerStringx;
      superPowerString = superPowerStringx;

      String[] TagCOsx = {"Grimm", "Kanbei", "Olaf", "Sensei", "Von Bolt"}; //Names of COs with special tags
      String[] TagNamesx = {"Cleaning House", "Former Glory", "Dual Strike", "Dual Strike", "Dual Strike"};
      int[] TagStarsx = {1, 1, 0, 0, 0}; //Number of stars for each special tag.
      int[] TagPercentx = {110, 105, 105, 90, 90}; //Percent for each special tag.

      setTagCOs(TagCOsx);
      setTagNames(TagNamesx);
      setTagStars(TagStarsx);
      setTagPercent(TagPercentx);

      String[] COPowerx = {
            "All those enemy men... heh, perfect!",
            "G-gah! They’re everywhere!",
            "Hrumph! This’ll learn yeh!",
            "Heh, could Sensei do THIS?",
            "Who the heck taught you to fight?",
            "Stay on your toes! I’m talking to you!!"
      };

      String[] Victoryx = {
            "With all of those troops, you’d think they’d last longer…",
            "A-aughk!! Uhm… I mean… congratulations, men.",
            "With Yellow Comet on the line, I can’t lose."
      };

      String[] Swapx = {
            "You won’t make a fool out of me!",
            "H-huh? Oh, all right."      
      };

      /*
      String[] Defeatx = {
            "W-we're surrounded!!! H-help... me....",
            "..."
      };
       */

      setCOPower(COPowerx);
      Victory = Victoryx;
      setSwap(Swapx);

      COPName = "Raid";
      SCOPName = "The Sweep";
      COPStars = 3.0;
      maxStars = 7.0;
      this.army = army;
      style = YELLOW_COMET;
   }

   public int getAtk(Unit attacker, Unit defender){
      int nearbyUnits = 0;

      // It seems that CO.getInventionAtk passes an argument of
      // 'null' for 'defender.' This avoids the problems stemming
      // from asking Java for the location of null.
      if(defender != null){
         int xOff = defender.getLocation().getCol();
         int yOff = defender.getLocation().getRow();

         // Loop through enemy units to see if any are nearby
         Army enemyArmy = defender.getArmy();
         Unit[] u;
         if(enemyArmy.getUnits() != null){
            u = enemyArmy.getUnits();
            for(int s = 0; s < u.length; s++){
               if(u[s].getClass() != null
                     && (Math.abs((u[s].getLocation().getCol() - xOff))
                           + Math.abs((u[s].getLocation().getRow() - yOff))
                           <= 2)
                           && u[s] != defender){
                  nearbyUnits++;
               }
            }
         }

         if(SCOP){
            // D2D bonus
            if(nearbyUnits > 0)
               return 140;
            // Air or Sea unit?
            if(defender.getMType() == defender.MOVE_AIR
                  || defender.getMType() == defender.MOVE_SEA
                  || defender.getMType() == defender.MOVE_TRANSPORT)
               return 110;
            // Penalty!
            return 100;
         }
         if(COP){
            // Nearby bonus
            if(nearbyUnits > 0)
               return 120 + ((this.getArmy().getBattle().getTurn() == this.getArmy().getSide()) ? (nearbyUnits * 5) : 0);
            // Air or Sea unit?
            if(defender.getMType() == defender.MOVE_AIR
                  || defender.getMType() == defender.MOVE_SEA
                  || defender.getMType() == defender.MOVE_TRANSPORT)
               return 100;
            // Penalty!
            return 90;
         }
         // D2D bonus
         if(nearbyUnits > 0)
            return 110;
         // Air or Sea unit?
         if(defender.getMType() == defender.MOVE_AIR
               || defender.getMType() == defender.MOVE_SEA
               || defender.getMType() == defender.MOVE_TRANSPORT)
            return 100;
         // Penalty!
         return 90;
      }
      // For inventions
      else {
         if(COP || SCOP) return 110;
         return 100;
      }
   }

   public int getDef(Unit attacker, Unit defender){
      if(COP) return 110;
      if(SCOP){
         int xOff = defender.getLocation().getCol();
         int yOff = defender.getLocation().getRow();

         // Loop through enemy units to see if any are nearby
         Army enemyArmy = defender.getArmy();
         Unit[] u;
         if(enemyArmy.getUnits() != null){
            u = enemyArmy.getUnits();
            for(int s = 0; s < u.length; s++){
               if(u[s].getClass() != null
                     && (Math.abs((u[s].getLocation().getCol() - xOff))
                           + Math.abs((u[s].getLocation().getRow() - yOff))
                           <= 2)
                           && u[s] != defender){
                  return 120;
               }
            }
         }
         else return 110;
      }
      return 100;
   }

   public void setChange(Unit u){};

   public void unChange(Unit u){};

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

   public void superCOPower(){
      SCOP = true;
      int nearbyUnits = 0;
      int xOff = 0;
      int yOff = 0;

      //mass damage
      Army[] armies = army.getBattle().getArmies();
      Unit[] u;
      for(int i = 0; i < armies.length; i++){
         if(armies[i].getSide() != army.getSide() && armies[i].getUnits() != null){
            u = armies[i].getUnits();
            for(int s = 0; s < u.length; s++){

               xOff = u[s].getLocation().getCol();
               yOff = u[s].getLocation().getRow();
               nearbyUnits = 0;

               // Loop through enemy units to see if any are nearby
               for(int j = 0; j < u.length; j++){
                  if(u[j].getClass() != null
                        && (Math.abs((u[j].getLocation().getCol() - xOff))
                              + Math.abs((u[j].getLocation().getRow() - yOff))
                              <= 2)
                              && u[s] != u[j]){
                     nearbyUnits++;
                  }
               }

               if(u[s].getClass() != null){
                  if(!u[s].isInTransport())u[s].damage(10 + (5 * nearbyUnits), false);
               } else
                  return;
            }
         }
      }
   }

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

   public void deactivateSCOP(){
      SCOP = false;
   }
}