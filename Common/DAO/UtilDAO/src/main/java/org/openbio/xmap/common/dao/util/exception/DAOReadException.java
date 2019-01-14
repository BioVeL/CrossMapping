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
 * Exception indicating that a read operation on the database failed
 * <p>
 * OpenBio XMap
 * <p>
 * Date: 15/05/12
 * @author scmjpg
 */
public class DAOReadException extends DAOException {

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
	 * Constructor of DAOReadException that receives the description of the exception
	 * @param description test with the description of the exception
	 */
    public DAOReadException(String description) {
        super(description);
    }
    
    /**
     * Constructor of DAOReadException that receives the cause of the exception
     * @param e cause of the exception
     */
    public DAOReadException(Exception e) {
        super(e);
    }
    
    /**
     * Constructor of DAOReadException that receives the table in which the exception occurred and its cause
     * @param tableName name of the table that caused the exception
     * @param e cause of the exception
     */
    public DAOReadException(String tableName, Exception e) {
        super(tableName, e);
    }

}
