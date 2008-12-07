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
    private static Image sprites[] = new Image[20];         //holds images of every terrain in the game [index]
    private static Image properties[][] = new Image[20][11]; //holds property images, most are empty [index][color]
    private static Image hq[][] = new Image[10][10];          //holds the HQ sprites [color][style]
    private static Image roads[] = new Image[11];           //holds the road images
    private static Image rivers[] = new Image[11];          //holds the river images
    private static Image bridges[] = new Image[2];          //holds the bridge images
    private static Image shoals[] = new Image[38];          //holds the shoal images
    private static Image seas[] = new Image[31];          //holds the sea images
    private static Image pipes[] = new Image[15];          //holds the pipe images
    private static Image pipeSeams[] = new Image[2];        //holds the pipe seam images
    private static Image destroyedPipeSeams[] = new Image[2];//holds the destroyed pipe seam images
    private static Image baseUrban;                         //holds the base sheet
    private static Image ownedUrban[] = new Image[MAX_ARMIES+1]; //holds the colored urban sheet
    private static Image terrain;        
    private static Image baseHQ;
    private static Image ownedHQ[] = new Image[MAX_ARMIES+1];//holds the terrain sprite-sheet
    //constructor
    public TerrainGraphics() {}
    
    //loads the images before use
    public static void loadImages(Component screen){
        //normal terrain
        /*sprites[0] = new ImageIcon("images/terrain/plains.gif").getImage();
        sprites[1] = new ImageIcon("images/terrain/wood.gif").getImage();
        sprites[2] = new ImageIcon("images/terrain/mountain.gif").getImage();
        sprites[3] = new ImageIcon("images/terrain/roadh.gif").getImage();
        sprites[4] = new ImageIcon("images/terrain/bridgeh.gif").getImage();
        sprites[5] = new ImageIcon("images/terrain/riverh.gif").getImage();
        sprites[6] = new ImageIcon("images/terrain/sea.gif").getImage();
        sprites[7] = new ImageIcon("images/terrain/reef.gif").getImage();
        sprites[8] = new ImageIcon("images/terrain/shoal.gif").getImage();
        sprites[15] = new ImageIcon("images/terrain/pipeh.gif").getImage();
        //Neutral Properties
        properties[10][0] = new ImageIcon("images/terrain/city.gif").getImage();
        properties[11][0] = new ImageIcon("images/terrain/base.gif").getImage();
        properties[12][0] = new ImageIcon("images/terrain/airport.gif").getImage();
        properties[13][0] = new ImageIcon("images/terrain/port.gif").getImage();
        properties[14][0] = new ImageIcon("images/terrain/comtower.gif").getImage();
        properties[16][0] = new ImageIcon("images/terrain/silo.gif").getImage();
        properties[16][1] = new ImageIcon("images/terrain/siloused.gif").getImage();
        properties[17][0] = new ImageIcon("images/terrain/pipestation.gif").getImage();*/
        
        String imagesLocation = ResourceLoader.properties.getProperty("imagesLocation");
    	
        baseUrban = new ImageIcon(imagesLocation + "/terrain/urban_CW.gif").getImage();
        baseHQ = new ImageIcon(imagesLocation + "/terrain/HQ_CW.gif").getImage();
        terrain = new ImageIcon(imagesLocation + "/terrain/terrain_CW.gif").getImage();
        
        //Owned Properties
        for(int i=0; i < 11; i++){
            PropFilter colorfilter = new PropFilter(i);
            //HQs
            //hq[i-1][i-1] = new ImageIcon("images/terrain/hq"+i+".gif").getImage();
            ownedUrban[i] = screen.createImage(new FilteredImageSource(baseUrban.getSource(),colorfilter));
            ownedHQ[i] = screen.createImage(new FilteredImageSource(baseHQ.getSource(),colorfilter));
            //Properties
            /*
            properties[10][i] = new ImageIcon("images/terrain/city"+i+".gif").getImage();
            properties[11][i] = new ImageIcon("images/terrain/base"+i+".gif").getImage();
            properties[12][i] = new ImageIcon("images/terrain/airport"+i+".gif").getImage();
            properties[13][i] = new ImageIcon("images/terrain/port"+i+".gif").getImage();
            properties[14][i] = new ImageIcon("images/terrain/comtower"+i+".gif").getImage();
            properties[17][i] = new ImageIcon("images/terrain/pipestation"+i+".gif").getImage();
             */
            
        }
        /*
        //Roads
        //roads[0] = new ImageIcon("images/terrain/roadh.gif").getImage();
        roads[1] = new ImageIcon("images/terrain/roadv.gif").getImage();
        roads[2] = new ImageIcon("images/terrain/roadne.gif").getImage();
        roads[3] = new ImageIcon("images/terrain/roadse.gif").getImage();
        roads[4] = new ImageIcon("images/terrain/roadsw.gif").getImage();
        roads[5] = new ImageIcon("images/terrain/roadnw.gif").getImage();
        roads[6] = new ImageIcon("images/terrain/roadtn.gif").getImage();
        roads[7] = new ImageIcon("images/terrain/roadte.gif").getImage();
        roads[8] = new ImageIcon("images/terrain/roadts.gif").getImage();
        roads[9] = new ImageIcon("images/terrain/roadtw.gif").getImage();
        roads[10] = new ImageIcon("images/terrain/roadnesw.gif").getImage();
        
        //Bridges
        //bridges[0] = new ImageIcon("images/terrain/bridgeh.gif").getImage();
        bridges[1] = new ImageIcon("images/terrain/bridgev.gif").getImage();
        
        //Rivers
        //rivers[0] = new ImageIcon("images/terrain/river.gif").getImage();
        rivers[1] = new ImageIcon("images/terrain/riverv.gif").getImage();
        rivers[2] = new ImageIcon("images/terrain/riverne.gif").getImage();
        rivers[3] = new ImageIcon("images/terrain/riverse.gif").getImage();
        rivers[4] = new ImageIcon("images/terrain/riversw.gif").getImage();
        rivers[5] = new ImageIcon("images/terrain/rivernw.gif").getImage();
        rivers[6] = new ImageIcon("images/terrain/rivernew.gif").getImage();
        rivers[7] = new ImageIcon("images/terrain/rivernse.gif").getImage();
        rivers[8] = new ImageIcon("images/terrain/riversew.gif").getImage();
        rivers[9] = new ImageIcon("images/terrain/rivernsw.gif").getImage();
        rivers[10] = new ImageIcon("images/terrain/rivernsew.gif").getImage();
        
        //Shoals
        //shoals[0] = new ImageIcon("images/terrain/shoal.gif").getImage();
        shoals[1] = new ImageIcon("images/terrain/shoalall.gif").getImage();
        shoals[2] = new ImageIcon("images/terrain/shoal3n.gif").getImage();
        shoals[3] = new ImageIcon("images/terrain/shoal3e.gif").getImage();
        shoals[4] = new ImageIcon("images/terrain/shoal3s.gif").getImage();
        shoals[5] = new ImageIcon("images/terrain/shoal3w.gif").getImage();
        shoals[6] = new ImageIcon("images/terrain/shoaln.gif").getImage();
        shoals[7] = new ImageIcon("images/terrain/shoale.gif").getImage();
        shoals[8] = new ImageIcon("images/terrain/shoals.gif").getImage();
        shoals[9] = new ImageIcon("images/terrain/shoalw.gif").getImage();
        shoals[10] = new ImageIcon("images/terrain/shoalne.gif").getImage();
        shoals[11] = new ImageIcon("images/terrain/shoalse.gif").getImage();
        shoals[12] = new ImageIcon("images/terrain/shoalsw.gif").getImage();
        shoals[13] = new ImageIcon("images/terrain/shoalnw.gif").getImage();
        shoals[14] = new ImageIcon("images/terrain/shoalnse.gif").getImage();
        shoals[15] = new ImageIcon("images/terrain/shoalesn.gif").getImage();
        shoals[16] = new ImageIcon("images/terrain/shoalsse.gif").getImage();
        shoals[17] = new ImageIcon("images/terrain/shoalwsn.gif").getImage();
        shoals[18] = new ImageIcon("images/terrain/shoalhne.gif").getImage();
        shoals[19] = new ImageIcon("images/terrain/shoalhse.gif").getImage();
        shoals[20] = new ImageIcon("images/terrain/shoalhsw.gif").getImage();
        shoals[21] = new ImageIcon("images/terrain/shoalhnw.gif").getImage();
        shoals[22] = new ImageIcon("images/terrain/shoalnsw.gif").getImage();
        shoals[23] = new ImageIcon("images/terrain/shoaless.gif").getImage();
        shoals[24] = new ImageIcon("images/terrain/shoalssw.gif").getImage();
        shoals[25] = new ImageIcon("images/terrain/shoalwss.gif").getImage();
        shoals[26] = new ImageIcon("images/terrain/shoalvne.gif").getImage();
        shoals[27] = new ImageIcon("images/terrain/shoalvse.gif").getImage();
        shoals[28] = new ImageIcon("images/terrain/shoalvsw.gif").getImage();
        shoals[29] = new ImageIcon("images/terrain/shoalvnw.gif").getImage();
        shoals[30] = new ImageIcon("images/terrain/shoalnb.gif").getImage();
        shoals[31] = new ImageIcon("images/terrain/shoaleb.gif").getImage();
        shoals[32] = new ImageIcon("images/terrain/shoalsb.gif").getImage();
        shoals[33] = new ImageIcon("images/terrain/shoalwb.gif").getImage();
        shoals[34] = new ImageIcon("images/terrain/shoalbne.gif").getImage();
        shoals[35] = new ImageIcon("images/terrain/shoalbse.gif").getImage();
        shoals[36] = new ImageIcon("images/terrain/shoalbsw.gif").getImage();
        shoals[37] = new ImageIcon("images/terrain/shoalbnw.gif").getImage();
        
        //Seas
        //seas[0] = new ImageIcon("images/terrain/sea.gif").getImage();
        seas[1] = new ImageIcon("images/terrain/sean.gif").getImage();
        seas[2] = new ImageIcon("images/terrain/seae.gif").getImage();
        seas[3] = new ImageIcon("images/terrain/seas.gif").getImage();
        seas[4] = new ImageIcon("images/terrain/seaw.gif").getImage();
        seas[5] = new ImageIcon("images/terrain/seane.gif").getImage();
        seas[6] = new ImageIcon("images/terrain/sease.gif").getImage();
        seas[7] = new ImageIcon("images/terrain/seasw.gif").getImage();
        seas[8] = new ImageIcon("images/terrain/seanw.gif").getImage();
        seas[9] = new ImageIcon("images/terrain/sea3n.gif").getImage();
        seas[10] = new ImageIcon("images/terrain/sea3e.gif").getImage();
        seas[11] = new ImageIcon("images/terrain/sea3s.gif").getImage();
        seas[12] = new ImageIcon("images/terrain/sea3w.gif").getImage();
        seas[13] = new ImageIcon("images/terrain/sea4.gif").getImage();
        seas[14] = new ImageIcon("images/terrain/sea2ns.gif").getImage();
        seas[15] = new ImageIcon("images/terrain/sea2ew.gif").getImage();
        seas[16] = new ImageIcon("images/terrain/seadne.gif").getImage();
        seas[17] = new ImageIcon("images/terrain/seadse.gif").getImage();
        seas[18] = new ImageIcon("images/terrain/seadsw.gif").getImage();
        seas[19] = new ImageIcon("images/terrain/seadnw.gif").getImage();
        seas[20] = new ImageIcon("images/terrain/seadn.gif").getImage();
        seas[21] = new ImageIcon("images/terrain/seade.gif").getImage();
        seas[22] = new ImageIcon("images/terrain/seads.gif").getImage();
        seas[23] = new ImageIcon("images/terrain/seadw.gif").getImage();
        seas[24] = new ImageIcon("images/terrain/sead3ne.gif").getImage();
        seas[25] = new ImageIcon("images/terrain/sead3se.gif").getImage();
        seas[26] = new ImageIcon("images/terrain/sead3sw.gif").getImage();
        seas[27] = new ImageIcon("images/terrain/sead3nw.gif").getImage();
        seas[28] = new ImageIcon("images/terrain/sead4.gif").getImage();
        seas[29] = new ImageIcon("images/terrain/sead2-1.gif").getImage();
        seas[30] = new ImageIcon("images/terrain/sead2-2.gif").getImage();
        
        //Pipes
        //pipes[0] = new ImageIcon("images/terrain/pipeh.gif").getImage();
        pipes[1] = new ImageIcon("images/terrain/pipev.gif").getImage();
        pipes[2] = new ImageIcon("images/terrain/pipene.gif").getImage();
        pipes[3] = new ImageIcon("images/terrain/pipese.gif").getImage();
        pipes[4] = new ImageIcon("images/terrain/pipesw.gif").getImage();
        pipes[5] = new ImageIcon("images/terrain/pipenw.gif").getImage();
        pipes[6] = new ImageIcon("images/terrain/pipetn.gif").getImage();
        pipes[7] = new ImageIcon("images/terrain/pipete.gif").getImage();
        pipes[8] = new ImageIcon("images/terrain/pipets.gif").getImage();
        pipes[9] = new ImageIcon("images/terrain/pipetw.gif").getImage();
        pipes[10] = new ImageIcon("images/terrain/pipenesw.gif").getImage();
        pipes[11] = new ImageIcon("images/terrain/pipeendn.gif").getImage();
        pipes[12] = new ImageIcon("images/terrain/pipeende.gif").getImage();
        pipes[13] = new ImageIcon("images/terrain/pipeends.gif").getImage();
        pipes[14] = new ImageIcon("images/terrain/pipeendw.gif").getImage();
        
        //Pipe Seams
        pipeSeams[0] = new ImageIcon("images/terrain/pipeseamh.gif").getImage();
        pipeSeams[1] = new ImageIcon("images/terrain/pipeseamv.gif").getImage();
        destroyedPipeSeams[0] = new ImageIcon("images/terrain/destroyedpsh.gif").getImage();
        destroyedPipeSeams[1] = new ImageIcon("images/terrain/destroyedpsv.gif").getImage();
        */

    }
    
    public static Image getTerrainImage(int index, int armyColor){
        if(index == 9){
            if(armyColor != 0)
                //return hq[armyColor-1][armyColor-1];
                return ownedUrban[armyColor];
            /*else
                return sprites[0];*/
        /*}else if(index == 16){ //Silo
            return properties[16][0];
        }else if((index > 9 && index < 15) || index == 17){
            return properties[index][armyColor];*/
        }
        return terrain;
        //TEMP
        /*if(sprites[index] == null){
            return sprites[0];
        }
        return sprites[index];*/
    }
    
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
    public static Image getTerrainSpriteSheet(){
        return terrain;
    }
    public static Image getUrbanSpriteSheet(int i) {
        return ownedUrban[i];
    }
    
    public static Image getHQSpriteSheet(int i){
        return ownedHQ[i];
    }
}
