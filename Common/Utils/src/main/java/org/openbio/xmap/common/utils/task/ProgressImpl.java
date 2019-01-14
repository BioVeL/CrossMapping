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

import org.openbio.xmap.common.serviceinterfaces.types.xmap.TaskStatus;

/**
 * Class that implement the progress of a task
 * <p>
 * OpenBio XMap
 * <p>
 * Date: 15/05/12
 * @author scmjpg
 * @param <R> generic class that indicates the return value of the task.
 */
public class ProgressImpl<R> implements Progress<R> {

	/****************************************************************************************/
	/* ATTRIBUTES																			*/														
	/****************************************************************************************/
	
	/**
	 * State of the task
	 */
    TaskStatus state;
    
    /**
     * String with the name of the current phase of the task
     */
    String phase;
    
    /**
     * String with the name of the current sub-phase of the task 
     */
    String subPhase;
    
    /**
     * integer that reflects the amount of work performed for the current phase of the task
     */
    int curProgress;
    
    /**
     * integer that reflects the maximum amount of work to be performed for the current phase of the task
     */
    int maxProgress;
    
    /**
     * object with the result value of execution of the task
     */
    R result;
    
    /**
     * exception when the task fails 
     */
    TaskException exception;
    
    long startDate;
    
    long finishDate;    

    
	/****************************************************************************************/
	/* CONSTRUCTORS																			*/		
	/****************************************************************************************/
    
    /**
     * Constructor of ProgressImpl
     */
    ProgressImpl() {
        state = TaskStatus.PENDING;
        curProgress = 0;
        maxProgress = 1;
               
        startDate = System.currentTimeMillis();
        finishDate = -1;
    }

    /**
     * Constructor of ProgressImpl that receives an ProgresImpl object to copy some of its values
     * @param old ProgresImpl object to copy some of its values
     */
    ProgressImpl(ProgressImpl old) {    	
        state = old.state;
        phase = old.phase;
        subPhase = old.subPhase;
        curProgress = old.curProgress;
        maxProgress = old.maxProgress;
        startDate = old.startDate;
        finishDate = old.finishDate;
    }

    
    
	/****************************************************************************************/
	/* PUBLIC METHODS																		*/														
	/****************************************************************************************/
    
    /**
     * Method responsible to pass to string the Progress object
     * @return string with the information of this object
     */
    @Override
    public String toString() {
        return "ProgressImpl{" +
                "state=" + state +
                ", phase=" + quote(phase) +
                ", subPhase=" + quote(subPhase) +
                ", curProgress=" + curProgress +
                ", maxProgress=" + maxProgress +
                ", result=" + result +
                ", exception=" + exception +
                '}';
    }

    /**
     * Method responsible to retrieve the state of the task
     * @return a TaskStatus
     */    
    @Override
    public TaskStatus getState() {
        return state;
    }

    /**
     * Method responsible to retrieve a description of the current phase.
     * @return a string describing the phase
     */    
    @Override
    public String getPhase() {
        if (phase == null) {
            return subPhase == null ? null : subPhase;
        }
        else {
            return subPhase == null ? phase : phase + ": " + subPhase;
        }
    }

    /**
     * Method responsible to retrieve a value which reflects the amount of work performed for the current phase of the task.
     * @return an integer indicating the proportion of the task completed (in relation to {@code maxProgress})
     */    
    @Override
    public int getCurProgress() {
        return curProgress;
    }

    /**
     * Method responsible to retrieve the maximum value for the progress.
     * @return an integer indicating the maximum value the current progress will reach
     */    
    @Override
    public int getMaxProgress() {
        return maxProgress;
    }

    /**
     * Method responsible to retrieve the percentage of the completion of the task
     * @return a float with a value from 0.0 to 100.0 to indicate the percentage completion of the task
     */    
    @Override
    public float getPercentProgress() {
        return (100.0f * curProgress) / maxProgress;
    }

    /**
     * Method responsible to retrieve result object from the task if its state is DONE, otherwise the return value is undefined
     * @return if state is {@code DONE}, return a result object from the task, otherwise the return value is undefined
     */    
    @Override
    public R getResult() {
        return result;
    }

    /**
     * Method responsible to return the exception indicating the reason for failure when the state of the task is fail, 
     * otherwise the return value is undefined
     * @return if state is {@code FAIL}, return an exception indicating the reason for failure, otherwise the
     * return value is undefined
     */    
    @Override
    public TaskException getException() {
        return exception;
    }
    
    @Override
	public long getStartDate() {
		return this.startDate;
	}

	@Override
	public long getFinishDate() {		
		return this.finishDate;
	}    
        
    
	/****************************************************************************************/
	/* PRIVATE METHODS																		*/														
	/****************************************************************************************/	
    


	/**
     * Method responsible to quote the string received as parameter
     * @param s string to be quoted
     * @return if the string is null it will return "null", otherwise the string received as parameter 
     * inside quotation marks
     */
    private static String quote(String s) {
        return s != null ? '"' + s + '"' : "null";
    }
    
}
