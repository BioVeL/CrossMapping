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

import java.util.Observable;

import org.openbio.xmap.common.serviceinterfaces.types.xmap.TaskStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class to monitor the progress of a task.
 * It implements progress reader and progress writer and it also extends observable
 * <p>
 * OpenBio XMap
 * <p>
 * Date: 08/11/11
 * @author scmjpg
 * @param <R> generic class that indicates the return value of the task.
 */
public class ProgressMonitor<R> extends Observable implements ProgressReader<R>,ProgressWriter<R> {

	/****************************************************************************************/
	/* ATTRIBUTES																			*/														
	/****************************************************************************************/
	
	/**
	 * slf4j for this class
	 */
	private static Logger logger = LoggerFactory.getLogger(ProgressMonitor.class);
	
	/**
	 * saved progress of this task
	 */
    private ProgressImpl<R> savedProgress = new ProgressImpl<R>();
   
    /**
     * Indicates whether the current {@code savedProgress} field has been exported from
     * this instance (through the {@code getProgress} method. If it has, we create
     * a new copy when the state is modified. Otherwise, we can modify this instance.
     */
    private boolean released = false;
    
    /**
     * Indicates that the running task has acknowledged a request to abort.
     */
    private boolean cancelling = false;

    
    
	/****************************************************************************************/
	/* PUBLIC METHODS																		*/														
	/****************************************************************************************/    
    
    /**
     * Method responsible to return the progress info of the task
     * @return the progress of the task
     */    
    @Override
    public synchronized Progress<R> getProgress() {
        released = true;
        return savedProgress;
    }

    /**
     * Method responsible to check whether the task has finished    
     * @return true if the task has completed successfully or failed
     */    
    @Override
    public synchronized boolean isFinished() {
        return savedProgress.state == TaskStatus.COMPLETED || savedProgress.state == TaskStatus.FAILED;
    }

    /**
     * Method responsible to set the current phase for the task     
     * @param phase a brief description of the sub-task to be performed
     */    
    @Override
    public void startPhase(String phase) {
        synchronized (this) {
            if (savedProgress.state == TaskStatus.PENDING || savedProgress.state == TaskStatus.ACTIVE){
	            ProgressImpl<R> progress = released ? new ProgressImpl<R>(savedProgress) : savedProgress;
	            progress.state = TaskStatus.PENDING;
	            progress.phase = phase;
	            progress.subPhase = null;
	            progress.curProgress = 0;
	            updateProgress(progress);
            }
        }
        notifyObservers();
    }

    /**
     * Method responsible to set the current phase to "cancelling". This is useful when cancellation may take a significant
     * amount of time, and hence should display progress.
     */    
    @Override
    public void startCancellingPhase() {
        startCancellingPhase(0, 1);
    }

    /**
     * Method responsible to set the current phase to "cancelling", and also set the progress for the phase
     * @param curProgress the current progress value
     * @param maxProgress the maximum value the current progress will reach
     */    
    @Override
    public void startCancellingPhase(int curProgress, int maxProgress) {
        synchronized (this) {
            if (!isFinished()) {
                ProgressImpl<R> progress = released ? new ProgressImpl<R>(savedProgress) : savedProgress;
                progress.state = TaskStatus.CANCELLING;
                progress.phase = "cancelling";
                progress.subPhase = null;
                progress.curProgress = curProgress;
                progress.maxProgress = maxProgress;
                cancelling = true;
                updateProgress(progress);
            }
        }
        notifyObservers();
    }

