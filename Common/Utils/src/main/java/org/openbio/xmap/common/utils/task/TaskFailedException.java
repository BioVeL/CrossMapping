/*
 * #%L
 * Xmap Utils
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
package org.openbio.xmap.common.utils.task;

/**
 * Exception indicating that a task has failed
 * <p>
 * OpenBio XMap
 * <p>
 * Date: 15/05/12
 * @author scmjpg
 */
public class TaskFailedException extends TaskException {
   
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
	 * Constructor of TaskFailedException
	 */
	public TaskFailedException() {
        super();
    }

	/**
	 * Constructor of TaskFailedException that receives the description of the exception
	 * @param message description of the exception
	 */
    public TaskFailedException(String message) {
        super(message);
    }

    /**
     * Constructor of TaskFailedException that receives the cause of the exception
     * @param cause cause of the exception
     */
    public TaskFailedException(Throwable cause) {
        super(cause);
    }

	/**
	 * Method responsible to return the description of the exception
	 * @return string with the description of the exception
	 */    
    @Override
    public String getMessage() {
        String message = super.getMessage();
        if (message == null) {
            message = "task failed";
        }
        return message;
    }
}
