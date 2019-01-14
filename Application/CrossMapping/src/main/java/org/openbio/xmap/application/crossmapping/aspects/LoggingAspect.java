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
package org.openbio.xmap.application.crossmapping.aspects;

import java.util.ArrayList;
import java.util.List;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.openbio.xmap.application.crossmapping.aspects.interfaces.AopAdministrable;
import org.openbio.xmap.common.serviceinterfaces.services.xmap.XMapServiceFault_Exception;
import org.openbio.xmap.common.utils.misc.Misc;
import org.openbio.xmap.common.utils.misc.StackTraceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that acts as a proxy for the methods that match the point cuts it defines, allowing us to apply
 * cross-cutting concerns (aspects) such as logging and exception control for these methods. 
 * For that reason, we are using AspectJ as the framework to implement the AOP. 
 * <p>
 * The project E2Store wave the classes in the TreeStore project allowing us to create point cuts for methods in
 * its classes. 
 * <p>
 * OpenBio
 * <p>
 * Date: 13/12/11
 * @author Fran   
 */
@Aspect
public class LoggingAspect {
	
	/****************************************************************************************/
	/* ATTRIBUTES																			*/														
	/****************************************************************************************/
	
	/**
	 * level for the log messages
	 */
	private final byte levelDBLog = 1;
	
	/**
	 * slf4j logger for this class
	 */		
	private static Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

	
	/****************************************************************************************/
	/* POINTCUTS																			*/		
	/****************************************************************************************/
	
	/**
	 * Point cut that picks out each join point in the program flow that match the execution of any public methods defined 
	 * in the interface navigate or edit and that the target, classes that implements those interfaces, also implements 
	 * the interface AopAdministrable. 
	 */		
    @Pointcut("execution(public * org.openbio.xmap.common.serviceinterfaces.services.xmap.XMapService..*(..)) " +
    		"&& " +
    		"target(org.openbio.xmap.application.crossmapping.aspects.interfaces.AopAdministrable)")
	public void businessServicePublicMethodExecution() {
	}
    
    
    @Pointcut("execution(*.new(..))" +
    		"&& " +
    		"target(org.openbio.xmap.application.crossmapping.aspects.interfaces.AopAdministrable)")
    public void businessServiceConstructor() {
    }    
    
	
    
    
   
    /**
     * A data access operation is the execution of any method defined on a 
     * dao interface. This definition assumes that interfaces are placed in the
     * "dao" package, and that implementation types are in sub-packages.
     */
    @Pointcut("call(* (org.openbio.xmap.common.dao.crossmap..*  || org.openbio.xmap.common.dao.importer..*).*(..)) && " +
    		"!(call(* org.openbio.xmap.common.dao..*.get*DAO(..)) || call(* org.openbio.xmap.common.dao..*.getChecklists(..)) " +
    		"|| call(* org.openbio.xmap.common.dao..*.getCrossMaps(..)) || call(* org.openbio.xmap.common.dao..*.getCrossMapResultsExported(..)))")
    public void dataAccessOperation() {}
    
    @Pointcut("within(org.openbio.xmap.core.xmap..*)")
    public void inBusinessLogic() {}	    
    
    
	/****************************************************************************************/
	/* ADVICES																				*/		
	/****************************************************************************************/
    
