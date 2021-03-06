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
 * Exception indicating that a JAXB datatype had invalid values, e.g.
 * JAXB field is empty, but database requires non-null value, or
 * database restricted to enumerated values, but JAXB allows any string.
 * <p>
 * OpenBio XMap
 * <p>
 * Date: 15/05/12
 * @author scmjpg
 */
public class DAOValueException extends DAOException {

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
	 * Constructor of DAOValueException that receives the description of the exception
	 * @param description text with the description of the exception
	 */
    public DAOValueException(String description) {
        super(description);
    }

    /**
     * Constructor of DAOValueException that receives the description and the cause of the exception
     * @param description text with the description of the exception
     * @param e cause of the exception
     */
    public DAOValueException(String description, Exception e) {
        super(description, e);
    }

}
