/*
 * #%L
 * XMap Admin DAO
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
package org.openbio.xmap.common.dao.admin;

import java.util.List;

import org.openbio.xmap.common.dao.util.exception.DAOException;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.TaskId;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.TaskProgress;
import org.openbio.xmap.common.utils.task.Task;

public interface AdminDAO {

	/**
	 * Method that ensure that the user exits in the db
	 */
	public void verifyUserExistence(String user, String scope) throws DAOException;
	
	/**
	 * Method that return the list of users in the db
	 * @return list of users in the db
	 * @throws DAOException
	 */
	public List<String> getUsers() throws DAOException;
	
	/**
	 * Method to register the exception that its information is received as parameter .
	 * @param exceptionId pseudoId of the exception. This id will be show to the user to then later can be utilized
	 * to track down the exception recovering it from the Admin database. 
	 * @param msg Message of the exception.
	 * @param stackTrace stack trace of the exception. Including the stack trace of the initial exception of that was 
	 * wrapper in other exception.
	 * @param className full class name of the class where the exception was captured and processed.
	 * @param parameters List of the parameters (names and its values) with which the method that provoked the exception 
	 * was invoked.
	 * @throws DAOException if there is a problem executing this method
	 */
	public void registerException(String exceptionId, String msg, String stackTrace, String className, List<String> parameters, String user, String scope) throws DAOException;	
	
		
	/**
	 * Method responsible to log the execution time of the method received as parameter.
	 * @param methodName name of the method
	 * @param className class in which this method is
	 * @param parameters List of the parameters (names and its values) with which the method was invoked
	 * @param elapsedTime number of milliseconds that the method has taken to execute
	 * @param logLevel level of log of this message. If this level is great or equals to the one
	 * define in the database, this message will be register.
	 * @throws DAOException
	 */	
	public void registerMethodExecutionTime(String methodName, String className, List<String> parameters, long elapsedTime, byte logLevel, String user, String scope) throws DAOException;	
		
	
	public void updateTaskProgress(Task task) throws DAOException;
	
	public List<TaskId> getActiveTasks(String user, String scope) throws DAOException;
	
	public void cancelTask(TaskId taskId) throws DAOException;
	
	public TaskProgress getTaskProgress(TaskId taskId) throws DAOException;
	
}
