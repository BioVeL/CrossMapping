/*
 * #%L
 * XMap GUI CrossMapping Portlet
 * %%
 * Copyright (C) 2012 - 2013 Cardiff University
 * %%
 * Use of this software is governed by the attached licence file. If no licence 
 * file is present the software must not be used.
 * 
 * The use of this software, including reverse engineering, for any other purpose 
 * is prohibited without the express written permission of the software owners, 
 * Cardiff University and Italy National Research Council.
 * #L%
 */
package org.gcube.portlets.user.crossmapping.shared;

import java.io.Serializable;

public class TaskProgressGWT implements Serializable
{
	private static final long serialVersionUID = 754013388577705947L;
	
	protected String taskName;
    protected TaskStatusGWT status;
    protected String details;
    protected int percentage;
    protected long startDate;
    protected long finishDate;
    
    public TaskProgressGWT(){}
    
	/**
	 * @param taskName
	 * @param status
	 * @param details
	 * @param percentage
	 * @param startDate
	 * @param finishDate
	 */
	public TaskProgressGWT(String taskName, TaskStatusGWT status, String details, int percentage, long startDate, long finishDate) {
		this.taskName = taskName;
		this.status = status;
		this.details = details;
		this.percentage = percentage;
		this.startDate = startDate;
		this.finishDate = finishDate;
	}

	/**
	 * @return the taskName
	 */
	public String getTaskName() {
		return taskName;
	}

	/**
	 * @param taskName the taskName to set
	 */
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	/**
	 * @return the status
	 */
	public TaskStatusGWT getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(TaskStatusGWT status) {
		this.status = status;
	}

	/**
	 * @return the details
	 */
	public String getDetails() {
		return details;
	}

	/**
	 * @param details the details to set
	 */
	public void setDetails(String details) {
		this.details = details;
	}

	/**
	 * @return the percentage
	 */
	public int getPercentage() {
		return percentage;
	}

	/**
	 * @param percentage the percentage to set
	 */
	public void setPercentage(int percentage) {
		this.percentage = percentage;
	}

	/**
	 * @return the startDate
	 */
	public long getStartDate() {
		return startDate;
	}

	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(long startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return the finishDate
	 */
	public long getFinishDate() {
		return finishDate;
	}

	/**
	 * @param finishDate the finishDate to set
	 */
	public void setFinishDate(long finishDate) {
		this.finishDate = finishDate;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TaskProgressGWT [taskName=");
		builder.append(taskName);
		builder.append(", status=");
		builder.append(status);
		builder.append(", details=");
		builder.append(details);
		builder.append(", percentage=");
		builder.append(percentage);
		builder.append(", startDate=");
		builder.append(startDate);
		builder.append(", finishDate=");
		builder.append(finishDate);
		builder.append("]");
		return builder.toString();
	}
    
}
