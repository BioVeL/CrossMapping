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
public class XMapGWT implements Serializable
{
    
	private static final long serialVersionUID = 6046953477645263339L;

	protected String id;
    
    protected String shortName;
    
    protected String longName;
    
    protected String leftChecklistName;
    
    protected String leftChecklistId;
    
    protected String rightChecklistName;
    
    protected String rightChecklistId;
    
    protected boolean strict;
    
    protected IdentifyExtraTaxaTypeGWT identifyExtraTaxa;
    
    protected TaskStatusGWT status;
    
    protected boolean compareHigherTaxa;
    
    protected TaxonomicRankGWT highestRankToCompare;
    
    protected String taskId;
    
    protected String tableName;
   

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getLongName() {
		return longName;
	}

	public void setLongName(String longName) {
		this.longName = longName;
	}

	public String getLeftChecklistName() {
		return leftChecklistName;
	}

	public void setLeftChecklistName(String leftChecklistName) {
		this.leftChecklistName = leftChecklistName;
	}

	public String getLeftChecklistId() {
		return leftChecklistId;
	}

	public void setLeftChecklistId(String leftChecklistId) {
		this.leftChecklistId = leftChecklistId;
	}

	public String getRightChecklistName() {
		return rightChecklistName;
	}

	public void setRightChecklistName(String rightChecklistName) {
		this.rightChecklistName = rightChecklistName;
	}

	public String getRightChecklistId() {
		return rightChecklistId;
	}

	public void setRightChecklistId(String rightChecklistId) {
		this.rightChecklistId = rightChecklistId;
	}

	public boolean isStrict() {
		return strict;
	}

	public void setStrict(boolean strict) {
		this.strict = strict;
	}

	public IdentifyExtraTaxaTypeGWT getIdentifyExtraTaxa() {
		return identifyExtraTaxa;
	}

	public void setIdentifyExtraTaxa(IdentifyExtraTaxaTypeGWT identifyExtraTaxa) {
		this.identifyExtraTaxa = identifyExtraTaxa;
	}

	public TaskStatusGWT getStatus() {
		return status;
	}

	public void setStatus(TaskStatusGWT status) {
		this.status = status;
	}

	public boolean isCompareHigherTaxa() {
		return compareHigherTaxa;
	}

	public void setCompareHigherTaxa(boolean compareHigherTaxa) {
		this.compareHigherTaxa = compareHigherTaxa;
	}

	public TaxonomicRankGWT getHighestRankToCompare() {
		return highestRankToCompare;
	}

	public void setHighestRankToCompare(TaxonomicRankGWT highestRankToCompare) {
		this.highestRankToCompare = highestRankToCompare;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	
    public XMapGWT(){}

	public XMapGWT(String id, String shortName, String longName,
			String leftChecklistName, String leftChecklistId,
			String rightChecklistName, String rightChecklistId, boolean strict,
			IdentifyExtraTaxaTypeGWT identifyExtraTaxa, TaskStatusGWT status,
			boolean compareHigherTaxa, TaxonomicRankGWT highestRankToCompare,
			String taskId, String tableName) {
		super();
		this.id = id;
		this.shortName = shortName;
		this.longName = longName;
		this.leftChecklistName = leftChecklistName;
		this.leftChecklistId = leftChecklistId;
		this.rightChecklistName = rightChecklistName;
		this.rightChecklistId = rightChecklistId;
		this.strict = strict;
		this.identifyExtraTaxa = identifyExtraTaxa;
		this.status = status;
		this.compareHigherTaxa = compareHigherTaxa;
		this.highestRankToCompare = highestRankToCompare;
		this.taskId = taskId;
		this.tableName = tableName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (compareHigherTaxa ? 1231 : 1237);
		result = prime
				* result
				+ ((highestRankToCompare == null) ? 0 : highestRankToCompare
						.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime
				* result
				+ ((identifyExtraTaxa == null) ? 0 : identifyExtraTaxa
						.hashCode());
		result = prime
				* result
				+ ((leftChecklistName == null) ? 0 : leftChecklistName
						.hashCode());
		result = prime * result
				+ ((leftChecklistId == null) ? 0 : leftChecklistId.hashCode());
		result = prime * result
				+ ((longName == null) ? 0 : longName.hashCode());
		result = prime
				* result
				+ ((rightChecklistName == null) ? 0 : rightChecklistName
						.hashCode());
		result = prime
				* result
				+ ((rightChecklistId == null) ? 0 : rightChecklistId.hashCode());
		result = prime * result
				+ ((shortName == null) ? 0 : shortName.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + (strict ? 1231 : 1237);
		result = prime * result
				+ ((tableName == null) ? 0 : tableName.hashCode());
		result = prime * result + ((taskId == null) ? 0 : taskId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		XMapGWT other = (XMapGWT) obj;
		if (compareHigherTaxa != other.compareHigherTaxa)
			return false;
		if (highestRankToCompare == null) {
			if (other.highestRankToCompare != null)
				return false;
		} else if (!highestRankToCompare.equals(other.highestRankToCompare))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (identifyExtraTaxa != other.identifyExtraTaxa)
			return false;
		if (leftChecklistName == null) {
			if (other.leftChecklistName != null)
				return false;
		} else if (!leftChecklistName.equals(other.leftChecklistName))
			return false;
		if (leftChecklistId == null) {
			if (other.leftChecklistId != null)
				return false;
		} else if (!leftChecklistId.equals(other.leftChecklistId))
			return false;
		if (longName == null) {
			if (other.longName != null)
				return false;
		} else if (!longName.equals(other.longName))
			return false;
		if (rightChecklistName == null) {
			if (other.rightChecklistName != null)
				return false;
		} else if (!rightChecklistName.equals(other.rightChecklistName))
			return false;
		if (rightChecklistId == null) {
			if (other.rightChecklistId != null)
				return false;
		} else if (!rightChecklistId.equals(other.rightChecklistId))
			return false;
		if (shortName == null) {
			if (other.shortName != null)
				return false;
		} else if (!shortName.equals(other.shortName))
			return false;
		if (status != other.status)
			return false;
		if (strict != other.strict)
			return false;
		if (tableName == null) {
			if (other.tableName != null)
				return false;
		} else if (!tableName.equals(other.tableName))
			return false;
		if (taskId == null) {
			if (other.taskId != null)
				return false;
		} else if (!taskId.equals(other.taskId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "XMapGWT [id=" + id + ", shortName=" + shortName + ", longName="
				+ longName + ", leftChecklistName=" + leftChecklistName
				+ ", leftChecklistId=" + leftChecklistId
				+ ", rightChecklistName=" + rightChecklistName
				+ ", rightChecklistId=" + rightChecklistId + ", strict="
				+ strict + ", identifyExtraTaxa=" + identifyExtraTaxa
				+ ", status=" + status + ", compareHigherTaxa="
				+ compareHigherTaxa + ", highestRankToCompare="
				+ highestRankToCompare + ", taskId=" + taskId + ", tableName="
				+ tableName + "]";
	}
    
  
	
}