    /**
     * Method responsible to set the phase, set the state to {@code ACTIVE}, and also set the progress for the phase
     * @param phase a brief description of the sub-task to be performed
     * @param curProgress the current progress value
     * @param maxProgress the maximum value the current progress will reach
     */    
    @Override
    public void startActivePhase(String phase, int curProgress, int maxProgress) {
        synchronized (this) {
            if (savedProgress.state == TaskStatus.PENDING || savedProgress.state == TaskStatus.ACTIVE){
                ProgressImpl<R> progress = released ? new ProgressImpl<R>(savedProgress) : savedProgress;
                progress.state = TaskStatus.ACTIVE;
                progress.phase = phase;
                progress.subPhase = null;
                progress.curProgress = curProgress;
                progress.maxProgress = maxProgress;
                updateProgress(progress);            	
            }
        }
        notifyObservers();
    }

    /**
     * Method responsible to set the state to {@code ACTIVE}
     */    
    @Override
    public void setActive() {
        setActive(0, 1);
    }

    /**
     * Method responsible to set the state to {@code ACTIVE}
     * @param curProgress the current progress value
     * @param maxProgress the maximum value the current progress will reach
     */    
    @Override
    public void setActive(int curProgress, int maxProgress) {
        synchronized (this) {
            if (savedProgress.state != TaskStatus.ACTIVE){
	            if (savedProgress.state == TaskStatus.PENDING) {
	                ProgressImpl<R> progress = released ? new ProgressImpl<R>(savedProgress) : savedProgress;
	                progress.state = TaskStatus.ACTIVE;
	                progress.curProgress = curProgress;
	                progress.maxProgress = maxProgress;
	                updateProgress(progress);
	            }
            }
        }
        notifyObservers();
    }

    /**
     * Method responsible to set the progress of the task phase
     * @param curProgress the current progress value
     */    
    @Override
    public void updateActive(int curProgress) {
        synchronized (this) {
            ProgressImpl<R> progress = savedProgress;
            if (progress.state != TaskStatus.PENDING){
	            if (progress.state == TaskStatus.ACTIVE && progress.curProgress != curProgress) {
	                if (released) {
	                    progress = new ProgressImpl<R>(progress);
	                }
	                progress.curProgress = curProgress;
	                updateProgress(progress);
	            }
            }
        }
        notifyObservers();
    }

    /**
     * Method responsible to set the progress of the task phase, updating the maximum progress as well
     * @param curProgress the current progress value
     * @param maxProgress the maximum value the current progress will reach
     */    
    @Override
    public void updateActive(int curProgress, int maxProgress) {
        synchronized (this) {
            ProgressImpl<R> progress = savedProgress;
            if (progress.state != TaskStatus.PENDING){
	            if (progress.state == TaskStatus.ACTIVE &&
	                    (progress.curProgress != curProgress || progress.maxProgress != maxProgress)) {
	                if (released) {
	                    progress = new ProgressImpl<R>(progress);
	                }
	                progress.curProgress = curProgress;
	                progress.maxProgress = maxProgress;
	                updateProgress(progress);
	            }
            }
        }
        notifyObservers();
    }

    /**
     * Method responsible to set the progress to reflect the progress of a sub-task which implements {@code ProgressReader}.
     * <p>
     * If the sub-task provides Progress information, it can be passed to the main task's progress
     * using this method.
     * @param subTaskProgress the {@code Progress} for a sub-task
     */    
    @Override
    public void updateActiveSubtask(Progress subTaskProgress) {
        synchronized (this) {
            ProgressImpl<R> progress = savedProgress;
            if (progress.state == TaskStatus.ACTIVE) {
                if (released) {
                    progress = new ProgressImpl<R>(progress);
                }
                progress.subPhase = subTaskProgress.getPhase();
                progress.curProgress = subTaskProgress.getCurProgress();
                progress.maxProgress = subTaskProgress.getMaxProgress();
                updateProgress(progress);
            }
        }
        notifyObservers();
    }

    /**
     * Method responsible to indicate that the task has successfully completed. There is no return value.
     */    
    @Override
    public void setDone() {
        setDone(null);
    }

