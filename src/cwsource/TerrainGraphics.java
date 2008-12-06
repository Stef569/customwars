package cwsource;
/*
 *TerrainGraphics.java
 *Author: Urusan
 *Contributors:
 *Creation: July 12, 2006, 10:59 AM
 *Holds graphics information related to Terrain
 */

import java.awt.*;
import javax.imageio.*;
import java.awt.image.*;
import java.io.*;
import javax.swing.*;

public class TerrainGraphics{
    private static int MAX_ARMIES = 10;
    private static Image baseImage;
    private static Image ownedImage[] = new Image[MAX_ARMIES+1];//holds the terrain sprite-sheet
    //constructor
    public TerrainGraphics() {}
    
    //loads the images before use
    public static void loadImages(Component screen){
        
        String imagesLocation = ResourceLoader.properties.getProperty("imagesLocation");
    	
        baseImage = new ImageIcon(imagesLocation + "/terrain/cw-terrain.gif").getImage();
        
        //Owned Properties
        for(int i=0; i < 11; i++){
            PropFilter colorfilter = new PropFilter(i);
            ownedImage[i] = screen.createImage(new FilteredImageSource(baseImage.getSource(),colorfilter));
        }
    }
    
    public static Image getTerrainImage(int index, int armyColor){
        if(index == 9){
            if(armyColor != 0)
                return ownedImage[armyColor];
        }
        return baseImage;
    }

    /*
    public static Image getTerrainImage(Terrain t){
        if(t.getStyle() != 0){
            if(t.getIndex() == 3){
                return roads[t.getStyle()];
            }else if(t.getIndex() == 4){
                return bridges[t.getStyle()];
            }else if(t.getIndex() == 5){
                return rivers[t.getStyle()];
            }else if(t.getIndex() == 6){
                return seas[t.getStyle()];
            }else if(t.getIndex() == 8){
                return shoals[t.getStyle()];
            }else if(t.getIndex() == 15){
                return pipes[t.getStyle()];
            }
        }
        if(t.getIndex() == 9){
            //right now
            //return hq[((Property)t).getOwner().getColor()][((Property)t).getOwner().getColor()];
            return ownedUrban[((Property)t).getOwner().getColor()];
            //eventually
            //return hq[((Property)t).getOwner().getColor()][((Property)t).getOwner().getCO().getStyle()];
        }else if(t instanceof Silo){
            if(!((Silo) t).isLaunched())return properties[t.getIndex()][0];
            else return properties[t.getIndex()][1];
        }else if(t instanceof Property && ((Property)t).owner != null){
            //return properties[t.getIndex()][((Property) t).getColor()];
            return ownedUrban[((Property)t).getOwner().getColor()];
        }else if(t instanceof Property && ((Property)t).owner == null){
            return properties[t.getIndex()][((Property) t).getColor()];
        }else if(t instanceof PipeSeam){
            return pipeSeams[t.getStyle()];
        }else if(t instanceof DestroyedPipeSeam){
            return destroyedPipeSeams[t.getStyle()];
        }
        //TEMP
        if(sprites[t.getIndex()] == null){
            return sprites[0];
        }
        return sprites[t.getIndex()];
    }
     */

    public static Image getSpriteSheet(){
        return baseImage;
    }
    public static Image getColoredSheet(int armyColor){
        return ownedImage[armyColor];
    }
}
