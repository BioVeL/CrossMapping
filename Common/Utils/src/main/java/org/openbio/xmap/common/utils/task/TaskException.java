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

import java.util.List;

/**
 * Class to wraps the exception that can occurs executing a task
 * <p>
 * OpenBio XMap
 * <p>
 * Date: 15/05/12
 * @author scmjpg
 */
public class TaskException extends Exception {
	
	/****************************************************************************************/
	/* ATTRIBUTES																			*/														
	/****************************************************************************************/
	
    /**
	 * serial uid
	 */
	private static final long serialVersionUID = 1L;
	
	private String userData = "";
	
	private List<String> parameters;
	
	/****************************************************************************************/
	/* GETTERS AND SETTERS																	*/														
	/****************************************************************************************/
	
	public String getUserData() {
		return userData;
	}

	public void setUserData(String userData) {
		this.userData = userData;
	}
	
	
	public List<String> getParameters() {
		return parameters;
	}

	public void setParameters(List<String> parameters) {
		this.parameters = parameters;
	}	
		
	/****************************************************************************************/
	/* CONSTRUCTORS																			*/		
	/****************************************************************************************/	


	/**
	 * Constructor of TaskException
	 */
	TaskException() {
        super();
    }

	/**
	 * Constructor of TaskException that received the description of the exception
	 * @param message the description of the exception
	 */
    public TaskException(String message) {
        super(message);
        this.userData = message;
    }

	/**
	 * Constructor of TaskException that received the cause of the exception
	 * @param cause the cause of the exception
	 */    
    public TaskException(Throwable cause) {
    	super(cause);
    	this.userData = cause.getMessage();
    }
    
    
	/**
	 * Constructor of TaskException that received the cause of the exception
	 * @param cause the cause of the exception
	 * @param userData
	 */    
    public TaskException(Throwable cause, String userData) {
    	super(cause);
    	this.userData = userData;
    }
    
    
}
