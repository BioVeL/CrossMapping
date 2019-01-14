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
package org.openbio.xmap.common.dao.util.exception;

/**
 * Class that wraps exceptions occur when we can not connect to the database connection  
 * <p>
 * OpenBio XMap
 * <p>
 * Date: 15/05/12
 * @author scmjpg
 */
public class DAOConnectionException extends DAOException {

	/****************************************************************************************/
	/* ATTRIBUTES																			*/														
	/****************************************************************************************/	
	
	/**
	 * serial version uid
	 */
	private static final long serialVersionUID = 1L;

	
	
	/****************************************************************************************/
	/* CONSTRUCTORS																			*/		
	/****************************************************************************************/

	/**
	 * Constructor of DAOConnectionException that receives the description of the exception
	 * @param description message of the exception
	 */
	public DAOConnectionException(String description) {
		super(description);
	}
	
	/**
	 * Constructor of DAOConnectionException that receives the cause of the exception
	 * @param e cause of the exception
	 */
    public DAOConnectionException(Exception e) {
        super(e);
    }
    
	/**
	 * Constructor of DAOConnectionException that receives the description and the cause of the exception
	 * @param description string with the description of the exception
	 * @param e cause of the exception
	 */    
    public DAOConnectionException(String description, Exception e) {
        super(description, e);
    }
}
