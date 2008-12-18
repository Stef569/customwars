package com.customwars.map;
/*
 *Map.java
 *Author: Adam Dziuk
 *Contributors: Urusan
 *Creation: June 25, 2006, 10:04 AM
 *The Map class holds a map, including the tile list and map size
 */

import java.io.*;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.customwars.Trigger;
import com.customwars.map.location.Location;
import com.customwars.map.location.Plain;
import com.customwars.map.location.Property;
import com.customwars.unit.Army;
import com.customwars.unit.Unit;

public class Map implements Serializable{
    
    private Tile[][] map;    //Contains the tile list
    private int maxRow;      //The map's maximum number of rows
    private int maxCol;      //The map's maximum number of columns
    
    private String name="";        //The map's name
    private String author="";      //The author's name
    private String description=""; //The map's description
    private Vector commands;
    //maxRow and maxCol are exclusive limits
	final static Logger logger = LoggerFactory.getLogger(Map.class); 
    //constructor
    public Map(int col, int row) {
        map = new Tile[col][row];
        maxRow = row;
        maxCol = col;
        loadMap();
        initStyle();
        commands = new Vector<Trigger>();
    }
    
    //Removes a Unit from the Map
    public void remove(Unit u){
        map[u.getLocation().getCol()][u.getLocation().getRow()].removeUnit();
    }
    
    //Moves the Unit to the given Location on the Map
    public void move(Unit u, Location l){
        if(onMap(l)){
            map[u.getLocation().getCol()][u.getLocation().getRow()].removeUnit();
            map[l.getCol()][l.getRow()].addUnit(u);
        }
    }
    
    //Move the Unit to the r,c coordinates.
    public void move(Unit u, int c, int r){
        if(onMap(c,r)){
            map[u.getLocation().getCol()][u.getLocation().getRow()].removeUnit();
            map[c][r].addUnit(u);
        }
    }
    
    //Sets a given tile, used my loadMap(), map editing, and changing terrain (pipe-seam -> destroyed)
    public void setTile(Tile t){
        map[t.getLocation().getCol()][t.getLocation().getRow()] = t;
    }
    
    //loads a Map from file, currently a placeholder (sets all tiles to plains)
    private void loadMap(){
        for(int r = 0; r < maxRow; r++){
            for(int c = 0; c < maxCol; c++){
                setTile(new Tile(c,r, new Plain()));
            }
        }
    }
    
    //copys a map into this map
    public void copyMap(Map m){
        for(int r = 0; r < maxRow; r++){
            for(int c = 0; c < maxCol; c++){
                if(r < m.getMaxRow() && c < m.getMaxCol()){
                    setTile(new Tile(c,r, m.find(new Location(c,r)).getTerrain()));
                    if(m.find(new Location(c,r)).hasUnit()){
                        addUnit(m.find(new Location(c,r)).getUnit());
                    }
                }else{
                    setTile(new Tile(c,r, new Plain()));
                }
            }
        }
    }
    
    public void initStyle(){
        for(int i=0; i<maxRow; i++){
            for(int j=0; j<maxCol; j++){
                map[j][i].initStyle(this);
            }
        }
    }
    
    //adds a given unit to a given tile
    public void addUnit(Tile t, Unit u){
        map[t.getLocation().getCol()][t.getLocation().getRow()].addUnit(u);
    }
    
    //adds a given unit to its location's tile
    public void addUnit(Unit u){
        map[u.getLocation().getCol()][u.getLocation().getRow()].addUnit(u);
    }
    
    //adds a given unit to a given location
    public void addUnit(Location l, Unit u){
        map[l.getCol()][l.getRow()].addUnit(u);
    }
    
    //adds a given unit to a given r,c coordinate
    public void addUnit(int c, int r, Unit u){
        map[c][r].addUnit(u);
    }
    
    //checks if a given col,row has a unit occupying it
    public boolean hasUnit(int c, int r){
        if(map[c][r].getUnit()!=null)return true;
        else return false;
    }
    
    public boolean hasProperty(int c, int r){
        if(map[c][r].getTerrain().isUrban())return true;
        else return false;
    }
    
    //Tests to make sure that the location is on the map
    public boolean onMap(Location l){
        if(l.getCol() >= 0 &&  l.getCol() < maxCol){
            if(l.getRow() >= 0 && l.getRow() < maxRow){
                return true;
            }
        }
        return false;
    }
    
