/*
 * #%L
 * XMap Web Service Application
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
package org.openbio.xmap.application.crossmapping.servlets;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Class that implement servlet context listener and will be used to add some
 * system properties to be used then later by application to know where to create the log files
 * <p>
 * OpenBio XMap
 * <p>
 * Date: 14/05/12
 * @author Fran  
 */
public final class MyServletContextListener implements ServletContextListener {

	/****************************************************************************************/
	/* CONSTRUCTORS																			*/		
	/****************************************************************************************/	
	
	/**
	 * Constructor of MyServletContextListener
	 */
	public MyServletContextListener() {
	  
  	}
  
	/****************************************************************************************/
	/* PUBLIC METHODS																		*/														
	/****************************************************************************************/  
	
	/**
	 * This method is invoked when the Web Application is ready to service requests
	 * and it sets the system property appRootPath with the physical path in which 
	 * this application is executed 
	 */
	public void contextInitialized(ServletContextEvent event)
	{
		ServletContext context = event.getServletContext();
	    System.setProperty("appRootPath", context.getRealPath("/"));
	}
	    
	/**
	 * This method is invoked when the Web Application has been removed and is no longer able to accept requests
	 */
	public void contextDestroyed(ServletContextEvent event)
	{
		
	}
  
}
