package com.customwars.client.io.loading;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;

import org.newdawn.slick.util.ResourceLoader;

import com.customwars.client.model.map.Tile;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.gameobject.Terrain;
import com.customwars.client.model.gameobject.Locatable;
import com.customwars.client.model.gameobject.TerrainFactory;
import com.customwars.client.model.gameobject.CityFactory;
import com.customwars.client.model.gameobject.UnitFactory;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Map;

public class MapParser {
    
    private final String BASE_PATH = basePath();
    private final int NEUTRAL = 0;
    private final int DEFAULT_SIZE = 1;
    
    private final int TYPE_UNIT = 0;
    private final int TYPE_TERRAIN = 1;
    private final int TYPE_CITY = 2;
            
    private String mapPath;
    private InputStream in;
    private OutputStream out;
    private Scanner scan;
    private ArrayList<Tile> theTiles;
    
    public MapParser(){
        mapPath = "";
        //Prints out the root directory for any computer.
        //System.out.println(BASE_PATH);
        theTiles = new ArrayList<Tile>();
    }
    
    public void setMapPath(String mapPath){
        //Error checking
        if(mapPath == null)
            return;
        else if(!mapPath.matches("") && 
                mapPath.charAt(mapPath.length()-1) != '/')
            mapPath = (mapPath+"/");
        this.mapPath = mapPath;
    }
    
    public void printMap(String filename) throws FileNotFoundException {
        in = new FileInputStream(BASE_PATH+mapPath+filename); 
        printLoad();
    }
    
    public void loadMap(String filename) throws FileNotFoundException, IOException {
        in = new FileInputStream(BASE_PATH+mapPath+filename); 
        load();
    }
    
    public void loadMapAsResource(String filename) throws IOException{
        in = ResourceLoader.getResourceAsStream(mapPath+filename);
        load();
    }
    
    //mapName
    //version
    //mapCreator
    //description
    //column
    //row
    //maxPlayers
    //Fow
    //fillTerrain
    public void writeMap(String filename, String[] descrip,
            Map<Tile> map) throws FileNotFoundException, IOException{
        String data = "";
        Terrain terrain;
        Unit unit;
        City city;
        
        if(descrip != null){
            for(int i = 0; i < descrip.length; i++)
                data += "["+descrip[i]+"]";
        }
        
        if(new File(BASE_PATH+mapPath+filename).createNewFile())
            System.out.println(filename+" Created!");
        else
            System.out.println(filename+" Overwritten.");
        
        out = new FileOutputStream(BASE_PATH+mapPath+filename);
        out.write(data.getBytes());
        
        DataOutputStream dataOut = new DataOutputStream(out);
        if(map != null){
            for(Tile t : map.getAllTiles()){
                //Get current Terrain and Unit
                terrain = t.getTerrain();
                city = map.getCityOn(t);

                if(city == null){
                    //(BasicID:) How to deal with this data
                    dataOut.writeByte(TYPE_TERRAIN);
                    //(Player): Who owns this terrain
                    dataOut.writeByte(NEUTRAL-1);
                    //(TerrainType): What kind of terrain is this
                    dataOut.writeByte(terrain.getID());
                }else{
                    //(BasicID:) How to deal with this data
                    dataOut.writeByte(TYPE_CITY);
                    //(Player): Who owns this terrain
                    dataOut.writeByte(city.getOwner().getId());
                    //(TerrainType): What kind of terrain is this
                    dataOut.writeByte(terrain.getID());
                    //(CityType): What kind of city terrain this is
                    dataOut.writeByte(city.getID());
                }

                //(Column): Where it is Located on the x-axis
                dataOut.writeInt(t.getCol());
                //(Row): Where it is located on the y-axis
                dataOut.writeInt(t.getRow());
                //(SizeX:) How much tilespace this unit occupies x-axis
                dataOut.writeByte(DEFAULT_SIZE);
                //(SizeY:) How much tilespace this unit occupies y-axis
                dataOut.writeByte(DEFAULT_SIZE);
                //(Active:) Whether inventions are active or not
                dataOut.writeByte(NEUTRAL);

                //Get current unit...
                unit = map.getUnitOn(t);
                if(unit != null){
                    //(BasicID:) How to deal with this data
                    dataOut.writeByte(TYPE_UNIT);
                    //(Player): Who owns this unit
                    dataOut.writeByte(unit.getOwner().getId());
                    //(unitType:) The type of unit
                    dataOut.writeByte(unit.getID());
                    //(Rank): The current rank of this unit (experience)
                    dataOut.writeByte(NEUTRAL);
                    //(Column): Where it is Located on the x-axis
                    dataOut.writeInt(t.getCol());
                    //(Row): Where it is located on the y-axis
                    dataOut.writeInt(t.getRow());
                    //(Load): How many units are loaded in this one
                    dataOut.writeInt(searchLoadedUnits(unit.getLocation()));
                    //Continue HERE

                    //for map making....
                    //We need TERRAIN/CITY:LAUNCH-STATE (SILOS/INVENTION)
                    //We need TERRAIN/CITY:SIZEX (INVENTION)
                    //We need TERRAIN/CITY:SIZEY (INVENTION)

                    //for game saves....
                    //We need UNIT:HP
                    //We need UNIT:AMMO
                    //We need UNIT:FUEL
                    //We need UNIT:MATERIAL (SEAPLANES/TEMPORARY BUILDINGS)
                }         
            }
        }
    }
    
