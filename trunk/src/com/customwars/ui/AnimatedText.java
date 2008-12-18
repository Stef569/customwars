package com.customwars.ui;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

import com.customwars.Battle;

import java.awt.image.*;

public class AnimatedText extends Animation{
    String animString; //The string to be drawn
    int interval; //The interval over which it is drawn
    int size; //the fontsize of the string.
    int wrap; //how many characters before the string is wrapped.
    String substring; //if the string is to be split up, this holds the current substring.
    Color fill;
    public AnimatedText(Battle b, int layer, Color color, int height, int length, int appear, String contain, int startx, int starty, int endx, int endy, int sAlpha, int eAlpha, int duration, int delay, double ease) {
        super(b,null,layer,0,0,startx,starty,endx,endy,sAlpha,eAlpha,duration,delay,ease);
        animString = contain;
        interval = appear;
        size = height;
        wrap = length;
        fill = color;
        substring = "";
    }
    
    public void draw(Graphics2D g, BattleScreen bs) {
        int i = 0;
        int shift = 0;
        if(lockpos) {
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, a/100.0f));
            g.setColor(fill);
            if(substring.length() <= wrap)
            {
                g.drawString(substring,x,y+size);
            }
            else { //Time to parse. ;_;
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
        } else {
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, a/355.0f));
            g.setColor(fill);
            if(substring.length() <= wrap)
                g.drawString(substring,x-bs.getSX(),y-bs.getY()+size);
            else { //Time to parse. ;_;
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
                        g.drawString(sub2string, x-bs.getSX(), y+i*size+2-bs.getY()+size);
                        shift+=s; //Shift the substring over
                    } else {
                        //if this is the last line to be scanned.
                        String sub2string = substring.substring((i+1)*wrap-shift);
                        g.drawString(sub2string, x-bs.getSX(), y+i*size+2-bs.getY()+size);
                    }
                }
            }
        }
    }
    //Starts the animation timers
    public void setup(boolean lock){
        lockpos = lock; 
        Listener animationTerminator = new Listener(this);
        timer = new Timer(20, animationTerminator);
        timer.setInitialDelay(delay*20 + 20);
        timer.setCoalesce(false);
        b.queue.add(this);
    }
    public void start() {
        timer.start();
    }
    private class Listener implements ActionListener{
        Animation animation;
        public Listener(Animation a) {
            animation = a;
            tick = 0;
            store = 0;
            if(ease != 0) //If the ease is a nonzero value, handle ease calculations
            {
                for(int i = 0; i<duration; i++) //Change to i<= duration if there are problems.
                {
                    store+= i;
                }
            }
        }
        public void actionPerformed(ActionEvent evt) {
            tick++;
            if(trigger == false) {
                if(layer == 1)
                    b.getLayerOne().add(animation);
                else if(layer == 2)
                    b.getLayerTwo().add(animation);
                else if(layer == 3)
                    b.getLayerThree().add(animation);
                else if(layer == 4)
                    b.getLayerFour().add(animation);
                b.queue.remove(this);
                trigger = true;
            }
            //Move it!
            if(ease == 0 && tick!=1) //No easeing required
            {
                a+= (ea-sa)/duration;
                y+= (ey-sy)/duration;
                x+= (ex-sx)/duration;
            } else if(ease > 0) {
                a += ((tick/store) * (ea-sa) * ease + (ea-sa)/duration)/(ease+1.0);
                y += ((tick/store) * (ey-sy) * ease + (ey-sy)/duration)/(ease+1.0);
                x += ((tick/store) * (ex-sx) * ease + (ex-sx)/duration)/(ease+1.0);
            } else if(ease < 0) {
                a += (((duration-tick)/store) * (ea-sa) * ease + (ea-sa)/duration)/(ease+1.0);
                y += (((duration-tick)/store) * (ey-sy) * ease + (ey-sy)/duration)/(ease+1.0);
                x += (((duration-tick)/store) * (ex-sx) * ease + (ex-sx)/duration)/(ease+1.0);
            }
            if(a>100)
                a = 100;
            if(a<0)
                a = 0;

            if(interval!=-1)
            {
            if(tick<interval)
                substring = animString.substring(0,(int)(tick*(animString.length()/(interval*1.0))));
            else
                substring = animString;
            }
            else
            {
            //here! 3 frames per character
            if(tick/3<animString.length())
                substring = animString.substring(0,tick/3);
            else
                substring = animString;
             }
            //Too high...delete T_T
            if(tick>duration) {
                    
                if(layer == 1)
                    b.getLayerOne().remove(animation);
                else if(layer == 2)
                    b.getLayerTwo().remove(animation);
                else if(layer == 3)
                    b.getLayerThree().remove(animation);
                else if(layer == 4)
                    b.getLayerFour().remove(animation);
                timer.setRepeats(false); //Timer, you're no longer needed.
            }
            
        }
    }
    
}