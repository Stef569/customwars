package cwsource;
/*
 *MapMenu.java
 *Author: Urusan
 *Contributors:
 *Creation: July 19, 2006, 3:28 PM
 *The main menu in the map editor
 */

import java.awt.*;
import java.awt.image.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MapMenu extends InGameMenu{
    
    //constructor
	final static Logger logger = LoggerFactory.getLogger(MapMenu.class);  
    public MapMenu(ImageObserver screen) {
        //super((256-96)/2,(192-80)/2);
        //super((480-96)/2,(320-112)/2,96,screen);
        //super((480-112)/2,(320-112)/2,96,screen);
        super((480-128)/2,(320-112)/2,96,screen);
        String[] n = {"Scale","Fill","Save","Load","Set Map Info","Map Size","Map Resize","Resize Screen","Mirror Map","Random Map","Return to Main"};
        super.loadStrings(n);
    }
    
    public int doMenuItem(){
        switch(item){
            case 0:
                logger.info("Scale Screen");
                return 1;
            case 1:
                logger.info("Fill Map");
                return 2;
            case 2:
                logger.info("Save");
                return 3;
            case 3:
                logger.info("Load");
                return 4;
            case 4:
                logger.info("Set Map Info");
                return 9;
            case 5:
                logger.info("Map Size");
                return 5;
            case 6:
                logger.info("Map Resize");
                return 7;
            case 7:
                logger.info("Resize");
                return 6;
            case 8:
                logger.info("Mirror Map");
                return 11;
            case 9:
                logger.info("Random Map");
                return 10;
            case 10:
                logger.info("Return to Main");
                return 8;
        }
        return 0;
    }
}
