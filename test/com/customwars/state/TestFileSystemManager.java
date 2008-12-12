package com.customwars.state;

import java.io.File;
import java.io.IOException;
import java.util.List;

import junit.framework.TestCase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestFileSystemManager extends TestCase {

	private String expectedTestCategory1 = "test1";
	private String expectedTestCategory2 = "test2";
	private String expectedTestCategory3 = "test3";
	
	private String expectedTestMap1 = "testMap1.map";
	private String expectedTestMap2 = "testMap2.map";
	private String expectedTestMap3 = "testMap3.map";
	
	final static Logger logger = LoggerFactory.getLogger(TestFileSystemManager.class);  
	
	protected void setUp() {
		createExpectedTestCategories();
		createExpectedTestMaps();
	}

	protected void tearDown() throws Exception {
		removeExpectedTestCategories();
	}
	
	public void testGetMapCategoryFolders() {
		List<File> categoryDirs; 
		
		categoryDirs = FileSystemManager.getMapCatagories();
		assertTrue(expectedTestCategory1.equals(categoryDirs.get(0).getName()));
		assertTrue(expectedTestCategory2.equals(categoryDirs.get(1).getName()));
		assertTrue(expectedTestCategory3.equals(categoryDirs.get(2).getName()));
	}

	public void testGettingAllMaps(){
        List<File> mapFiles = FileSystemManager.getAllAvailableMaps();
        assertTrue(expectedTestMap1.equals(mapFiles.get(0).getName()));
        assertTrue(expectedTestMap2.equals(mapFiles.get(1).getName()));
        assertTrue(expectedTestMap3.equals(mapFiles.get(2).getName()));
	}

	private void createExpectedTestCategories() {
		FileSystemManager.createDir(ResourceLoader.properties.get("mapsLocation") + "/" + expectedTestCategory1);
		FileSystemManager.createDir(ResourceLoader.properties.get("mapsLocation") + "/" + expectedTestCategory2);
		FileSystemManager.createDir(ResourceLoader.properties.get("mapsLocation") + "/" + expectedTestCategory3);
	}
	
	private void createExpectedTestMaps() {
		createMapFile(expectedTestCategory1, expectedTestMap1);
		createMapFile(expectedTestCategory2, expectedTestMap2);
		createMapFile(expectedTestCategory3, expectedTestMap3);
	}
	
	private void createMapFile(String category, String name) {
		try {
	        File file = new File(ResourceLoader.properties.get("mapsLocation") + "/" + category + "/" + name);
	        boolean success = file.createNewFile();

	        if (success) {
	            logger.debug("successfullly created file:" + ResourceLoader.properties.get("mapsLocation") + "/" + category + "/" + name);
	        } else {
	        	logger.debug("Unable to create file :" + ResourceLoader.properties.get("mapsLocation") + "/" + category + "/" + name);
	        }
	    } catch (IOException e) {
	    	logger.error("Error creating file :" + ResourceLoader.properties.get("mapsLocation") + "/" + category + "/" + name);
	    }
	}

	private void removeExpectedTestCategories() {
		FileSystemManager.deleteDir(ResourceLoader.properties.get("mapsLocation") + "/" + expectedTestCategory1);
		FileSystemManager.deleteDir(ResourceLoader.properties.get("mapsLocation") + "/" + expectedTestCategory2);
		FileSystemManager.deleteDir(ResourceLoader.properties.get("mapsLocation") + "/" + expectedTestCategory3);
	}
	

}
