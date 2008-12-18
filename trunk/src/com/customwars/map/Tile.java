package com.customwars.map;
/*
 *Tile.java
 *Author: Adam Dziuk
 *Contributors:
 *Creation: June 25, 2006, 9:44 AM
 *The Tile class holds a tile, including its terrain type and any occupying unit
 */
import java.io.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.customwars.map.location.Locatable;
import com.customwars.map.location.Location;
import com.customwars.map.location.TerrType;
import com.customwars.map.location.Terrain;
import com.customwars.unit.Unit;

public class Tile implements Locatable,Serializable{
    
    private Location loc;   //This tile's location
    private Terrain ter;    //This tile's terrain type
    private Unit unit;      //The unit occupying this tile (null if empty)
	final static Logger logger = LoggerFactory.getLogger(Tile.class); 
    
    //constructor: makes a new tile at the given location with the given unit
    public Tile(int col, int row, Terrain tera, Unit un) {
        loc = new Location(col, row);
        ter = tera;
        unit = un;
    }
    
    //constructor: makes a new tile at the given location with no unit
    public Tile(int col, int row, Terrain tera) {
        loc = new Location(col, row);
        ter = tera;
        unit = null;
    }
    
    public void initStyle(Map m){
        //only needed for 3,4,5,6,8,15,18,and 19
        if((ter.getIndex() >= 3 && ter.getIndex() <= 6)||(ter.getIndex() == 8)||(ter.getIndex()==15)||((ter.getIndex()>=18)||(ter.getIndex()<=20))){
            Terrain north = null;
            Terrain south = null;
            Terrain west = null;
            Terrain east = null;
            boolean n=false,s=false,w=false,e=false;
            
            if(m.find(new Location(loc.getCol(),loc.getRow()-1))!=null)north = m.find(new Location(loc.getCol(),loc.getRow()-1)).getTerrain();
            if(m.find(new Location(loc.getCol(),loc.getRow()+1))!=null)south = m.find(new Location(loc.getCol(),loc.getRow()+1)).getTerrain();
            if(m.find(new Location(loc.getCol()-1,loc.getRow()))!=null)west = m.find(new Location(loc.getCol()-1,loc.getRow())).getTerrain();
            if(m.find(new Location(loc.getCol()+1,loc.getRow()))!=null)east = m.find(new Location(loc.getCol()+1,loc.getRow())).getTerrain();
            
            //roads
            if(ter.getIndex()==3){
                if(north!=null)
                    if(north.getIndex()==3 || north.getIndex()==4 || (north.getIndex()>=9 && north.getIndex()<=14) || north.getIndex()==16)
                        n=true;
                if(south!=null)
                    if(south.getIndex()==3 || south.getIndex()==4 || (south.getIndex()>=9 && south.getIndex()<=14) || south.getIndex()==16)
                        s=true;
                if(west!=null)
                    if(west.getIndex()==3 || west.getIndex()==4 || (west.getIndex()>=9 && west.getIndex()<=14) || west.getIndex()==16)
                        w=true;
                if(east!=null)
                    if(east.getIndex()==3 || east.getIndex()==4 || (east.getIndex()>=9 && east.getIndex()<=14) || east.getIndex()==16)
                        e=true;
                
                if(n==true && s==true && w==true && e==true)ter.setStyle(10);
                else if(n==true && s==true && w==true && e==false)ter.setStyle(9);
                else if(n==false && s==true && w==true && e==true)ter.setStyle(8);
                else if(n==true && s==true && w==false && e==true)ter.setStyle(7);
                else if(n==true && s==false && w==true && e==true)ter.setStyle(6);
                else if(n==true && s==false && w==true && e==false)ter.setStyle(5);
                else if(n==false && s==true && w==true && e==false)ter.setStyle(4);
                else if(n==false && s==true && w==false && e==true)ter.setStyle(3);
                else if(n==true && s==false && w==false && e==true)ter.setStyle(2);
                else if(n==true || s==true)ter.setStyle(1);
                else ter.setStyle(0);
            }
            
            //bridges
            if(ter.getIndex()==4 || ter.getIndex() == 20){
                n = false; s = false;
                if(west!=null){
                    if(west.getIndex()==3 || west.getIndex()==4 || (west.getIndex()>=9 && west.getIndex()<=14) || west.getIndex()==16)
                        w=true;
                    //TODO: Fix this
                    if(west.getIndex()>=5 && west.getIndex() <= 7)
                        n=true;
                }
                if(east!=null){
                    if(east.getIndex()==3 || east.getIndex()==4 || (east.getIndex()>=9 && east.getIndex()<=14) || east.getIndex()==16)
                        e=true;
                    //TODO: Fix this
                    if(east.getIndex()>=5 && east.getIndex()<=7)
                        s=true;
                }
                
                if(north!=null)
                    if(north.getIndex()==3 || north.getIndex()==4 || (north.getIndex()>=9 && north.getIndex()<=14) || north.getIndex()==16)
                        n=true;
                if(south!=null)
                    if(south.getIndex()==3 || south.getIndex()==4 || (south.getIndex()>=9 && south.getIndex()<=14) || south.getIndex()==16)
                        s=true;
                
                /*if(east != null && west != null){
                    if(east.getIndex()==4 && west.getIndex()==4){
                        n=false;
                        s=false;
                    }
                    if(east.getIndex()==4 && west.getIndex()==4 && west.getStyle()==1){
                        n=true;
                        s=true;
                    }
                }*/
                if(north != null && south != null){
                    if((north.getIndex()==4 && (south.getIndex()>=5 && south.getIndex()<=7)) || ((north.getIndex()>=5 && north.getIndex()<=7) && south.getIndex()==4) || (north.getIndex()==4 && south.getIndex()==4 && north.getStyle()==0)){
                        n=false;
                        s=false;
                    }
                }
                /*if(west != null && east != null){
                    //TODO: Fix this
                    if((west.getIndex()>=6 && west.getIndex()<=8)&&(east.getIndex()>=6 && east.getIndex()<=8))
                        n=true;
                }*/
                
                if(n==true || s==true)ter.setStyle(1);
                else ter.setStyle(0);
            }
            
            //rivers
            if(ter.getIndex()==5){
                if(north!=null)
                    if(north.getIndex()==5 || north.getIndex()==4)
                        n=true;
                if(south!=null)
                    if(south.getIndex()==5 || south.getIndex()==4)
                        s=true;
                if(west!=null)
                    if(west.getIndex()==5 || west.getIndex()==4)
                        w=true;
                if(east!=null)
                    if(east.getIndex()==5 || east.getIndex()==4)
                        e=true;
                
                if(n==true && s==true && w==true && e==true)ter.setStyle(10);
                else if(n==true && s==true && w==true && e==false)ter.setStyle(9);
                else if(n==false && s==true && w==true && e==true)ter.setStyle(8);
                else if(n==true && s==true && w==false && e==true)ter.setStyle(7);
                else if(n==true && s==false && w==true && e==true)ter.setStyle(6);
                else if(n==true && s==false && w==true && e==false)ter.setStyle(5);
                else if(n==false && s==true && w==true && e==false)ter.setStyle(4);
                else if(n==false && s==true && w==false && e==true)ter.setStyle(3);
                else if(n==true && s==false && w==false && e==true)ter.setStyle(2);
                else if(n==true || s==true)ter.setStyle(1);
                else ter.setStyle(0);
            }
            
            //shoals (true = sea tile)
            if(ter.getIndex()==8){
                boolean ns=false,ss=false,es=false,ws=false;    //used for shoal detection, true means a shoal in that direction
                if(north!=null){
                    if(north.getIndex()==4 || (north.getIndex()>=6 && north.getIndex()<=8))n=true;
                    if(north.getIndex()==8)ns=true;
                }else{
                    n=true;
                }
                
                if(south!=null){
                    if(south.getIndex()==4 || (south.getIndex()>=6 && south.getIndex()<=8))s=true;
                    if(south.getIndex()==8)ss=true;
                }else{
                    s=true;
                }
                
                if(west!=null){
                    if(west.getIndex()==4 || (west.getIndex()>=6 && west.getIndex()<=8))w=true;
                    if(west.getIndex()==8)ws=true;
                }else{
                    w=true;
                }
                
                if(east!=null){
                    if(east.getIndex()==4 || (east.getIndex()>=6 && east.getIndex()<=8))e=true;
                    if(east.getIndex()==8)es=true;
                }else{
                    e=true;
                }
                
                //shoal independent types
                if(n==false && s==false && w==false && e==false)ter.setStyle(0);
                else if(n==true && s==true && w==true && e==true)ter.setStyle(1);
                else if(n==false && s==false && w==true && e==false)ter.setStyle(5);
                else if(n==false && s==true && w==false && e==false)ter.setStyle(4);
                else if(n==false && s==false && w==false && e==true)ter.setStyle(3);
                else if(n==true && s==false && w==false && e==false)ter.setStyle(2);
                //corners
                else if(n==true && s==false && w==true && e==false){
                    //NW
                    int base = 13;
                    if(!ns && ws)base+=8;
                    else if(ns && !ws)base+=16;
                    else if(ns && ws)base+=24;
                    ter.setStyle(base);
                }
                else if(n==false && s==true && w==true && e==false){
                    //SW
                    int base = 12;
                    if(!ss && ws)base+=8;
                    else if(ss && !ws)base+=16;
                    else if(ss && ws)base+=24;
                    ter.setStyle(base);
                }
                else if(n==false && s==true && w==false && e==true){
                    //SE
                    int base = 11;
                    if(!ss && es)base+=8;
                    else if(ss && !es)base+=16;
                    else if(ss && es)base+=24;
                    ter.setStyle(base);
                }
                else if(n==true && s==false && w==false && e==true){
                    //NE
                    int base = 10;
                    if(!ns && es)base+=8;
                    else if(ns && !es)base+=16;
                    else if(ns && es)base+=24;
                    ter.setStyle(base);
                }
                //singulars
                else if(n==true && s==true && w==true && e==false){
                    //W
                    int base = 9;
                    if(ns && !ss)base+=8;
                    else if(!ns && ss)base+=16;
                    else if(ns && ss)base+=24;
                    ter.setStyle(base);
                }
                else if(n==false && s==true && w==true && e==true){
                    //S
                    int base = 8;
                    if(es && !ws)base+=8;
                    else if(!es && ws)base+=16;
                    else if(es && ws)base+=24;
                    ter.setStyle(base);
                }
                else if(n==true && s==true && w==false && e==true){
                    //E
                    int base = 7;
                    if(ns && !ss)base+=8;
                    else if(!ns && ss)base+=16;
                    else if(ns && ss)base+=24;
                    ter.setStyle(base);
                }
                else if(n==true && s==false && w==true && e==true){
                    //N
                    int base = 6;
                    if(es && !ws)base+=8;
                    else if(!es && ws)base+=16;
                    else if(es && ws)base+=24;
                    ter.setStyle(base);
                }
                else{
                    ter.setStyle(0);
                }
            }
            
            /*
             * 
                if(north!=null)
                    if(north.getIndex()==TerrType.SEA_PIPE || north.getIndex()==TerrType.SP_SEAM || north.getIndex()==TerrType.DEST_SPS)
                        n=true;
                if(south!=null)
                    if(south.getIndex()==TerrType.SEA_PIPE || south.getIndex()==TerrType.SP_SEAM || south.getIndex() ==TerrType.DEST_SPS)
                        s=true;
                if(west!=null)
                    if(west.getIndex()==TerrType.SEA_PIPE || west.getIndex()==TerrType.SP_SEAM || west.getIndex() ==TerrType.DEST_SPS)
                        w=true;
                if(east!=null)
                    if(east.getIndex()==TerrType.SEA_PIPE || east.getIndex()==TerrType.SP_SEAM || east.getIndex() ==TerrType.DEST_SPS)
                        e=true;
             */
            
            //seas (true = sea tile)
            if(ter.getIndex()==6)
            {
                if(north!=null){
                    if((north.getIndex()>=6 && north.getIndex()<=8) || north.getIndex() == 4 || north.getIndex() == 20 || 
                    		north.getIndex()==TerrType.SEA_PIPE || north.getIndex()==TerrType.SP_SEAM || north.getIndex()==TerrType.DEST_SPS)
                        n=true;
                }else{
                    n=true;
                }
                
                if(south!=null){
                    if((south.getIndex()>=6 && south.getIndex()<=8) || south.getIndex() == 4|| south.getIndex() == 20 ||
                    		south.getIndex()==TerrType.SEA_PIPE || south.getIndex()==TerrType.SP_SEAM || south.getIndex() ==TerrType.DEST_SPS)
                        s=true;
                }else{
                    s=true;
                }
                
                if(west!=null){
                    if((west.getIndex()>=6 && west.getIndex()<=8) || west.getIndex() == 4|| west.getIndex() == 20 ||
                    		west.getIndex()==TerrType.SEA_PIPE || west.getIndex()==TerrType.SP_SEAM || west.getIndex() ==TerrType.DEST_SPS)
                        w=true;
                }else{
                    w=true;
                }
                
                if(east!=null){
                    if((east.getIndex()>=6 && east.getIndex()<=8) || east.getIndex() == 4|| east.getIndex() == 20 ||
                    		east.getIndex()==TerrType.SEA_PIPE || east.getIndex()==TerrType.SP_SEAM || east.getIndex() ==TerrType.DEST_SPS)
                        e=true;
                }else{
                    e=true;
                }
                
                if(n==false && s==false && w==true && e==true)ter.setStyle(15);
                else if(n==true && s==true && w==false && e==false)ter.setStyle(14);
                else if(n==false && s==false && w==false && e==false)ter.setStyle(13);
                else if(n==false && s==false && w==true && e==false)ter.setStyle(12);
                else if(n==false && s==true && w==false && e==false)ter.setStyle(11);
                else if(n==false && s==false && w==false && e==true)ter.setStyle(10);
                else if(n==true && s==false && w==false && e==false)ter.setStyle(9);
                else if(n==true && s==false && w==true && e==false)ter.setStyle(8);
                else if(n==false && s==true && w==true && e==false)ter.setStyle(7);
                else if(n==false && s==true && w==false && e==true)ter.setStyle(6);
                else if(n==true && s==false && w==false && e==true)ter.setStyle(5);
                else if(n==true && s==true && w==true && e==false)ter.setStyle(4);
                else if(n==false && s==true && w==true && e==true)ter.setStyle(3);
                else if(n==true && s==true && w==false && e==true)ter.setStyle(2);
                else if(n==true && s==false && w==true && e==true)ter.setStyle(1);
                else ter.setStyle(0);
                
                if(ter.getStyle() == 0){
                    if(m.find(new Location(loc.getCol()+1,loc.getRow()-1))!=null)north = m.find(new Location(loc.getCol()+1,loc.getRow()-1)).getTerrain();
                    if(m.find(new Location(loc.getCol()+1,loc.getRow()+1))!=null)south = m.find(new Location(loc.getCol()+1,loc.getRow()+1)).getTerrain();
                    if(m.find(new Location(loc.getCol()-1,loc.getRow()+1))!=null)west = m.find(new Location(loc.getCol()-1,loc.getRow()+1)).getTerrain();
                    if(m.find(new Location(loc.getCol()-1,loc.getRow()-1))!=null)east = m.find(new Location(loc.getCol()-1,loc.getRow()-1)).getTerrain();
                    
                    n = false;
                    s = false;
                    w = false;
                    e = false;
                    
                    if(north!=null){
                        if((north.getIndex()>=6 && north.getIndex()<=8) || north.getIndex() == 4 || 
                        		north.getIndex()==TerrType.SEA_PIPE || north.getIndex()==TerrType.SP_SEAM || north.getIndex()==TerrType.DEST_SPS)
                            n=true;
                    }else{
                        n=true;
                    }
                    
                    if(south!=null){
                        if((south.getIndex()>=6 && south.getIndex()<=8) || south.getIndex() == 4 ||
                        		south.getIndex()==TerrType.SEA_PIPE || south.getIndex()==TerrType.SP_SEAM || south.getIndex() ==TerrType.DEST_SPS)
                            s=true;
                    }else{
                        s=true;
                    }
                    
                    if(west!=null){
                        if((west.getIndex()>=6 && west.getIndex()<=8) || west.getIndex() == 4 ||
                        		west.getIndex()==TerrType.SEA_PIPE || west.getIndex()==TerrType.SP_SEAM || west.getIndex() ==TerrType.DEST_SPS)
                            w=true;
                    }else{
                        w=true;
                    }
                    
                    if(east!=null){
                        if((east.getIndex()>=6 && east.getIndex()<=8) || east.getIndex() == 4 ||
                        		east.getIndex()==TerrType.SEA_PIPE || east.getIndex()==TerrType.SP_SEAM || east.getIndex() ==TerrType.DEST_SPS)
                            e=true;
                    }else{
                        e=true;
                    }
                    
                    if(n==false && s==true && w==true && e==true)ter.setStyle(16);
                    else if(n==true && s==false && w==true && e==true)ter.setStyle(17);
                    else if(n==true && s==true && w==false && e==true)ter.setStyle(18);
                    else if(n==true && s==true && w==true && e==false)ter.setStyle(19);
                    else if(n==false && s==true && w==true && e==false)ter.setStyle(20);
                    else if(n==false && s==false && w==true && e==true)ter.setStyle(21);
                    else if(n==true && s==false && w==false && e==true)ter.setStyle(22);
                    else if(n==true && s==true && w==false && e==false)ter.setStyle(23);
                    else if(n==true && s==false && w==false && e==false)ter.setStyle(24);
                    else if(n==false && s==true && w==false && e==false)ter.setStyle(25);
                    else if(n==false && s==false && w==true && e==false)ter.setStyle(26);
                    else if(n==false && s==false && w==false && e==true)ter.setStyle(27);
                    else if(n==false && s==false && w==false && e==false)ter.setStyle(28);
                    else if(n==false && s==true && w==false && e==true)ter.setStyle(29);
                    else if(n==true && s==false && w==true && e==false)ter.setStyle(30);
                }
            }
            
            //pipes
            if(ter.getIndex()==15){
                if(north!=null)
                    if(north.getIndex()==15 || north.getIndex()==17 || north.getIndex()==18 || north.getIndex()==19)
                        n=true;
                if(south!=null)
                    if(south.getIndex()==15 || south.getIndex()==17 || south.getIndex()==18 || south.getIndex()==19)
                        s=true;
                if(west!=null)
                    if(west.getIndex()==15 || west.getIndex()==17 || west.getIndex()==18 || west.getIndex()==19)
                        w=true;
                if(east!=null)
                    if(east.getIndex()==15 || east.getIndex()==17 || east.getIndex()==18 || east.getIndex()==19)
                        e=true;
                
                if(n==true && s==true && w==true && e==true)ter.setStyle(10);
                else if(n==true && s==true && w==true && e==false)ter.setStyle(9);
                else if(n==false && s==true && w==true && e==true)ter.setStyle(8);
                else if(n==true && s==true && w==false && e==true)ter.setStyle(7);
                else if(n==true && s==false && w==true && e==true)ter.setStyle(6);
                else if(n==true && s==false && w==true && e==false)ter.setStyle(5);
                else if(n==false && s==true && w==true && e==false)ter.setStyle(4);
                else if(n==false && s==true && w==false && e==true)ter.setStyle(3);
                else if(n==true && s==false && w==false && e==true)ter.setStyle(2);
                //end pieces
                else if(n==false && s==true && w==false && e==false)ter.setStyle(11);
                else if(n==false && s==false && w==true && e==false)ter.setStyle(12);
                else if(n==true && s==false && w==false && e==false)ter.setStyle(13);
                else if(n==false && s==false && w==false && e==true)ter.setStyle(14);
                //back to normal
                else if(n==true || s==true)ter.setStyle(1);
                else ter.setStyle(0);
            }
            
            //pipe seams & destroyed pipe seams
            if(ter.getIndex()==18 || ter.getIndex()==19){
                if(north!=null)
                    if(north.getIndex()==15 || north.getIndex()==18 || north.getIndex()==19)
                        n=true;
                if(south!=null)
                    if(south.getIndex()==15 || south.getIndex()==18 || south.getIndex()==19)
                        s=true;
                if(west!=null)
                    if(west.getIndex()==15 || west.getIndex()==18 || west.getIndex()==19)
                        w=true;
                if(east!=null)
                    if(east.getIndex()==15 || east.getIndex()==18 || east.getIndex()==19)
                        e=true;
                
                if(n==true || s==true)ter.setStyle(1);
                else ter.setStyle(0);
            }            
            
            //wall
            //assuming that walls are 21
            if(ter.getIndex()==21)
            {
                if(north!=null)
                    if(north.getIndex()==22 || north.getIndex()==21)
                        n=true;
                if(south!=null)
                    if(south.getIndex()==22 || south.getIndex()==21)
                        s=true;
                if(west!=null)
                    if(west.getIndex()==22 || west.getIndex()==21)
                        w=true;
                if(east!=null)
                    if(east.getIndex()==22 || east.getIndex()==21)
                        e=true;
                
                if(n==true && s==true && w==true && e==true)ter.setStyle(10);
                else if(n==true && s==true && w==true && e==false)ter.setStyle(9);
                else if(n==false && s==true && w==true && e==true)ter.setStyle(8);
                else if(n==true && s==true && w==false && e==true)ter.setStyle(7);
                else if(n==true && s==false && w==true && e==true)ter.setStyle(6);
                else if(n==true && s==false && w==true && e==false)ter.setStyle(5);
                else if(n==false && s==true && w==true && e==false)ter.setStyle(4);
                else if(n==false && s==true && w==false && e==true)ter.setStyle(3);
                else if(n==true && s==false && w==false && e==true)ter.setStyle(2);
                //end pieces
                else if(n==false && s==true && w==false && e==false)ter.setStyle(11);
                else if(n==false && s==false && w==true && e==false)ter.setStyle(12);
                else if(n==true && s==false && w==false && e==false)ter.setStyle(13);
                else if(n==false && s==false && w==false && e==true)ter.setStyle(14);
                //back to normal
                else if(n==true || s==true)ter.setStyle(1);
                else ter.setStyle(0);
            }

            //destroyed walls
            //assuming that walls are 22
            if(ter.getIndex()==22)
            {
                if(north!=null)
                    if(north.getIndex()==22 || north.getIndex()==21)
                        n=true;
                if(south!=null)
                    if(south.getIndex()==22 || south.getIndex()==21)
                        s=true;
                if(west!=null)
                    if(west.getIndex()==22 || west.getIndex()==21)
                        w=true;
                if(east!=null)
                    if(east.getIndex()==22 || east.getIndex()==21)
                        e=true;
                
                if(n==true || s==true)ter.setStyle(1);
                else ter.setStyle(0);
            }
            
            //sea pipes
            if(ter.getIndex()==TerrType.SEA_PIPE)
            {
                if(north!=null)
                    if(north.getIndex()==TerrType.SEA_PIPE || north.getIndex()==TerrType.PIPE_STATION  || north.getIndex()==TerrType.SP_SEAM || north.getIndex()==TerrType.DEST_SPS)
                        n=true;
                if(south!=null)
                    if(south.getIndex()==TerrType.SEA_PIPE || south.getIndex()==TerrType.PIPE_STATION  || south.getIndex()==TerrType.SP_SEAM || south.getIndex() ==TerrType.DEST_SPS)
                        s=true;
                if(west!=null)
                    if(west.getIndex()==TerrType.SEA_PIPE || west.getIndex()==TerrType.PIPE_STATION  || west.getIndex()==TerrType.SP_SEAM || west.getIndex() ==TerrType.DEST_SPS)
                        w=true;
                if(east!=null)
                    if(east.getIndex()==TerrType.SEA_PIPE || east.getIndex()==TerrType.PIPE_STATION  || east.getIndex()==TerrType.SP_SEAM || east.getIndex() ==TerrType.DEST_SPS)
                        e=true;
                
                if(n==true && s==true && w==true && e==true)ter.setStyle(10);
                else if(n==true && s==true && w==true && e==false)ter.setStyle(9);
                else if(n==false && s==true && w==true && e==true)ter.setStyle(8);
                else if(n==true && s==true && w==false && e==true)ter.setStyle(7);
                else if(n==true && s==false && w==true && e==true)ter.setStyle(6);
                else if(n==true && s==false && w==true && e==false)ter.setStyle(5);
                else if(n==false && s==true && w==true && e==false)ter.setStyle(4);
                else if(n==false && s==true && w==false && e==true)ter.setStyle(3);
                else if(n==true && s==false && w==false && e==true)ter.setStyle(2);
                //end pieces
                else if(n==false && s==true && w==false && e==false)ter.setStyle(11);
                else if(n==false && s==false && w==true && e==false)ter.setStyle(12);
                else if(n==true && s==false && w==false && e==false)ter.setStyle(13);
                else if(n==false && s==false && w==false && e==true)ter.setStyle(14);
                //back to normal
                else if(n==true || s==true)ter.setStyle(1);
                else ter.setStyle(0);
            }
            
            //sea pipe seams & destroyed sea pipe seams
            if(ter.getIndex()==TerrType.SP_SEAM || ter.getIndex()==TerrType.DEST_SPS)
            {
                if(north!=null)
                    if(north.getIndex()==TerrType.SEA_PIPE || north.getIndex()==TerrType.PIPE_STATION  || north.getIndex()==TerrType.SP_SEAM || north.getIndex()==TerrType.DEST_SPS)
                        n=true;
                if(south!=null)
                    if(south.getIndex()==TerrType.SEA_PIPE || south.getIndex()==TerrType.PIPE_STATION  || south.getIndex()==TerrType.SP_SEAM || south.getIndex() ==TerrType.DEST_SPS)
                        s=true;
                if(west!=null)
                    if(west.getIndex()==TerrType.SEA_PIPE || west.getIndex()==TerrType.PIPE_STATION  || west.getIndex()==TerrType.SP_SEAM || west.getIndex() ==TerrType.DEST_SPS)
                        w=true;
                if(east!=null)
                    if(east.getIndex()==TerrType.SEA_PIPE || east.getIndex()==TerrType.PIPE_STATION  || east.getIndex()==TerrType.SP_SEAM || east.getIndex() ==TerrType.DEST_SPS)
                        e=true;
                
                if(n==true || s==true)ter.setStyle(1);
                else ter.setStyle(0);
            }          
        }
    }
    
    //adds a given unit to the tile
    public void addUnit(Unit u){
        if(unit == null)
            unit = u;
        else
            logger.info("Error: Trying to add a Unit to Tile at: " + loc + ", Another Unit already present");
    }
    
    //removes the occupying unit from the tile
    public void removeUnit(){
        unit = null;
    }
    
    //checks if the tile has a unit occupying it
    public boolean hasUnit(){
        if(unit == null)
            return false;
        return true;
    }
    
    //returns the tile's location
    public Location getLocation(){
        return loc;
    }
    
    //returns the unit occupying the tile (null if empty)
    public Unit getUnit(){
        return unit;
    }
    
    //returns the tile's terrain type
    public Terrain getTerrain(){
        return ter;
    }
    
    //used in map editing and for pipe seams
    public void setTerrain(Terrain t){
        ter = t;
    }
    
    //returns a string with the contents of the tile
    public String toString(){
        return ("Tile at:\n" + loc + "\nContaining: " + ter + "\n" + unit);
    }
}