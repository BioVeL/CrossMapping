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

public class EntryInXMapGWT implements Serializable {

	private final static long serialVersionUID = 1L;
	
    protected String taxonIdLeft;
    
    protected String checklistNameLeft;
    
    protected String rankLeft;
    
    protected String acceptedNameLeft;        
    
    protected String uuidLeft;
    
    protected String relationship;
    
    protected String taxonIdRight;
    
    protected String checklistNameRight;
    
    protected String rankRight;
    
    protected String acceptedNameRight;
    
    protected String uuidRight;

	public String getTaxonIdLeft() {
		return taxonIdLeft;
	}

	public void setTaxonIdLeft(String taxonIdLeft) {
		this.taxonIdLeft = taxonIdLeft;
	}

	public String getChecklistNameLeft() {
		return checklistNameLeft;
	}

	public void setChecklistNameLeft(String checklistNameLeft) {
		this.checklistNameLeft = checklistNameLeft;
	}

	public String getAcceptedNameLeft() {
		return acceptedNameLeft;
	}

	public void setAcceptedNameLeft(String acceptedNameLeft) {
		this.acceptedNameLeft = acceptedNameLeft;
	}

	public String getUuidLeft() {
		return uuidLeft;
	}

	public void setUuidLeft(String uuidLeft) {
		this.uuidLeft = uuidLeft;
	}

	public String getRelationship() {
		return relationship;
	}

	public void setRelationship(String relationship) {
		this.relationship = relationship;
	}

	public String getTaxonIdRight() {
		return taxonIdRight;
	}

	public void setTaxonIdRight(String taxonIdRight) {
		this.taxonIdRight = taxonIdRight;
	}

	public String getChecklistNameRight() {
		return checklistNameRight;
	}

	public void setChecklistNameRight(String checklistNameRight) {
		this.checklistNameRight = checklistNameRight;
	}

	public String getAcceptedNameRight() {
		return acceptedNameRight;
	}

	public void setAcceptedNameRight(String acceptedNameRight) {
		this.acceptedNameRight = acceptedNameRight;
	}

	public String getUuidRight() {
		return uuidRight;
	}

	public void setUuidRight(String uuidRight) {
		this.uuidRight = uuidRight;
	}

	public String getRankLeft() {
		return rankLeft;
	}

	public void setRankLeft(String rankLeft) {
		this.rankLeft = rankLeft;
	}

	public String getRankRight() {
		return rankRight;
	}

	public void setRankRight(String rankRight) {
		this.rankRight = rankRight;
	}

	public EntryInXMapGWT(){
		
	}

	public EntryInXMapGWT(String taxonIdLeft, String checklistNameLeft,
			String rankLeft, String acceptedNameLeft, String uuidLeft,
			String relationship, String taxonIdRight,
			String checklistNameRight, String rankRight,
			String acceptedNameRight, String uuidRight) {
		super();
		this.taxonIdLeft = taxonIdLeft;
		this.checklistNameLeft = checklistNameLeft;
		this.rankLeft = rankLeft;
		this.acceptedNameLeft = acceptedNameLeft;
		this.uuidLeft = uuidLeft;
		this.relationship = relationship;
		this.taxonIdRight = taxonIdRight;
		this.checklistNameRight = checklistNameRight;
		this.rankRight = rankRight;
		this.acceptedNameRight = acceptedNameRight;
		this.uuidRight = uuidRight;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((acceptedNameLeft == null) ? 0 : acceptedNameLeft.hashCode());
		result = prime
				* result
				+ ((acceptedNameRight == null) ? 0 : acceptedNameRight
						.hashCode());
		result = prime
				* result
				+ ((checklistNameLeft == null) ? 0 : checklistNameLeft
						.hashCode());
		result = prime
				* result
				+ ((checklistNameRight == null) ? 0 : checklistNameRight
						.hashCode());
		result = prime * result
				+ ((rankLeft == null) ? 0 : rankLeft.hashCode());
		result = prime * result
				+ ((rankRight == null) ? 0 : rankRight.hashCode());
		result = prime * result
				+ ((relationship == null) ? 0 : relationship.hashCode());
		result = prime * result
				+ ((taxonIdLeft == null) ? 0 : taxonIdLeft.hashCode());
		result = prime * result
				+ ((taxonIdRight == null) ? 0 : taxonIdRight.hashCode());
		result = prime * result
				+ ((uuidLeft == null) ? 0 : uuidLeft.hashCode());
		result = prime * result
				+ ((uuidRight == null) ? 0 : uuidRight.hashCode());
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
		EntryInXMapGWT other = (EntryInXMapGWT) obj;
		if (acceptedNameLeft == null) {
			if (other.acceptedNameLeft != null)
				return false;
		} else if (!acceptedNameLeft.equals(other.acceptedNameLeft))
			return false;
		if (acceptedNameRight == null) {
			if (other.acceptedNameRight != null)
				return false;
		} else if (!acceptedNameRight.equals(other.acceptedNameRight))
			return false;
		if (checklistNameLeft == null) {
			if (other.checklistNameLeft != null)
				return false;
		} else if (!checklistNameLeft.equals(other.checklistNameLeft))
			return false;
		if (checklistNameRight == null) {
			if (other.checklistNameRight != null)
				return false;
		} else if (!checklistNameRight.equals(other.checklistNameRight))
			return false;
		if (rankLeft == null) {
			if (other.rankLeft != null)
				return false;
		} else if (!rankLeft.equals(other.rankLeft))
			return false;
		if (rankRight == null) {
			if (other.rankRight != null)
				return false;
		} else if (!rankRight.equals(other.rankRight))
			return false;
		if (relationship == null) {
			if (other.relationship != null)
				return false;
		} else if (!relationship.equals(other.relationship))
			return false;
		if (taxonIdLeft == null) {
			if (other.taxonIdLeft != null)
				return false;
		} else if (!taxonIdLeft.equals(other.taxonIdLeft))
			return false;
		if (taxonIdRight == null) {
			if (other.taxonIdRight != null)
				return false;
		} else if (!taxonIdRight.equals(other.taxonIdRight))
			return false;
		if (uuidLeft == null) {
			if (other.uuidLeft != null)
				return false;
		} else if (!uuidLeft.equals(other.uuidLeft))
			return false;
		if (uuidRight == null) {
			if (other.uuidRight != null)
				return false;
		} else if (!uuidRight.equals(other.uuidRight))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "EntryInXMapGWT [taxonIdLeft=" + taxonIdLeft
				+ ", checklistNameLeft=" + checklistNameLeft + ", rankLeft="
				+ rankLeft + ", acceptedNameLeft=" + acceptedNameLeft
				+ ", uuidLeft=" + uuidLeft + ", relationship=" + relationship
				+ ", taxonIdRight=" + taxonIdRight + ", checklistNameRight="
				+ checklistNameRight + ", rankRight=" + rankRight
				+ ", acceptedNameRight=" + acceptedNameRight + ", uuidRight="
				+ uuidRight + "]";
	}
	
	
	
}
