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
package org.openbio.xmap.application.crossmapping.services;

import java.util.List;

import org.openbio.xmap.application.crossmapping.aspects.interfaces.AopAdministrable;
import org.openbio.xmap.common.dao.util.exception.DAOException;
import org.openbio.xmap.common.serviceinterfaces.services.xmap.XMapServiceFault_Exception;
import org.openbio.xmap.core.xmap.XMapImpl;

import org.oasisopen.sca.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/*** 
 * Implementation class of the XMap for the OpenBio.
 * <p>
 * The XMap allows the comparison of taxonomic checklists in order to discover the relationships between lists of species 
 * and other taxa in one species information system to be related to those in another species information system
 * <p>
 * OpenBio XMap
 * <p>
 * Date: 12/05/12
 * @author Fran   
 */ 
public class XMapImplSCA extends XMapImpl implements AopAdministrable {

	
	/****************************************************************************************/
	/* ATTRIBUTES																			*/														
	/****************************************************************************************/
	
	/**
	 * slf4j logger for this class
	 */		
	private static Logger logger = LoggerFactory.getLogger(XMapImplSCA.class);		
		
	
	/****************************************************************************************/
	/* GETTERS & SETTERS																	*/														
	/****************************************************************************************/		
	
	
	/****************************************************************************************/
	/* CONSTRUCTORS																			*/		
	/****************************************************************************************/
	
	/**
	 * Constructor of the StoreImplSCA that basically calls the constructor of 
	 * its supper class that it the class that has all the business logic
	 * @throws Exception
	 */	
	@Constructor
	public XMapImplSCA(@Property(name="xmapJndiDs") String xmapJndiDs,
                        @Property(name="exportFolder") String exportFolder,
                        @Property(name="uploadFolder") String uploadFolder)	
            throws XMapServiceFault_Exception {
		
		super(xmapJndiDs,exportFolder,uploadFolder);
	}


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
	@Override
	public void registerException(String exceptionId, String msg, String stackTrace, String className, 
			List<String> parameters, String user, String scope){
		try {
			super.getAdminDAO().registerException(exceptionId, msg, stackTrace, className, parameters, user, scope);
		} catch (DAOException e) {
			logger.error(e.getMessage(),e);
		}
	}	
		
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
	public void registerMethodExecutionTime(String methodName, String className, List<String> parameters, 
			long elapsedTime, byte logLevel, String user, String scope){
		try {
			super.getAdminDAO().registerMethodExecutionTime(methodName, className, parameters, elapsedTime, logLevel, user, scope);
		} catch (DAOException e) {
			logger.error(e.getMessage(),e);
		}
	}

	@Override
	public String getTargetClassName() {
		return getClass().getName();
	}
	
	
}
