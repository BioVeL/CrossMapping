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

public class RelationshipDetailGWT implements Serializable{

	private final static long serialVersionUID = 1L;
	
    protected RelationshipPairTaxaGWT directRelationship;
    protected List<RelationshipPairTaxaGWT> indirectRelationshipsLeft2Right;
    protected List<RelationshipPairTaxaGWT> indirectRelationshipsRight2Left;
	
    public RelationshipPairTaxaGWT getDirectRelationship() {
		return directRelationship;
	}
	public void setDirectRelationship(RelationshipPairTaxaGWT directRelationship) {
		this.directRelationship = directRelationship;
	}
	
	public List<RelationshipPairTaxaGWT> getIndirectRelationshipsLeft2Right() {
		return indirectRelationshipsLeft2Right;
	}
	public void setIndirectRelationshipsLeft2Right(
			List<RelationshipPairTaxaGWT> indirectRelationshipsLeft2Right) {
		this.indirectRelationshipsLeft2Right = indirectRelationshipsLeft2Right;
	}
	
	public List<RelationshipPairTaxaGWT> getIndirectRelationshipsRight2Left() {
		return indirectRelationshipsRight2Left;
	}
	public void setIndirectRelationshipsRight2Left(
			List<RelationshipPairTaxaGWT> indirectRelationshipsRight2Left) {
		this.indirectRelationshipsRight2Left = indirectRelationshipsRight2Left;
	}
	
	public RelationshipDetailGWT(){
		
	}
	
	public RelationshipDetailGWT(RelationshipPairTaxaGWT directRelationship,
			List<RelationshipPairTaxaGWT> indirectRelationshipsLeft2Right,
			List<RelationshipPairTaxaGWT> indirectRelationshipsRight2Left) {
		super();
		this.directRelationship = directRelationship;
		this.indirectRelationshipsLeft2Right = indirectRelationshipsLeft2Right;
		this.indirectRelationshipsRight2Left = indirectRelationshipsRight2Left;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((directRelationship == null) ? 0 : directRelationship
						.hashCode());
		result = prime
				* result
				+ ((indirectRelationshipsLeft2Right == null) ? 0
						: indirectRelationshipsLeft2Right.hashCode());
		result = prime
				* result
				+ ((indirectRelationshipsRight2Left == null) ? 0
						: indirectRelationshipsRight2Left.hashCode());
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
		RelationshipDetailGWT other = (RelationshipDetailGWT) obj;
		if (directRelationship == null) {
			if (other.directRelationship != null)
				return false;
		} else if (!directRelationship.equals(other.directRelationship))
			return false;
		if (indirectRelationshipsLeft2Right == null) {
			if (other.indirectRelationshipsLeft2Right != null)
				return false;
		} else if (!indirectRelationshipsLeft2Right
				.equals(other.indirectRelationshipsLeft2Right))
			return false;
		if (indirectRelationshipsRight2Left == null) {
			if (other.indirectRelationshipsRight2Left != null)
				return false;
		} else if (!indirectRelationshipsRight2Left
				.equals(other.indirectRelationshipsRight2Left))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "RelationshipDetailGWT [directRelationship="
				+ directRelationship + ", indirectRelationshipLeft2Right="
				+ indirectRelationshipsLeft2Right
				+ ", indirectRelationshipRight2Left="
				+ indirectRelationshipsRight2Left + "]";
	}
	
	
	
	

    
    
    
}
