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
import java.util.List;

public class RelationshipPairTaxaGWT implements Serializable {

	private final static long serialVersionUID = 1L;
		
	private String leftTaxonId;
	private String leftTaxonName;
	private String leftTaxonRank;
	private String relationship;
	private String rightTaxonId;
	private String rightTaxonName;
	private String rightTaxonRank;
	private List<RelationshipPairTaxaEntryGWT> elementsRel;


	public String getLeftTaxonId() {
		return leftTaxonId;
	}


	public void setLeftTaxonId(String leftTaxonId) {
		this.leftTaxonId = leftTaxonId;
	}


	public String getLeftTaxonName() {
		return leftTaxonName;
	}


	public void setLeftTaxonName(String leftTaxonName) {
		this.leftTaxonName = leftTaxonName;
	}


	public String getLeftTaxonRank() {
		return leftTaxonRank;
	}


	public void setLeftTaxonRank(String leftTaxonRank) {
		this.leftTaxonRank = leftTaxonRank;
	}


	public String getRelationship() {
		return relationship;
	}


	public void setRelationship(String relationship) {
		this.relationship = relationship;
	}


	public String getRightTaxonId() {
		return rightTaxonId;
	}


	public void setRightTaxonId(String rightTaxonId) {
		this.rightTaxonId = rightTaxonId;
	}


	public String getRightTaxonName() {
		return rightTaxonName;
	}


	public void setRightTaxonName(String rightTaxonName) {
		this.rightTaxonName = rightTaxonName;
	}


	public String getRightTaxonRank() {
		return rightTaxonRank;
	}


	public void setRightTaxonRank(String rightTaxonRank) {
		this.rightTaxonRank = rightTaxonRank;
	}


	public List<RelationshipPairTaxaEntryGWT> getElementsRel() {
		return elementsRel;
	}


	public void setElementsRel(List<RelationshipPairTaxaEntryGWT> elementsRel) {
		this.elementsRel = elementsRel;
	}


	public RelationshipPairTaxaGWT(){
		
	}


	public RelationshipPairTaxaGWT(String leftTaxonId, String leftTaxonName,
			String leftTaxonRank, String relationship, String rightTaxonId,
			String rightTaxonName, String rightTaxonRank,
			List<RelationshipPairTaxaEntryGWT> elementsRel) {
		super();
		this.leftTaxonId = leftTaxonId;
		this.leftTaxonName = leftTaxonName;
		this.leftTaxonRank = leftTaxonRank;
		this.relationship = relationship;
		this.rightTaxonId = rightTaxonId;
		this.rightTaxonName = rightTaxonName;
		this.rightTaxonRank = rightTaxonRank;
		this.elementsRel = elementsRel;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((elementsRel == null) ? 0 : elementsRel.hashCode());
		result = prime * result
				+ ((leftTaxonId == null) ? 0 : leftTaxonId.hashCode());
		result = prime * result
				+ ((leftTaxonName == null) ? 0 : leftTaxonName.hashCode());
		result = prime * result
				+ ((leftTaxonRank == null) ? 0 : leftTaxonRank.hashCode());
		result = prime * result
				+ ((relationship == null) ? 0 : relationship.hashCode());
		result = prime * result
				+ ((rightTaxonId == null) ? 0 : rightTaxonId.hashCode());
		result = prime * result
				+ ((rightTaxonName == null) ? 0 : rightTaxonName.hashCode());
		result = prime * result
				+ ((rightTaxonRank == null) ? 0 : rightTaxonRank.hashCode());
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
		RelationshipPairTaxaGWT other = (RelationshipPairTaxaGWT) obj;
		if (elementsRel == null) {
			if (other.elementsRel != null)
				return false;
		} else if (!elementsRel.equals(other.elementsRel))
			return false;
		if (leftTaxonId == null) {
			if (other.leftTaxonId != null)
				return false;
		} else if (!leftTaxonId.equals(other.leftTaxonId))
			return false;
		if (leftTaxonName == null) {
			if (other.leftTaxonName != null)
				return false;
		} else if (!leftTaxonName.equals(other.leftTaxonName))
			return false;
		if (leftTaxonRank == null) {
			if (other.leftTaxonRank != null)
				return false;
		} else if (!leftTaxonRank.equals(other.leftTaxonRank))
			return false;
		if (relationship == null) {
			if (other.relationship != null)
				return false;
		} else if (!relationship.equals(other.relationship))
			return false;
		if (rightTaxonId == null) {
			if (other.rightTaxonId != null)
				return false;
		} else if (!rightTaxonId.equals(other.rightTaxonId))
			return false;
		if (rightTaxonName == null) {
			if (other.rightTaxonName != null)
				return false;
		} else if (!rightTaxonName.equals(other.rightTaxonName))
			return false;
		if (rightTaxonRank == null) {
			if (other.rightTaxonRank != null)
				return false;
		} else if (!rightTaxonRank.equals(other.rightTaxonRank))
			return false;
		return true;
	}


	@Override
	public String toString() {
		return "RelationshipPairTaxaGWT [leftTaxonId=" + leftTaxonId
				+ ", leftTaxonName=" + leftTaxonName + ", leftTaxonRank="
				+ leftTaxonRank + ", relationship=" + relationship
				+ ", rightTaxonId=" + rightTaxonId + ", rightTaxonName="
				+ rightTaxonName + ", rightTaxonRank=" + rightTaxonRank
				+ ", elementsRel=" + elementsRel + "]";
	}


	
	
}