    /**
     * Advice to be executed AROUND the execution of any method that match the point cut "anyPublicMethodExecution"
     * Note: The advices in the AOP are the ones that actually implement cross-cutting behavior. In this advice we 
     * are going to control the execution time of the joint point (method) and also register the exception that it can
     * rise.
     * @param jp joint point (method) picked up by the point cut and over which we will apply cross-cutting concerns
     * @return The result of the execution of the joint point (method)
     * @throws Throwable
     */    
    @Around("businessServicePublicMethodExecution() || businessServiceConstructor()")
    public Object adminManagementSection(ProceedingJoinPoint jp) throws Throwable {  	    	   		
    	logger.trace("AOP Logging enter in method {}",jp.getSignature());      	
    	long startTime = System.currentTimeMillis();
    	try {    		
            return jp.proceed();
        }
		catch (Exception ex){
			//If an exception happens in the execution of the method, we register it in the db and raise it to
			//the client that calls this method once we've added to it the pseudoExceptionId register in the db 				
			logger.trace("AOP Logging exception in method {}",jp.getSignature());
			String pseudoId = registerException(ex,jp);
			throw raiseException(ex,pseudoId); 
		}
		finally{
			logger.trace("AOP Logging exit method {}", jp.getSignature());		
			long elapsedTime = System.currentTimeMillis() - startTime;							
			registerExecutionTime(elapsedTime,jp);
		}
    }
    
    
    /*
    @Around("dataAccessOperation()")
    public Object daoManagementSection(ProceedingJoinPoint jp) throws Throwable {  	    	   		
    	// logger.debug("enter method {}",jp.getSignature());      	
    	long startTime = System.currentTimeMillis();
    	StringBuilder sbParameters = new StringBuilder();
    	try {   
    		for (Object arg:jp.getArgs()){
    			sbParameters.append((arg!=null?arg.toString():"null") + " ");
	    	}    		    		
            return jp.proceed();
        }
		catch (Exception ex){
			//If an exception happens in the execution of the method, we register it in the db and raise it to
			//the client that calls this method once we've added to it the pseudoExceptionId register in the db 				
			logger.debug("exception in method {} parameters {}",new Object[]{jp.getSignature(), sbParameters.toString()});
			throw ex;
		}
		finally{
			long elapsedTime = System.currentTimeMillis() - startTime;		
			logger.debug("elapsed time method {} = {}ms parameters={}", new Object[]{jp.getSignature(),elapsedTime,sbParameters.toString()});										
		}
    }    
    */
    
	/****************************************************************************************/
	/* PRIVATE METHODS																		*/														
	/****************************************************************************************/	
    
    /**
     * Method to register the exception produce in the execution of the joint point
     * @param ex exception occurs in the execution of the joint point
     * @param jp joint point that its execution rise and exception 
     * @return pseudoExceptionId to show to the user and it could be used later to track down the exception in the db
     */      
    private String registerException(Exception ex, ProceedingJoinPoint jp){
    	String pseudoId = Misc.generatePseudoId(); 	
    	try{
    		AopAdministrable target = (AopAdministrable)jp.getTarget();
	    	
	    	List<String> parameters = new ArrayList<String>();
	    	for (Object arg:jp.getArgs()){
	    		parameters.add(arg!=null?arg.toString():"null");
	    	}
	    	
	    	logger.error(ex.getMessage()+ " UserData: {}",parameters,ex);
	    				
			String stackTrace = StackTraceUtil.getStackTrace(ex);				
			target.registerException(pseudoId,ex.getMessage(),stackTrace,target.getTargetClassName(),parameters,"aop","aop");
		}
		catch (Exception e){
			logger.error(e.getMessage(),e);
		}		
		return pseudoId;
    }    
    
    
    /**
     * Method to register the time of the execution of the joint point
     * @param elapsedTime number of millisecond that took to execute the joint point (method)
     * @param jp joint point that its execution has be measure
     */     
    private void registerExecutionTime(long elapsedTime, ProceedingJoinPoint jp){
    	try{
	    	AopAdministrable target = (AopAdministrable)jp.getTarget();
	    	
	    	List<String> parameters = new ArrayList<String>();
	    	for (Object arg:jp.getArgs()){
	    		parameters.add(arg!=null?arg.toString():"null");
	    	}
	    	
	    	logger.trace("Elapsed time for method {} with parameters {} was {}ms.",new Object[]{jp.getSignature(),parameters,elapsedTime});
	    	
			StackTraceElement currentStackTraceElement = Thread.currentThread().getStackTrace()[4];
			target.registerMethodExecutionTime(currentStackTraceElement.getMethodName(),target.getTargetClassName(),parameters,elapsedTime,levelDBLog,"aop","aop");
		}
		catch (Exception ex){
			logger.error(ex.getMessage(),ex);
		}    	
    }       
    
    
    /**
     * Method to raise the exception that occurred in the execution of the join point once 
     * we've added to it the pseudoExceptionId to be show to the user
     * @param ex ex exception occurs in the execution of the joint point
     * @param idException pseudoExceptionId to be show to the user
     * @return the exception occurred once we've added to it the pseudoExceptionId to be showed to the user
     */    
    private Exception raiseException(Exception ex, String idException){       	
    	if (ex.getClass() == XMapServiceFault_Exception.class){
    		((XMapServiceFault_Exception)ex).getFaultInfo().setIdException(idException);
    	}   	
    	return ex;
    }
    
}
