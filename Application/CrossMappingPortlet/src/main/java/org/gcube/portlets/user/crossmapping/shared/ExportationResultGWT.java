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
import java.util.Date;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 * Modification: 10/10/12 F. Quevedo. Changes
 *		 - TaskStatus by TaskStatusGWT
 */
public class ExportationResultGWT implements Serializable
{

	private static final long serialVersionUID = 7016826672196906892L;

	protected String id;
    protected String name;
    protected TaskStatusGWT status;
    protected String taskId;
    protected String xMapId;
    protected String resultFileURL;
    protected boolean includeAcceptedName;
    protected Date exportDate;
    
    public ExportationResultGWT(){}
    
	/**
	 * @param id
	 * @param name
	 * @param status
	 * @param taskId
	 * @param xMapId
	 * @param resultFileURL
	 * @param includeAcceptedName
	 * @param exportDate
	 */
	public ExportationResultGWT(String id, String name, TaskStatusGWT status, String taskId, String xMapId, String resultFileURL, boolean includeAcceptedName,
			Date exportDate) {
		this.id = id;
		this.name = name;
		this.status = status;
		this.taskId = taskId;
		this.xMapId = xMapId;
		this.resultFileURL = resultFileURL;
		this.includeAcceptedName = includeAcceptedName;
		this.exportDate = exportDate;
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
	 * @return the xMapId
	 */
	public String getxMapId() {
		return xMapId;
	}

	/**
	 * @param xMapId the xMapId to set
	 */
	public void setxMapId(String xMapId) {
		this.xMapId = xMapId;
	}

	/**
	 * @return the resultFileURL
	 */
	public String getResultFileURL() {
		return resultFileURL;
	}

	/**
	 * @param resultFileURL the resultFileURL to set
	 */
	public void setResultFileURL(String resultFileURL) {
		this.resultFileURL = resultFileURL;
	}

	/**
	 * @return the includeAcceptedName
	 */
	public boolean isIncludeAcceptedName() {
		return includeAcceptedName;
	}

	/**
	 * @param includeAcceptedName the includeAcceptedName to set
	 */
	public void setIncludeAcceptedName(boolean includeAcceptedName) {
		this.includeAcceptedName = includeAcceptedName;
	}

	/**
	 * @return the exportDate
	 */
	public Date getExportDate() {
		return exportDate;
	}

	/**
	 * @param exportDate the exportDate to set
	 */
	public void setExportDate(Date exportDate) {
		this.exportDate = exportDate;
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
		ExportationResultGWT other = (ExportationResultGWT) obj;
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
		builder.append("ExportationResultGWT [id=");
		builder.append(id);
		builder.append(", name=");
		builder.append(name);
		builder.append(", status=");
		builder.append(status);
		builder.append(", taskId=");
		builder.append(taskId);
		builder.append(", xMapId=");
		builder.append(xMapId);
		builder.append(", resultFileURL=");
		builder.append(resultFileURL);
		builder.append(", includeAcceptedName=");
		builder.append(includeAcceptedName);
		builder.append(", exportDate=");
		builder.append(exportDate);
		builder.append("]");
		return builder.toString();
	}
 }
