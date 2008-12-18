package com.customwars.officer;

import com.customwars.ai.BaseDMG;
import com.customwars.unit.Army;
import com.customwars.unit.Unit;

public class Koshi extends CO
{
   private boolean chickenKick;
   private boolean didNegateLuck;
   private int posLuck;
   private int negLuck;

    //constructor
    public Koshi()
    {
        name = "Koshi";
        setId(56);

        String CObiox = "Jade Cosmos' lead intel-anaylst. Has a long history with strategy games, which his peers believe is what helped him to achieve his position. Specializes in communications sabotage.";             //Holds the condensed CO bio'
        String titlex = "Blinding Tactics";
        String hitx = "Computer Games"; //Holds the hit
        String missx = "Board Games"; //Holds the miss
        String skillStringx = "Koshi's experience with intel analysis and computer networking allows him to hide his status from his opponents.";
        String powerStringx = "Scrambles enemy readouts. Opponents cannot view damage estimations and misreads Koshi's unit types. Damage fluctuates and enemies cannot activate powers."; //Holds the Power description
        String superPowerStringx = "Enemies are faced with Fog of War and their vision is reduced to one. In addition, Koshi's units can ambush enemies."; //Holds the Super description

        CObio = CObiox;
        title = titlex;
        hit = hitx;
        miss = missx;
        skillString = skillStringx;
        powerString = powerStringx;
        superPowerString = superPowerStringx;
                      //"                                    " sizing markers
        String intelx = "Koshi specializes in flummoxing his " +
                        "opponents. Able to hide his star    " +
                        "charge, his power hides the nature  " +
                        "of his units, while his super lets  " +
                        "Koshi attack units that are ambushed" +
                        "";//Holds CO intel on CO select menu, 6 lines max

        intel = intelx;
        String[] TagCOsx = {"Carrie","Eniac","Ozzy"}; //Names of COs with special tags
        String[] TagNamesx = {"Otaku Attack","Hard Drive","Ramble On"}; //Names of the corresponding Tags
        int[] TagStarsx = {2,1,1}; //Number of stars for each special tag.
        int[] TagPercentx = {110,110,110}; //Percent for each special tag.

        setTagCOs(TagCOsx);
        setTagNames(TagNamesx);
        setTagStars(TagStarsx);
        setTagPercent(TagPercentx);

        String[] COPowerx =
        {"Anonymous does not forgive, and neither will I!",
              "I just downloaded this strategy from the Internet!",
              "Believe it! Check out my troops' ninja skills! Believe it!",
              "Super fighting power, initiate!",
              "Now you see me...now you don't!",
              "lol" };

        String[] Victoryx =
        {"Might wanna go back to the noob server...",
              "War's a game. Gaming's my life. It's what I do.",
              "I win. You lose. Where's my Internet?"};

        String[] Swapx =
        {"My turn already?! But I haven't --",
              "And we're off! Squadellah!"};

        setCOPower(COPowerx);
        Victory = Victoryx;
        setSwap(Swapx);
        //Used to be quirky beserky / chicken kick
        COPName = "Memory Lapse";
        SCOPName = "Blinding Tactics";
        COPStars = 4.0;
        maxStars = 8.0;

        this.army = army;
        style = JADE_COSMOS;

        setHiddenIntel(true);
        setHiddenPower(true);
        didNegateLuck = false;

        posLuck = 0;
        negLuck = 0;

        chickenKick = false;
    }

    //used to get the attack bonus for damage calculation
    public int getAtk(Unit friend, Unit enemy)
    {
       int atk = 100;

        if(SCOP || COP)
        {
            atk += 10;
        }

        return atk;
    }


    //used to get the defense bonus for damage calculation
    public int getDef(Unit enemy, Unit friend)
    {
       int def = 100;

       if(COP || SCOP)
        {
            def += 10;
        }

        return def;
    }

