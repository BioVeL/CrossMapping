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

public class EntryInChecklistGWT implements Serializable {

	private final static long serialVersionUID = 1L;
    
	protected String taxonId;
    protected String tidyName;
    protected String status;
    protected String higher;
    protected String species;
    protected String infraspecies;
    protected String authority;
    protected String rank;
    protected String parentId;
    
	public String getTaxonId() {
		return taxonId;
	}
	public void setTaxonId(String taxonId) {
		this.taxonId = taxonId;
	}
	public String getTidyName() {
		return tidyName;
	}
	public void setTidyName(String tidyName) {
		this.tidyName = tidyName;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getHigher() {
		return higher;
	}
	public void setHigher(String higher) {
		this.higher = higher;
	}
	public String getSpecies() {
		return species;
	}
	public void setSpecies(String species) {
		this.species = species;
	}
	public String getInfraspecies() {
		return infraspecies;
	}
	public void setInfraspecies(String infraspecies) {
		this.infraspecies = infraspecies;
	}
	public String getAuthority() {
		return authority;
	}
	public void setAuthority(String authority) {
		this.authority = authority;
	}
	public String getRank() {
		return rank;
	}
	public void setRank(String rank) {
		this.rank = rank;
	}
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	
	
	public EntryInChecklistGWT(){
		
	}
	
	public EntryInChecklistGWT(String taxonId, String tidyName, String status,
			String higher, String species, String infraspecies,
			String authority, String rank, String parentId) {
		super();
		this.taxonId = taxonId;
		this.tidyName = tidyName;
		this.status = status;
		this.higher = higher;
		this.species = species;
		this.infraspecies = infraspecies;
		this.authority = authority;
		this.rank = rank;
		this.parentId = parentId;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((authority == null) ? 0 : authority.hashCode());
		result = prime * result + ((higher == null) ? 0 : higher.hashCode());
		result = prime * result
				+ ((infraspecies == null) ? 0 : infraspecies.hashCode());
		result = prime * result
				+ ((parentId == null) ? 0 : parentId.hashCode());
		result = prime * result + ((rank == null) ? 0 : rank.hashCode());
		result = prime * result + ((species == null) ? 0 : species.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((taxonId == null) ? 0 : taxonId.hashCode());
		result = prime * result
				+ ((tidyName == null) ? 0 : tidyName.hashCode());
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
		EntryInChecklistGWT other = (EntryInChecklistGWT) obj;
		if (authority == null) {
			if (other.authority != null)
				return false;
		} else if (!authority.equals(other.authority))
			return false;
		if (higher == null) {
			if (other.higher != null)
				return false;
		} else if (!higher.equals(other.higher))
			return false;
		if (infraspecies == null) {
			if (other.infraspecies != null)
				return false;
		} else if (!infraspecies.equals(other.infraspecies))
			return false;
		if (parentId == null) {
			if (other.parentId != null)
				return false;
		} else if (!parentId.equals(other.parentId))
			return false;
		if (rank == null) {
			if (other.rank != null)
				return false;
		} else if (!rank.equals(other.rank))
			return false;
		if (species == null) {
			if (other.species != null)
				return false;
		} else if (!species.equals(other.species))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		if (taxonId == null) {
			if (other.taxonId != null)
				return false;
		} else if (!taxonId.equals(other.taxonId))
			return false;
		if (tidyName == null) {
			if (other.tidyName != null)
				return false;
		} else if (!tidyName.equals(other.tidyName))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "EntryInChecklistGWT [taxonId=" + taxonId + ", tidyName="
				+ tidyName + ", status=" + status + ", higher=" + higher
				+ ", species=" + species + ", infraspecies=" + infraspecies
				+ ", authority=" + authority + ", rank=" + rank
				+ ", parentId=" + parentId + "]";
	}
    
    
	
    
}
