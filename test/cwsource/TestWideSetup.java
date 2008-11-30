package cwsource;
import org.slf4j.Logger;  
import org.slf4j.LoggerFactory;  

import junit.extensions.TestSetup;
import junit.framework.Test;

public class TestWideSetup extends TestSetup {
	final Logger logger = LoggerFactory.getLogger(TestWideSetup.class);  

	public TestWideSetup(Test test) {
			super(test);
			ResourceLoader.initTestProperties();
			logger.info("The ResourceManager has been setup with Test Properties");
	}

}