    //carries out Koshi's CO Power, called by CO.activateCOP()
    public void COPower()
    {
        COP = true;
        setHiddenUnitInfo(true);
        setDisruptFireDisplay(true);
        setHiddenUnitType(true);
        
        setPositiveLuck(getPositiveLuck() + 30);

        //for each enemy CO, mess their luck up!

        Army[] allArmies = army.getBattle().getArmies();

        if(allArmies != null)
        {
           for(int i = 0; i < allArmies.length; i++)
           {
              Army testArmy = allArmies[i];

              if(testArmy.getSide() != army.getSide())
              {
                 CO currCO = testArmy.getCO();
                 CO altCO = testArmy.getAltCO();

                 if(currCO != null)
                 {
                    currCO.setPositiveLuck(currCO.getPositiveLuck() + 15);
                    currCO.setNegativeLuck(currCO.getNegativeLuck() + 15);
                 }

                 if(altCO != null)
                 {
                    altCO.setPositiveLuck(altCO.getPositiveLuck() + 15);
                    altCO.setNegativeLuck(altCO.getNegativeLuck() + 15);
                 }
              }
           }
        }
    }

    //carries out Koshi's Super CO Power, called by CO.activateSCOP()
    public void superCOPower()
    {
        SCOP = true;
        chickenKick = true;

        //Blind the enemy!
        army.getBattle().setFog(true);

        Army[] allArmies = army.getBattle().getArmies();

        if(allArmies != null)
        {
           for(int i = 0; i < allArmies.length; i++)
           {
              Army testArmy = allArmies[i];

              if(testArmy.getSide() != army.getSide())
              {
                 Unit[] allUnits = testArmy.getUnits();

                 if(allUnits != null)
                 {
                    for(int j = 0; j < allUnits.length; j++)
                    {
                       Unit testUnit = allUnits[j];

                       testUnit.getEnemyCOstore()[getStatIndex()][0] = testUnit.getVision() - 1;
                       testUnit.setVision(1);
                       testUnit.setChanged(true);
                    }
                 }
              }
           }
        }

        Unit[] u = army.getUnits();

        for(int i = 0; i < u.length; i++)
        {
            if(u[i].getClass() != null)
            {
                u[i].setVision(u[i].getVision() + 2);
                u[i].setChanged(true);
            }
        }

        army.getBattle().calculateFoW();
    }

    //used to deactivate Koshi's CO Power the next day
    public void deactivateCOP()
    {
        COP = false;
        setHiddenUnitInfo(false);
        setDisruptFireDisplay(false);
        setHiddenUnitType(false);
        
        setPositiveLuck(getPositiveLuck() - 30);

        //for each enemy CO, give them back their luck!

        Army[] allArmies = army.getBattle().getArmies();

        if(allArmies != null)
        {
           for(int i = 0; i < allArmies.length; i++)
           {
              Army testArmy = allArmies[i];

              if(testArmy.getSide() != army.getSide())
              {
                 CO currCO = testArmy.getCO();
                 CO altCO = testArmy.getAltCO();

                 if(currCO != null)
                 {
                    currCO.setPositiveLuck(currCO.getPositiveLuck() - 15);
                    currCO.setNegativeLuck(currCO.getNegativeLuck() - 15);
                 }

                 if(altCO != null)
                 {
                    altCO.setPositiveLuck(altCO.getPositiveLuck() - 15);
                    altCO.setNegativeLuck(altCO.getNegativeLuck() - 15);
                 }
              }
           }
        }

        BaseDMG.restoreDamageTables();
        BaseDMG.restoreBalanceDamageTables();
    }

