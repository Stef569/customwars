package com.customwars.unit;

/*
 *Transport.java
 *Author: Urusan
 *Contributors:
 *Creation: July 27, 2006, 9:09 PM
 *Transports are a class of Units that can carry other units
 */
import com.customwars.map.Map;
import com.customwars.map.Tile;
import com.customwars.map.location.Location;

public abstract class Transport extends Unit{
    private Unit slot1;               //Holds the first unit being carried
    private Unit slot2;               //Holds the second unit being carried
    protected int maxUnits;             //the maximum number of units the transport can carry
    protected boolean transportTable[] = new boolean[32]; //the kinds of units the transport can carry
    
    //constructor
    public Transport(Location l, Army arm, Map m){
        super(l,arm,m);
        setSlot1(null);
        setSlot2(null);
        for(int i=0;i<transportTable.length;i++)transportTable[i]=false;
    }
    
    //load a unit onto the transport
    public void load(Unit u){
        if(getSlot1() != null && maxUnits > 1){
            setSlot2(u);
            u.setInTransport(true);
        }else{
            setSlot1(u);
            u.setInTransport(true);
        }
    }
    
    //unloads a unit by deleting from the transport and returning the unit
    //(also ensures that slot1 is always full)
    public Unit unload(int slot){
        Unit temp = null;
        if(slot == 2){
            temp = getSlot2();
            setSlot2(null);
        }else{
            temp = getSlot1();
            setSlot1(null);
            if(getSlot2() != null){
                setSlot1(getSlot2());
                setSlot2(null);
            }
        }
        temp.setOutTransport();
        return temp;
    }
    
    //Determines is a given location is a valid unloading point
    public boolean checkUnloadRange(Location l, int unloadslot){
        Map m = getMap();
        if(m.onMap(l)){
            int r = Math.abs(l.getRow() - getLoc().getRow()) + Math.abs(l.getCol() - getLoc().getCol());
            if(r > 0 && r <= 1){
                Tile t = m.find(new Location(l.getCol(),l.getRow()));
                if(t!=null){
                    if(t.getTerrain().moveCost(this.getUnit(unloadslot).getMType())!=-1 && !(t.hasUnit() && !t.getUnit().isHidden()))return true;
                }
            }
        }
        return false;
    }
    
    //returns a given unit, used for display and unloading
    public Unit getUnit(int slot){
        if(slot == 2)return getSlot2();
        return getSlot1();
    }
    
    //returns the number of units being carried in the transport
    public int getUnitsCarried(){
        if(getSlot1() != null){
            if(getSlot2() != null)return 2;
            return 1;
        }
        return 0;
    }
    
    public boolean roomAvailable(){
        if(getUnitsCarried() < maxUnits)return true;
        return false;
    }
    
    //returns the maximum number of units the transport can carry
    public int getMaxUnits(){
        return maxUnits;
    }
    
    //checks if a given unit can be carried by the transport
    public boolean canCarry(int unitType){
        return transportTable[unitType];
    }
    
    //used for debug
    public String toString(){
        return (name + ": HP:" + hP + " Active:" + active + " Capacity:" + maxUnits + " Slot1:" + getSlot1() + " Slot2:" + getSlot2());
    }

	public void setSlot1(Unit slot1) {
		this.slot1 = slot1;
	}

	public Unit getSlot1() {
		return slot1;
	}

	public void setSlot2(Unit slot2) {
		this.slot2 = slot2;
	}

	public Unit getSlot2() {
		return slot2;
	}
}
