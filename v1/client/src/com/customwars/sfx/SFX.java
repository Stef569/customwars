/*
 * SFX.java
 *
 * Created on October 20, 2007, 8:25 PM
 * Author: Urusan
 * Makes sound effects easy!
 */

package com.customwars.sfx;

import com.customwars.ai.Options;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

public class SFX {
    private static String soundLocation = "";

    public static void playSound(String filename) {
        playClip(soundLocation + "/" + filename);
    }

    //Play a short clip from a file
    public static void playClip(String fullPath) {
        if (!Options.isSFXOn()) {
            Clip c = openClip(fullPath);
            if (c != null) {
                if (c.isRunning()) {
                    c.stop();   // Stop the player if it is still running
                }
                c.setFramePosition(0); // rewind to the beginning
                c.start();     // Start playing

            }
        }
    }

    //Play a short clip from a file a given number of times
    public static void playClip(String filename, int times) {
        if (!Options.isSFXOn()) {
            Clip c = openClip(filename);
            if (c != null) {
                c.loop(times - 1);
                c.close();
            }
        }
    }

    //prepares the clip for playback, used internally
    private static Clip openClip(String filename) {
        Clip clip;
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

    public static void setSoundLocation(String soundLocation) {
        SFX.soundLocation = soundLocation;
    }
}

class Ender implements LineListener {
    Clip target;

    Ender(Clip c) {
        target = c;
    }

    public void update(LineEvent e) {
        if (e.getType() == LineEvent.Type.STOP) {
            target.close();
        }
    }
}