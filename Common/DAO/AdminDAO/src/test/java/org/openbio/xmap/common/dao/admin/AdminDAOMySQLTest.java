/*
 * #%L
 * XMap Admin DAO
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
package org.openbio.xmap.common.dao.admin;

import static org.junit.Assume.assumeNoException;
import static org.junit.Assume.assumeNotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.junit.BeforeClass;
import org.junit.Test;
import org.openbio.xmap.common.dao.admin.AdminDAO;
import org.openbio.xmap.common.dao.admin.AdminDAOFactory;
import org.openbio.xmap.common.dao.admin.AdminDAOMySQLTest;
import org.openbio.xmap.common.test.jndi.JndiDatasourceCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for running JUnit tests to verify that the methods in XMapDAO work as expected. 
 * <p>
 * OpenBio XMap
 * <p>
 * Date: 24/05/12
 * @author Fran 
 */
public class AdminDAOMySQLTest {

	/****************************************************************************************/
	/* ATTRIBUTES																			*/														
	/****************************************************************************************

	/**
	 * slf4j logger for the given class.
	 */
	private static Logger logger = LoggerFactory.getLogger(AdminDAOMySQLTest.class);
	
	
	
	/****************************************************************************************/
	/* BEFORE AND AFER TEST METHODS															*/	
	/****************************************************************************************/	
	
	/**
	 * Method used to set up all the things needed to be configured before to run the tests
	 * <p>
 	 * This method uses the jUnit annotation "BeforeClass", being only run once before any 
	 * of the tests in this class are executed.
	 */
    @BeforeClass
    public static void setUpClass() {
		//Create the DataSource and bind it to the initial context
        try { 
        	//Obtain the xmap test properties to be used during jUnit tests        	
        	Properties prop = getXmapTestProperties();
        	
        	//Obtain the jdbcUrl of the xmap database to use during the tests
            String jdbcUrl = prop.getProperty("test.database.xmap.jdbc");
            assumeNotNull(jdbcUrl);
        	
        	//Create jndi datasource 
        	JndiDatasourceCreator.create("jdbc/XMapDB", jdbcUrl);
        	
        }
        catch (Exception e) {
            assumeNoException(e);
        }
    }

    
	/****************************************************************************************/
	/* TEST	METHODS																			*/	
	/****************************************************************************************/
    
    /**
     * Test to verify the method to obtain the list of users
     * @throws Exception
     */
    @Test
    public void testGetUsers() throws Exception {
    	logger.info("Test get users");
    	
    	//Obtain admin dao for the given jndi datasource
    	String jndiDatasourceName = "jdbc/XMapDB";
    	AdminDAO adminDAO = AdminDAOFactory.getAdminDAO(jndiDatasourceName);
		    	
    	
    	List<String> users = adminDAO.getUsers();
    	System.out.println("Users in the database:");
    	for (String user:users){
    		System.out.println("  " + user);
    	}
    }
    
	/****************************************************************************************/
	/* PRIVATE METHODS																		*/	
	/****************************************************************************************/
    
    /**
     * Method responsible to return the properties object for the xmap test
     * @return  properties object for the xmap test
     * @throws IOException 
     */
    private static Properties getXmapTestProperties() throws IOException{
        String home = System.getProperty("user.home");
        File config = new File(home, "xmap-test.properties");
        Properties properties = new Properties();

        FileInputStream is = new FileInputStream(config);
        properties.load(is);
        return properties;
    }
    
}