    private int searchLoadedUnits(Location theLoc){
        int counter = 0;
        for(int i = 0; i < theLoc.getLocatableCount(); i++){
            counter += searchLoadedUnits(theLoc.getLocatable(i).getLocation());
        }
        return counter;
    }
    
    private void printLoad(){
        String temp;
        scan = new Scanner(in);
        int counter = 0;
        
        do{
            temp = scan.findInLine("\\W[^\\x5B\\x5D]*\\W");//Perl jargon
            if(temp != null){
                if(counter == 0)
                    System.out.println("Map Name: "+temp);
                if(counter == 1)
                    System.out.println("Version: "+temp);
                if(counter == 2)
                    System.out.println("Creator: "+temp);
                if(counter == 3)
                    System.out.println("Map Description:"+temp);
            }
            counter++;
        }while(temp != null);
        
        System.out.println("Done");
    }
    
    private void load() throws IOException{
        String temp;
        scan = new Scanner(in);
        int counter = 0;
        
        String mapName = "";//0:MapName
        String version = "";//1:version
        String mapCreator = "";//2:mapCreator
        String description = "";//3:description
        int column = 0;//4:column
        int row = 0;//5:row
        int maxPlayers = 0;//6:MaxPlayers
        boolean fogOfWar = false;//7:Fog active
        String fillTerrain = "plain";//8:default fill
        
        while(scan.hasNext("\\W[^\\x5B\\x5D]*\\W")){
            temp = scan.findInLine("\\W[^\\x5B\\x5D]*\\W");//Perl jargon
            if(temp != null){
                if(counter == 0)
                    mapName = temp.substring(1,temp.length()-1);
                else if(counter == 1)
                    version = temp.substring(1,temp.length()-1);
                else if(counter == 2)
                    mapCreator = temp.substring(1,temp.length()-1);
                else if(counter == 3)
                    description = temp.substring(1,temp.length()-1);
                else if(counter == 4)
                    column = new Integer(temp.substring(1,temp.length()-1));
                else if(counter == 5)
                    row = new Integer(temp.substring(1,temp.length()-1));
                else if(counter == 6)
                    maxPlayers = new Integer(temp.substring(1,temp.length()-1));
                else if(counter == 7)
                    fogOfWar = (temp.matches("[Tt][Rr][Uu][Ee]"));
                else if(counter == 8)
                    fillTerrain = temp.substring(1,temp.length()-1);
            }
            System.out.println(temp);
        }
        
        
        
        Map<Tile> newMap = 
                new Map<Tile>(column, row, column*row, maxPlayers, fogOfWar);
        Tile newTile = null;
        byte basicID;
        byte player;
        byte terrainType;
        byte cityType;
        byte unitType;
        byte sizex;
        byte sizey;
        byte active;
        int load;
        byte rank;
        
        if(counter >= 7){
            DataInputStream datain = new DataInputStream(in);
            while(true){
                try{
                     basicID = datain.readByte();
                }catch(EOFException e){
                    break;
                }
                
                if(basicID == TYPE_TERRAIN || basicID == TYPE_CITY){
                    if(newTile != null)
                        newMap.setTile(newTile);
                }
                
                if(basicID == TYPE_TERRAIN){
                    //For player
                    player = datain.readByte();
                    //For terrain
                    terrainType = datain.readByte();
                    //For column
                    column = datain.readInt();
                    //For row
                    row = datain.readInt();
                    //For sizeX
                    sizex = datain.readByte();
                    //For sizeY
                    sizey = datain.readByte();
                    //For active
                    active = datain.readByte();
                    //Makes a new Tile
                    newTile = new Tile(column, row, 
                            TerrainFactory.getTerrain((int)terrainType));
                }else if(basicID == TYPE_CITY){
                    //For player
                    player = datain.readByte();
                    //For terrain
                    terrainType = datain.readByte();
                    //For city
                    cityType = datain.readByte();
                    //For column
                    column = datain.readInt();
                    //For row
                    row = datain.readInt();
                    //For sizeX
                    sizex = datain.readByte();
                    //For sizeY
                    sizey = datain.readByte();
                    //For active
                    active = datain.readByte();
                    //Makes a new Tile
                    newTile = new Tile(column, row, 
                            CityFactory.getCity((int)cityType));
                }else if(basicID == TYPE_UNIT){
                    //For player
                    player = datain.readByte();
                    //For ID
                    unitType = datain.readByte();
                    //For rank
                    rank = datain.readByte();
                    //For column
                    column = datain.readInt();
                    //For row
                    row = datain.readInt();
                    //For load
                    load = datain.readInt();
                    //Adds a unit to the Tile
                    newTile.add(UnitFactory.getUnit((int)unitType));
                }
            }
        }
        
        System.out.println("Done");
    }
    
    private String basePath(){
        return new File("").getAbsolutePath().replace('\\','/')+"/";
    }
    
    private String braketSplit(){
        //finds everything in-between brackets (includes brackets)
        return "\\W[^\\x5B\\x5D]*\\W";
    }   
}
