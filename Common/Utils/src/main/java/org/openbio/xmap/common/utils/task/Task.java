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

import org.openbio.xmap.common.serviceinterfaces.types.xmap.TaskId;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.TaskProgress;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.TaskResponse;
import org.openbio.xmap.common.serviceinterfaces.types.xmap.TaskStatus;
import org.openbio.xmap.common.utils.misc.Misc;

/**
 * Abstract class that defines a generic Task
 * <p>
 * OpenBio XMap
 * <p>
 * Date: 15/05/12
 * @author scmjpg
 * @param <R> generic class that indicates the return value of the task.
 */
public abstract class Task<R> extends ProgressMonitor<R> implements Runnable {

	/****************************************************************************************/
	/* ATTRIBUTES																			*/														
	/****************************************************************************************/
	
	/**
	 * Thread in which the task will be executed
	 */
    private Thread runThread = null;
  
    /**
     * Indicates that the caller has requested the task to abort.
     */
    private boolean cancelled = false;
    
    
    protected TaskId taskId = new TaskId(Misc.generatePseudoId());
    
    protected String taskName;
         
	protected String user; //User that started the task
	
	protected String scope; //Scope in which the user started the task
	
	protected String taskType;
	
	protected String exceptionId;

  	
	/**
     * Get the thread in which the task will be executed
     * @return the thread in which the task will be executed
     */
    public synchronized Thread getRunThread() {
        return runThread;
    }

    /**
     * Set the thread in which the task will be executed
     * @param runThread
     */
    public synchronized void setRunThread(Thread runThread) {
        this.runThread = runThread;
    }
           
    public TaskId getTaskId() {
		return taskId;
	}

	public void setTaskId(TaskId taskId) {
		this.taskId = taskId;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}    
	
	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}    
	
    public String getTaskType() {
		return taskType;
	}

	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}	
	    
    public String getExceptionId() {
		return exceptionId;
	}

	public void setExceptionId(String exceptionId) {
		this.exceptionId = exceptionId;
	}	
	
	/****************************************************************************************/
	/* PUBLIC METHODS																		*/														
	/****************************************************************************************/

	/**
     * Method responsible to called the task, interrupting the executing of the thread in
     * which the task is running
     */
    public synchronized void cancel() {
        cancelled = true;
        startCancellingPhase();
        if (runThread != null) {
            runThread.interrupt();
        }
    }

    /**
     * Method responsible to check if the task has been cancelled
     * @return true if the task has been cancelled, false otherwise 
     */
    public synchronized boolean isCancelled() {
        return cancelled;
    }
    
    /**
     * Method responsible to run the task
     */
    @Override
    public void run() {
        try {
            setRunThread(Thread.currentThread());
            throwIfCancelled();
            perform();
            setDone();
        }
        catch (TaskCancelledException ex){
        	Thread.currentThread().interrupt();
        }
        catch (TaskException e) {
            setException(e);
        }
        finally {
            setRunThread(null);
        }
    }

    /**
     * Abstract method that will define how the task is perform
     * @throws TaskException
     */
    public abstract void perform() throws TaskException;

    /**
     * Method to contain the code to do the clean up after the task has been executed
     */
    public void cleanup() {
    }

    
	public TaskProgress getTaskProgress() {
		Progress progress = this.getProgress();
					
		TaskStatus status;
		String detail;
		switch (progress.getState()) {
			case PENDING:
				status = TaskStatus.fromValue("Pending");
				detail = progress.getPhase();
				break;
			case ACTIVE:
				status = TaskStatus.fromValue("Active");
				detail = progress.getPhase();
				break;
			case CANCELLING:
				status = TaskStatus.fromValue("Cancelling");
				detail = progress.getPhase();
				break;				
			case COMPLETED:
				status = TaskStatus.fromValue("Completed");
				detail = progress.getPhase();
				break;
			default:
				status = TaskStatus.fromValue("Aborted");
				if (progress.getException() != null){
					detail = "Error: " + progress.getException().getMessage();
				}
				else{
					detail = "fail";
				}
				break;
		}
		TaskResponse taskResponse = (progress.getResult()!=null ? (TaskResponse) progress.getResult() : null);
				
		return new TaskProgress(this.getTaskName(),this.getTaskType(), status, detail, (int) progress.getPercentProgress(),
				progress.getStartDate(), progress.getFinishDate(), this.getUser(), this.getScope(), taskResponse, null);  
	        
	}   
    
    
	/****************************************************************************************/
	/* PROTECTED METHODS																	*/														
	/****************************************************************************************/	    
    
    /**
     * Method responsible to throw an exception TaskCancelledException when
     * the task has been cancel
     * @throws TaskCancelledException
     */
    protected synchronized void throwIfCancelled() throws TaskCancelledException {
        if (cancelled) {
            startCancellingPhase();
            throw new TaskCancelledException();
        }
    }
    

}
