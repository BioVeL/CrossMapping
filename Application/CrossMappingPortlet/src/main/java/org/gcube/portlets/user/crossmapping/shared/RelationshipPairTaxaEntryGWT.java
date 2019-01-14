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

public class RelationshipPairTaxaEntryGWT implements Serializable {

	private final static long serialVersionUID = 1L;

	private String taxId1;
	private String element1;
	private String extra1;
	private String relType;
	private String taxId2;
	private String element2;
    private String extra2;
    private boolean inOthers;
    private boolean nameLevel;
        
	
	public String getTaxId1() {
		return taxId1;
	}


	public void setTaxId1(String taxId1) {
		this.taxId1 = taxId1;
	}


	public String getElement1() {
		return element1;
	}


	public void setElement1(String element1) {
		this.element1 = element1;
	}


	public String getExtra1() {
		return extra1;
	}


	public void setExtra1(String extra1) {
		this.extra1 = extra1;
	}


	public String getRelType() {
		return relType;
	}


	public void setRelType(String relType) {
		this.relType = relType;
	}


	public String getTaxId2() {
		return taxId2;
	}


	public void setTaxId2(String taxId2) {
		this.taxId2 = taxId2;
	}


	public String getElement2() {
		return element2;
	}


	public void setElement2(String element2) {
		this.element2 = element2;
	}


	public String getExtra2() {
		return extra2;
	}


	public void setExtra2(String extra2) {
		this.extra2 = extra2;
	}


	public boolean isInOthers() {
		return inOthers;
	}


	public void setInOthers(boolean inOthers) {
		this.inOthers = inOthers;
	}


	public boolean isNameLevel() {
		return nameLevel;
	}


	public void setNameLevel(boolean nameLevel) {
		this.nameLevel = nameLevel;
	}


	/**
     * Default no-arg constructor
     * 
     */
	public RelationshipPairTaxaEntryGWT(){
		
	}


	public RelationshipPairTaxaEntryGWT(String taxId1, String element1,
			String extra1, String relType, String taxId2, String element2,
			String extra2, boolean inOthers, boolean nameLevel) {
		super();
		this.taxId1 = taxId1;
		this.element1 = element1;
		this.extra1 = extra1;
		this.relType = relType;
		this.taxId2 = taxId2;
		this.element2 = element2;
		this.extra2 = extra2;
		this.inOthers = inOthers;
		this.nameLevel = nameLevel;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((element1 == null) ? 0 : element1.hashCode());
		result = prime * result
				+ ((element2 == null) ? 0 : element2.hashCode());
		result = prime * result + ((extra1 == null) ? 0 : extra1.hashCode());
		result = prime * result + ((extra2 == null) ? 0 : extra2.hashCode());
		result = prime * result + (inOthers ? 1231 : 1237);
		result = prime * result + (nameLevel ? 1231 : 1237);
		result = prime * result + ((relType == null) ? 0 : relType.hashCode());
		result = prime * result + ((taxId1 == null) ? 0 : taxId1.hashCode());
		result = prime * result + ((taxId2 == null) ? 0 : taxId2.hashCode());
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
		RelationshipPairTaxaEntryGWT other = (RelationshipPairTaxaEntryGWT) obj;
		if (element1 == null) {
			if (other.element1 != null)
				return false;
		} else if (!element1.equals(other.element1))
			return false;
		if (element2 == null) {
			if (other.element2 != null)
				return false;
		} else if (!element2.equals(other.element2))
			return false;
		if (extra1 == null) {
			if (other.extra1 != null)
				return false;
		} else if (!extra1.equals(other.extra1))
			return false;
		if (extra2 == null) {
			if (other.extra2 != null)
				return false;
		} else if (!extra2.equals(other.extra2))
			return false;
		if (inOthers != other.inOthers)
			return false;
		if (nameLevel != other.nameLevel)
			return false;
		if (relType == null) {
			if (other.relType != null)
				return false;
		} else if (!relType.equals(other.relType))
			return false;
		if (taxId1 == null) {
			if (other.taxId1 != null)
				return false;
		} else if (!taxId1.equals(other.taxId1))
			return false;
		if (taxId2 == null) {
			if (other.taxId2 != null)
				return false;
		} else if (!taxId2.equals(other.taxId2))
			return false;
		return true;
	}


	@Override
	public String toString() {
		return "RelationshipPairTaxaEntryGWT [taxId1=" + taxId1 + ", element1="
				+ element1 + ", extra1=" + extra1 + ", relType=" + relType
				+ ", taxId2=" + taxId2 + ", element2=" + element2 + ", extra2="
				+ extra2 + ", inOthers=" + inOthers + ", nameLevel="
				+ nameLevel + "]";
	}
	
	

	
}
