/*
 * #%L
 * XMap Util Data Access Objects
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
package org.openbio.xmap.common.dao.util.database;

/**
 * Class to wrap the exceptions that can occur using a database
 * <p>
 * OpenBio XMap 
 * <p>
 * Date: 15/05/12
 * @author scmjpg
 */
public class DatabaseException extends Exception {
   
	/****************************************************************************************/
	/* CONSTRUCTORS																			*/		
	/****************************************************************************************/	
	
	/**
	 * Constructor of DatabaseException that receives the message of the exception
	 * @param message text with message of the exception 
	 */
	public DatabaseException(String message) {
        super(message);
    }

	/**
	 * Constructor of DatabaseException that receives the cause of the exception
	 * @param cause cause of the exception
	 */
    public DatabaseException(Throwable cause) {
        super(cause);
    }
}
