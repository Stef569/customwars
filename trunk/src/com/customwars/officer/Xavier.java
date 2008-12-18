package com.customwars.officer;

import com.customwars.Mission;
import com.customwars.map.Map;
import com.customwars.map.Tile;
import com.customwars.map.location.Location;
import com.customwars.unit.Unit;

public class Xavier extends CO
{
   //What are modSpace?
   //Basically it's used to separate the column variable and row variable of Xavier's unit in its COstore[0] array
   //
   //For example,
   //
   //If there was an Infantry located at (8, 30) on a map, the value stored into its COstore[0] would be 830. This
   //is achieved in this manner:
   //
   //  tempCol = unit.col * modSpace
   //  tempRow = unit.row
   //  unit.COstore[0] = tempCol + tempRow
   //
   //Likewise, if a unit is located at (20, 15) on a map, the value stored into its COstore[0] would be 2015.
   //
   //To get the coordinates back, use the following formulas:
   //
   //  tempCol = unit.COstore[0] / modSpace
   //  tempRow = unit.COstore[0] % modSpace
   //
   //Using a previous example, 2015 % 100 should return 15, and 2015 / 100 should return 20, which are the
   //original column and row values.
   //
   //But this is obviously not applicable for all maps. For example if there was a map which was 250 by 250
   //and a unit was located at (25, 200), it would end up with a COstore[0] value of 2700. Which would not
   //produce the correct coordinates back! So the while loop in Xavier's dayStart will increase modSpace
   //by a factor of 10 until it exeeds the coordinates of the map, in order to keep accurate track.
   //
   //Hopefully no map size is so great that it would cause modSpace to exceed the limits of integers :(
   //
   private int modSpace = 100;
   
   //The values down there are used to keep track of the luck values being
   //thrown around for Xavier and his opponent. The luck minimums and
   //maximums are reset to normal after each attack.
   //
   private int baseMaxPosLuck_self = 0;
   private int baseMinPosLuck_self = 0;
   private boolean selfAdjusted = false;
   private int baseMinNegLuck_enemy = 0;
   private boolean enemyAdjusted = false;
   
    //constructor
    public Xavier()
    {
        name = "Xavier";
        setId(62);
        String CObiox = "Fulfills his duties without second thought or consideration of the after-effects of his actions. Wears a pair of fake claws.";             //Holds the condensed CO bio'
        String titlex = "Reality Virtual";
        String hitx = "Uncertainty"; //Holds the hit
        String missx = "Definitives"; //Holds the miss
        String skillStringx = "When Xavier's units drop below 5 HP, they are able to strike for maximum luck damage.";
        String powerStringx = "Attacks inflict more damage than expected."; //Holds the Power description
        String superPowerStringx = "When moved, units leave copies of themselves, able to take orders. These copies vanish the next day."; //Holds the Super description
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
       
        String[] TagCOsx = {"Mina","Cassidy","Levenworth","Graves"}; //Names of COs with special tags
        String[] TagNamesx = {"Kaleidoscope","Hidden Agendas","Dual Strike","Dual Strike"}; //Names of the corresponding Tags
        int[] TagStarsx = {1,1,0,0}; //Number of stars for each special tag.
        int[] TagPercentx = {105,110,105,85}; //Percent for each special tag.

        setTagCOs(TagCOsx);
        setTagNames(TagNamesx);
        setTagStars(TagStarsx);
        setTagPercent(TagPercentx);
       
        String[] COPowerx =
        {"You know, the laws of physics are made to be broken. Observe.",
              "You might say I'm a rather.. twisted individual.",
              "Reality is only trivial. Watch and learn." ,
              "Allow me to reprimand, for your atrocious command.",
              "I'm sure you'll get quite a scare out of this!" ,
              "Are you sure of the truth in what you see?"};
       
        String[] Victoryx =
        {"... I must return to my work now.",
              "See, this is reality. You never stood a chance.",
              "You are intellectually inferior to me. Simple as that."};
       
        String[] Swapx =
        {"Move over, I'll give it a shot.",
              "Couldn't handle them, eh?"
        };
       
        setSwap(Swapx);
        setCOPower(COPowerx);
        Victory = Victoryx;
       
        COPName = "Phasing Charge";
        SCOPName = "Reality Minus";
        COPStars = 3.0;
        maxStars = 7.0;
        this.army = army;
        style = PARALLEL_GALAXY;
       
        setCleanStore(false);
    }
   
