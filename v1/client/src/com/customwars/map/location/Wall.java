package com.customwars.map.location;

import com.customwars.map.Map;
import com.customwars.map.Tile;

public class Wall extends Invention
{   
    //constructor map editor
    public Wall()
    {
        super();
        initStatistics();
    }
    
    //constructor for battles
    public Wall(Map map, Tile t)
    {
        super(map,t);
        initStatistics();
    }    
    
    private void initStatistics()
    { 
        //Statistics 
        name = "Wall"; 
        setMove(new double[] {-1,-1,-1,-1,1,-1,-1,-1,-1,-1}); 
        basemove = new double[] {-1,-1,-1,-1,1,-1,-1,-1,-1,-1}; 
        def = 0; 
        index = 21; 
        
        hp = 99; 
        baseDMG = new int[] {-1,10,10,35,-1,1,1,25,35,-1,-1,-1,-1,35,-1,15,-1,70,40,80,35,-1,-1,35,-1,40,30,-1,15,-1,30}; 
        altDMG = new int[] {1,1,1,1,1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,1,-1,-1,1,1,-1,-1,-1,-1,-1,1,-1,-1,-1,-1,-1}; 
    } 
}