    //used to deactivate Koshi's Super CO Power the next day
    public void deactivateSCOP()
    {
        SCOP = false;

        Army[] allArmies = army.getBattle().getArmies();

        if(allArmies != null)
        {
           for(int i = 0; i < allArmies.length; i++)
           {
              Army testArmy = allArmies[i];

              if(testArmy.getSide() != army.getSide())
              {
                 Unit[] allUnits = testArmy.getUnits();

                 if(allUnits != null)
                 {
                    for(int j = 0; j < allUnits.length; j++)
                    {
                       Unit testUnit = allUnits[j];

                       //If Koshi is still the CO in front
                       if(army.getCO() == this)
                       {
                          testUnit.setVision(1 + testUnit.getEnemyCOstore()[getStatIndex()][0]);
                          testUnit.getEnemyCOstore()[getStatIndex()][0] = 0;
                          testUnit.setChanged(false);
                       }
                       else
                       {
                          testUnit.setVision(1 + testUnit.getAltEnemyCOstore()[getStatIndex()][0]);
                          testUnit.getAltEnemyCOstore()[getStatIndex()][0] = 0;
                          testUnit.setChanged(false);
                       }
                    }
                 }
              }
           }
        }

        Unit[] u = army.getUnits();

        for(int i = 0; i < u.length; i++)
        {
            if(u[i].getClass() != null)
            {
                if(u[i].isChanged())
                {
                    u[i]
							.setVision(u[i].getVision() - 2);
                    u[i].setChanged(false);
                }
            }
        }

        army.getBattle().calculateFoW();
    }

    public void dayStart(boolean main)
    {
       if(chickenKick)
       {
          army.getBattle().resetVisibility();

          chickenKick = false;
       }
    }

    public void enemyDayStart(boolean main)
    {
       /*if(main && COP)
       {
          //Causes the enemy's damage tables to go kablooey and not work right
          BaseDMG.loadAlternateTables("QB_BaseDMG.txt", "QB_AltDMG.txt", "QB_BaseDMGb.txt", "QB_AltDMGb.txt");

          //Prevents the enemy from using CO Powers
          int turn = army.getBattle().getTurn();
          Army targArmy = army.getBattle().getArmy(turn);
          disableCOPowers(targArmy);
       }*/
        if(chickenKick)
        {
            army.getBattle().setFog(true);
            army.getBattle().calculateFoW();
        }
    }

    public void beforeAttack(Unit owned, Unit enemy, int damage, boolean attack)
    {
       if(!attack && COP)
       {
          //Do a damage check to see if the enemy unit could attack the friendly unit to begin with
        BaseDMG.restoreDamageTables();
        BaseDMG.restoreBalanceDamageTables();

          if(enemy.displayDamageCalc(owned) > -1)
          {
             //Everything is fine!
          }
          else
          {
             //The enemy unit is normally not supposed to damage the friendly unit
             //So enemy luck must be negated for this battle!
             //Keep track of current values to restore them later
              posLuck = enemy.getArmy().getCO().getPositiveLuck();
              negLuck = enemy.getArmy().getCO().getNegativeLuck();
             enemy.getArmy().getCO().setPositiveLuck(0);
             enemy.getArmy().getCO().setNegativeLuck(0);
             didNegateLuck = true;
          }

          //Restore the kablooeyness
          BaseDMG.loadAlternateTables("QB_BaseDMG.txt", "QB_AltDMG.txt", "QB_BaseDMGb.txt", "QB_AltDMGb.txt");
       }
    }

    //Damage tables are reset after the enemy attacks
    //This is so that Koshi's own units don't do anything funny and counter-attack when they shouldn't
    //Like a Tank counter-attacking a Fighter >_> (which is still possible if a Tank deals 0% damage to a Fighter!)
    public void afterAttack(Unit owned, Unit enemy, int damage, boolean destroy, boolean attack)
    {
       if(!attack && COP)
       {
        BaseDMG.restoreDamageTables();
        BaseDMG.restoreBalanceDamageTables();
       }
    }

