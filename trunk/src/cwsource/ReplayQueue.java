package cwsource;
/*
 *ReplayQueue.java
 *Author: Uru
 *Contributors:
 *Creation: January 8, 2007, 3:53 AM
 *A queue containing CWEvents for replays
 */

import java.io.*;
import java.util.LinkedList;

public class ReplayQueue implements Serializable{
    
 public LinkedList<CWEvent> list;
    
    //constructor
    public ReplayQueue(){
        list = new LinkedList<CWEvent>();
    }
    
    //is the list empty?
    public boolean isEmpty(){
        return list.isEmpty();
    }
    
    //insert the an object at the beginning of the list
    public void insertFirst(CWEvent o){
        list.addFirst(o);
    }
    
    //insert the an object at the end of the list
    public void insertLast(CWEvent o){
        list.addLast(o);
    }
    
    //delete the first link, returns false if the item was not deleted
    public boolean deleteFirst(){
        
        return list.removeFirst() == null;
    }
    
    //deletes a given object from the list
    public boolean deleteItem(CWEvent o){
        return list.remove(o);
    }
    
    /*//deletes the first equivalent object from the list
    public boolean deleteEqualItem(CWEvent o){
        if(isEmpty())return false;
        CWEventLink current = first;
        CWEventLink previous = null;
        
        while(current!=null){
            if(current.data.equals(o)){
                if(previous == null){
                    first = current.next;
                }else{
                    previous.next = current.next;
                }
                return true;
            }
            previous = current;
            current = current.next;
        }
        return false;
    }*/
    
    //gets the size of the list
    public int getSize(){
        return list.size();
    }
    
    //returns an array with all the objects in the list
    public CWEvent[] getItems(){
        CWEvent[] u = new CWEvent[list.size()];
        for(int i = 0; i < list.size(); i++)
            u[i] = (CWEvent)list.get(i);
        return u;
    }
    
    //converts the list to a string for debugging
    public String toString(){
        String s = "";
        for(int i = 0; i < list.size(); i++)
            s += list.get(i) + " ";
        
        return s;
    }
    
    public void push(CWEvent o){
        list.addLast(o);
    }
    
    //pop the next CWEvent off the queue and return it
    public CWEvent pop(){
        if(!list.isEmpty()){
            return list.removeFirst();
        }
        return null;
    }
    
    public void clear(){
        list.clear();
    }
}
