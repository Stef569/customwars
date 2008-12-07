package cwsource;
/*
 *Invention.java
 *Author: Urusan
 *Contributors:
 *Creation: August 13, 2006, 9:46 AM
 *Invention is an abstract class for Inventions
 */

import java.io.*;

public abstract class Invention extends Terrain{
    
    protected int hp;           //The invention's HP
    protected int[] baseDMG;    //The base damage for the invention
    protected int[] altDMG;     //The alt damage for the invention
    Tile tile;                  //the tile containing the invention
    Map m;                      //the map that the invention is on
    
    //constructor, creates a neutral invention
    public Invention(){
    }
    
    //constructor, creates a neutral invention
    public Invention(Map map, Tile t){
        m = map;
        tile = t;
    }
    
    public int getHP(){
        return hp;
    }
    
    //Deals damage to the HP of the Invention, deals with unit destruction
    public void damage(int dmg){
        if(dmg < 0) return;
        hp -= dmg;
        if (hp <= 0){
            if(this instanceof PipeSeam)
                tile.setTerrain(new DestroyedPipeSeam());
            else if(this instanceof Wall)
                tile.setTerrain(new DestroyedWall());
            else if(this instanceof SeaPipeSeam)
            	tile.setTerrain(new DestroyedSeaPipeSeam());
            
            m.initStyle();
        }
    }
    
    //finds the appropriate value on the table, used by Unit.DamageCalc()
    public int find(int ammo, int index){
        if(ammo > 0)
            if(baseDMG[index]!=-1)
                return baseDMG[index];
        return altDMG[index];
    }
    
    //finds the appropriate base value on the table
    public int findBase(int index){
        return baseDMG[index];
    }
}