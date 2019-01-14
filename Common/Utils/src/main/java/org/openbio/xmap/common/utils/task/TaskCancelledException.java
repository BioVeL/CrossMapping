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
 * Exception indicating that a task has been cancelled
 * <p>
 * OpenBio XMap
 * <p>
 * Date: 15/05/12
 * @author scmjpg
 */
public class TaskCancelledException extends TaskException {
    
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
	 * Constructor of TaskCancelledException
	 */
    public TaskCancelledException() {
        super("Task cancelled by the user");
    }
    
	/**
	 * Constructor of TaskCancelledException that received the cause of the exception
	 * @param cause cause of the exception
	 */
	public TaskCancelledException(Throwable cause) {
        super(cause);
    }


}
