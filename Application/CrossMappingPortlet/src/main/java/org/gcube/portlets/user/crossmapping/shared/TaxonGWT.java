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
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class TaxonGWT implements Serializable {

		private final static long serialVersionUID = 1L;
		
		private String taxonId;
	    
		private String checklistId;
		
		private String rank;
		
		private String status;
	    
		private String acceptedName;

		private List<String> synonyms;
		
		private String parentId;
		
		private List<String> childrenId;
		
		private List<XMapRelPathGWT> xmapRels;

		public String getTaxonId() {
			return taxonId;
		}

		public void setTaxonId(String taxonId) {
			this.taxonId = taxonId;
		}

		public String getChecklistId() {
			return checklistId;
		}

		public void setChecklistId(String checklistId) {
			this.checklistId = checklistId;
		}

		public String getAcceptedName() {
			return acceptedName;
		}

		public void setAcceptedName(String acceptedName) {
			this.acceptedName = acceptedName;
		}

		public List<String> getSynonyms() {
			return synonyms;
		}

		public void setSynonyms(List<String> synonyms) {
			this.synonyms = synonyms;
		}

		public String getParentId() {
			return parentId;
		}

		public void setParentId(String parentId) {
			this.parentId = parentId;
		}

		public List<String> getChildrenId() {
			return childrenId;
		}

		public void setChildrenId(List<String> childrenId) {
			this.childrenId = childrenId;
		}
		
		public String getRank() {
			return rank;
		}

		public void setRank(String rank) {
			this.rank = rank;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public List<XMapRelPathGWT> getXmapRels() {
			return xmapRels;
		}

		public void setXmapRels(List<XMapRelPathGWT> xmapRels) {
			this.xmapRels = xmapRels;
		}

		public TaxonGWT(){
			
		}

		public TaxonGWT(String taxonId, String checklistId, String rank,
				String status, String acceptedName, List<String> synonyms,
				String parentId, List<String> childrenId,
				List<XMapRelPathGWT> xmapRels) {
			super();
			this.taxonId = taxonId;
			this.checklistId = checklistId;
			this.rank = rank;
			this.status = status;
			this.acceptedName = acceptedName;
			this.synonyms = synonyms;
			this.parentId = parentId;
			this.childrenId = childrenId;
			this.xmapRels = xmapRels;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((acceptedName == null) ? 0 : acceptedName.hashCode());
			result = prime * result
					+ ((checklistId == null) ? 0 : checklistId.hashCode());
			result = prime * result
					+ ((childrenId == null) ? 0 : childrenId.hashCode());
			result = prime * result
					+ ((parentId == null) ? 0 : parentId.hashCode());
			result = prime * result + ((rank == null) ? 0 : rank.hashCode());
			result = prime * result
					+ ((status == null) ? 0 : status.hashCode());
			result = prime * result
					+ ((synonyms == null) ? 0 : synonyms.hashCode());
			result = prime * result
					+ ((taxonId == null) ? 0 : taxonId.hashCode());
			result = prime * result
					+ ((xmapRels == null) ? 0 : xmapRels.hashCode());
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
			TaxonGWT other = (TaxonGWT) obj;
			if (acceptedName == null) {
				if (other.acceptedName != null)
					return false;
			} else if (!acceptedName.equals(other.acceptedName))
				return false;
			if (checklistId == null) {
				if (other.checklistId != null)
					return false;
			} else if (!checklistId.equals(other.checklistId))
				return false;
			if (childrenId == null) {
				if (other.childrenId != null)
					return false;
			} else if (!childrenId.equals(other.childrenId))
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
			if (status == null) {
				if (other.status != null)
					return false;
			} else if (!status.equals(other.status))
				return false;
			if (synonyms == null) {
				if (other.synonyms != null)
					return false;
			} else if (!synonyms.equals(other.synonyms))
				return false;
			if (taxonId == null) {
				if (other.taxonId != null)
					return false;
			} else if (!taxonId.equals(other.taxonId))
				return false;
			if (xmapRels == null) {
				if (other.xmapRels != null)
					return false;
			} else if (!xmapRels.equals(other.xmapRels))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "TaxonGWT [taxonId=" + taxonId + ", checklistId="
					+ checklistId + ", rank=" + rank + ", status=" + status
					+ ", acceptedName=" + acceptedName + ", synonyms="
					+ synonyms + ", parentId=" + parentId + ", childrenId="
					+ childrenId + ", xmapRels=" + xmapRels + "]";
		}

		
		public TaxonGWT deepClone(){
			TaxonGWT cloneTaxon = new TaxonGWT();
			cloneTaxon.setChecklistId(clone(this.getChecklistId()));
			cloneTaxon.setTaxonId(clone(this.getTaxonId()));
			cloneTaxon.setRank(clone(this.getRank()));
			cloneTaxon.setStatus(clone(this.getStatus()));
			cloneTaxon.setAcceptedName(clone(this.getAcceptedName()));
			cloneTaxon.setSynonyms(this.getSynonyms());
			cloneTaxon.setParentId(clone(this.getParentId()));
			cloneTaxon.setChildrenId(this.getChildrenId());
			List<XMapRelPathGWT> cloneRels = new ArrayList<XMapRelPathGWT>();
			for (XMapRelPathGWT rel: this.getXmapRels()){
				XMapRelPathGWT cloneRel = new XMapRelPathGWT();
				cloneRel.setRelationship(clone(rel.getRelationship()));	
				if (rel.getOtherTaxon()!=null){
					cloneRel.setOtherTaxon(rel.getOtherTaxon().deepClone());
				}
				Stack<String> clonePath = new Stack<String>();
				for (String pathNode:rel.getPath()){
					clonePath.add(clone(pathNode));
				}
				cloneRel.setPath(clonePath);
				cloneRels.add(cloneRel);
			}
			cloneTaxon.setXmapRels(cloneRels);
			return cloneTaxon;
		}			
		
		private String clone(String obj){
			if (obj!=null && obj!=""){
				return obj.substring(0, obj.length());
			}
			else return "";
		}
		
}