    /**
     * Method responsible to indicate that the task has successfully completed, with a return value
     * @param result the return value for the task
     */    
    @Override
    public void setDone(R result) {
        synchronized (this) {
            if (savedProgress.state == TaskStatus.PENDING || savedProgress.state == TaskStatus.ACTIVE) {
                ProgressImpl<R> progress = released ? new ProgressImpl<R>(savedProgress) : savedProgress;
                if (cancelling) {
                    setCancelState(progress);
                }
                else {
                    progress.state = TaskStatus.COMPLETED;
                    progress.phase = null;
                    progress.curProgress = progress.maxProgress;
                    progress.result = result;
                    progress.finishDate = System.currentTimeMillis();
                }
                updateProgress(progress);
                notifyAll();
            }
        }
        notifyObservers();
    }

    /**
     * Method responsible to indicate that the task has failed
     * @param message a message describing the failure
     */    
    @Override
    public void setFail(String message) {
        synchronized (this) {
            if (savedProgress.state == TaskStatus.PENDING || savedProgress.state == TaskStatus.ACTIVE) {
                ProgressImpl<R> progress = released ? new ProgressImpl<R>(savedProgress) : savedProgress;
                if (cancelling) {
                    setCancelState(progress);
                }
                else {
                    progress.state = TaskStatus.FAILED;
                    progress.phase = null;
                    progress.curProgress = progress.maxProgress;
                    progress.exception = new TaskFailedException(message);
                    progress.finishDate = System.currentTimeMillis();
                }
                updateProgress(progress);
                notifyAll();
            }
        }
        notifyObservers();
    }

    /**
     * Method responsible to indicate that the task has failed
     * @param ex an exception describing the failure
     */    
    @Override
    public void setException(TaskException ex) {
    	logger.error("Task Exception", ex);
        synchronized (this) {
            if (!isFinished()) {
                ProgressImpl<R> progress = released ? new ProgressImpl<R>(savedProgress) : savedProgress;
                if (cancelling) {
                    setCancelState(progress);
                }
                else {
                    progress.state = TaskStatus.FAILED;
                    progress.phase = null;
                    progress.curProgress = progress.maxProgress;
                    progress.exception = ex;
                    progress.finishDate = System.currentTimeMillis();
                }
                updateProgress(progress);
                notifyAll();
            }
        }
        notifyObservers();
    }

    /**
     * Method responsible to wait for task to complete.     
     * @return the return value of the task thread
     * @throws TaskException if the task fails with an exception
     * @throws InterruptedException if the task is interrupted
     */    
    @Override
    public synchronized R waitForResult() throws TaskException, InterruptedException {
        waitForFinished();
        if (savedProgress.state == TaskStatus.FAILED) {
            Exception e = savedProgress.exception;
            if (e instanceof TaskException) {
                throw (TaskException) e;
            }
            else {
                throw new TaskException(e);
            }
        }
        return savedProgress.result;
    }

    /**
     * Method responsible to wait for task to complete or fail.
     * @throws InterruptedException if the task is interrupted
     */    
    @Override
    public synchronized void waitForFinished() throws InterruptedException {
        while (!isFinished()) {
            wait();
        }
    }

    
	/****************************************************************************************/
	/* PROTECTED METHODS																	*/														
	/****************************************************************************************/	    
    
    /**
     * Method responsible to update the saved progress of this task
     * @param progress current progress of the task to be saved
     */
    protected void updateProgress(ProgressImpl<R> progress) {
        savedProgress = progress;
        released = false;
        setChanged();
    }    
    
    
	/****************************************************************************************/
	/* PRIVATE METHODS																		*/														
	/****************************************************************************************/
    
    /**
     * Method responsible set the progress of the task to cancel state
     * @param progress progress of the task to be set to cancel
     */
    private static void setCancelState(ProgressImpl progress) {
        progress.state = TaskStatus.FAILED;
        progress.phase = "Cancelled by the user";
        progress.exception = new TaskCancelledException();
        progress.curProgress = progress.maxProgress;
        progress.finishDate = System.currentTimeMillis();
    }


}
