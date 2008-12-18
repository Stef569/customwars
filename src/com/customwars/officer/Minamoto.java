package com.customwars.officer;

import com.customwars.map.Map;
import com.customwars.map.location.Location;
import com.customwars.map.location.Terrain;
import com.customwars.unit.Unit;

/** Minamoto: A proud, vain CO and a master swordsman who grew
 * up in the mountains of Yellow Comet.
 * Units have increased firepower while near mountains. Powers allow
 * him to move away enemy units. */
public class Minamoto extends CO{
   /** Indicates that Minamoto dealt enough damage to cause knockback. */
   private boolean kbTrigger = false;
   private static final int DIR_UP = 0;
   private static final int DIR_DOWN = 1;
   private static final int DIR_LEFT = 2;
   private static final int DIR_RIGHT = 3;

   //constructor
   public Minamoto() {
      name = "Minamoto";
      setId(68);

      String CObiox =
         "A skilled but arrogant CO and a master  " +
         "swordsman who grew up in the mountains  " +
         "of Yellow Comet.                        ";

      String titlex = "Death on the Wind";
      String hitx = "Rice Cakes";
      String missx = "Mackerel";
      String skillStringx =
         "Units near mountains have increased     " +
         "firepower.                              ";
      String powerStringx =
         "Units can blow lighter enemies away when" +
         "attacking.                              ";
      String superPowerStringx =
         "Unit movement is increased by two, and  " +
         "large units blow enemies away.          ";
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

      String[] TagCOsx = {"Kanbei", "Sensei", "Javier", "Sabaki", "Ozzy", "Zandra", "Yukio", "Eric", "Carmen", "Edward", "Mary"}; //Names of COs with special tags
      String[] TagNamesx = {"Rising Sun", "Eastern Gale", "Sword and Shield", "Dual Strike", "Dual Strike", "Dual Strike", "Dual Strike", "Dual Strike","Dual Strike", "Dual Strike", "Dual Strike", "Dual Strike"};
      int[] TagStarsx = {2, 1, 1, 0, 0, 0, 0, 0, 0, 0}; //Number of stars for each special tag.
      int[] TagPercentx = {120, 115, 110, 80, 90, 90, 90, 90, 90, 90, 90}; //Percent for each special tag.

      setTagCOs(TagCOsx);
      setTagNames(TagNamesx);
      setTagStars(TagStarsx);
      setTagPercent(TagPercentx);

      String[] COPowerx = {
            "Clear a path! We shall not stop for any man!",
            "You fight skillfully... But I fight flawlessly!",
            "Are you familiar with the taste of steel? You shall be soon!",
            "Hm hm hm... Are you trying to mock me, or is this truly your best effort?",
            "Mountain winds, hone my blade... and scatter my enemies!",
            "Begone! I do not have time to waste on peons such as yourself!"
      };

      String[] Victoryx = {
            "Hm hm hm! Perhaps next time I should use a wooden sword?",
            "The battle has ended. Yield now, or suffer for this insolence.",
            "Hm hm hm... I can't fault you for having tried!"
      };

      String[] Swapx = {
            "Rest. I shall assume the duties of command.",
            "My blade is drawn and ready!"
      };

      /*
      String[] Defeatx = {
            "My Emperor... I have failed you...",
            "I underestimated your skill, nothing more!"
      };
       String[] BattleGood = {
             "Hm hm!",
             "Yes!"
       };
       String[] BattleNeutral = {
             "Hmm...",
             ""
      };

       String[] BattleBad = {
             "No!",
             "Curses!"
      };
       */

      setCOPower(COPowerx);
      Victory = Victoryx;
      setSwap(Swapx);

      COPName = "Wind Blade";
      SCOPName = "Storm Blades";
      COPStars = 3.0;
      maxStars = 6.0;
      this.army = army;
      style = YELLOW_COMET;
   }

   public int getAtk(Unit attacker, Unit defender){
      Map curMap = attacker.getMap();
      int xOff = attacker.getLocation().getCol();
      int yOff = attacker.getLocation().getRow();

      // Loop over columns
      for(int i = -2; i <= 2; i++){
         // Loop over rows
         for(int j = -2; j <= 2; j++){
            // If the tile is within range and is a mountain...
            if((Math.abs(i) + Math.abs(j) <= 2)
                  && curMap.onMap(xOff + i, yOff + j)
                  && (curMap.find(new Location(xOff + i, yOff + j)).getTerrain().getIndex() == 2)){
               if(COP || SCOP)   return 130;
               return 115;
            }
         }
      }

      //      Default power boosts
      if(SCOP || COP) return 110;

      //      No bonus
      return 100;
   }

   public int getDef(Unit attacker, Unit defender){
      if(COP || SCOP)
         return 110;
      return 100;
   }

   public void setChange(Unit u){};

   public void unChange(Unit u){};

   //   Keep track of stuff for the sake of FWOOSHING after the attack.
   public void afterAttack(Unit owned, Unit enemy, int damage, boolean destroy, boolean attack){
      // Minamoto attacked
      if(attack){
         if(COP && damage >= 45)
            kbTrigger = true;
         else if(SCOP && damage >= 40)
            kbTrigger = true;
         else kbTrigger = false;
      }
      else kbTrigger = false;
   }

