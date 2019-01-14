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
 * Class that wraps exceptions occur when we don't support certain type of datasources 
 * <p>
 * OpenBio XMap 
 * <p>
 * Date: 15/05/12
 * @author scmjpg
 */
public class DAODataSourceException extends DAOException {

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
	 * Constructor of DAODataSourceException that receives the description of the exception
	 * @param description test with the description of the exception
	 */	
    public DAODataSourceException(String description) {
        super(description);
    }

    /**
     * Constructor of DAODataSourceException that receives the source of the exception
     * @param e source of the exception
     */
    public DAODataSourceException(Exception e) {
        super(e);
    }

    /**
     * Constructor of DAODataSourceException that receives the description and the cause of the exception
     * @param description text with the description of the exception
     * @param e source of the exception
     */
    public DAODataSourceException(String description, Exception e) {
        super(description, e);
    }

}
