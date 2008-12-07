package cwsource;
/*
 *Property.java
 *Author: Urusan
 *Contributors: Adam Dziuk
 *Creation: July 12, 2006, 1:12 AM
 *Property is an abstract class for Properties, this extends Terrain
 */

import javax.swing.JOptionPane;

public class Property extends Terrain{
    protected int color = -1;       //the color of the army that owns this property (-1 for neutral)
    protected Army owner;           //the owning army
    protected int income;           //the income recieved from this property
    protected int totalcp;          //total Capture Points (usu. 20)
    protected int cp;               //remaining Capture Points
    protected boolean isCapturable; //is the property capturable? (if not it will have a special action, like silos)
    protected boolean repairLand;   //can it repair Land Units?
    protected boolean repairSea;    //can it repair Sea Units?
    protected boolean repairAir;    //can it repair Air Units?
    protected boolean repairPipe = false;    //can it repair Pipe Units? (Land includes Pipe units)
    protected boolean createLand;   //can it produce Land Units?
    protected boolean createSea;    //can it produce Sea Units?
    protected boolean createAir;    //can it produce Air Units?
    protected boolean createPipe = false;    //can it produce Pipe Units? (Land includes Pipe units)
    protected Tile tile;            //The tile that the property resides in
    
    //New variables!
    protected int vision;           //properties have vision now?
    								//x = see things within x vision spaces in FoW
    								//0 = see only itself
    								//-1 = can't see anything captain! 
    protected int baseVis;
    
    //constuctor for a neutral property
    public Property() 
    {
        owner = null;
        color = 0;
        tile = null;
        baseVis = 0;
        vision = baseVis;
    }
    
    //constuctor for a pre-owned property
    public Property(Army army) 
    {
        owner = army;
        //owner.addProperty(this);
        color = owner.getColor()+1;
        tile = null;
        baseVis = 0;
        vision = baseVis;
    }
    
    //constuctor for a neutral property
    public Property(Tile t) 
    {
        owner = null;
        color = 0;
        tile = t;
        baseVis = 0;
        vision = baseVis;
    }
    
    //constuctor for a pre-owned property
    public Property(Army army, Tile t) 
    {
        owner = army;
        //owner.addProperty(this);
        color = owner.getColor()+1;
        tile = t;
        baseVis = 0;
        vision = baseVis;
    }
    
    //capture the property
    public boolean capture(Unit u){
        //is it capturable?
        if(isCapturable){
            //is the capturing unit the right kind? (Infantry/Mech)
            if(u.getUType()==0||u.getUType()==1){
                //reduce cp by the unit's HP
                cp -= u.getDisplayHP()*u.getArmy().getCO().getCaptureMultiplier()/100;
                //if the capture is complete, change the property's status
                if(cp <= 0){
                    if(this instanceof HQ){
                        if(owner.getBattle().removeArmy(owner,u.getArmy(),true))return true;
                    }else{
                        if(owner != null)owner.removeProperty(this);
                        owner = u.getArmy();
                        owner.addProperty(this);
                        color = owner.getColor()+1;
                        cp = totalcp;
                    }
                    if(owner.getBattle().getBattleOptions().getCapLimit()>0 && owner.getBattle().getBattleOptions().getCapLimit() <= owner.getProperties().length){
                        JOptionPane.showMessageDialog(null, "Capture Limit Exceeded!");
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    //call when a capture attempt ends in failure
    public void endCapture(){
        cp = totalcp;
    }
    
    public void setCapturePoints(int amount){
        if(amount >= 0)
            cp = amount;
    }
    
    //returns the owner of this property
    public Army getOwner(){
        return owner;
    }
    
    //sets the owner of a property (used by Carmen and Joey)
    public boolean setOwner(Army A)
    {
        if(this instanceof HQ)
        {
            if(owner.getBattle().removeArmy(owner, A, true))
            {
            	return true;
            }
        }
        else
        {
            if(owner != null)
            {
            	owner.removeProperty(this);
            }
            
            owner = A;
            owner.addProperty(this);
            color = owner.getColor()+1;
            cp = totalcp;
        }
        if(owner.getBattle().getBattleOptions().getCapLimit()>0 && owner.getBattle().getBattleOptions().getCapLimit() <= owner.getProperties().length)
        {
            JOptionPane.showMessageDialog(null, "Capture Limit Exceeded!");
            return true;
        }
        return false;
    }
    
    public int getColor(){
        return color;
    }
    
    //returns the income provided by this tile
    public int getIncome(){
        return income;
    }
    
    //is the property capturable?
    public boolean isCapturable(){
        return isCapturable;
    }
    
    //can the property repair land units?
    public boolean canRepairLand(){
        return repairLand;
    }
    
    //can the property repair sea units?
    public boolean canRepairSea(){
        return repairSea;
    }
    
    //can the property repair Air units?
    public boolean canRepairAir(){
        return repairAir;
    }
    
    //can the property repair Pipe units?
    public boolean canRepairPipe(){
        return repairPipe;
    }
    
    //can the property create land units?
    public boolean canCreateLand(){
        return createLand;
    }
    
    //can the property create sea units?
    public boolean canCreateSea(){
        return createSea;
    }
    
    //can the property create air units?
    public boolean canCreateAir(){
        return createAir;
    }
    
    //can the property create pipe units?
    public boolean canCreatePipe(){
        return createPipe;
    }
    
    //returns the remaining capture points
    public int getCapturePoints(){
        return cp;
    }
    
    //returns the maximum capture points
    public int getMaxCapturePoints(){
        return totalcp;
    }
    
    //returns the tile
    public Tile getTile(){
        return tile;
    }
    
    //Returns a string with the Property's important information
    public String toString(){
        return (name + ": Color:" + color + " Capture Points:" + cp + " Income: " + income + " " + tile.getLocation());
    }
    
    //Returns the property's current vision value
    public int getVisionRange()
    {
    	return vision;
    }
    
    //Sets the property's current vision value
    public void setVisionRange(int v)
    {
    	vision = v;
    	
    	if(vision < -1)
    	{
    		vision = -1;
    	}
    }
    
    //Returns the property's vision range to normal
    public void restoreVisionRange()
    {
    	vision = baseVis;
    }
}
