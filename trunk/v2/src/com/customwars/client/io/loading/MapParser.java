package com.customwars.client.io.loading;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;

import org.newdawn.slick.util.ResourceLoader;

import com.customwars.client.model.map.Tile;
import com.customwars.client.model.gameobject.Locatable;
import com.customwars.client.model.map.Location;

public class MapParser {
    
    private final String BASE_PATH = basePath();
            
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
    
    public void loadMap(String filename) throws FileNotFoundException {
        in = new FileInputStream(BASE_PATH+mapPath+filename); 
        load();
    }
    
    public void loadMapAsResource(String filename){
        in = ResourceLoader.getResourceAsStream(mapPath+filename);
        load();
    }
    
    public void writeMap(String filename, String mapName,
            String version, String mapCreator,
            String description, ArrayList<Tile> theTiles) 
            throws FileNotFoundException, IOException{
        if(mapName == null)
            mapName = "";
        if(version == null)
            version = "";
        if(mapCreator == null)
            mapCreator = "";
        if(description == null)
            description = "";
        
        String dataString = "["+mapName+"]["+version+"]" +
                "["+mapCreator+"]["+description+"]";
        
        if(theTiles != null){
            for(int i = 0; i < theTiles.size(); i++){
                Tile tempTile = theTiles.get(i);
                //We need the ID...
                tempTile.getTerrain().getID();
                //We need the column...
                tempTile.getCol();
                //We need the row...
                tempTile.getRow();
                
                for(int j = 0; j < tempTile.getLocatableCount(); j++){
                    //for the unit
                    Location unitLoc = tempTile.getLocatable(j).getLocation();
                    //How many units is loaded in this one (recursive)
                    searchLoadedUnits(unitLoc);
                }
                
                //for map making....
                //We need TERRAIN/CITY:LAUNCH-STATE (SILOS/INVENTION)
                //We need TERRAIN/CITY:SIZEX (INVENTION)
                //We need TERRAIN/CITY:SIZEY (INVENTION)
                //We need TERRAIN/CITY:PLAYER COLOR
                
                //We need UNIT:PLAYER COLOR
                
                //for game saves....
                //We need UNIT:HP
                //We need UNIT:AMMO
                //We need UNIT:FUEL
                //We need UNIT:MATERIAL (SEAPLANES/TEMPORARY BUILDINGS)
            }
        }
        
        writeMap(filename, dataString);
    }
    
    private int searchLoadedUnits(Location theLoc){
        int counter = 0;
        for(int i = 0; i < theLoc.getLocatableCount(); i++){
            counter += searchLoadedUnits(theLoc.getLocatable(i).getLocation());
        }
        return counter;
    }
    
    public void writeMap(String filename, String data) 
    throws FileNotFoundException, IOException{
        if(new File(BASE_PATH+mapPath+filename).createNewFile())
            System.out.println(filename+" Created!");
        else
            System.out.println(filename+" Overwritten.");
        
        out = new FileOutputStream(BASE_PATH+mapPath+filename);
        out.write(data.getBytes());
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
    
    private void load(){
        String temp;
        scan = new Scanner(in);
        
        while(scan.hasNext("\\W[^\\x5B\\x5D]*\\W")){
            temp = scan.findInLine("\\W[^\\x5B\\x5D]*\\W");//Perl jargon
            System.out.println(temp);
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
