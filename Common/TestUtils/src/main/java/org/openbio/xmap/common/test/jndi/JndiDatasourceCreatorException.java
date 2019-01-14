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

/**
 * Class to wrap the exceptions that can occurs when creating jndi datasources   
 * <p>
 * OpenBio XMap
 * <p>
 * Date: 12/05/12
 * @author scmjpg  
 */
public class JndiDatasourceCreatorException extends Exception {
    
	/****************************************************************************************/
	/* ATTRIBUTES																			*/														
	/****************************************************************************************/
	
	/**
	 * Serial version uid
	 */
	private static final long serialVersionUID = 1L;

	
	
	/****************************************************************************************/
	/* CONSTRUCTORS																			*/		
	/****************************************************************************************/
	
	/**
	 * Constructor of JndiDatasourceCreatorException that receive the message of the exception
	 * @param message
	 */
	public JndiDatasourceCreatorException(String message) {
        super(message);
    }
	
	/**
	 * Constructor of JndiDatasourceCreatorException that receive the cause of the exception
	 * @param cause
	 */
	public JndiDatasourceCreatorException(Throwable cause) {
        super(cause);
    }
	
	/**
	 * Constructor of JndiDatasourceCreatorException that receive the message and the cause of the exception
	 * @param cause
	 * @param message
	 */
	public JndiDatasourceCreatorException(String message,Throwable cause) {
        super(message,cause);
    }	
}
