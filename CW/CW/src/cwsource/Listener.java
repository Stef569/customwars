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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class Listener implements Runnable{
    static ServerSocket ss;
    static  Socket s ;
    static InputStream is;
    static Writer out;
	final static Logger logger = LoggerFactory.getLogger(Listener.class); 
    
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
            logger.error("error",e);
        }
    }
    
    public void turnOffThread(){
        try{
            ss.close();
        } catch(Exception e){
        	logger.error("error",e);
        }
    }
    
}
