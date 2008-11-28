package cwsource;
/*
 *Smitan.java
 *Author: -
 *Contributors: -
 *Creation:
 *The Smitan class is used to create an instance of the Green Earth CO Smitan
 */

public class Smitan extends CO {
    //constructor
    public Smitan() {
        name = "Smitan";
        id = 69;
        
        String CObiox = "A violent old commander of the Green Earth army who hates limitations.";             //Holds the condensed CO bio'
        String titlex = "FIRE! FIRE! FIRE!";
        String hitx = "Dominance, yelling"; //Holds the hit
        String missx = "(Victory by) Surrender "; //Holds the miss
        String skillStringx = "Direct units gain additional firepower against enemy units which may come under fire from indirect units.";
        String powerStringx = "Increases the range of indirect units by one space. Indirect units can move after firing."; //Holds the Power description
        String superPowerStringx = "Increases the range of indirect units by two spaces. Indirect units can fire twice and move after firing."; //Holds the Super description
                      //"                                    " sizing markers
        String intelx = "Direct units gain firepower against " +
                        "units that are within range of      " +
                        "Smitan's indirect units. His powers " +
                        "give indirect units the ability to  " +
                        "fire and move." +
                        "";//Holds CO intel on CO select menu, 6 lines max
        
        intel = intelx;
        CObio = CObiox;
        title = titlex;
        hit = hitx;
        miss = missx;
        skillString = skillStringx;
        powerString = powerStringx;
        superPowerString = superPowerStringx;
        
        String[] TagCOsx = {"Eagle", "Belle", "Grimm", "Flak", "Peter"};              //Names of COs with special tags
        String[] TagNamesx  = {"Overkill", "Beauty and the Beast", "Dual Strike", "Dual Strike", "Dual Strike"};          //Names of the corresponding Tags
        int[] TagStarsx = {2, 1, 0, 0, 0};           //Number of stars for each special tag.
        int[] TagPercentx = {110, 110, 105, 105, 90};       //Percent for each special tag.
        
        TagCOs = TagCOsx;
        TagNames = TagNamesx;
        TagStars = TagStarsx;
        TagPercent = TagPercentx;
        
        String[] COPowerx =
        {"Whites of their eyes? Pahaha!" ,
         "It's over! Get used to it!!" ,
         "Fire, you idiots!!" ,
         "What're you waiting for?! SHOOT them!!" ,
         "'Calm before the storm'? ... Nonsense." ,
         "No more games! All units, full power!!"};
        
        String[] Victoryx =
        {"Wasn't even any dust to settle." ,
         "Grah hah hah. Ooh, that was FUN."};
        
        String[] Swapx =
        {"Get out of the way before you kill yourself, child!",
         "STEP BACK. This will take but a second."};
        
        COPower = COPowerx;
        Victory = Victoryx;
        Swap = Swapx;
        
        COPName = "Scramble Tactics";
        SCOPName = "Flare Drive";
        COPStars = 3.0;
        maxStars = 8.0;
        this.army = army;
        style = GREEN_EARTH;
    }
    
    //used to get the attack bonus for damage calculation
    public int getAtk(Unit attacker, Unit defender) {
        int atk = 100;
        if(COP || SCOP) {
            atk += 10;
        }
        if(defender == null)
        {
            return atk;
        }
        //Direct only firepower bonus
        if(attacker.getMinRange() <= 1) {
            int dCol = defender.getLocation().getCol();
            int dRow = defender.getLocation().getRow();
            
            Unit[] myUnits = army.getUnits();
            
            for(int i = 0; i < myUnits.length; i++) {
                Unit pickedUnit = myUnits[i];
                
                //PickedUnit cannot be null.
                //PickedUnit also needs to be able to fire
                //on the defending unit.
                if(pickedUnit != null && BaseDMG.find(pickedUnit, defender, army.getBattle().getBattleOptions().isBalance()) > -1) {
                    //Indirects only
                    if(pickedUnit.getMinRange() > 1) {
                        int pCol = pickedUnit.getLocation().getCol();
                        int pRow = pickedUnit.getLocation().getRow();
                        
                        int dist = Math.abs(pCol - dCol) + Math.abs(pRow - dRow);
                        
                        if(dist <= pickedUnit.maxRange && dist >= pickedUnit.minRange) {
                            atk += 10;
                            
                            if(SCOP) {
                                atk += 10;
                            }
                        }
                    }
                }
            }
        }
        
        return atk;
    }
    
    //used to get the defense bonus for damage calculation
    public int getDef(Unit attacker, Unit defender) {
        int def = 100;
        
        if(COP || SCOP) {
            def += 10;
        }
        
        return def;
    }
    
    //changes unit for this CO
    public void setChange(Unit u) {
        
    }
    
    //unchanges unit
    public void unChange(Unit u) {
        
    }
    
    //carries out Smitan's CO Power, called by CO.activateCOP()
    public void COPower() {
        COP = true;
        
        Unit[] u = army.getUnits();
        
        for(int i = 0; i < u.length; i++) {
            if(u[i].getClass() != null) {
                if(u[i].getMinRange() > 1) {
                    u[i].maxRange++;
                    u[i].changed = true;
                    
                    //Give each indirect 1 refresh charge
                    u[i].COstore[0] = 1;
                }
            } else
                return;
        }
    }
    
    //carries out Smitan's Super CO Power, called by CO.activateSCOP()
    public void superCOPower() {
        SCOP = true;
        
        Unit[] u = army.getUnits();
        
        for(int i = 0; i < u.length; i++) {
            if(u[i].getClass() != null) {
                if(u[i].getMinRange() > 1) {
                    u[i].maxRange += 2;
                    u[i].changed = true;
                    
                    //Give each indirect 2 refresh charges
                    u[i].COstore[0] = 2;
                }
            } else
                return;
        }
    }
    
    //used to deactivate Smitan's CO Power the next day
    public void deactivateCOP() {
        COP = false;
        
        Unit[] u = army.getUnits();
        
        for(int i = 0; i < u.length; i++) {
            if(u[i].getClass() != null) {
                if(u[i].changed) {
                    u[i].maxRange--;
                    u[i].changed = false;
                    
                    //Restore the indirect's ability to attack
                    u[i].noFire = false;
                    
                    //Remove refresh counters
                    u[i].COstore[0] = 0;
                }
            } else
                return;
        }
    }
    
    //used to deactivate Smitan's Super CO Power the next day
    public void deactivateSCOP() {
        SCOP = false;
        
        Unit[] u = army.getUnits();
        
        for(int i = 0; i < u.length; i++) {
            if(u[i].getClass() != null) {
                if(u[i].changed) {
                    u[i].maxRange -= 2;
                    u[i].changed = false;
                    
                    //Restore the indirect's ability to attack
                    u[i].noFire = false;
                    
                    //Remove refresh counters
                    u[i].COstore[0] = 0;
                }
            } else
                return;
        }
    }
    
    public void afterAction(Unit u, int index, Unit repaired, boolean main) {
        //Indirect units get refreshed after attacking a target. Each
        //indirect unit has a limited number of charges for determining
        //how many times it is allowed to attack and be refreshed after.
        //
        //After all refresh charges are used, the unit is refreshed once
        //more, but at the cost of losing its attack until the SCOP wears
        //off.
        if(index == 1 && u.COstore[0] > 0 && u.getMinRange() > 1) {
            
            u.COstore[0]--;
            System.out.println("Tested at : " + u.COstore[0]);
            
            if(u.COstore[0] < 1) {
                u.noFire = true;
            }
            
            u.setActive(true);
        }
    }
}
