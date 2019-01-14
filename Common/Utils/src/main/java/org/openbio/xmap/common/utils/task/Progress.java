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
 * Interface with the methods to obtain the progress of a task at a particular moment.
 * <p>
 * For some tasks, it is difficult to provide meaningful progress information for the entire task. 
 * However, the task may contain sub-tasks which can be described as phases. Progress can at the very 
 * least be displayed through changes to the phase. Where the progress of the individual phase sub-tasks 
 * can be obtained, this can be sed to return progress information. When the phase changes, the progress 
 * is permitted to return to 0. This is a common behavior when a new phase is started.
 * <p>
 * The generic class {@code R} indicates the return value of the task thread.
 * <p>
 * OpenBio XMap
 * <p>
 * Date: 15/05/12
 * @author scmjpg 
 * @param <R> generic class that indicates the return value of the task.
 */
public interface Progress<R> {
   
    
	/****************************************************************************************/
	/* PUBLIC METHODS																		*/														
	/****************************************************************************************/
    
    /**
     * Method responsible to retrieve the state of the task
     * @return a TaskStatus
     */
    TaskStatus getState();

    /**
     * Method responsible to retrieve a description of the current phase.
     * @return a string describing the phase
     */
    String getPhase();

    /**
     * Retrieve a value which reflects the amount of work performed for the current phase of the task.
     * @return an integer indicating the proportion of the task completed (in relation to {@code maxProgress})
     */
    int getCurProgress();

    /**
     * Method responsible to retrieve the maximum value for the progress.
     * @return an integer indicating the maximum value the current progress will reach
     */
    int getMaxProgress();

    /**
     * Method responsible to retrieve the percentage of the completion of the task
     * @return a float with a value from 0.0 to 100.0 to indicate the percentage completion of the task
     */
    float getPercentProgress();

    /**
     * Method responsible to retrieve result object from the task if its state is DONE, otherwise the return value is undefined
     * @return if state is {@code DONE}, return a result object from the task, otherwise the return value is undefined
     */
    R getResult();

    /**
     * Method responsible to return the exception indicating the reason for failure when the state of the task is fail, 
     * otherwise the return value is undefined
     * @return if state is {@code FAIL}, return an exception indicating the reason for failure, otherwise the
     * return value is undefined
     */
    TaskException getException();
    
    long getStartDate();
    
    long getFinishDate();
}
