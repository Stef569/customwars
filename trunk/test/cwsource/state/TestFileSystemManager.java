package cwsource.state;

import java.util.ArrayList;

import cwsource.ResourceLoader;

import junit.framework.TestCase;

public class TestFileSystemManager extends TestCase {

	private String expectedTestCategory1 = "test1";
	private String expectedTestCategory2 = "test2";
	private String expectedTestCategory3 = "test3";
	
	protected void setUp() {
		createExpectedTestCategories();
	}
	
	protected void tearDown() throws Exception {
		removeExpectedTestCategories();
	}
	
	public void testGetMapCategoryFolders() {
		ArrayList<String> categories; 
		
		categories = FileSystemManager.getMapCatagories();
		assertTrue(expectedTestCategory1.equals(categories.get(0)));
		assertTrue(expectedTestCategory2.equals(categories.get(1)));
		assertTrue(expectedTestCategory3.equals(categories.get(2)));
	}

	private void createExpectedTestCategories() {
		FileSystemManager.createDir(ResourceLoader.properties.get("mapsLocation") + "/" + expectedTestCategory1);
		FileSystemManager.createDir(ResourceLoader.properties.get("mapsLocation") + "/" + expectedTestCategory2);
		FileSystemManager.createDir(ResourceLoader.properties.get("mapsLocation") + "/" + expectedTestCategory3);
	}
	
	private void removeExpectedTestCategories() {
		FileSystemManager.deleteDir(ResourceLoader.properties.get("mapsLocation") + "/" + expectedTestCategory1);
		FileSystemManager.deleteDir(ResourceLoader.properties.get("mapsLocation") + "/" + expectedTestCategory2);
		FileSystemManager.deleteDir(ResourceLoader.properties.get("mapsLocation") + "/" + expectedTestCategory3);
	}

}