    //Tests to make sure that the r,c coordinate is on the map
    public boolean onMap(int c, int r){
        if(r >= 0 &&  r < maxRow)
            if(c >= 0 && c < maxCol)
                return true;
        return false;
    }
    
    //returns the tile at a given location, returns null if the location is off the map
    public Tile find(Location l){
        if(onMap(l))
            return map[l.getCol()][l.getRow()];
        return null;
    }
    
    //returns the tile at a given Unit's location, returns null if the Unit is off the map/not on the map
    public Tile find(Unit l){
        if(onMap(l.getLocation()))
            return map[l.getLocation().getCol()][l.getLocation().getRow()];
        return null;
    }
    
    //returns maxRow
    public int getMaxRow(){
        return maxRow;
    }
    
    //returns maxCol
    public int getMaxCol(){
        return maxCol;
    }
    
    //returns the map name
    public String getMapName(){
        return name;
    }
    
    //returns the map author
    public String getMapAuthor(){
        return author;
    }
    
    //returns the map descriptiion
    public String getMapDescription(){
        return description;
    }
    
    //sets the map name
    public void setMapName(String newName){
        name = newName;
    }
    
    //sets the map author
    public void setMapAuthor(String newName){
        author = newName;
    }
    
    //sets the map descriptiion
    public void setMapDescription(String newDesc){
        description = newDesc;
    }

    //makes an explosion (ex. Silos, Black Bombs)
    public void doExplosion(int radius, int damage, int x, int y, boolean paralyze){
        int offset = 0;
        logger.info("Center: " + x + "," + y);
        for(int i=-1*radius; i <= radius; i++){
            for(int j=-1*offset; j <= offset; j++){
                if(onMap(x+j,y+i) && map[x+j][y+i].getUnit()!=null){
                    map[x+j][y+i].getUnit().damage(damage*10,false);
                    if(paralyze)
                        map[x+j][y+i].getUnit().setParalyzed(true);
                }
            }
            if(i<0)offset++;
            else offset--;
        }
    }
    
    public void addExplosion(int[][] set, int radius, int x, int y, int value){
        int offset = 0;
        for(int i=-1*radius; i <= radius; i++){
            for(int j=-1*offset; j <= offset; j++){
                if(x+j > -1 && x+j < maxCol && y+i > -1 && y+i < maxRow)
                    set[x+j][y+i]+= value;
            }
            if(i<0)offset++;
            else offset--;
        }
    }
    
    
    //This method is for value auto targeting only
    public void doAutoExplosion(Army call,int radius, int damage, boolean paralyze, int side){
        int[][] temp = new int[maxCol][maxRow];
        Army[] arms = call.getBattle().getArmies();
        Unit[] units;
        int si;
        for(int i = 0; i < arms.length; i++){
            units = arms[i].getUnits();
            si = arms[i].getSide();
            for(int s = 0; s < units.length; s++)
                if(si != side)
                    addExplosion(temp, radius, units[s].getLoc().getCol(), units[s].getLoc().getRow(), units[s].getValue() );
                else
                    addExplosion(temp, radius, units[s].getLoc().getCol(), units[s].getLoc().getRow(), units[s].getValue() * -1);
        }
        int tempVal = 0;
        int tempCol = -1;
        int tempRow = -1;
        for(int x = 0; x < maxCol; x++)
            for(int y = 0; y < maxRow; y++){
            if(temp[x][y] > tempVal){
                tempCol = x;
                tempRow = y;
                tempVal = temp[x][y];
            }
            }
        if(tempCol > 0)
            doExplosion(radius, damage, tempCol, tempRow, paralyze);
    }
    
