package com.customwars;
/*
 *Locatable.java
 *Author: Adam Dziuk
 *Contributors:
 *Creation: June 24, 2006, 3:46 PM
 *An interface related to the Location class. Originally from an AP Case Study.
 */

public interface Locatable {   
   
   //in order to be locatable, there must be a way to get the Location
   public Location getLocation();
}