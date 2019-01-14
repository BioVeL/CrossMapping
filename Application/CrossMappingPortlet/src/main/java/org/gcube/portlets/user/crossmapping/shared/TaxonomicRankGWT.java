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


public class TaxonomicRankGWT implements Serializable {

    private final static long serialVersionUID = 1L;

    private String id;
    private String name;
    private String parentId;
    private boolean isHigherRank;

    /**
     * Default no-arg constructor
     * 
     */
    public TaxonomicRankGWT() {
    }

	/**
	 * @param id
	 * @param name
	 * @param parentId
	 */
	public TaxonomicRankGWT(String id, String name, String parentId, boolean isHigherRank) {
		this.id = id;
		this.name = name;
		this.parentId = parentId;
		this.isHigherRank = isHigherRank;
	}
	
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	
	public boolean getIsHigherRank() {
		return isHigherRank;
	}

	public void setIsHigherRank(boolean isHigherRank) {
		this.isHigherRank = isHigherRank;
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
		TaxonomicRankGWT other = (TaxonomicRankGWT) obj;
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
		builder.append("TaxonomicRankGWT [id=");
		builder.append(id);
		builder.append(", name=");
		builder.append(name);
		builder.append(", parentId=");
		builder.append(parentId);
		builder.append(", isHigherRank=");
		builder.append(isHigherRank);
		builder.append("]");
		return builder.toString();
	}	
	
}
