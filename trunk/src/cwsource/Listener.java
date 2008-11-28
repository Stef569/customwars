package cwsource;
/*
 * Listener.java
 *
 * Created on August 12, 2006, 4:42 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/**
 *
 * @author Adam Dziuk
 */

import java.io.*;
import java.net.*;
public class Listener implements Runnable{
    static ServerSocket ss;
    static  Socket s ;
    static InputStream is;
    static Writer out;
    
    /** Creates a new instance of Listener */
    public Listener() throws Exception{
        
        ss = new ServerSocket(Options.getPort());
        
        
    }
    
    
    public void run(){
        try{
            while (true) {
                s = ss.accept();
                if(s != null){
                    is = s.getInputStream();
                    Mission.recieveMission(is);
                }
            }
        } catch(Exception e){
            System.out.println(e);
        }
    }
    
    public void turnOffThread(){
        try{
            ss.close();
        } catch(Exception e){
            System.out.println(e);
        }
    }
    
}
