package cwsource;
/*
 *Blandie.java... wait no I'm not, I'm Adam.java!
 *Author: Vimes
 *Contributors: -
 *Creation: -
 *A not very bland CO, perhaps the furthest thing from a generic commanding officer?
 */

public class Adam extends CO{
    
    //constructor
    public Adam() {
        name = "Adam";
        id = 63;
        
        String CObiox = "A researcher-turned-commander of Jade" +
                "Cosmos, Adam Deckster is a reclusive" +
                "geek who nevertheless is slowly finding" +
                "a place among the other commanders.";
        //This is seperated into blocks 40 characters long!
        //Use this as a guide for a better look proper word-wrapping.
        String titlex = "Pin-point Dexter";
        String hitx = "Vectors"; //Holds the hit
        String missx = "Percent Error"; //Holds the miss
        String skillStringx = "Adam's units finish off units that are within luck range. Whenever he destroys an enemy unit, the attacking unit gains a defense boost. However, Adam's units lose firepower as they lose HP.";
        String powerStringx = "All of Adam's units gains a firepower boost. Whenever Adam destroys a unit, he can choose a unit. That unit gains bonus firepower and bonus move."; //Holds the Power description
        String superPowerStringx = "Adam selects and deals 2 damage to one enemy unit. Each time Adam destroys an enemy unit, the attacking unit loses firepower and can move again, while Adam can select and deal 2 damage to another enemy unit."; //Holds the Super description
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
        {"Get a hobby? This is my hobby!", 
        		"Woo! Watch out for shrapnel!", 
        		"Precision pays off!", 
        		"As my troops approach their limit...you become insignificant!", 
        		"Prepare to have your strategy debunked!", 
        		"I'll blind you with science!"};
        
        String[] Victoryx =
        {"Call it bad luck, if you must.", 
        		"Who said math had no applications in real life?", 
        		"Ha! The revenge of the nerds!"};
        
        String[] Swapx =
        {"Vector 32 degrees into the wind, velocity at...", 
        		"Clean up your shirt! Brush your hair! Let me take care of things!"};
        
        COPower = COPowerx;
        Victory = Victoryx;
        Swap = Swapx;
        
        //No special tags
        String[] TagCOsx = {"Koshi"}; //Names of COs with special tags
        String[] TagNamesx = {"Geek Out"}; //Names of the corresponding Tags
        int[] TagStarsx = {2}; //Number of stars for each special tag.
        int[] TagPercentx = {110}; //Percent for each special tag.
        
        TagCOs = TagCOsx;
        TagNames = TagNamesx;
        TagStars = TagStarsx;
        TagPercent = TagPercentx;
        
        COPName = "Decimation";
        SCOPName = "Cartesian Cleanup";
        COPStars = 3.0;
        maxStars = 8.0;
        this.army = army;
        style = JADE_COSMOS;
        cleanStore = false;
    }
    
//used to get the attack bonus for damage calculation
    public int getAtk(Unit attacker, Unit defender){
        int bonus = 0;
        int atk = 100;
        if(COP) {
            //This gets the amount of times the unit has been selected under the COP
            for(int i = 0; i<attacker.COstore[0]; ++i) {
                bonus+= 10;
            }
        }
        if(SCOP) {
            //This gets the amount of times the unit has been selected under the COP
            for(int i = 0; i<attacker.COstore[0]/10; ++i) {
                bonus-=30 + i*10;
            }
        }
        //Adam's weakness
        bonus -= (10-attacker.getDisplayHP())*3;
        if(SCOP)atk+= 40 + bonus;
        else if(COP)atk += 30 + bonus;
        else atk+= bonus;
        
        if(defender != null)
        {
            double store; //If store isn't double, bad things happen. Expect a +/- 1% problem here or there.

            int baseDamage = BaseDMG.find(attacker.getAmmo(), attacker.getUType(), defender.getUType(),army.getBattle().getBattleOptions().isBalance());
                if(defender instanceof Submarine && defender.dived)baseDamage = ((Submarine)defender).getDivedDamage(attacker.getUType());
                else
                if(defender instanceof Stealth && defender.dived)baseDamage = ((Stealth)defender).getHiddenDamage(attacker.getUType());
            
            int tdef = 0;
            if(defender.getMType() != defender.MOVE_AIR)
                tdef = defender.getMap().find(defender).getTerrain().getDef();
            if(tdef < 0)tdef = 0;
            tdef = (int)(defender.getArmy().getCO().getTerrainDefenseMultiplier() * tdef);
            
            store = (int)Math.floor((attacker.getDisplayHP()/10.0 * ((baseDamage*(atk/100.0) + (attacker.getArmy().getComTowers()*10) + attacker.getArmy().getAtkPercent())) * ((200.0-(defender.getArmy().getCO().getDef(attacker,defender) + defender.getDisplayHP()*tdef))/100.0)));
            //Store is calculated after terrain effects and before luck
            if(store>0 && defender.getHP()-store <= ((positiveLuck*attacker.getDisplayHP())/10) && defender.getHP()-store >= 0) {
                minPosLuck = (positiveLuck*attacker.getDisplayHP())/10;
            } else {
                minPosLuck = -1;
            }
            
        }
        return atk;
    }
    
