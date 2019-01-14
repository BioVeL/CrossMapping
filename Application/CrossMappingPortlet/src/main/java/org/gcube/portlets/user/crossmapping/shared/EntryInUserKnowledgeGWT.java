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

public class EntryInUserKnowledgeGWT implements Serializable {

	private final static long serialVersionUID = 1L;
	
    private String id;
    private String name1;
    private NamesRelationshipTypeGWT relationship;
    private String name2;
    private String xMapId;
    private String xMapName;
    private String user;
    private String scope;
    private String comment;
    
    
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getName1() {
		return name1;
	}
	
	public void setName1(String name1) {
		this.name1 = name1;
	}
	
	public NamesRelationshipTypeGWT getRelationship() {
		return relationship;
	}
	
	public void setRelationship(NamesRelationshipTypeGWT relationship) {
		this.relationship = relationship;
	}
	
	public String getName2() {
		return name2;
	}
	
	public void setName2(String name2) {
		this.name2 = name2;
	}
	
	public String getxMapId() {
		return xMapId;
	}
	
	public void setxMapId(String xMapId) {
		this.xMapId = xMapId;
	}
	
	public String getxMapName() {
		return xMapName;
	}
	
	public void setxMapName(String xMapName) {
		this.xMapName = xMapName;
	}
	
	public String getUser() {
		return user;
	}
	
	public void setUser(String user) {
		this.user = user;
	}
	
	public String getScope() {
		return scope;
	}
	
	public void setScope(String scope) {
		this.scope = scope;
	}
	
	public String getComment() {
		return comment;
	}
	
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	
	public EntryInUserKnowledgeGWT(){
		
	}
	
	public EntryInUserKnowledgeGWT(String id, String name1,
			NamesRelationshipTypeGWT relationship, String name2, String xMapId,
			String xMapName, String user, String scope, String comment) {
		super();
		this.id = id;
		this.name1 = name1;
		this.relationship = relationship;
		this.name2 = name2;
		this.xMapId = xMapId;
		this.xMapName = xMapName;
		this.user = user;
		this.scope = scope;
		this.comment = comment;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((comment == null) ? 0 : comment.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name1 == null) ? 0 : name1.hashCode());
		result = prime * result + ((name2 == null) ? 0 : name2.hashCode());
		result = prime * result
				+ ((relationship == null) ? 0 : relationship.hashCode());
		result = prime * result + ((scope == null) ? 0 : scope.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		result = prime * result + ((xMapId == null) ? 0 : xMapId.hashCode());
		result = prime * result
				+ ((xMapName == null) ? 0 : xMapName.hashCode());
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
		EntryInUserKnowledgeGWT other = (EntryInUserKnowledgeGWT) obj;
		if (comment == null) {
			if (other.comment != null)
				return false;
		} else if (!comment.equals(other.comment))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name1 == null) {
			if (other.name1 != null)
				return false;
		} else if (!name1.equals(other.name1))
			return false;
		if (name2 == null) {
			if (other.name2 != null)
				return false;
		} else if (!name2.equals(other.name2))
			return false;
		if (relationship != other.relationship)
			return false;
		if (scope == null) {
			if (other.scope != null)
				return false;
		} else if (!scope.equals(other.scope))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		if (xMapId == null) {
			if (other.xMapId != null)
				return false;
		} else if (!xMapId.equals(other.xMapId))
			return false;
		if (xMapName == null) {
			if (other.xMapName != null)
				return false;
		} else if (!xMapName.equals(other.xMapName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "EntryInUserKnowledgeGWT [id=" + id + ", name1=" + name1
				+ ", relationship=" + relationship + ", name2=" + name2
				+ ", xMapId=" + xMapId + ", xMapName=" + xMapName + ", user="
				+ user + ", scope=" + scope + ", comment=" + comment + "]";
	}
		
}
