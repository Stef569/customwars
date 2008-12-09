/*
 *
 *
 *
 * 	CUSTOMWARS GAME SERVER PERL OUTPUT PARSER
 *
 *
 *  Admittedly, its fucking ugly. But as far as i can tell, its debugged and it works :)
 *
 *
 *
 *
 */

package com.customwars.lobbyclient;


import java.util.LinkedList;
import java.net.*;

import com.customwars.Options;

public class parser {
    boolean debug = true;
    public LinkedList opengames;
    public LinkedList passwordgames;
    public LinkedList fullgames;
    
    
    EasyReader reader = new EasyReader("gamelist.dat");
    public parser() {
        opengames = new LinkedList();
        passwordgames = new LinkedList();
        fullgames = new LinkedList();
        
        char nextchar = '!';
        int index = 0;
        while(index < 35 && !reader.eof()) {
            nextchar = reader.readChar();
            if(nextchar == '<') {
                getarg();
                index++;
                //System.out.println(getarg());
            }
        }
        boolean lastgame = false;
        boolean previousborked = false;
        
        
        nextchar = reader.readChar();
        nextchar = reader.readChar();
        nextchar = reader.readChar();
        System.out.println();
        
        
        if(nextchar != '2') {
            if(debug){System.out.println("***************  OPEN  GAMES  ***************");}
            getarg(2);
            //nextchar = reader.readChar();
            while(!lastgame) {
                Game newGame = readopengame(nextchar);
                
                if(newGame.version.equals(Options.version))
                    opengames.add(newGame);
                getarg(2); //wipe first two <>
                System.out.println();
                nextchar = reader.readChar();
                nextchar = reader.readChar();
                if(nextchar != 'T') {
                    lastgame = true;
                } else {
                    getarg(3);
                }
            }
        } else {
            previousborked = true;
        }
        getarg(23);
        nextchar = reader.readChar();
        nextchar = reader.readChar();
        nextchar = reader.readChar();
        
        if(nextchar != '2') {
            
            if(debug){System.out.println("*************** PRIVATE GAMES ***************");}
            getarg(1);
            //OKAY. Here the cursor is at the first beginning of the name
            if(!previousborked) {
                getarg(1);
                previousborked = false;
            } else {
                previousborked = false;
            }
            //nextchar = reader.readChar();
            lastgame = false;
            while(!lastgame) {
                Game newGame = readopengame(nextchar);
                if(newGame.version.equals(Options.version))
                    passwordgames.add(newGame);
                getarg(2);
                System.out.println();
                nextchar = reader.readChar();
                nextchar = reader.readChar();
                if(nextchar != 'T') {
                    lastgame = true;
                } else {
                    getarg(3);
                }
            }
            
        } else {
            previousborked = true;
        }
        if(debug){System.out.println("***************  FULL  GAMES  ***************");}
        getarg(23);
        nextchar = reader.readChar();
        nextchar = reader.readChar();
        nextchar = reader.readChar();
        //System.out.print("" + nextchar);
        if(nextchar != '2') {
            getarg(1);
            if(!previousborked) {
                getarg(1);
                previousborked = false;
            } else {
                previousborked = false;
            }
            lastgame = false;
            while(!lastgame) {
                Game newGame = readopengame(nextchar);
                if(newGame.version .equals( Options.version))
                    fullgames.add(newGame);
                getarg(2);
                System.out.println();
                nextchar = reader.readChar();
                nextchar = reader.readChar();
                if(nextchar != 'T') {
                    lastgame = true;
                } else {
                    getarg(3);
                }
            }
        }
    }
    public Game readopengame(char nextchar) {
        Game newgame = new Game();
        
        boolean borked = false;
        String gamename = "";
        nextchar = reader.readChar();
        while(nextchar != '<') {
            if(!borked) {
                newgame.gamename = newgame.gamename + nextchar;
            } else {
                borked = false;
            }
            nextchar = reader.readChar();
            if(nextchar == '<') {
                nextchar = reader.readChar();
                nextchar = reader.readChar();
                if(nextchar == 'f' || nextchar == 'o') {
                    getarg(1);
                    borked = true;
                } else {
                    nextchar = '<';
                }
            }
            
        }
        //System.out.println("Game Name: " + newgame.gamename);
        //borked = false;
        getarg(2);
        
        
        int numplayers;
        String numplayers_s = "";
        nextchar = reader.readChar();
        while(nextchar != '/' && !reader.eof()) {
            numplayers_s = numplayers_s + nextchar;
            nextchar = reader.readChar();
        }
        if(!numplayers_s.isEmpty())
            newgame.numplayers = Integer.parseInt(numplayers_s);
        else
            borked = true;
        int maxplayers;
        String maxplayers_s = "";
        nextchar = reader.readChar();
        while(nextchar != '<' && !reader.eof()) {
            maxplayers_s = maxplayers_s + nextchar;
            nextchar = reader.readChar();
        }
        if(!maxplayers_s.isEmpty())
            newgame.maxplayers = Integer.parseInt(maxplayers_s);
        else
            borked = true;
        //System.out.println("Players: " + numplayers + "/" + maxplayers);
        
        if(!borked) {
            getarg(2);
            
            int turnnumber;
            String turnnumber_s = "";
            nextchar = reader.readChar();
            while(nextchar != '/' && !reader.eof()) {
                turnnumber_s = turnnumber_s + nextchar;
                nextchar = reader.readChar();
            }
            newgame.turnnumber = Integer.parseInt(turnnumber_s);
            
            int maxturns;
            String maxturns_s = "";
            nextchar = reader.readChar();
            System.out.println("" + (int)nextchar + " - " + nextchar);
            while(nextchar != 10 && !reader.eof()) {
                maxturns_s = maxturns_s + nextchar;
                nextchar = reader.readChar();
                
            }
            newgame.maxturns = Integer.parseInt(maxturns_s);
            //System.out.println("Turn: " + turnnumber + "/" + maxturns);
            
            getarg(2);
            
            String mapname = "";
            nextchar = reader.readChar();
            while(nextchar != '<') {
                newgame.mapname = newgame.mapname + nextchar;
                nextchar = reader.readChar();
            }
            //System.out.println("Map Name: " + mapname);
            
            getarg(2);
            
            String version = "";
            nextchar = reader.readChar();
            while(nextchar != '<') {
                newgame.version = newgame.version + nextchar;
                nextchar = reader.readChar();
            }
            //System.out.println("Version: " + version);
            
            getarg(2);
            
            LinkedList playerslist = new LinkedList();
            nextchar = reader.readChar();
            if(nextchar == '<') {
                //getarg(2);
                nextchar = reader.readChar();
                nextchar = reader.readChar();
                if(nextchar == 'f' || nextchar == 'o') {
                    getarg(1);
                    borked = true;
                } else {
                    nextchar = '<';
                }
            }
            //nextchar = reader.readChar();
            boolean firsttime = true;
            while(nextchar != '<' || firsttime) {
                firsttime = false;
                String playername = "";
                
                if(nextchar == '<') {
                    nextchar = reader.readChar();
                    System.out.print("" + nextchar);
                    if(nextchar == 'f' || nextchar == 'o') {
                        getarg(1);
                        borked = true;
                    } else {
                        nextchar = '<';
                    }
                }
                //nextchar = reader.readChar();
                
                while(nextchar != ',' && nextchar != '<') {
                    if(!borked) {
                        playername = playername + nextchar;
                    } else {
                        borked = false;
                    }
                    nextchar = reader.readChar();
                }
                if(playername != "") {
                    playerslist.add(playername);
                }
                borked = false;
                
                if(nextchar != '<') {
                    nextchar = reader.readChar();
                    nextchar = reader.readChar();
                    if(nextchar == '<') {
                        nextchar = reader.readChar();
                        nextchar = reader.readChar();
                        if(nextchar == 'f' || nextchar == 'o') {
                            
                            getarg(1);
                            borked = true;
                        } else {
                            nextchar = '<';
                        }
                    }
                }
                
                else {
                    
                    nextchar = reader.readChar();
                    nextchar = reader.readChar();
                    if(nextchar == 'f' || nextchar == 'o') {
                        
                        getarg(1);
                        borked = true;
                    } else {
                        nextchar = '<';
                    }
                }
                
            }
                /*
                System.out.print("Players: ");
                for(int i = 0; i < playerslist.size(); i++)
                {
                        System.out.print(playerslist.get(i) + " ");
                }
                System.out.println();
                 */
            newgame.playerlist = playerslist;
            getarg(2);
            
            String lastaction = "";
            nextchar = reader.readChar();
            while(nextchar != '<') {
                newgame.lastaction = newgame.lastaction + nextchar;
                nextchar = reader.readChar();
            }
            //System.out.println("Last Action: " + lastaction);
            
            getarg(2); //<TD></TD>
            
            String comment = "";
            nextchar = reader.readChar(); // > is stripped
            while(nextchar != '<') {
                newgame.comment = newgame.comment + nextchar;
                nextchar = reader.readChar();
            }
            //System.out.println("Comment: " + comment);
            if(debug) {
                newgame.printgame();
            }
            return newgame;
        }else{
            //In theory, a broken game = me advancing the reader until the next game.
            getarg(2);
            nextchar = reader.readChar();
            while(nextchar != '/' && !reader.eof()) {
                nextchar = reader.readChar();
            }
            nextchar = reader.readChar();
            while(nextchar != 10 && !reader.eof()) {
                nextchar = reader.readChar();
                
            }
            getarg(2);
            nextchar = reader.readChar();
            while(nextchar != '<') {
                nextchar = reader.readChar();
            }
            getarg(2);
            nextchar = reader.readChar();
            while(nextchar != '<') {
                nextchar = reader.readChar();
            }
            getarg(2);
            nextchar = reader.readChar();
            if(nextchar == '<') {
                nextchar = reader.readChar();
                nextchar = reader.readChar();
                if(nextchar == 'f' || nextchar == 'o') {
                    getarg(1);
                    borked = true;
                } else {
                    nextchar = '<';
                }
            }
            while(nextchar != '<') {
                if(nextchar == '<') {
                    nextchar = reader.readChar();
                    if(nextchar == 'f' || nextchar == 'o') {
                        getarg(1);
                        borked = true;
                    } else {
                        nextchar = '<';
                    }
                }
                //nextchar = reader.readChar();
                
                while(nextchar != ',' && nextchar != '<') {
                    nextchar = reader.readChar();
                }
                
                if(nextchar != '<') {
                    nextchar = reader.readChar();
                    nextchar = reader.readChar();
                    if(nextchar == '<') {
                        nextchar = reader.readChar();
                        nextchar = reader.readChar();
                        if(nextchar == 'f' || nextchar == 'o') {
                            
                            getarg(1);
                        } else {
                            nextchar = '<';
                        }
                    }
                }
                
                else {
                    
                    nextchar = reader.readChar();
                    nextchar = reader.readChar();
                    if(nextchar == 'f' || nextchar == 'o') {
                        
                        getarg(1);
                        borked = true;
                    } else {
                        nextchar = '<';
                    }
                }
                
            }
            
            getarg(2);
            nextchar = reader.readChar();
            while(nextchar != '<') {
                nextchar = reader.readChar();
            }
            //System.out.println("Last Action: " + lastaction);
            
            getarg(2); //<TD></TD>
            nextchar = reader.readChar(); // > is stripped
            while(nextchar != '<') {
                nextchar = reader.readChar();
            }
        }
        return newgame;
    }
    public String getarg(int repeat) {
        String arg = "";
        for(int i = 0; i < repeat; i++) {
            
            char nextchar = reader.readChar();
            while(nextchar != '>') {
                arg = arg + nextchar;
                nextchar = reader.readChar();
            }
        }
        return arg;
    }
    public String getarg() {
        String arg = "";
        char nextchar = reader.readChar();
        while(nextchar != '>') {
            arg = arg + nextchar;
            nextchar = reader.readChar();
        }
        return arg;
    }
    
}