    //used to get the attack bonus for damage calculation
    public int getAtk(Unit attacker, Unit defender)
    {
       int atk = 100;
       
       //After-image detection
       if(attacker.getCOstore()[0] == -20 || attacker.getAltCOstore()[0] == -20)
       {
          atk -= 20;
       }
       
        if(COP || SCOP)
        {
            atk += 10;
        }
        if(attacker.getDisplayHP() < 5)
            setMinPosLuck(10);
        else
            setMinPosLuck(-1);
        return atk;
    }
   
    //used to get the defense bonus for damage calculation
    public int getDef(Unit attacker, Unit defender)
    {
       int def = 100;
       
       //After-image detection
       if(defender.getCOstore()[0] == -20 || defender.getAltCOstore()[0] == -20)
       {
          def -= 30;
       }
       
        if(COP || SCOP)
            def += 10;
       
        return def;
    }
   
    //carries out Xavier's CO Power, called by CO.activateCOP()
    public void COPower()
    {
        COP = true;
    }
   
    //carries out Xavier's Super CO Power, called by CO.activateSCOP()
    public void superCOPower()
    {
        SCOP = true;
       
        Unit[] units = army.getUnits();
       
        if(units != null)
        {
           for(int i = 0; i < units.length; i++)
           {
              //store the current row and column values of the unit in its COstore[0] array
              //  tempCol = unit.col * modSpace
              //  tempRow = unit.row
              //  unit.COstore[0] = tempCol + tempRow
              int tempCol = units[i].getLocation().getCol() * modSpace;
              int tempRow = units[i].getLocation().getRow();
              units[i].getCOstore()[0] = tempRow + tempCol;
           }
        }
    }   
   
    //used to deactivate Xavier's CO Power the next day
    public void deactivateCOP()
    {
        COP = false;
    }
   
    public void setChange(Unit u){};
   
    public void unChange(Unit u){};
   
    //used to deactivate Xavier's Super CO Power the next day
    public void deactivateSCOP()
    {
        SCOP = false;
    }
   
    //Yeah I'm too lazy to go calculate it in atk so I'm just going to do it here >_>
    //
    //I really do hope it's accurate <_<
    //
    //The damage was already calculated by this point (my observation being based on the
    //damage parameter being passed in), so I doubt inflicting damage at this point will
    //mess up the real amount of damage that was supposed to be dealt (since prematurely
    //inflicting damage could mess up terrain defense).
    //
    //The annoying thing is that the player won't see the luck damage in the damage
    //display.
    //
    //Holy heck I can't believe I missed the D2D >_>
    //
    //Ok I changed a lot of stuff around I hope everything is working fine.
    //oh boy oh boy =[
    //
    public void beforeAttack(Unit owned, Unit enemy, int damage, boolean attack)
    {
       
       //Guaranteed 20 luck damage for non-Infantry units
       if(COP && attack && owned.getUnitType() != 0 && owned.getUnitType() != 1)
       {
          baseMinPosLuck_self = getMinPosLuck();
          baseMaxPosLuck_self = getMaxPosLuck();
          setMinPosLuck(20);
          setMaxPosLuck(20);
          selfAdjusted = true;
       }
    }

    public void beforeCounter(Unit owned, Unit enemy, int damage, boolean attack)
    {
    }
   
    //Xavier's units produce after-images of themselves after moving off of their original positions
    public void afterAction(Unit u, int index, Unit repaired, boolean main)
    {
       if(main && SCOP && u.getCOstore()[0] != -20 && index != 17 && index != 15)
       {
          //  tempCol = unit.COstore[0] / modSpace
          //  tempRow = unit.COstore[0] % modSpace
          int tempCol = u.getCOstore()[0] / modSpace;
          int tempRow = u.getCOstore()[0] % modSpace;
          
          // No after-images if the unit is still in the same place
          if((u.getLocation().getCol() != tempCol) || (u.getLocation().getRow() != tempRow))
          {
             //Crap...
             //Wellllllll for now I'm making the placeUnit method in Battle be public, because
             //without it I'll uh basically have to copy and paste that thing over lol X3
             Map targMap = army.getBattle().getMap();
             Location targLoc = new Location(tempCol, tempRow);
             Tile targTile = army.getBattle().getMap().find(targLoc);
             int targType = u.getUnitType();
             
             //And here it is lol!
             army.getBattle().placeUnit(targMap, targTile, targType, army);
             
             //Setup the after-image stats and stuff
             Unit targUnit = army.getBattle().getMap().find(targLoc).getUnit();
             targUnit.setActive(true);
             targUnit.getCOstore()[0] = -20;          //used to indicate an after-image unit
             targUnit.setNoJoin(true);
                        //Also disabled
                        targUnit.setNoLoad(true);
             if(targUnit.getAmmo() != -1)     //after-image units only have 1 ammo
             {
                targUnit.setAmmo(1);
             }
          }
       }
    }
   
