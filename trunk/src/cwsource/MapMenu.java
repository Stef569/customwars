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

public class MapMenu extends InGameMenu{
    
    //constructor
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
                System.out.println("Scale Screen");
                return 1;
            case 1:
                System.out.println("Fill Map");
                return 2;
            case 2:
                System.out.println("Save");
                return 3;
            case 3:
                System.out.println("Load");
                return 4;
            case 4:
                System.out.println("Set Map Info");
                return 9;
            case 5:
                System.out.println("Map Size");
                return 5;
            case 6:
                System.out.println("Map Resize");
                return 7;
            case 7:
                System.out.println("Resize");
                return 6;
            case 8:
                System.out.println("Mirror Map");
                return 11;
            case 9:
                System.out.println("Random Map");
                return 10;
            case 10:
                System.out.println("Return to Main");
                return 8;
        }
        return 0;
    }
}
