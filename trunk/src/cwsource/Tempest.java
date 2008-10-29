package cwsource;
/*
 *Tempest.java
 *Author: Fugue
 *Contributors: Kosheh, Urusan
 *Creation: 6/19/07
 *The Tempest class is used to create an instance of the Blue Moon CO Tempest (created by ThrawnFett).
 */

public class Tempest extends CO{
   //constructor
   public Tempest() {
      name = "Tempest";
      id = 58;

      String CObiox = "An old friend of Olaf's who rejoined the military in their time of need. A survivalist and has mastered fighting in even the most extreme weather conditions.";             //Holds the condensed CO bio'
      String titlex = "Fearsome Forecast";
      String hitx = "Clouds"; //Holds the hit
      String missx = "Sunshine"; //Holds the miss
      String skillStringx = "Trained to fight in extreme weather, Tempest's troops are immune to weather effects. ";
      String powerStringx = "Causes a sandstorm for two days. Indirect range is increased by one. "; //Holds the Power description
      String superPowerStringx = "Causes a snow to fall for two days. Enemy movement on rough terrain is reduced. All units replenish their fuel. Defense boosts apply. "; //Holds the Super description
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


      String[] COPowerx =
      {"Forecast? Today: Death. Tomorrow's not looking good either.",
            "Even nature is against you!" ,
            "This squall will stop you in your tracks!",
            "There's no warning for this severe weather." ,
            "We're gonna blow you away!" ,
            "You should have taken shelter.",};

      String[] Victoryx =
      {"The wind seperates the wheat from the chaff.",
            "Nature just helped to precipitate our victory.",
      "I'll bet you didn't predict this!"};

      String[] Swapx =
      {"Time to press forward.",
      "I think I see a storm brewing."};

      COPower = COPowerx;
      Victory = Victoryx;
      Swap = Swapx;

      String[] TagCOsx = {"Olaf","Drake","Lash","Von Bolt"}; //Names of COs with special tags
      String[] TagNamesx = {"Snowball Effect","Hailstorm","Dual Strike","Dual Strike"}; //Names of the corresponding Tags
      int[] TagStarsx = {1,0,0,0}; //Number of stars for each special tag.
      int[] TagPercentx = {110,105,80,90}; //Percent for each special tag.

      TagCOs = TagCOsx;
      TagNames = TagNamesx;
      TagStars = TagStarsx;
      TagPercent = TagPercentx;

      COPName = "Harmattan";
      SCOPName = "Katabatic Storm";
      COPStars = 3.0;
      maxStars = 7.0;
      this.army = army;
      style = BLUE_MOON;

      snowImmunity = true;
      rainImmunity = true;
      sandImmunity = true;
      cleanEnemyStoreBegin = false;
      cleanEnemyStoreEnd = false;
   }

   //used to get the attack bonus for damage calculation
   public int getAtk(Unit attacker, Unit defender){
      if(COP || SCOP) return 120;
      return 100;
   }

   public void setChange(Unit u){}

   public void unChange(Unit u){}


   //   used to get the defense bonus for damage calculation
   public int getDef(Unit attacker, Unit defender){
      if(SCOP) return 140;
      if(COP) return 110;
      return 100;
   }

   public void COPower(){
      COP = true;
      // Sandstorm
      army.getBattle().startWeather(3, 2);

      // +1 Range
      Unit[] u = army.getUnits();
      for(int i = 0; i < u.length; i++){
         if(u[i].getClass() != null){
            if(u[i].getMinRange() > 1){
               u[i].maxRange++;
               u[i].changed = true;
            }
         } else
            return;
      }
   }

   public void superCOPower(){
      SCOP = true;
      // Snow
      army.getBattle().startWeather(2,2);

      // Refuel
      Unit[] u = army.getUnits();
      for(int i = 0; i < u.length; i++){
         if(u[i].getClass() != null) u[i].gas = u[i].maxGas;
         else return;
      }

      // Movement penalty
      Army[] armies = army.getBattle().getArmies();
      Unit[] e;
      for(int i = 0; i < armies.length; i++){
         if(armies[i].getSide() != army.getSide() && armies[i].getUnits() != null){
            e = armies[i].getUnits();
            for(int s = 0; s < e.length; s++){
               if(e[s].getClass() != null){
                  if((e[s].getMType() != e[s].MOVE_AIR) || army.getBattle().getMap().find(e[s].getLocation()).getTerrain().getName() == "Airport"){
                     e[s].enemyCOstore[statIndex] = army.getBattle().getMap().find(e[s].getLocation()).getTerrain().def;
                     e[s].move -= e[s].enemyCOstore[statIndex];
                  }
               }
               else
                  return;
            }
         }
      }
   }

   public void deactivateCOP(){
      COP = false;
      Unit[] u = army.getUnits();
      for(int i = 0; i < u.length; i++){
         if(u[i].getClass() != null){
            if(u[i].changed){
               u[i].maxRange--;
               u[i].changed = false;
            }
         } else
            return;
      }
   }

   public void deactivateSCOP(){
      SCOP = false;
      boolean isFront = true;
      if(getArmy().getCO() != this) isFront = false;

      Army[] armies = army.getBattle().getArmies();
      Unit[] u;
      for(int i = 0; i < armies.length; i++){
         if(armies[i].getSide() != army.getSide() && armies[i].getUnits() != null){
            u = armies[i].getUnits();
            for(int s = 0; s < u.length; s++){
               if(u[s].getClass() != null){
                  if(isFront){
                     u[s].move += u[s].enemyCOstore[statIndex];
                     u[s].enemyCOstore[statIndex] = 0;
                  }
                  else{
                     u[s].move += u[s].altEnemyCOstore[statIndex];
                     u[s].altEnemyCOstore[statIndex] = 0;
                  }
               } else
                  return;
            }
         }
      }
   }
}