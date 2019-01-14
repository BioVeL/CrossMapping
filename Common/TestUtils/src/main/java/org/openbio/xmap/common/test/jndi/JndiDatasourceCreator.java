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
package org.openbio.xmap.common.test.jndi;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.dbcp.BasicDataSource;


/**
 * Class that creates jndidatasources from the e2-test propertie file. 
 * These jdniDatasources will be used in the JUnit tests.  
 * <p>
 * OpenBio XMap
 * <p>
 * Date: 12/05/12
 * @author fran   
 */
public class JndiDatasourceCreator {

	/****************************************************************************************/
	/* ATTRIBUTES																			*/														
	/****************************************************************************************/
	
	private static InitialContext ic = null;
	
	
	/****************************************************************************************/
	/* PUBLIC METHODS																		*/														
	/****************************************************************************************/
	    
    /**
     * Method responsible to create a new jndidatasource with the name jndiName for the
     * database with the url jdbcUrl.
     * @param jndiName name of the jndidatasource to create
     * @param jdbcUrl jdbc url of the database to create the datasource
     * @throws JndiDatasourceCreatorException
     */
    public static void create(String jndiName, String jdbcUrl) throws JndiDatasourceCreatorException{
    	try{
	    	requireInitialCatalog();
				
			BasicDataSource ds = new BasicDataSource();
			ds.setDriverClassName("com.mysql.jdbc.Driver");
			ds.setUrl(jdbcUrl);			
			ic.bind("java:/comp/env/" + jndiName, ds);
    	}
		catch(NamingException ex){
			String msg = "Error creating jndidatasource for jndiName= " + jndiName; 
			throw new JndiDatasourceCreatorException(msg,ex);
		}
    }
      
    
	/****************************************************************************************/
	/* PRIVATE METHODS																		*/														
	/****************************************************************************************/	
    
    /**
     * Method responsible to create the subcontext java:/comp/env/jdbc in the inital catalog.
     * @throws NamingException
     */
    private static void requireInitialCatalog() throws NamingException{
    	if (ic==null){
			// Create initial context
			System.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.naming.java.javaURLContextFactory");
			System.setProperty(Context.URL_PKG_PREFIXES, "org.apache.naming");
			ic = new InitialContext();
			ic.createSubcontext("java:");
			ic.createSubcontext("java:/comp");
			ic.createSubcontext("java:/comp/env");
			ic.createSubcontext("java:/comp/env/jdbc");
    	}
    }    
    
    /**
     * Method responsible to return the path of the home directory
     * @return the path of the home directory
     */
    private static String getHomeDir(){
        return System.getProperty("os.name").toLowerCase().contains("win") ? System.getenv("UserProfile") : System.getenv("HOME");
    }
}
