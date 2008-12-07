package cwsource;
/*
 *UnitGraphics.java
 *Author: Urusan
 *Contributors:
 *Creation: July 12, 2006, 10:07 AM
 *Holds graphics information related to Units
 */

import java.awt.*;
import javax.imageio.*;
import java.awt.image.*;
import java.io.*;
import javax.swing.*;

public class UnitGraphics 
{
	private static int MAX_ARMIES = 10;
	
    private static Image unitSheet;
    private static Image rUnitSheet;
    private static Image[] moveUnitSheet = new Image[4];
    
    
    private static Image infSheet;
    private static Image rInfSheet;

    
    //This stuff is new!
    private static Image SFWiSheet;
    private static Image SFWriSheet;
    
    private static Image uSheet[] = new Image[MAX_ARMIES];
    private static Image ruSheet[] = new Image[MAX_ARMIES];
    private static Image muSheet[][] = new Image[MAX_ARMIES][4];
    
    private static Image iSheet[] = new Image[MAX_ARMIES];
    private static Image riSheet[] = new Image[MAX_ARMIES];
    //This stuff is new!
    private static Image SFWinfSheet[] = new Image[MAX_ARMIES];
    private static Image SFWrInfSheet[] = new Image[MAX_ARMIES];
    
    //constructor
    public UnitGraphics() {}
    
    //loads the images before use
    public static void loadImages(Component screen)
    {
    	String imagesLocation = ResourceLoader.properties.getProperty("imagesLocation");
       
        unitSheet = new ImageIcon(imagesLocation + "/units/units.gif").getImage();
        rUnitSheet = new ImageIcon(imagesLocation + "images/units/unitsr.gif").getImage();
        infSheet = new ImageIcon(imagesLocation + "images/units/infantry.gif").getImage();
        rInfSheet = new ImageIcon(imagesLocation + "images/units/infantryr.gif").getImage();
        moveUnitSheet[0] = new ImageIcon(imagesLocation + "images/units/units_up.gif").getImage();
        moveUnitSheet[1] = new ImageIcon(imagesLocation + "images/units/units_right.gif").getImage();
        moveUnitSheet[2] = new ImageIcon(imagesLocation + "images/units/units_down.gif").getImage();
        moveUnitSheet[3] = new ImageIcon(imagesLocation + "images/units/units_left.gif").getImage();

                
        //This stuff is new!
        SFWiSheet = new ImageIcon(imagesLocation + "images/units/SFW_Inf.gif").getImage();
        SFWriSheet = new ImageIcon(imagesLocation + "images/units/SFW_Infr.gif").getImage();
        
        //TEMPORARY, later only generate what you need
        for(int i = 0; i < MAX_ARMIES; i++)
        {
            UnitFilter colorfilter = new UnitFilter(i);
            uSheet[i] = screen.createImage(new FilteredImageSource(unitSheet.getSource(),colorfilter));
            ruSheet[i] = screen.createImage(new FilteredImageSource(rUnitSheet.getSource(),colorfilter));
            iSheet[i] = screen.createImage(new FilteredImageSource(infSheet.getSource(),colorfilter));
            riSheet[i] = screen.createImage(new FilteredImageSource(rInfSheet.getSource(),colorfilter));
            
            for(int s = 0; s<4; s++)
            {
            muSheet[i][s] = screen.createImage(new FilteredImageSource(moveUnitSheet[s].getSource(),colorfilter));
            }
            //This stuff is new!
            SFWinfSheet[i] = screen.createImage(new FilteredImageSource(SFWiSheet.getSource(),colorfilter));
            SFWrInfSheet[i] = screen.createImage(new FilteredImageSource(SFWriSheet.getSource(),colorfilter));
        }
    }
    