    public void afterAttackAction(Unit owned, Unit enemy, boolean attack)
    {
       if(!attack && COP)
       {
          //Restores the kablooeyness (after undoing kablooeyness in afterAttack)
          BaseDMG.loadAlternateTables("QB_BaseDMG.txt", "QB_AltDMG.txt", "QB_BaseDMGb.txt", "QB_AltDMGb.txt");

          //If luck negation occured during this battle, the enemy's luck values must be restored
          if(didNegateLuck)
          {
             enemy.getArmy().getCO().setPositiveLuck(posLuck);
             enemy.getArmy().getCO().setNegativeLuck(negLuck);
             didNegateLuck = false;
          }
       }
    }

    public void enemyDayEnd(boolean main)
    {
      int turn = army.getBattle().getTurn();

      Army currArmy = army.getBattle().getArmy(turn);

      CO currCO = currArmy.getCO();
      CO altCO = currArmy.getAltCO();

      if(currCO != null)
      {
         currCO.setCOPoff(false);
         currCO.setSCOPoff(false);
      }

      if(altCO != null)
      {
         altCO.setCOPoff(false);
         altCO.setSCOPoff(false);
      }
    }

    public void afterEnemyAction(Unit u, int index, Unit repaired, boolean main)
    {
       if(main && COP)
       {
          Army targArmy = u.getArmy();
          //disableCOPowers(targArmy);
       }
       //indirect ambush!!!
       //Turned off for now.
       /*else if(main && SCOP && (index == 0))// && ((index == 0) || (index == 3) || (index == 3)))
       {
          int lowestRange = 10000;
          Unit validUnit = null;

          Unit[] myUnits = army.getUnits();

          if(myUnits != null)
          {
             for(int i = 0; i < myUnits.length; i++)
             {
                //  Determine if valid!
                //  Requirements:
                //
                //    (a) Tested unit needs to be indirect
                //    (b) Enemy needs to be within range of tested unit
                //    (c) Tested unit needs to be able to fire on the enemy unit
                //
                //  Use the unit that is "closest" when more than one valid unit found.
                //

                Unit testUnit = myUnits[i];

                if(testUnit.maxRange >= 2)
                {
                   int colOffset = Math.abs(u.getLocation().getCol() - testUnit.getLocation().getCol());
                   int rowOffset = Math.abs(u.getLocation().getRow() - testUnit.getLocation().getRow());

                   int distance = colOffset + rowOffset;

                   if((testUnit.minRange <= distance) &&
                      (testUnit.maxRange >= distance))
                   {
                      if(testUnit.displayDamageCalc(u) > -1)
                      {
                         if(distance < lowestRange)
                         {
                            lowestRange = distance;
                            validUnit = testUnit;
                         }
                      }
                   }
                }
             }
          }

          if(validUnit != null)
          {
            boolean destroyed = validUnit.fire(u);
          }
       }
       //TRAP! ambush!!!
       else*/ if(main && SCOP && (index == 18))
       {
          //Unit u = trapped

          Unit trapper = u.getTrapper();

          //First make sure the trapper is yours!
          if(trapper.getArmy().getID() == army.getID())
          {
             //Then make sure the trapping unit can fire on the trapped unit!
            if(trapper.displayDamageCalc(u) > -1)
            {
               //Then make sure that the trapping unit is direct!
               if(trapper.getMaxRange() < 2)
               {
                  boolean destroyed = trapper.fire(u);
               }
            }
          }
       }
    }

   //Prevents the target army from being able to use CO Powers
   public void disableCOPowers(Army currArmy)
   {
      CO currCO = currArmy.getCO();
      CO altCO = currArmy.getAltCO();

      if(currCO != null)
      {
         currCO.setCOPoff(true);
         currCO.setSCOPoff(true);
      }

      if(altCO != null)
      {
         altCO.setCOPoff(true);
         altCO.setSCOPoff(true);
      }
   }

   @Override
   public void setChange(Unit u)
   {
      // TODO Auto-generated method stub

   }

   @Override
   public void unChange(Unit u)
   {
      // TODO Auto-generated method stub
   }
}