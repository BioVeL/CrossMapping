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

public class TaxonDetailGWT  implements Serializable {

	private final static long serialVersionUID = 1L;
	
    protected EntryInChecklistGWT acceptedName;
    protected List<EntryInChecklistGWT> synonyms;
    protected EntryInChecklistGWT parent;
    protected List<EntryInChecklistGWT> children;
    protected RawDataGWT rawData;
    
	public EntryInChecklistGWT getAcceptedName() {
		return acceptedName;
	}
	public void setAcceptedName(EntryInChecklistGWT acceptedName) {
		this.acceptedName = acceptedName;
	}
	public List<EntryInChecklistGWT> getSynonyms() {
		return synonyms;
	}
	public void setSynonyms(List<EntryInChecklistGWT> synonyms) {
		this.synonyms = synonyms;
	}
	public EntryInChecklistGWT getParent() {
		return parent;
	}
	public void setParent(EntryInChecklistGWT parent) {
		this.parent = parent;
	}
	public List<EntryInChecklistGWT> getChildren() {
		return children;
	}
	public void setChildren(List<EntryInChecklistGWT> children) {
		this.children = children;
	}
	public RawDataGWT getRawData() {
		return rawData;
	}
	public void setRawData(RawDataGWT rawData) {
		this.rawData = rawData;
	}
	
	public TaxonDetailGWT(){
		
	}
		
	public TaxonDetailGWT(EntryInChecklistGWT acceptedName,
			List<EntryInChecklistGWT> synonyms, EntryInChecklistGWT parent,
			List<EntryInChecklistGWT> children, RawDataGWT rawData) {
		super();
		this.acceptedName = acceptedName;
		this.synonyms = synonyms;
		this.parent = parent;
		this.children = children;
		this.rawData = rawData;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((acceptedName == null) ? 0 : acceptedName.hashCode());
		result = prime * result + ((children == null) ? 0 : children.hashCode());
		result = prime * result + ((parent == null) ? 0 : parent.hashCode());
		result = prime * result + ((rawData == null) ? 0 : rawData.hashCode());
		result = prime * result + ((synonyms == null) ? 0 : synonyms.hashCode());
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
		TaxonDetailGWT other = (TaxonDetailGWT) obj;
		if (acceptedName == null) {
			if (other.acceptedName != null)
				return false;
		} else if (!acceptedName.equals(other.acceptedName))
			return false;
		if (children == null) {
			if (other.children != null)
				return false;
		} else if (!children.equals(other.children))
			return false;
		if (parent == null) {
			if (other.parent != null)
				return false;
		} else if (!parent.equals(other.parent))
			return false;
		if (rawData == null) {
			if (other.rawData != null)
				return false;
		} else if (!rawData.equals(other.rawData))
			return false;
		if (synonyms == null) {
			if (other.synonyms != null)
				return false;
		} else if (!synonyms.equals(other.synonyms))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "TaxonDetailGWT [acceptedName=" + acceptedName + ", synonyms="
				+ synonyms + ", parent=" + parent + ", children=" + children
				+ ", rawData=" + rawData + "]";
	}
	   
    
}