    //load the image for a given unit
    public static Image getUnitImage(Unit imgUnit)
    {
        boolean rev = false;
        
        if(imgUnit.getArmy().getColor() % 2 != 0)
        {
        	rev = true;
        }
        
        //Alright time to try some funky magic with Quirky Berserky
        //
        //   Conditions for getting "Quirky Berserky" units:
        //   (1) Quirky Berserky is on
        //       (a) COP name is Quirky Berserky
        //       (b) COP is active
        //   (2) Currently drawing enemy view
        //   (3) Front CO is Koshi
        //       (a) Should be true as long as first condition is fulfilled
        //
        if(isQuirkyBerserky(imgUnit))        
        {        	
        	if(rev)
        	{
        		return SFWrInfSheet[imgUnit.getArmy().getColor()];
        	}
            
            return SFWinfSheet[imgUnit.getArmy().getColor()];
        }
        
        //If the unit is an Infantry unit
        if(imgUnit.getUnitType() == 0 || imgUnit.getUnitType() == 1)
        {
            if(rev)
            {
            	return riSheet[imgUnit.getArmy().getColor()];
            }
            
            return iSheet[imgUnit.getArmy().getColor()];
        }
        
        //For all other units
        if(rev)
        {
        	return ruSheet[imgUnit.getArmy().getColor()];
        }
        
        return uSheet[imgUnit.getArmy().getColor()];
    }
    
    //Hmm I hope this works :(
    public static boolean isQuirkyBerserky(Unit targUnit)
    {
        if(targUnit.getArmy().getCO().hiddenUnitType && 
                targUnit.getArmy().getCO().COP &&
                (targUnit.getArmy().getBattle().getArmy(targUnit.getArmy().getBattle().getTurn()).getSide() !=
                targUnit.getArmy().getSide()))
        {
        	return true;
        }
        
        return false;
    }
    
    public void switchUnitSheets(String normalSheet, String reverseSheet) 
    {
    	
    }
    
    public void switchInfantrySheets(String normalSheet, String reverseSheet)
    {
    	
    }
    
    //load the image for a index number
    //Carp I can't add SFWInf stuff here lol
    public static Image getUnitImage(int type, int color)
    {
        boolean rev = false;
        
        if(color%2 != 0)
        {
        	rev = true;
        }
        
        if(type==0 || type==1)
        {
            if(rev)
            {
            	return riSheet[color];
            }
            
            return iSheet[color];
        }
        
        if(rev)
        {
        	return ruSheet[color];
        }
        
        return uSheet[color];
    }
    
    public static int findYPosition(Unit u)
    {        
        if(isQuirkyBerserky(u))
        {
        	return 0;
        }
        
        int type = u.getUnitType();
        
        if(type == 0 || type == 1)
        {
            int style = u.getArmy().getCO().getStyle();
            return type*16+style*32;
        }
        return (type-2)*16;
    }

    //Carp I can't add SFWInf stuff here lol
    public static int findYPosition(int type, int style)
    {
        if(type == 0 || type == 1)
        {
            //TEMPORARY
            if(style>7)
            {
            	style=0;
            }
            return type*16+style*32;
        }
        return (type-2)*16;
    }

