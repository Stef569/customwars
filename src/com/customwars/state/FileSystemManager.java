package com.customwars.state;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.customwars.AllTests;


public class FileSystemManager {
	
	final static Logger logger = LoggerFactory.getLogger(AllTests.class);  
	
	private static final String DIR_IGNORE_FILTER = ".";
	private static FilenameFilter fileNameFilters;
	private static File MAP_DIR;
	
	public static void init(){
		fileNameFilters = getFileFilters();
		MAP_DIR = new File((String)ResourceLoader.properties.get("mapsLocation"));
	}
	
	public static List<File> getMapCatagories() {
		List<File> directories = null; 
		
		if (MAP_DIR !=null){
			directories = readAllFiles(MAP_DIR);
			
			if (directories.isEmpty() && directories !=null) {
				logger.error("Problem reading from MapLocation: [" + MAP_DIR + " ]");
				return null;
			}
		}
	    
	    return directories;
	}

	private static FilenameFilter getFileFilters() {
		FilenameFilter filter = new FilenameFilter() {
	        public boolean accept(File dir, String name) {
	            return !name.startsWith(DIR_IGNORE_FILTER);
	        }
	    };
		return filter;
	}
	
	protected static boolean deleteDir(String dirName) {
		
		File dir = new File(dirName);
		
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i=0; i<children.length; i++) {
				File file = new File(dir, children[i]);
				boolean success = deleteDir(file.getAbsolutePath());
				if (!success) {
					return false;
				}
			}
		}
		
        return dir.delete();
	}
	
	protected static void createDir(String dirs) {
		boolean success = (new File(dirs)).mkdirs();
	    if (!success) {
			logger.error("Could not create a directory: [ " + dirs +" ]");
	    }
	}

	public static List<File> getAllAvailableMaps() {
		
		List<File> categories = FileSystemManager.getMapCatagories();
		List<File> allMaps = new ArrayList<File>();
		
		for(File category: categories){
			allMaps.addAll(readAllFiles(category.getAbsoluteFile()));
		}
		
		return allMaps;
	}

	private static List<File> readAllFiles(File file) {
		File[] list = file.listFiles(fileNameFilters);
		return Arrays.asList(list);
	}
}
