package cwsource;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.*;
import java.util.ArrayList;

public class DialogueBox extends Animation{
    String animString; //The string to be drawn
    int interval; //The interval over which it is drawn
    int size; //the fontsize of the string.
    int wrap; //how many characters before the string is wrapped.
    String substring; //if the string is to be split up, this holds the current substring.
    Color fill;
    int[] cPosition; //Holds positions of commas and semi-colons.
    int[] pPosition; //Holds positions of periods and colons.
    Battle b;
    ArrayList<Animation> linkedAnimation;
	final static Logger logger = LoggerFactory.getLogger(DialogueBox.class); 
    boolean linked = false;
    //public DialogueBox(Battle b, int layer, Color color, int height, int length, int appear, String contain, int startx, int starty, int endx, int endy, int sAlpha, int eAlpha ) {
    public DialogueBox(Battle b, String contain) {
        super();
        this.b = b;
        animString = contain;
        substring = "";
        wrap = 130; //130 characters good?
        linkedAnimation = new ArrayList<Animation>();
        x = 76;
        y = 284;
    }
    
    public void draw(Graphics2D g, BattleScreen bs) {
        int i = 0;
        int shift = 0;
        g.drawImage(MiscGraphics.getDialogueBox(b.getTurn()),0,270,480,50,bs);
        g.setColor(Color.BLACK);
        if(substring.length() <= wrap) {
            g.drawString(substring,x,y+size);
        } else { //Time to parse. ;_;
            for(i = 0; i<(substring.length()+(wrap-1)-shift)/wrap - 1; i++) { // Goes through all remaining strings
                int s = 0;
                if(i+1<(substring.length()+(wrap-1)-shift)/wrap - 1){
                    //If this is not the last line to be scanned.
                    for(s = 1; s<substring.substring((i+1)*wrap-shift, (i+2)*wrap-shift).length()+1;s++) {
                        //scan backwards from the end until you hit a white space
                        if(Character.isWhitespace(substring.substring((i+1)*wrap-shift, (i+2)*wrap).charAt(s)-shift))
                            break;
                    }
                    //s now stores the last whitespace found.
                    String sub2string = substring.substring((i+1)*wrap-shift, (i+2)*wrap-s-shift);
                    g.drawString(sub2string, x, y+i*size+2+size);
                    shift+=s; //Shift the substring over
                } else {
                    //if this is the last line to be scanned.
                    String sub2string = substring.substring((i+1)*wrap-shift);
                    g.drawString(sub2string, x, y+i*size+2+size);
                }
            }
        }
    }
    
    public void linkTo(Animation animation) {
        linkedAnimation.add(animation);
        linked = true;
    }
    
    public void linkTo(ArrayList<Animation> animations)
    {
        linkedAnimation.addAll(animations);
        linked = true;
    }
    //Begins the preliminary stage.
    public void setup(){
        lockpos = true; //This thing is always in the same area
        Listener animationTerminator = new Listener();
        timer = new Timer(20, animationTerminator);
        timer.setInitialDelay(delay*20 + 20);
        timer.setCoalesce(false);
        b.diagQueue.add(this);
    }
    //Only Battle.java will start, that's where this is governed.
    public void start() {
        b.animlock = true; //No other actions, please.
        timer.addActionListener(new unlockListener());
        timer.start();
    }
    //This is where all the 'end of' animation stuff occurrs.
    public void pressedA(){
        //if the text hasn't finished scanning, finish scanning'
        if(tick<animString.length()*3) {
            tick =animString.length()*3;
        } else { //if the text has finished scanning
            //if it is a independent animation, unlock the battlescreen 
            b.diagQueue.remove(0);
            if(!b.diagQueue.isEmpty()) 
            {
                //If not empty, pass on linked animations and start the next one.
                b.diagQueue.get(0).linkTo(linkedAnimation);
                b.diagQueue.get(0).start();
            }
            else {
                //Start all linked animations
                b.animlock = false;
                logger.info("Unlock");
                for(int i = 0; i<linkedAnimation.size();i++) {
                    linkedAnimation.get(i).start();
                }
                //and, if there are no linked animations, unlock this mofo.
            }
            //remove this block, and start the next box if there is one.
            
        }
        
    }
    //Only governs the scanning of the text
    private class Listener implements ActionListener{
        public Listener() {}
        public void actionPerformed(ActionEvent evt) {
            tick++;
            //No interval for text. It's set in stone  at 3 frames per character
            if(tick/3<animString.length())
                substring = animString.substring(0,tick/3);
            else
                substring = animString;
        }
    }
    
    private class unlockListener implements ActionListener{
        int time;
        public unlockListener() {
            time = (int)duration; //How long does it take for this animation to end?
        }
        public void actionPerformed(ActionEvent evt){
            //This piggybacks off the normal listener
            if(tick>=duration && !linked) {
                
            }
        }
    }
}