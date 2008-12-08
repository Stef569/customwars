/*
 * SFX.java
 *
 * Created on October 20, 2007, 8:25 PM
 * Author: Urusan
 * Makes sound effects easy!
 */

package cwsource;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import java.io.File;
import java.io.IOException;

public class SFX {
    private static boolean mute = false;
   
    //Play a short clip from a file
    public static void playClip(String filename){
        if(!mute){
            Clip c = openClip(filename);
            if(c!=null){
                if ( c.isRunning()){
                	 c.stop();   // Stop the player if it is still running
                }c.setFramePosition(0); // rewind to the beginning
                c.start();     // Start playing

            }
        }
    }
   
    //Play a short clip from a file a given number of times
    public static void playClip(String filename, int times){
        if(!mute){
            Clip c = openClip(filename);
            if(c!=null){
                c.loop(times-1);
                c.close();
            }
        }
    }
   
    //is mute on or off?
    public static boolean getMute(){
        return mute;
    }
   
    //set mute
    public static void setMute(boolean m){
        mute = m;
    }
   
    //toggle mute
    public static void toggleMute(){
        if(mute)mute = false;
        else mute = true;
        Options.saveOptions();
    }
   
    //prepares the clip for playback, used internally
    private static Clip openClip(String filename){
        Clip clip = null;
        try {
            // From file
            AudioInputStream stream = AudioSystem.getAudioInputStream(new File(filename));
            clip = AudioSystem.getClip();
            // Open audio clip and load samples from the audio input stream.
            clip.open(stream);

        } catch (IOException e) {
            System.err.println("Error: Could not read file " + filename);
            return null;
        } catch (LineUnavailableException e) {
            System.err.println("Error: Line unavailable");
            return null;
        } catch (UnsupportedAudioFileException e) {
            System.err.println("Error: " + filename + " not an audio file");
            return null;
        }

        return clip;
    }
   
    //EXAMPLES! REMOVE FROM FINAL VERSION
    //private static float volume = 100f;
    //Set Controls
    //((BooleanControl)clip.getControl(BooleanControl.Type.MUTE)).setValue(mute);
    //((FloatControl)clip.getControl(FloatControl.Type.VOLUME)).setValue(volume);
    /*Control[] ctrls = clip.getControls();
    System.out.println(""+ctrls.length);
    for(int i = 0; i < ctrls.length; i++)System.out.println(""+ctrls[i].getType());
    ((FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN)).setValue(volume);*/
}

class Ender implements LineListener{
    Clip target;
   
    Ender(Clip c){
        target = c;
    }
   
    public void update(LineEvent e){
        if(e.getType()==LineEvent.Type.STOP){
            target.close();
        }
    }
}