package test.slick;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Sound;
import org.newdawn.slick.Input;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

/**
 * Shows a menu on the screen
 */

public class TestMenu extends BasicGameState{
    
    private String[] options;
    private Color baseColor;
    private Color selectColor;
    private int locx;
    private int locy;
    private Image cursor;
    private int curoptn;
    
    TestMenu(int noOfOptions){
        locx = 0;
        locy = 0;
        curoptn = 0;
        cursor = null;
        options = new String[noOfOptions];
        baseColor = new Color(Color.white);
        selectColor = new Color(Color.black);
    }
    
    public void changeLocation(int x, int y){
        locx = x;
        locy = y;
    }
    
    public void changeImage(Image theImage){
        cursor = theImage;
    }
    
    public void changeColor(Color base, Color selected){
        baseColor = base;
        selectColor = selected;
    }
    
    public void numberOfOptions(int noOfOptions){
        String[] temp = options;
        options = new String[noOfOptions];
        for(int i = 0; i < options.length; i++){
            options[i] = "";
            if(i < temp.length)
                options[i] = temp[i];
        }
    }
    
    public void optionName(int index, String name){
        if(index >= 0 && index < options.length){
            if(name != null)
                options[index] = name;
        }
    }
    
    public void selectUp(){
        if(curoptn > 0)
            curoptn--;
    }
    
    public void selectDown(){
        if(curoptn+1 < options.length)
            curoptn++;
    }
    
    public void init(GameContainer gameContainer, StateBasedGame stateBasedGame) throws SlickException {
    }

    public void render(GameContainer gameContainer, StateBasedGame stateBasedGame, Graphics g) throws SlickException {
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

    public void update(GameContainer gameContainer, StateBasedGame stateBasedGame, int i) throws SlickException {
    }
    
    public int getID() {
        return 0;
    }
    
}