    public void setChange(Unit u){
        
    }
    
    public void unChange(Unit u){
        
    }
    
    
//used to get the defense bonus for damage calculation
    public int getDef(Unit attacker, Unit defender){
        int bonus = 0;
        //This gets the amount of times an enemy unit has been destroyed
        for(int i = 0; i<defender.COstore[0]%10; ++i) {
            if(i == 1)
                bonus += 15;
            else
                bonus += 10;
        }
        
        if(SCOP || COP)return 110 + bonus;
        return 100 + bonus;
    }
    
//carries out Blandie's CO Power, called by CO.activateCOP()
    public void COPower(){
        Unit[] u = army.getUnits();
        for(int i = 0 ; i<u.length; i++)
        {
            u[i].COstore[0] = 0;
        }
        COP = true;
        selecting = true;
    }
    
//carries out Blandie's Super CO Power, called by CO.activateSCOP()
    public void superCOPower(){
        SCOP = true;
        selecting = true;
    }
    
//used to deactivate Blandie's CO Power the next day
    public void deactivateCOP(){
        COP = false;
        Unit[] u = army.getUnits();
        for(int i = 0; i< u.length; i++) {
            if(u[i].COstore[0] > 0) {
                for(int s = 0; s<u[i].COstore[0]; s++) {
                    u[i].move--;
                }
            }
        }
    }
    
//used to deactivate Blandie's Super CO Power the next day
    public void deactivateSCOP(){
        SCOP = false;
    }
    public void afterAttack(Unit owned, Unit enemy, int damage, boolean destroy, boolean attack) {
        if(attack && destroy) {
            owned.COstore[0] += 1;
            if(COP || SCOP)
                selecting = true;
        }
    }
    public void afterAction(Unit u, int index, Unit repaired, boolean main) {
        if(index == 1 && SCOP && selecting) {
            u.setActive(true);
            u.COstore[0] += 10;
            System.out.println(u.COstore[0]);
        }
    }
    
    //Handles the selection thing for the powers
    public void selectAction(Tile t) {
        if(COP && t.hasUnit() && t.getUnit().getArmy().getSide() == army.getSide()) {
            t.getUnit().COstore[0] +=1;
            t.getUnit().move++;
        }
        if(SCOP && t.hasUnit() && t.getUnit().getArmy().getSide() != army.getSide())
            t.getUnit().damage(20,false);
        selecting = false;
    }
    public boolean validSelection(Tile t) {
        if(COP && t.hasUnit() && t.getUnit().getArmy().getSide() == army.getSide()) {
            return true;
        }
        if(SCOP && t.hasUnit() && t.getUnit().getArmy().getSide() != army.getSide())
            return true;
        return false;
    }
    public void invalidSelection() //IF they hit the wrong button
    {
        //Try again. Nothing happens.
    }
    public void cancelSelection() //If they press B
    {
        selecting = false;
    }
}
