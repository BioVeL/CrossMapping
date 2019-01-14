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

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 * Modification: 10/10/12 F. Quevedo. Changes
 *		 - TaskStatus by TaskStatusGWT
 */
public class ChecklistGWT implements Serializable
{

    private final static long serialVersionUID = 1L;

    protected String id;
    protected String name;
    protected String taskId;
    protected String tableName;
    protected TaskStatusGWT taskStatus;

    /**
     * Default no-arg constructor
     * 
     */
    public ChecklistGWT() {
    }

	/**
	 * @param id
	 * @param name
	 * @param taskId
	 * @param tableName
	 * @param taskStatus
	 */
	public ChecklistGWT(String id, String name, String taskId, String tableName, TaskStatusGWT taskStatus) {
		this.id = id;
		this.name = name;
		this.taskId = taskId;
		this.tableName = tableName;
		this.taskStatus = taskStatus;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the taskId
	 */
	public String getTaskId() {
		return taskId;
	}

	/**
	 * @param taskId the taskId to set
	 */
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	/**
	 * @return the tableName
	 */
	public String getTableName() {
		return tableName;
	}

	/**
	 * @param tableName the tableName to set
	 */
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	/**
	 * @return the taskStatus
	 */
	public TaskStatusGWT getTaskStatus() {
		return taskStatus;
	}

	/**
	 * @param taskStatus the taskStatus to set
	 */
	public void setTaskStatus(TaskStatusGWT taskStatus) {
		this.taskStatus = taskStatus;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ChecklistGWT other = (ChecklistGWT) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ChecklistGWT [id=");
		builder.append(id);
		builder.append(", name=");
		builder.append(name);
		builder.append(", taskId=");
		builder.append(taskId);
		builder.append(", tableName=");
		builder.append(tableName);
		builder.append(", taskStatus=");
		builder.append(taskStatus);
		builder.append("]");
		return builder.toString();
	}
 }
