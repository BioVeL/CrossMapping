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
import java.util.Stack;

public class XMapRelPathGWT implements Serializable
{

    private final static long serialVersionUID = 1L;
    
    private String relationship;
    
    private TaxonGWT otherTaxon;
    
    private Stack<String> path;

	public String getRelationship() {
		return relationship;
	}

	public void setRelationship(String relationship) {
		this.relationship = relationship;
	}

	public TaxonGWT getOtherTaxon() {
		return otherTaxon;
	}

	public void setOtherTaxon(TaxonGWT otherTaxon) {
		this.otherTaxon = otherTaxon;
	}

	public Stack<String> getPath() {
		return path;
	}

	public void setPath(Stack<String> path) {
		this.path = path;
	}

	
	public XMapRelPathGWT(){
		
	}
	
	public XMapRelPathGWT(String relationship, TaxonGWT otherTaxon,
			Stack<String> path) {
		super();
		this.relationship = relationship;
		this.otherTaxon = otherTaxon;
		this.path = path;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((otherTaxon == null) ? 0 : otherTaxon.hashCode());
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		result = prime * result
				+ ((relationship == null) ? 0 : relationship.hashCode());
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
		XMapRelPathGWT other = (XMapRelPathGWT) obj;
		if (otherTaxon == null) {
			if (other.otherTaxon != null)
				return false;
		} else if (!otherTaxon.equals(other.otherTaxon))
			return false;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		if (relationship == null) {
			if (other.relationship != null)
				return false;
		} else if (!relationship.equals(other.relationship))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "XMapRelPathGWT [relationship=" + relationship + ", otherTaxon="
				+ otherTaxon + ", path=" + path + "]";
	}

	
	
    
	
}
