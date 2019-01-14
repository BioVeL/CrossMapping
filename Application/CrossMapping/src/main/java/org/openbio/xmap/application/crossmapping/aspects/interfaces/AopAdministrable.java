/*
 * #%L
 * XMap Web Service Application
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

package org.openbio.xmap.application.crossmapping.aspects.interfaces;

import java.util.List;

import org.oasisopen.sca.annotation.OneWay;


/**
 * Interface to be implemented for the classes that we want to control by AOP, facilitating
 * the logging and exception register.
 * <p>
 * OpenBio
 * <p>
 * Date: 23/09/11
 * @author Fran  
 */
public interface AopAdministrable {

	/**
	 * Method to return the qualified name of the class that implements this interface.
	 * @return the qualified name of the class that implements this interface
	 */
	public String getTargetClassName();

	/**
	 * Method responsible to register the information of the exception received as parameter.
	 * <p>   
	 * Note: This method has the sca annotation OneWay because we want that the clients that call the class
	 * that implements this interface, make these calls asynchronously.
	 * @param exceptionId pseudoId of the exception. This id will be show to the user to then later can be utilized
	 * to track down the exception recovering it from the Admin database 
	 * @param msg Message of the exception
	 * @param stackTrace stack trace of the exception. Including the stack trace of the initial exception of that was 
	 * wrapper in other exception
	 * @param className full class name of the class where the exception was captured and processed
	 * @param parameters List of the parameters (names and its values) with which the method that provoked the exception 
	 * was invoked 
	 */
	@OneWay
	public void registerException(String exceptionId, String msg, String stackTrace, String className, List<String> parameters, String user, String scope);
	
	/**
	 * Method responsible for loggin the execution time of the method received as parameter
	 * <p>  
	 * Note: This method has the sca annotation "OneWay" because we want that the clients that call the class
	 * that implements this interface, make these calls asynchronously.
	 * @param methodName name of the method
	 * @param className class in which this method is
	 * @param parameters List of the parameters (names and its values) with which the method was invoked
	 * @param elapsedTime number of milliseconds that the method has taken to execute
	 * @param logLevel level of log of this message. If this level is great or equals to the one
	 * define in the database, this message will be register.
	 */
	@OneWay
	public void registerMethodExecutionTime(String methodName, String className, List<String> parameters, long elapsedTime, byte logLevel, String user, String scope);	
}