    public static Image getNorthImage(Unit imgUnit) {
        boolean rev = false;
        
        if(imgUnit.getArmy().getColor() % 2 != 0) {
            rev = true;
        }
        if(isQuirkyBerserky(imgUnit)) {
            if(rev) {
                return SFWrInfSheet[imgUnit.getArmy().getColor()];
            } 
            return SFWinfSheet[imgUnit.getArmy().getColor()];
        }
        //If the unit is an Infantry unit
        if(imgUnit.getUnitType() == 0 || imgUnit.getUnitType() == 1) {
            if(rev) {
                return riSheet[imgUnit.getArmy().getColor()];
            }
            return iSheet[imgUnit.getArmy().getColor()];
        }
        //For all other units
        if(rev) {
            return muSheet[imgUnit.getArmy().getColor()][0];
        }
        return muSheet[imgUnit.getArmy().getColor()][0];
    }
    public static Image getSouthImage(Unit imgUnit) {
        boolean rev = false;
        
        if(imgUnit.getArmy().getColor() % 2 != 0) {
            rev = true;
        }
        if(isQuirkyBerserky(imgUnit)) {
            if(rev) {
                return SFWrInfSheet[imgUnit.getArmy().getColor()];
            } 
            return SFWinfSheet[imgUnit.getArmy().getColor()];
        }
        //If the unit is an Infantry unit
        if(imgUnit.getUnitType() == 0 || imgUnit.getUnitType() == 1) {
            if(rev) {
                return riSheet[imgUnit.getArmy().getColor()];
            }
            return iSheet[imgUnit.getArmy().getColor()];
        }
        //For all other units
        if(rev) {
            return muSheet[imgUnit.getArmy().getColor()][2];
        }
        return muSheet[imgUnit.getArmy().getColor()][2];
    }
    public static Image getEastImage(Unit imgUnit) {
        boolean rev = false;
        
        if(imgUnit.getArmy().getColor() % 2 != 0) {
            rev = true;
        }
        if(isQuirkyBerserky(imgUnit)) {
            if(rev) {
                return SFWrInfSheet[imgUnit.getArmy().getColor()];
            } 
            return SFWinfSheet[imgUnit.getArmy().getColor()];
        }
        //If the unit is an Infantry unit
        if(imgUnit.getUnitType() == 0 || imgUnit.getUnitType() == 1) {
            if(rev) {
                return riSheet[imgUnit.getArmy().getColor()];
            }
            return iSheet[imgUnit.getArmy().getColor()];
        }
        //For all other units
        if(rev) {
            return muSheet[imgUnit.getArmy().getColor()][1];
        }
        return muSheet[imgUnit.getArmy().getColor()][1];
    }
    public static Image getWestImage(Unit imgUnit) {
        boolean rev = false;
        
        if(imgUnit.getArmy().getColor() % 2 != 0) {
            rev = true;
        }
        if(isQuirkyBerserky(imgUnit)) {
            if(rev) {
                return SFWrInfSheet[imgUnit.getArmy().getColor()];
            } 
            return SFWinfSheet[imgUnit.getArmy().getColor()];
        }
        //If the unit is an Infantry unit
        if(imgUnit.getUnitType() == 0 || imgUnit.getUnitType() == 1) {
            if(rev) {
                return riSheet[imgUnit.getArmy().getColor()];
            }
            return iSheet[imgUnit.getArmy().getColor()];
        }
        //For all other units
        if(rev) {
            return muSheet[imgUnit.getArmy().getColor()][3];
        }
        return muSheet[imgUnit.getArmy().getColor()][3];
    }
    /*
    //load the image for a given unit
    public static Image getUnitImage(Unit u){
        boolean rev = false;
        if(u.getArmy().getColor()%2 != 0)rev = true;
        if(u.getUnitType()==0 || u.getUnitType()==1){
            if(rev)return rInfSheet;
            return infSheet;
        }
        if(rev)return rUnitSheet;
        return unitSheet;
    }
    
    //load the image for a index number
    public static Image getUnitImage(int type, int color){
        boolean rev = false;
        if(color%2 != 0)rev = true;
        if(type==0 || type==1){
            if(rev)return rInfSheet;
            return infSheet;
        }
        if(rev)return rUnitSheet;
        return unitSheet;
    }
    
    public static int findYPosition(Unit u){
        int type = u.getUnitType();
        if(type == 0 || type == 1){
            int style = u.getArmy().getCO().getStyle();
            return type*16+style*16;
        }
        return (type-2)*16;
    }
    
    public static int findYPosition(int type, int style){
        if(type == 0 || type == 1){
            //TEMPORARY
            if(style>7)style=0;
            return type*16+style*16;
        }
        return (type-2)*16;
    }
     */
}