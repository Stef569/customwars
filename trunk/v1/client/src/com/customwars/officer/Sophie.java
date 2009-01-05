package com.customwars.officer;

import com.customwars.unit.Unit;

public class Sophie extends CO{
    
    private boolean attacking = false; //Is Sophie attacking?
    private boolean turn = false; //Is it Sophie's turn - used for Total War?
    private boolean destroyedByCounter = false; //Has either Sophie's unit or the defending unit been destroyed?
    //constructor
    public Sophie() {
        name = "Sophie";
        setId(51);
        
        String CObiox = 
                "A skilled veteran that demands          " +
                "perfection from her soldiers. She is    " +
                "small but feisty. ";
        //This is seperated into blocks 40 characters long!
        //Use this as a guide for a better look proper word-wrapping.
        String titlex = "Perfection Personified";
        String hitx = "Mail Call"; //Holds the hit
        String missx = "Mess Hall"; //Holds the miss
        String skillStringx = 
                "Enemy counterattacks deal less damage  " +
                "to Sophie’s units. ";
        String powerStringx = 
                "Enemy counterattacks deal no damage to " +
                "Sophie’s units. "; //Holds the Power description
        String superPowerStringx = 
                "Sophie's units strike twice when ordered" +
                "to attack. However, firepower is reduced"; //Holds the Super description
                      //"                                    " sizing markers
        String intelx = "Sophie's units take less damage from" +
                        "counterattacks. During her COP she  " +
                        "ignores counterattack damage, while " +
                        "her SCOP allows her units to attack " +
                        "twice in a row, although with less  " +
                        "firepower.";//Holds CO intel on CO select menu, 6 lines max

        CObio = CObiox;
        title = titlex;
        hit = hitx;
        miss = missx;
        skillString = skillStringx;
        powerString = powerStringx;
        superPowerString = superPowerStringx;
        intel = intelx;
        String[] COPowerx =
        {"Get your butts in gear troops! The bad guys are not going to wait all day for us!",
"Give’em lead men!",
"It’s time to prove that we are made of the right stuff!",
"War ain't no picnic soldier! You have to put in 110% if you want to survive!",
"All right, move out and try not to get yourselves killed!",
"I expect nothing but the best!" };
        
        String[] Victoryx =
        {"Hoorah! The day is done and the battle is won!",
"That showed them!",
"We are the finest fighting force in the world!"};
        
        String[] Swapx =
        {"I won't let you down!",
         "I am assuming command"};
        
        setCOPower(COPowerx);
        Victory = Victoryx;
        setSwap(Swapx);
        
        //No special tags
        String[] TagCOsx = {"Max", "Kanbei", "Sami", "Artemis", "Colin", "Adder"}; //Names of COs with special tags
        String[] TagNamesx = {"Show of Strength", "Call of Duty", "Dual Strike", "Dual Strike", "Dual Strike", "Dual Strike"}; //Names of the corresponding Tags
        int[] TagStarsx = {1,1,0,0,0,0}; //Number of stars for each special tag.
        int[] TagPercentx = {110,110,105,105,90,80}; //Percent for each special tag.
        
        setTagCOs(TagCOsx);
        setTagNames(TagNamesx);
        setTagStars(TagStarsx);
        setTagPercent(TagPercentx);
        
        COPName = "Sudden Strike";
        SCOPName = "Total War";
        COPStars = 2.0;
        maxStars = 6.0;
        this.army = army;
        style = ORANGE_STAR;
    }
    
//used to get the attack bonus for damage calculation
    public int getAtk(Unit attacker, Unit defender){
        if(SCOP&&attacker.getMaxRange()>1 && turn)return 85;
        if(SCOP&&attacker.getMaxRange()==1 && turn)return 95;
        if(COP||SCOP)return 110;
        return 100;
        
    }
    public void dayStart(boolean main ) {
        turn = true;
    }
    
    public void dayEnd(boolean main ) {
        turn = false;}
    
    public void setChange(Unit u){
        
    }
    
    public void unChange(Unit u){
        
    }
    
    
//used to get the defense bonus for damage calculation
    public int getDef(Unit attacker, Unit defender){
        if(attacking) {
            if(COP)
                return 200;
            if(SCOP)
                return 130;
            return 120;
        } else if(SCOP || COP)return 110;
        return 100;
    }
    
//carries out Blandie's CO Power, called by CO.activateCOP()
    public void COPower(){
        COP = true;
    }
    
//carries out Blandie's Super CO Power, called by CO.activateSCOP()
    public void superCOPower(){
        SCOP = true;
    }
    
//used to deactivate Blandie's CO Power the next day
    public void deactivateCOP(){
        COP = false;
    }
    
//used to deactivate Blandie's Super CO Power the next day
    public void deactivateSCOP(){
        SCOP = false;
    }
    public void beforeAttack(Unit owned, Unit enemy, int damage, boolean attack) {
        destroyedByCounter = false;
        if(attack)
            attacking = true;
    }
    public void afterAttack(Unit owned, Unit enemy, int damage, boolean destroy, boolean attack) {
        if(attack && destroy)
            destroyedByCounter = true;
    }
    public void afterCounter(Unit owned, Unit enemy, int damage, boolean destroy, boolean attack) {
        if(attack && destroy)
            destroyedByCounter = true;
    }
    public void afterAttackAction(Unit owned, Unit enemy, boolean attack) {
        attacking = false;
        //If the SCOP is active and the unit hasn't fired twice, fire AGAIN WOOP WOOP'
        if(SCOP && owned.getCOstore()[0] != 1 && turn && !destroyedByCounter) {
            owned.getCOstore()[0] = 1;
            owned.fire(enemy);
        }
    }
}