    //After an attack is complete, all affected parties have their luck
    //minimum and maximum values reset to normal.
    public void afterAttackAction(Unit owned, Unit enemy, boolean attack)
    {
       if(selfAdjusted)
       {
          setMaxPosLuck(baseMaxPosLuck_self);
          setMinPosLuck(baseMinPosLuck_self);
          baseMinPosLuck_self = 0;
          baseMaxPosLuck_self = 0;
          selfAdjusted = false;
       }
       
       if(enemyAdjusted)
       {
          enemy.getArmy().getCO().setMinNegLuck(baseMinNegLuck_enemy);
          baseMinNegLuck_enemy = 0;
          enemyAdjusted = false;
       }
    }
   
    public void dayStart(boolean main)
    {       
       //Uh apparently it doesn't behave well if I put this while loop in Xavier's constructor?
       //Doesn't really matter I think, after the first day modSpace will be set
       //correctly and won't run for more than one line after (unless the map's size is
       //constantly changing, in which case the map is really screwed up).
        while((modSpace <= army.getBattle().getMap().getMaxCol()) || (modSpace <= army.getBattle().getMap().getMaxRow()))
        {
           modSpace *= 10;
        }
       
       //Destroy all after-images and reset coordinate values
       //Wait... if Xavier only has after-images left, I guess he loses if they all die?
       Unit[] units = army.getUnits();
       Unit tempUnit = null;
       boolean destroyed = false;
       int lastUnitCol = 0;
       int lastUnitRow = 0;
       
       if(units != null)
       {
          for(int i = 0; i < units.length; i++)
          {
             tempUnit = units[i];
             
             if(tempUnit != null)
             {
                if(main)
                {
                   //Destroy after-images
                   if(tempUnit.getCOstore()[0] == -20)
                   {
                      tempUnit.getCOstore()[0] = 0;
                      lastUnitCol = tempUnit.getLocation().getCol();
                      lastUnitRow = tempUnit.getLocation().getRow();
                      destroyed = tempUnit.damage(150, true);
                   }
                   //Reset coordinates
                   else
                   {
                      tempUnit.getCOstore()[0] = 0;
                   }
                }
                else
                {
                   //Destroy after-images
                   if(tempUnit.getAltCOstore()[0] == -20)
                   {
                      tempUnit.getAltCOstore()[0] = 0;
                      lastUnitCol = tempUnit.getLocation().getCol();
                      lastUnitRow = tempUnit.getLocation().getRow();
                      destroyed = tempUnit.damage(150, true);
                   }
                   //Reset coordinates
                   else
                   {
                      tempUnit.getAltCOstore()[0] = 0;
                   }
                }
             }
          }
       }
       
       if(tempUnit != null)
       {       
           if(destroyed && tempUnit.isRout())
           {
        	   Mission.getBattleScreen().endBattle();
        	   //boolean gameEnd = tempUnit.getArmy().getBattle().removeArmy(tempUnit.getArmy(),null,false);
              
              //Err can't call endBattle() through CO xD
              //So like Xavier will end up being removed but I can't stop the battle, even if
              //his player is the last :(
              
              //I have a bunch of other things down here but ignore then for now plz
              
              /*
              army.addUnit(tempUnit);
              army.getBattle().getMap().addUnit(lastUnitCol, lastUnitRow, tempUnit);
              tempUnit.damage(200, false);
              tempUnit.ammo = 0;
              tempUnit.gas = 1;
              tempUnit.move = 0;
              tempUnit.vision = 0;
              
              defeated = true;
              army.getBattle().endTurn();
              */
           }
       }
    }
} 