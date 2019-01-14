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
 * Interface for monitoring progress of task
 * <p>
 * The generic class {@code R} indicates the return value of the task.
 * <p>
 * OpenBio XMap
 * <p>
 * Date: 15/05/12
 * @author scmjpg
 * @param <R> generic class that indicates the return value of the task.
 */
public interface ProgressReader<R> {

    /**
     * Method responsible to return the progress info of the task
     * @return the progress of the task
     */
    Progress<R> getProgress();

    /**
     * Method responsible to wait for task to complete.     
     * @return the return value of the task thread
     * @throws TaskException if the task fails with an exception
     * @throws InterruptedException if the task is interrupted
     */
    R waitForResult() throws TaskException, InterruptedException;

    /**
     * Method responsible to wait for task to complete or fail.
     * @throws InterruptedException if the task is interrupted
     */
    void waitForFinished() throws InterruptedException;

    /**
     * Method responsible to check whether the task has finished    
     * @return true if the task has completed successfully or failed
     */
    boolean isFinished();
}