   //   Keep track of stuff for the sake of FWOOSHING after the attack.
   public void afterCounter(Unit owned, Unit enemy, int damage, boolean destroy, boolean attack){
      // Minamoto countered
      if(attack){
         if(COP && damage >= 45)
            kbTrigger = true;
         else if(SCOP && damage >= 40)
            kbTrigger = true;
      }
   }

   public void afterAttackAction(Unit owned, Unit enemy, boolean attack){
      // Will the enemy be FWOOSHed?
      if((SCOP || COP)
            && (kbTrigger)
            && (owned.getMaxRange() == 1)
            // This is a lame way to check if the enemy unit was destroyed. If it isn't on the map
            // anymore, it was killed.
            && owned.getMap().hasUnit(enemy.getLocation().getCol(), enemy.getLocation().getRow())){
         
         kbTrigger = false;

         // Distance to blow the enemy unit.
         int dist = 2;
         if(SCOP) dist = 4;
         
         int dir = DIR_UP;

         Location atkPos = owned.getLocation();
         Location defPos = enemy.getLocation();

         // Store the unit's original position
         int oldX = defPos.getCol();
         int oldY = defPos.getRow();
         int distMoved = 0;

         // Is the unit being knocked right?
         if(atkPos.getCol() < oldX) dir = DIR_RIGHT;
         // How about left?
         else if(atkPos.getCol() > oldX) dir = DIR_LEFT;
         // Down?
         else if(atkPos.getRow() < oldY) dir = DIR_DOWN;
         // In that case it must be above.

         
         // Move the unit
         blowUnitBack(defPos, enemy, dist, dir);
         
         // Find the unit's new position
         Location newPos = enemy.getLocation();
         int newX = newPos.getCol();
         int newY = newPos.getRow();

         // Find the distance moved
         if(oldX != newX)
            distMoved = Math.abs(oldX - newX);
         else distMoved = Math.abs(oldY - newY);
         
         // Deal damage.
         enemy.damage((dist * 10) - (distMoved * 5), false);
         
         dist = 0;
         }
      }

   //   Activates Minamoto's COP.
   public void COPower(){
      COP = true;
   }

   //   Activates Minamoto's SCO.
   public void superCOPower(){
      SCOP = true;

      Unit[] u = army.getUnits();
      if(u != null)
         for(int i = 0; i < u.length; i++){
            // Increase movement.
            u[i].setMove(u[i].getMove() + 1);
            u[i].setChanged(true);
         }
   }

   //   Removes the effects of Minamoto's COP.
   public void deactivateCOP(){
      COP = false;
   }

   //   Removes the effects of Minamoto's SCO.
   public void deactivateSCOP(){
      SCOP = false;

      Unit[] u = army.getUnits();
      if(u != null)
         for(int i = 0; i < u.length; i++){
            if(u[i].isChanged() == true)
               // Reset movement.
               u[i].setMove(u[i].getMove() - 1);
            u[i].setChanged(false);
         }
   }

   //   The FWOOSH function. If you aren't familiar with recursion, turn back now.   
   /** Moves a unit in a specified direction.
    * @param pos The initial location of the unit.
    * @param blowee The unit to be knocked back.
    * @param distLeft The remaining distance to move the enemy unit.
    * @param dir The direction the enemy unit will be moved
    * @return True if the unit was moved, false if it was not.
    */
   private boolean blowUnitBack(Location pos, Unit blowee, int distLeft, int dir){
      // Have we already gone the max distance?
      if(distLeft <= 0) return false;

      Map map = blowee.getMap();
      int targetXPos = pos.getCol();
      int targetYPos = pos.getRow();

      // Is the unit being knocked right?
      if(dir == DIR_RIGHT) targetXPos++;
      // How about left?
      else if(dir == DIR_LEFT) targetXPos--;
      // Down?
      else if(dir == DIR_DOWN) targetYPos++;
      // In that case it must be up.
      else if(dir == DIR_UP) targetYPos--;

      Location dest = new Location(targetXPos, targetYPos);
      
      // Are we still on the map?
      if(!map.onMap(dest))
         return false;
      
      Terrain destT = map.find(dest).getTerrain();
      // Needed for Hovers
      Terrain curT = map.find(pos).getTerrain();

      // Can the target move onto that tile?
      if(destT.moveCost(blowee.getMType()) == -1)
         return false;

      //Hovercraft exception, cannot move straight from sea to land
      if(blowee.getMType() == blowee.MOVE_HOVER){
         if(destT.getIndex() == 6 || destT.getIndex() == 7){
            //moving from water
            //only allow movement onto water, shoals, or ports
            if(!((curT.getIndex() >= 5 && curT.getIndex() <= 8) || curT.getIndex() == 13))
               return false;
         }else if(curT.getIndex() == 6 || curT.getIndex() == 7){
            //moving into water
            //only allow movement from water, shoals, or ports
            if(!((destT.getIndex() >= 5 && destT.getIndex() <= 8) || destT.getIndex() == 13))
               return false;
         }
      }

      // Can't pass through enemy units!
      if(map.hasUnit(targetXPos, targetYPos) &&
            (map.find(dest).getUnit().getArmy().getSide() != blowee.getArmy().getSide()))
            return false;

      // If we don't find a suitable spot ahead...
      if(!blowUnitBack(dest, blowee, distLeft - 1, dir)){
         // Any allied units in the way?
         if(map.hasUnit(targetXPos, targetYPos))
            return false;
         
         // Then it's safe to move.
         map.move(blowee, dest);
         blowee.setLocation(dest);
      }

      // We've found the last tile.
      return true;
   }
}