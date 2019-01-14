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
 * Abstract class that represent any type of exception that can occur using any DAO
 * <p> 
 * OpenBio XMap 
 * <p>
 * Date: 15/05/12
 * @author scmjpg
 */
public abstract class DAOException extends Exception {

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
	 * Constructor of DAOException that receives the description of the exception
	 * @param message description of the exception
	 */
    public DAOException(String message) {
		super(message);
	}

    /**
     * Constructor of DAOException that receives the cause of the exception
     * @param e cause of the exception
     */
    public DAOException(Exception e) {
		super(e.getMessage());
        initCause(e);
	}

    /**
     * Constructor of DAOException that receives a prefix and the cause of the exception
     * @param prefix prefix to be added to the description of the cause of the exception
     * @param e cause of the exception
     */
	public DAOException(String prefix, Exception e) {
		super(prefix + ": " + e.getMessage());
        initCause(e);
	}

}
