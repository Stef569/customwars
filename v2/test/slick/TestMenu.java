package test.slick;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Sound;
import org.newdawn.slick.command.Command;
import org.newdawn.slick.command.InputProviderListener;
import client.ui.CWInput;

/**
 * Shows a menu on the screen
 */

public class TestMenu{
    
    private String[] options;
    private Color baseColor;
    private Color selectColor;
    private int locx;
    private int locy;
    private int mousex;
    private int mousey;
    private Image cursor;
    private int curoptn;
    private Sound menuSound;
      
    TestMenu(int noOfOptions){
      locx = 0;
      locy = 0;
      mousex = -1;
      mousey = -1;
      curoptn = 0;
      cursor = null;
      menuSound = null;
      options = new String[noOfOptions];
      baseColor = new Color(Color.white);
      selectColor = new Color(Color.black);
    }

    public int getOption(){
      return curoptn;
    }
    
    public void setLocation(int x, int y){
      locx = x;
      locy = y;
    }
    
    public void setImage(Image theImage){
      cursor = theImage;
    }
    
    public void setSound(Sound theSound){
       if(theSound != null)
        menuSound = theSound; 
    }
    
    public void setColor(Color base, Color selected){
      baseColor = base;
      selectColor = selected;
    }
    
    public void setSize(int noOfOptions){
      String[] temp = options;
      options = new String[noOfOptions];
      for(int i = 0; i < options.length; i++){
        options[i] = "";
        if(i < temp.length)
           options[i] = temp[i];
      }
    }
    
    public void setOptionName(String name) {
      for(int i = 0; i < options.length; i++){
        if(name != null){
           if(options[i] == null || options[i].matches("")){
             options[i] = name;
             return;
           }
        }
      }
    }
    
    public void setOptionName(int index, String name){
      if(index >= 0 && index < options.length){
        if(name != null)
          options[index] = name;
      }
    }
    
    public void moveUp(){
      if(curoptn > 0)
        curoptn--;
    }
    
    public void moveDown(){
      if(curoptn+1 < options.length)
        curoptn++;
    }

    public void render(Graphics g){
      //Deals with the mouse...
      for(int i = 0; i < options.length; i++){
        if(cursor == null){
          if(mousex >= locx && mousex <= locx+g.getFont().getWidth(options[i])){
            if(mousey >= locy+(i*20) &&
                   mousey <= locy+(i*20)+g.getFont().getHeight(options[i])){
               if(curoptn != i){
                 curoptn = i;
                 if(menuSound != null)
                   menuSound.play();
               }
             }
          }
        }else{
           if(mousex >= locx && mousex <= 
                   cursor.getWidth()+5+locx+g.getFont().getWidth(options[i])){
            if(mousey >= locy+(i*20) &&
                   mousey <= locy+(i*20)+g.getFont().getHeight(options[i])){
               if(curoptn != i){
                 curoptn = i;
                 if(menuSound != null)
                   menuSound.play();
               }
             }
          } 
        }
      }
      
      for(int i = 0; i < options.length; i++){
        if(curoptn == i)
          g.setColor(selectColor);
        else
          g.setColor(baseColor);
            
        if(cursor == null)
          g.drawString(options[i], locx, locy+(i*20));
        else
          g.drawString(options[i], cursor.getWidth()+5+locx, locy+(i*20));
      }
        
      if(cursor != null)
        g.drawImage(cursor, locx, (locy+(curoptn*20)));
        
      g.setColor(baseColor);
    }

    public void controlPressed(Command command, CWInput cwInput) {
      if (cwInput.isNextMenuItemPressed(command)) {
        moveDown();
        if(menuSound != null)
           menuSound.play();
      } else if (cwInput.isPreviousMenuItemPressed(command)) {
        moveUp();
        if(menuSound != null)
           menuSound.play();
      }
    }
    
    public void mouseMoved(int mousex, int mousey){
        this.mousex = mousex;
        this.mousey = mousey;
    }
}
