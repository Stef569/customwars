package com.customwars.state;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;

import junit.framework.TestCase;

public class TestResourceLoader extends TestCase {

	public void testLoadingFileFromResourcesIsAvailable() throws FileNotFoundException{
		URL fileURL = getClass().getResource("/testres/sound/ok.wav");
		boolean fileExists = new File(fileURL.getFile()).exists();

		assertTrue(fileExists);
	}

}
