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
 * Class that wraps exceptions occur when we don't support a method in a certain DAO
 * <p>
 * OpenBio XMap
 * <p>
 * Date: 15/05/12
 * @author scmjpg
 */
public class DAONotSupportedException extends DAOException {

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
	 * Constructor of DAONotSupportedException that receives the name of the method not supported
	 * @param method name of the method not supported
	 */
	public DAONotSupportedException(String method) {
		super("operation " + method + " not supported");
	}
}