    //This method is for value auto targeting only
    public void doInfExplosion(Army call,int radius, int damage, boolean paralyze, int side){
        int[][] temp = new int[maxCol][maxRow];
        Army[] arms = call.getBattle().getArmies();
        Unit[] units;
        int si;
        for(int i = 0; i < arms.length; i++){
            units = arms[i].getUnits();
            si = arms[i].getSide();
            for(int s = 0; s < units.length; s++){
                int dam = units[s].getDisplayHP();
                if(dam > damage)dam = damage;
                if(si != side){
                    if(units[s].getUnitType() < 2){
                        if(find(units[s].getLocation()).getTerrain() instanceof Property){
                            Property p = (Property)find(units[s].getLocation()).getTerrain();
                            if(p.getCapturePoints()<p.getMaxCapturePoints()){
                                addExplosion(temp, radius, units[s].getLoc().getCol(), units[s].getLoc().getRow(), dam*6);
                            }else{
                                addExplosion(temp, radius, units[s].getLoc().getCol(), units[s].getLoc().getRow(), dam*3);
                            }
                        }else{
                            addExplosion(temp, radius, units[s].getLoc().getCol(), units[s].getLoc().getRow(), dam*3);
                        }
                    }else{
                        addExplosion(temp, radius, units[s].getLoc().getCol(), units[s].getLoc().getRow(), dam);
                    }
                }else{
                    addExplosion(temp, radius, units[s].getLoc().getCol(), units[s].getLoc().getRow(), dam * -1);
                }
            }
        }
        int tempVal = 0;
        int tempCol = -1;
        int tempRow = -1;
        for(int x = 0; x < maxCol; x++)
            for(int y = 0; y < maxRow; y++){
            if(temp[x][y] > tempVal){
                tempCol = x;
                tempRow = y;
                tempVal = temp[x][y];
            }
            }
        if(tempCol > 0)
            doExplosion(radius, damage, tempCol, tempRow, paralyze);
    }
    
    //This method is for value auto targeting only
    public void doHPExplosion(Army call,int radius, int damage, boolean paralyze, int side){
        int[][] temp = new int[maxCol][maxRow];
        Army[] arms = call.getBattle().getArmies();
        Unit[] units;
        int si;
        for(int i = 0; i < arms.length; i++){
            units = arms[i].getUnits();
            si = arms[i].getSide();
            for(int s = 0; s < units.length; s++){
                int dam = units[s].getDisplayHP();
                if(dam > damage)dam = damage;
                if(si != side)
                    addExplosion(temp, radius, units[s].getLoc().getCol(), units[s].getLoc().getRow(), dam );
                else
                    addExplosion(temp, radius, units[s].getLoc().getCol(), units[s].getLoc().getRow(), dam * -1);
            }
        }
        int tempVal = 0;
        int tempCol = -1;
        int tempRow = -1;
        for(int x = 0; x < maxCol; x++)
            for(int y = 0; y < maxRow; y++){
            if(temp[x][y] > tempVal){
                tempCol = x;
                tempRow = y;
                tempVal = temp[x][y];
            }
            }
        if(tempCol > 0)
            doExplosion(radius, damage, tempCol, tempRow, paralyze);
    }
    
       
    public Unit[] getUnits(boolean[][] area){
        LinkedList<Unit> ll = new LinkedList<Unit>();
        for(int col = 0; col < maxCol; col++)
            for(int row = 0; row < maxRow; row++)
               if(area[col][row] && hasUnit(col, row))
                   ll.add(map[col][row].getUnit());
        if(ll.size() == 0)
            return null;
        logger.info("size:"+ll.size());
        Unit[] tagg = new Unit[ll.size()];
        logger.info("tagg.length:"+tagg.length);
        for(int i = 0; i < ll.size(); i++)
            tagg[i] =(Unit) ll.get(i);
        return tagg;
            }
    
    public Tile[] getNeighbors(int col, int row){
        LinkedList<Tile> ll = new LinkedList<Tile>();
        if(onMap(col+1,row))
            ll.add(map[col+1][row]);
        if(onMap(col-1,row))
            ll.add(map[col-1][row]);
        if(onMap(col,row+1))
            ll.add(map[col][row+1]);
        if(onMap(col,row-1))
            ll.add(map[col][row-1]);
         Tile[] tagg = new Tile[ll.size()];
        for(int i = 0; i < ll.size(); i++)
            tagg[i] =(Tile) ll.get(i);
        return tagg;
    }
    
    public Property[] HQs(){
     LinkedList<Property> ll = new LinkedList<Property>();
        for(int col = 0; col < maxCol; col++)
            for(int row = 0; row < maxRow; row++)
               if(map[col][row].getTerrain().getIndex() == 9)
                   ll.add((Property) map[col][row].getTerrain());
        if(ll.size() == 0)
            return null;
        else{
          Property[] sven = new Property[ll.size()];
        for(int i = 0; i < ll.size(); i++)
            sven[i] =(Property) ll.get(i);
        return sven;
        }
    }
    public Vector<Trigger> getTriggers()
    {
        return commands;
    }
}


