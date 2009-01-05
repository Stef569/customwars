package com.customwars.map.location;

import com.customwars.map.Map;
import com.customwars.map.Tile;

public class PipeSeam extends Invention{
   
    //constructor map editor
    public PipeSeam(){
        super();
        initStatistics();
    }
    
    //constructor for battles
    public PipeSeam(Map map, Tile t){
        super(map,t);
        initStatistics();
    }
    
    private void initStatistics(){
        //Statistics
        name = "Pipe Seam";
        setMove(new double[] {-1,-1,-1,-1,-1,-1,-1,-1,1,-1});
        basemove = new double[] {-1,-1,-1,-1,-1,-1,-1,-1,1,-1};;
        def  = 0;
        index = 18;
        
        hp = 99;
        baseDMG = new int[] {-1,15,15,55,-1,10,15,45,55,-1,-1,-1,-1,55,-1,25,-1,95,75,125,55,-1,-1,70,-1,75,50,-1,25,-1,55};
        altDMG = new int[] {1,1,1,1,1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,1,-1,-1,1,1,-1,-1,-1,-1,-1,1,-1,-1,-1,-1,-1};
    }
}