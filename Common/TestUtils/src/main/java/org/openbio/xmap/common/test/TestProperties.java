/*
 * #%L
 * XMap Test Utils
 * %%
 * Copyright (C) 2012 - 2013 Cardiff University
 * %%
 * Use of this software is governed by the attached licence file. If no licence 
 * file is present the software must not be used.
 * 
 * The use of this software, including reverse engineering, for any other purpose 
 * is prohibited without the express written permission of the software owner, 
 * Cardiff University.
 * #L%
 */
package org.openbio.xmap.common.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/** 
 * Class to retrieve the test properties on the local machine
 * <p>
 * OpenBio XMap
 * <p>
 * Date: 12/05/12
 * @author scmjpg  
 */
public class TestProperties {
	
	/****************************************************************************************/
	/* ATTRIBUTES																			*/														
	/****************************************************************************************/    
	private static boolean checked = false;
    private static Properties properties = null;

    
	/****************************************************************************************/
	/* PUBLIC METHODS																		*/														
	/****************************************************************************************/    
    
    /**
     * Retrieve the test properties
     * @return a Java Properties instance, or null if the file does not exist (or other problems occur)
     */
    public synchronized static Properties getProperties(String propertyFileName) {
        if (!checked) {
            String home = System.getProperty("user.home");
            File config = new File(home, propertyFileName);
            properties = new Properties();
            try {
                FileInputStream is = new FileInputStream(config);
                properties.load(is);
            }
            catch (IOException e) {
                properties = null;
            }
            checked = true;
        }
        return properties;
    }
}
