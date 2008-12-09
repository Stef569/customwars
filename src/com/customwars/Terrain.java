package com.customwars;
/*
 *Terrain.java
 *Author: Adam Dziuk
 *Contributors:
 *Creation: June 24, 2006, 4:09 PM
 *Terrain is an abstract class for Terrains
 */

import java.io.*;

public abstract class Terrain implements Serializable
{    
    //move[] 0=infantry, 1=mech, 2=tread, 3=tires, 4=air, 5=sea, 6=transport, 7=oozium, 8=pipe, 9=hover
    protected double[] move = new double[MoveType.MAX_MOVE_TYPES];  //holds the mp values for every movement type
    protected double[] basemove = new double[MoveType.MAX_MOVE_TYPES];
    protected int def;                  //the Terrain's defense bonus
    protected String name;              //the Terrain's name
    protected int index;                //the Terrain's index number
    protected int visualStyle = 0;      //The "visual style" of the tile (roads, rivers, pipes, sea, shoal, etc.)
    protected boolean urban = false;    //Is this an urban terrain? (used for Kindle and Peter)

    protected Terrain()
    {
    	
    }
    
    protected Terrain(int terrStats[], double terrCosts[], String name, boolean isUrban)
    {
    	System.arraycopy(terrCosts, 0, this.basemove, 0, MoveType.MAX_MOVE_TYPES);
    	System.arraycopy(terrCosts, 0, this.move, 0, MoveType.MAX_MOVE_TYPES);
    	
    	this.def = terrStats[TerrStats.DEF_STARS];
    	this.name = name;
    	this.index = terrStats[TerrStats.TERR_TYPE];
    	this.visualStyle = terrStats[TerrStats.STYLE];
    	this.urban = isUrban;
    }
    
    /**
     * This completely replaces the moveset of this terrain. 
     * <p>
     * In the event the parameter is not of 10 elements, this method does nothing.
     * @param newMove a 10 element array containing the new array.
     */
    public void newMoveSet(double[] newMove)
    {
        if(newMove.length == 10)
        {
            move = newMove;
        }
    }
    /**
     * This replaces one specific move type for one specific movement type
     * @param type movetype to be changed. For reference: 0=infantry, 1=mech, 2=tread, 3=tires, 4=air, 5=sea, 6=transport, 7=oozium, 8=pipe, 9=hover
     * @param alter amount the movement cost is to be changed to
     */
    public void changeCost(int type, double alter)
    {
        move[type] = alter;
    }
    /**
     * This adds newMove to the current moveset.
     * @param newMove a 10 element array containing the relative amounts the movearray is to be changed.
     */
    public void addMoveSet(double[] newMove)
    {
        if(newMove.length == 10)
        {
            for(int i = 0; i<10; i++)
            {
            move[i]+= newMove[i];
            }
        }
    }
    /**
     * This adds one specific move type for one specific movement type
     * @param type movetype to be changed. For reference: 0=infantry, 1=mech, 2=tread, 3=tires, 4=air, 5=sea, 6=transport, 7=oozium, 8=pipe, 9=hover
     * @param alter new relative amount the movement cost is.
     */
    public void addCost(int type, double alter)
    {
        move[type] += alter;
    }
    //Retore move costs.
    public void restoreCost()
    {
        move = basemove;
    }
    //returns the mp cost for a given movement type, used by MoveTraverse
    public double moveCost(int type){
        return move[type];
    }
    public double baseMoveCost(int type){
        return basemove[type];
    }
    public double[] getBaseMove(){
        return basemove;
    }
    //returns the terrain's defense bonus
    public int getDef(){
        return def;
    }
    
    //returns the terrain's index number
    public int getIndex(){
        return index;
    }
    
    //returns the terrain's name
    public String getName(){
        return name;
    }
    
    //is the property urban?
    public boolean isUrban(){
        return urban;
    }
    
    //returns the terrain's visual style
    public int getStyle(){
        return visualStyle;
    }
    
    //sets the terrain's visual style
    public void setStyle(int style){
        visualStyle = style;
    }
    
    //only returns true if the object is a terrain of the same type
    public boolean equals(Object o){
        if(o.getClass() != this.getClass())
            return false;
        Terrain t = (Terrain) o;
        if(t.getName().equals(this.getName()))
            return true;
        else
            return false;
    }
    
    //returns a string containing all of movement costs and defense bonus
    public String toString(){
        return (name + ": inf: " + move[0] + " mech: " + move[1] + " tread: " + move[2] + " tires: "
                + move[3] + " air: " + move[4] + " sea: " + move[5] + " transport: " + move[6] + " oozium: " + move[7]
                + " pipe: " + move[8] + "Hover" + move[9] + " Defense: " + def);
    }
}