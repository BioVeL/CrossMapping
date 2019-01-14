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
 * Class that implements the ProgressWriter, but will not write anything
 * <p>
 * OpenBio XMap
 * <p>
 * Date: 15/05/12
 * @author scmjpg
 */
public class NoProgressWriter implements ProgressWriter {

	/****************************************************************************************/
	/* ATTRIBUTES																			*/														
	/****************************************************************************************/
	
	/**
	 * Singleton of this instance
	 */
    private static ProgressWriter singleton = new NoProgressWriter();

    
    
	/****************************************************************************************/
	/* PUBLIC METHODS																		*/		
	/****************************************************************************************/
    
    /**
     * Method responsible to return the singleton instance of this class 
     * @return  instance of the ProgressWriter
     */
    public static ProgressWriter getInstance() {
        return singleton;
    }

    /**
     * Method responsible to set the current phase for the task     
     * @param phase a brief description of the sub-task to be performed
     */
    @Override
    public void startPhase(String phase) {
    }

    /**
     * Method responsible to set the current phase to "cancelling". This is useful when cancellation may take a significant
     * amount of time, and hence should display progress.
     */    
    @Override
    public void startCancellingPhase() {
    }

    /**
     * Method responsible to set the current phase to "cancelling", and also set the progress for the phase
     * @param curProgress the current progress value
     * @param maxProgress the maximum value the current progress will reach
     */    
    @Override
    public void startCancellingPhase(int curProgress, int maxProgress) {
    }

    /**
     * Method responsible to set the phase, set the state to {@code ACTIVE}, and also set the progress for the phase
     * @param phase a brief description of the sub-task to be performed
     * @param curProgress the current progress value
     * @param maxProgress the maximum value the current progress will reach
     */    
    @Override
    public void startActivePhase(String phase, int curProgress, int maxProgress) {
    }

    /**
     * Method responsible to set the state to {@code ACTIVE}
     */    
    @Override
    public void setActive() {
    }

    /**
     * Method responsible to set the state to {@code ACTIVE}
     * @param curProgress the current progress value
     * @param maxProgress the maximum value the current progress will reach
     */    
    @Override
    public void setActive(int curProgress, int maxProgress) {
    }

    /**
     * Method responsible to set the progress of the task phase
     * @param curProgress the current progress value
     */    
    @Override
    public void updateActive(int curProgress) {
    }

    /**
     * Method responsible to set the progress of the task phase, updating the maximum progress as well
     * @param curProgress the current progress value
     * @param maxProgress the maximum value the current progress will reach
     */    
    @Override
    public void updateActive(int curProgress, int maxProgress) {
    }

    /**
     * Method responsible to set the progress to reflect the progress of a sub-task which implements {@code ProgressReader}.
     * <p>
     * If the sub-task provides Progress information, it can be passed to the main task's progress
     * using this method.
     * @param progress the {@code Progress} for a sub-task
     */    
    @Override
    public void updateActiveSubtask(Progress progress) {
    }

    /**
     * Method responsible to indicate that the task has successfully completed. There is no return value.
     */    
    @Override
    public void setDone() {
    }

    /**
     * Method responsible to indicate that the task has successfully completed, with a return value
     * @param result the return value for the task
     */    
    @Override
    public void setDone(Object result) {
    }

    /**
     * Method responsible to indicate that the task has failed
     * @param message a message describing the failure
     */    
    @Override
    public void setFail(String message) {
    }

    /**
     * Method responsible to indicate that the task has failed
     * @param ex an exception describing the failure
     */    
    @Override
    public void setException(TaskException ex) {
    }
}
